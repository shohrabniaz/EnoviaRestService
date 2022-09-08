package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.deliverable;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.exceptions.DeliStatusListException;
import com.bjit.common.rest.app.service.comosData.responseHandler.HandleResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.deliverableProcessors.DeliverableStatusList;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService.TaskAssigneeRespondedData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusRequestEnvelope;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Log4j
@RestController
@Validated
@RequestMapping("/comos/v1")
public class DeliverableController {
    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;
    
    

    @Autowired
    DeliverableResponseHandler handleResponse;

    @Autowired
    @Qualifier("DeliverableStatusList")
    IStructurePreparation<List<TaskAssigneeRespondedData>, DeliStatusListServiceResponse, DeliStatusListRequestData> structurePreparation;
//    IStructurePreparation<List<TaskAssigneeRespondedData>, DeliStatusListServiceResponse, DeliStatusListRequestData>

    @LogExecutionTime
    @PostMapping("/deliverableStatusList")
    public String index() throws IOException {
        return "Comos Project Structure Import Greetings Service";
    }

    @LogExecutionTime
    @PostMapping("/task/deliverable/assignee")
    public String deliverableStatusList(@Valid @RequestBody DeliStatusRequestEnvelope requestData) {
        try {
            List<TaskAssigneeRespondedData>  taskAssigneeRespondedDataList = structurePreparation.prepareStructure(requestData.getDeliStatusListRequestData());
            String buildResponse = handleResponse.handleResponse(taskAssigneeRespondedDataList, (DeliverableStatusList)structurePreparation);
            return buildResponse;
        } catch (Exception ex) {
            log.error(ex);
            throw new DeliStatusListException(ex.getMessage(), requestData.getDeliStatusListRequestData());
        }
    }
}
