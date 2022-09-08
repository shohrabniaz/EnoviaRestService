package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryImpls;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.project_structure.model.ComosProjectSpaceBean;
import com.bjit.common.rest.app.service.comosData.project_structure.services.ITaskImportProcess;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosServiceResponses;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryServices.IModelConverterAdapter;
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
@Qualifier("ProjectStructurePreparation")
@RequestScope
public class ProjectStructurePreparation implements IStructurePreparation<ResponseMessageFormaterBean, ProjectStructureServiceResponse, ProjectStructureRequestData> {
    private static final org.apache.log4j.Logger PROJECT_STRUCTURE_PREPARATION_LOGGER = org.apache.log4j.Logger.getLogger(ProjectStructurePreparation.class);

    @Autowired
    @Qualifier("ProjectStructureServiceConsumer")
    IComosData<ProjectStructureRequestData> projectStructureServiceConsumer;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

    @Autowired
    @Qualifier("ProjectStructureModelConverter")
    IModelConverterAdapter<ProjectStructureServiceResponse, ComosProjectSpaceBean> projectStructureModelConverter;

    @Autowired
    @Qualifier("ComosTaskImportProcess")
    ITaskImportProcess<ComosProjectSpaceBean, ResponseMessageFormaterBean> projectAndTaskImport;

    @Value("classpath:data/Comos_Project_Structure.json")
    Resource resourceFile;

    @Autowired
    ComosRuntimeData comosRuntimeData;

    @Autowired
    ComosServiceResponses serviceResponses;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    @Override
    public ResponseMessageFormaterBean prepareStructure(ProjectStructureRequestData projectStructureRequestData) throws IOException {
        try {
            ProjectStructureServiceResponse projectStructureServiceResponse = getServiceData(projectStructureRequestData);
            comosRuntimeDataBuilder.build();
            ComosProjectSpaceBean comosProjectSpaceBean = projectStructureModelConverter.convert(projectStructureServiceResponse);
            comosProjectSpaceBean.setCompassId(projectStructureRequestData.getCompassId());
            ResponseMessageFormaterBean responseMessageFormaterBean = projectAndTaskImport.projectAndTaskCreate(comosProjectSpaceBean);

            setRuntimeInformation(responseMessageFormaterBean);

            return responseMessageFormaterBean;
        } catch (IOException exp) {
            PROJECT_STRUCTURE_PREPARATION_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void setRuntimeInformation(ResponseMessageFormaterBean responseMessageFormaterBean) {
        comosRuntimeData.getProjectSpaceData().setTnr(responseMessageFormaterBean.getTnr());
        comosRuntimeData.getProjectSpaceData().setObjectId(responseMessageFormaterBean.getObjectId());
    }

    @Override
    public ProjectStructureServiceResponse getServiceData(ProjectStructureRequestData requestData) throws IOException {
        ProjectStructureServiceResponse projectStructureServiceResponse = getProjectStructureServiceResponse(requestData);
        serviceResponses.setProjectStructureServiceResponse(projectStructureServiceResponse);
        return projectStructureServiceResponse;
    }

    @LogExecutionTime
    private ProjectStructureServiceResponse getProjectStructureServiceResponse(ProjectStructureRequestData requestData) throws IOException {
        boolean readFromFile = Boolean.parseBoolean(PropertyReader.getProperty("comos.get.json.data.from.file"));
        String projectStructureResponseData = readFromFile ? fileReader.readFile(resourceFile.getFile()) : projectStructureServiceConsumer.getComosData(requestData);
        ProjectStructureServiceResponse projectStructureServiceResponse = getProjectStructureResponseData(projectStructureResponseData);
        return projectStructureServiceResponse;
    }

    private ProjectStructureServiceResponse getProjectStructureResponseData(String jsonString) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();
            ProjectStructureServiceResponse serviceResponse = gson.fromJson(jsonString, ProjectStructureServiceResponse.class);

            Optional.ofNullable(serviceResponse.getData()).orElseThrow(() -> new ProjectStructureException(serviceResponse.getMessage()));

            return serviceResponse;
        } catch (Exception exp) {
            PROJECT_STRUCTURE_PREPARATION_LOGGER.error(exp);
            throw exp;
        }
    }
}
