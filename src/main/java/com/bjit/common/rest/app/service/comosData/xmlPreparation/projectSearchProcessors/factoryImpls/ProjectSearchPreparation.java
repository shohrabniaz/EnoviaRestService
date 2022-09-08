package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectSearchProcessors.factoryImpls;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.MilEquipmentIdSearchException;
import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosProjectSpaceBean;
import com.bjit.common.rest.app.service.comosData.project_structure.services.ITaskImportProcess;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectSearchRequestData;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosServiceResponses;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectSearch.ProjectSearchServiceResponse;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectSearchProcessors.factoryServices.IModelConverterAdapter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.Optional;

@Component
@Qualifier("ProjectSearchPreparation")
@RequestScope
public class ProjectSearchPreparation {
    private static final org.apache.log4j.Logger PROJECT_STRUCTURE_PREPARATION_LOGGER = org.apache.log4j.Logger.getLogger(ProjectSearchPreparation.class);

    @Autowired
    @Qualifier("ProjectSearchConsumer")
    IComosData<ProjectSearchRequestData> projectSearcheServiceConsumer;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

//    @Autowired
//    @Qualifier("ProjectStructureModelConverter")
//    IModelConverterAdapter<ProjectSearchServiceResponse, ComosProjectSpaceBean> projectStructureModelConverter;

//    @Autowired
//    @Qualifier("ComosTaskImportProcess")
//    ITaskImportProcess<ComosProjectSpaceBean, ResponseMessageFormaterBean> projectAndTaskImport;

    @Value("classpath:data/Comos_Project_Structure.json")
    Resource resourceFile;

    @Autowired
    ComosRuntimeData comosRuntimeData;

    @Autowired
    ComosServiceResponses serviceResponses;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;


    public ProjectSearchServiceResponse prepareStructure(ProjectSearchRequestData projectSearchRequestData) throws IOException {
        try {
            ProjectSearchServiceResponse projectSearchServiceResponse = getServiceData(projectSearchRequestData);
            comosRuntimeDataBuilder.build();
//            ComosProjectSpaceBean comosProjectSpaceBean = projectStructureModelConverter.convert(projectStructureServiceResponse);
//            comosProjectSpaceBean.setCompassId(projectStructureRequestData.getCompassId());
//            ResponseMessageFormaterBean responseMessageFormaterBean = projectAndTaskImport.projectAndTaskCreate(comosProjectSpaceBean);
//
//            setRuntimeInformation(responseMessageFormaterBean);
//
//            return responseMessageFormaterBean;
            return projectSearchServiceResponse;
        } catch (IOException exp) {

            throw exp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private void setRuntimeInformation(ResponseMessageFormaterBean responseMessageFormaterBean) {
//        comosRuntimeData.getProjectSpaceData().setTnr(responseMessageFormaterBean.getTnr());
//        comosRuntimeData.getProjectSpaceData().setObjectId(responseMessageFormaterBean.getObjectId());
//    }


    public ProjectSearchServiceResponse getServiceData(ProjectSearchRequestData requestData) throws IOException {
        ProjectSearchServiceResponse projectSearchServiceResponse = getProjectSearchServiceResponse(requestData);
        return projectSearchServiceResponse;
    }


    private ProjectSearchServiceResponse getProjectSearchServiceResponse(ProjectSearchRequestData requestData) throws IOException {
        boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String projectSearchResponseData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : projectSearcheServiceConsumer.getComosData(requestData);
        ProjectSearchServiceResponse projectSearchServiceResponse = getProjectSearchResponseData(projectSearchResponseData);
        return projectSearchServiceResponse;
    }

    private ProjectSearchServiceResponse getProjectSearchResponseData(String jsonString) {

        try {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();
            ProjectSearchServiceResponse serviceResponse = gson.fromJson(jsonString, ProjectSearchServiceResponse.class);

            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new MilEquipmentIdSearchException(serviceResponse.getMessage()));

            return serviceResponse;
        } catch (Exception exp) {
            throw exp;
        }
    }
}
