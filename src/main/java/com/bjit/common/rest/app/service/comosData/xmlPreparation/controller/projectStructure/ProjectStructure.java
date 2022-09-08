package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.projectStructure;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IComosFileWriter;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

//import static com.bjit.common.rest.app.service.comosData.xmlPreparation.model.validators.RFLVPMValidator.distinctByKey;
//@RestController
//@Validated
//@RequestMapping("/comos/v1")
public class ProjectStructure {
//
//    @Autowired
//    @Qualifier("ComosFileWriter")
//    IComosFileWriter fileWriter;
//
//    @Autowired
//    IJSON json;
//    @Autowired
//    BeanFactory beanFactory;
//
//    @Autowired
//    @Qualifier("ComosFileReader")
//    IFileReader fileReader;
////    @Autowired
////    @Qualifier("AssemblyStructurePreparation")
////    IStructurePreparation<HashMap<String, RFLP>, AssemblyServiceResponse, AssemblyRequestData> structurePreparation;
////was a texting controller
//    @LogExecutionTime
//    @PostMapping("/export/projectStructure/xml")
//    public String createJAXBPlantModel(@Valid @RequestBody ProjectStructureRequestData requestData) throws IOException {
//      //  HashMap<String, RFLP> prepareStructure = structurePreparation.prepareStructure(requestData);
//
//        String projectStructureData = fileReader.readFile("D:\\COMOS new\\get_project_structure_sample_response.json");
//
//        GsonBuilder builder = new GsonBuilder();
//        builder.serializeNulls();
//        Gson gson = builder.create();
//        ProjectStructureServiceResponse serviceResponse = gson.fromJson(projectStructureData, ProjectStructureServiceResponse.class);
//
////        ProjectStructureChild projectStructure = serviceResponse.getData().getProjectStructure();
////        String id = projectStructure.getId();
//
//        return "projectStructureResponseData" + " ....> " ;
//    }
}
