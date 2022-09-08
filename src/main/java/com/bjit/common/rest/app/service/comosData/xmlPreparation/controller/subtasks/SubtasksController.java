package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.subtasks;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.SubtasksStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtaskRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.subtasks.SubtasksServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IFileReader;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
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
import com.bjit.common.rest.app.service.payload.common_response.Status;
import javax.validation.Valid;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@Validated
@RequestMapping("/comos/v1")
public class SubtasksController {

    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

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
    public String createJAXBPlantModel(@Valid @RequestBody SubtaskRequestEnvelope requestData) throws IOException, SubtasksStructureException {
        //Object prepareStructure = structurePreparation.prepareStructure(requestData.getSubtasksRequestData());



      //  return prepareStructure;

        try {
            ResponseMessageFormaterBean responseMessageFormaterBean = structurePreparation.prepareStructure(requestData.getSubtasksRequestData());

            String buildResponse = responseBuilder.setData(responseMessageFormaterBean).setStatus(Status.OK).buildResponse();
            return buildResponse;
        } catch (Exception ex) {
            Pattern pattern = Pattern.compile(".*SubtasksStructureException: (.*).*");

            Matcher matcher = pattern.matcher(ex.getMessage());
            String errorMessage="";
            while (matcher.find()) {
                errorMessage=matcher.group(1);
            }
            throw new SubtasksStructureException(errorMessage, requestData.getSubtasksRequestData());
        }
    }
}
