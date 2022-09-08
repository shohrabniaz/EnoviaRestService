/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

import com.bjit.common.rest.app.service.enovia_pdm.models.Message;
import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseTNR;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @param <T>
 * @param <K>
 * @author BJIT
 */
public class ServiceResponseHandler<T, K> implements IResponseHandler<T, K> {

    @Autowired
    BeanFactory beanFactory;

    @Override
    public T handle(K mapOfPDMResponses) throws Exception {
        String buildResponse;
        IResponse responseBuilder = beanFactory.getBean(IResponse.class);

        HashMap<String, List<ResponseModel>> generateResponse = (HashMap<String, List<ResponseModel>>) mapOfPDMResponses;

        List<ResponseModel> successful = generateResponse.get("successful");
        List<ResponseModel> unsuccessful = generateResponse.get("unsuccessful");

        if (unsuccessful.size() > 0 && successful.size() > 0) {
            buildResponse = responseBuilder
                    .setData(flatteningResponse(successful))
                    .addErrorMessage(flatteningResponse(unsuccessful))
                    .setStatus(Status.FAILED)
                    .buildResponse();
        } else if (unsuccessful.size() > 0) {
            buildResponse = responseBuilder
                    .addErrorMessage(flatteningResponse(unsuccessful))
                    .setStatus(Status.FAILED)
                    .buildResponse();
        } else {
            buildResponse = responseBuilder
                    .setData(flatteningResponseSuccess(successful))
                    .setStatus(Status.OK)
                    .buildResponse();
        }

        return (T) buildResponse;
    }

    public List<Message> flatteningResponse(List<ResponseModel> errorResponse) {
        List<Message> allMessages = new ArrayList<>();
        errorResponse.forEach(responseModel -> allMessages.addAll(responseModel.getMessages()));
        return allMessages;
    }
    public List<ResponseTNR> flatteningResponseSuccess(List<ResponseModel> errorResponse) {
        List<ResponseTNR> allMessages = new ArrayList<>();
        errorResponse.forEach(responseModel -> allMessages.addAll(responseModel.getItems()));
        return allMessages;
    }
}
