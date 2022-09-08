package com.bjit.common.rest.app.service.comosData.xmlPreparation.subtasksProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.exceptions.SubtasksStructureException;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosProjectSpaceBean;
import com.bjit.common.rest.app.service.comosData.project_structure.services.ITaskImportProcess;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.Optional;

@Log4j
@Component
@Qualifier("SubtasksStructurePreparation")
@RequestScope
public class SubtasksPreparation implements IStructurePreparation<ResponseMessageFormaterBean, SubtasksServiceResponse, SubtasksRequestData> {

    @Autowired
    @Qualifier("SubtasksServiceConsumer")
    IComosData<SubtasksRequestData> subtasksServiceConsumer;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;
    @Autowired
    IJSON json;

    @Value("classpath:data/Comos_Project_Structure.json")
    Resource resourceFile;

    @Autowired
    @Qualifier("ComosTaskImportProcess")
    ITaskImportProcess<ComosProjectSpaceBean, ResponseMessageFormaterBean> projectAndTaskImport;

    @Autowired
    ComosRuntimeData comosRuntimeData;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    @Autowired
    @Qualifier("SubtasksModelConverter")
    SubtasksModelConverter subtasksModelConverter;

    @Override
    public ResponseMessageFormaterBean prepareStructure(SubtasksRequestData subtasksRequestData) throws IOException {
        try {
            SubtasksServiceResponse subtasksServiceResponse = getServiceData(subtasksRequestData);
            comosRuntimeDataBuilder.build();
            ComosProjectSpaceBean comosProjectSpaceBean = subtasksModelConverter.convert(subtasksServiceResponse);
//            System.out.println("#############################################");
//            System.out.println("#############################################");
//            System.out.println(json.serialize(comosProjectSpaceBean));
//            System.out.println("#############################################");
//            System.out.println("#############################################");
            log.info(comosProjectSpaceBean);
            ResponseMessageFormaterBean responseMessageFormaterBean = null;
            if (comosProjectSpaceBean.getProjectCode() == null) {

                responseMessageFormaterBean.setErrorMessage("Couldn't Import Task or project");
                log.info("Couldn't Import Task or project");
                return responseMessageFormaterBean;
            } else {
                responseMessageFormaterBean = projectAndTaskImport.projectAndTaskCreate(comosProjectSpaceBean);
                setRuntimeInformation(responseMessageFormaterBean);
                return responseMessageFormaterBean;
            }
        } catch (IOException exp) {
            log.error(exp.getMessage());
            throw exp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setRuntimeInformation(ResponseMessageFormaterBean responseMessageFormaterBean) {
        comosRuntimeData.getProjectSpaceData().setTnr(responseMessageFormaterBean.getTnr());
        comosRuntimeData.getProjectSpaceData().setObjectId(responseMessageFormaterBean.getObjectId());
    }
    @Override
    public SubtasksServiceResponse getServiceData(SubtasksRequestData requestData) throws IOException {
        SubtasksServiceResponse subtasksServiceResponse = getSubtasksServiceResponse(requestData);
        return subtasksServiceResponse;
    }


    private SubtasksServiceResponse getSubtasksServiceResponse(SubtasksRequestData requestData) throws IOException {
        boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String subtasksResponseData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : subtasksServiceConsumer.getComosData(requestData);
        SubtasksServiceResponse subtasksServiceResponse = getSubtasksResponseData(subtasksResponseData);
        return subtasksServiceResponse;
    }



    private SubtasksServiceResponse getSubtasksResponseData(String jsonString) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();
            SubtasksServiceResponse serviceResponse = gson.fromJson(jsonString, SubtasksServiceResponse.class);

            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new SubtasksStructureException(serviceResponse.getMessage()));

            return serviceResponse;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }
}
