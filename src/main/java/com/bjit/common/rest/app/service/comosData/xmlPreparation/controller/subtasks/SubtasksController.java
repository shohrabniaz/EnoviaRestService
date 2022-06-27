package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.subtasks;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtaskRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
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


@RestController
@Validated
@RequestMapping("/comos/v1")
public class SubtasksController {

//    @Autowired
//    @Qualifier("ComosFileWriter")
//    IComosFileWriter fileWriter;

    @Autowired
    IJSON json;
    @Autowired
    BeanFactory beanFactory;

    @Autowired
    @Qualifier("ComosFileReader")
    IFileReader fileReader;

    @Autowired
    @Qualifier("SubtasksStructurePreparation")
    IStructurePreparation<ResponseMessageFormaterBean, SubtasksServiceResponse, SubtasksRequestData> structurePreparation;

    @LogExecutionTime
    @PostMapping("/export/subtasks/xml")
    public Object createJAXBPlantModel(@Valid @RequestBody SubtaskRequestEnvelope requestData) throws IOException {
        Object prepareStructure = structurePreparation.prepareStructure(requestData.getSubtasksRequestData());

//        String subtasksData = fileReader.readFile("D:\\COMOS new\\subTasks.json");
//
//        GsonBuilder builder = new GsonBuilder();
//        builder.serializeNulls();
//        Gson gson = builder.create();
//        SubtasksResponse serviceResponse = gson.fromJson(subtasksData, SubtasksResponse.class);
        //Subtasksdeliverable subtasksdeliverable = serviceResponse.getData().getDeliverables();


        return prepareStructure;
    }
}
