package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.deliverable;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.deliverableProcessors.DeliverableStatusList;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assigneeService.TaskAssigneeRespondedData;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeliverableResponseHandler {
    @Autowired
    @Qualifier("CustomResponseBuilder")
    IResponse responseBuilder;

    @Autowired
    BeanFactory beanFactory;

    public String handleResponse(List<TaskAssigneeRespondedData> taskAssigneeRespondedDataList, DeliverableStatusList deliverableStatusList) {
        responseBuilder.setStatus(Status.OK);

        taskAssigneeRespondedDataList.forEach((TaskAssigneeRespondedData taskAssigneeRespondedData) -> {
            ResponseMessageFormaterBean responseMessageFormaterBean = beanFactory.getBean(ResponseMessageFormaterBean.class);
            if(taskAssigneeRespondedData.getRespondedModel().getSuccess().equalsIgnoreCase("true")){
                String assignee = taskAssigneeRespondedData.getUserAndStatusList().getAssignee();
                String deliverableId = taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("attribute[PRJ_ComosActivityUID]");

                responseMessageFormaterBean.setTnr(new TNR(taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("type"), taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("name"), taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("revision")));
                responseMessageFormaterBean.setObjectId(taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("physicalid"));
                responseMessageFormaterBean.setSuccessMessage(deliverableId + " has been assigned to '" + assignee + "'");

                responseBuilder.setData(responseMessageFormaterBean);
            }
            else{
                String assignee = taskAssigneeRespondedData.getUserAndStatusList().getAssignee();
                String deliverableId = taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("attribute[PRJ_ComosActivityUID]");

                responseMessageFormaterBean.setTnr(new TNR(taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("type"), taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("name"), taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("revision")));
                responseMessageFormaterBean.setObjectId(taskAssigneeRespondedData.getUserAndStatusList().getRootItemInfo().get("physicalid"));
//                responseMessageFormaterBean.setErrorMessage(deliverableId + " couldn't be assigned to '" + assignee + "'");
                responseMessageFormaterBean.setErrorMessage(taskAssigneeRespondedData.getRespondedModel().getError());

                responseBuilder.addErrorMessage(responseMessageFormaterBean);
                responseBuilder.setStatus(Status.FAILED);
            }
        });

        Optional.ofNullable(deliverableStatusList.getDeliverableAndAssignee()).orElse(new ArrayList<>()).forEach(emptyAssignee -> {
            responseBuilder.setStatus(Status.FAILED);
            responseBuilder.addErrorMessage(emptyAssignee);});

        return responseBuilder.buildResponse();

    }
}
