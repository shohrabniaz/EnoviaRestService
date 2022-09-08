/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.webServiceConsumer.RequestConstants;
import com.bjit.common.rest.app.service.webServiceConsumer.RequestModel;
import com.bjit.common.rest.app.service.webServiceConsumer.URLConnection;
import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public class RequestHandler implements IRequestHandler {

    static final Logger MASTER_SHIP_CHANGE_REQUEST_LOGGER = Logger.getLogger(RequestHandler.class);

    @Override
    public RequestModel prepareRequest(String requestData) throws Exception {
        MASTER_SHIP_CHANGE_REQUEST_LOGGER.info("Request Data : " + requestData);
        RequestModel serviceRequesterModel = new RequestModel();
        serviceRequesterModel.setServiceMethodType(RequestConstants.POST);
        serviceRequesterModel.setBodyData(requestData);
        serviceRequesterModel.setRequestHeaders("username", PropertyReader.getProperty("enovia.pdm.integration.userName"));
        serviceRequesterModel.setRequestHeaders("password", PropertyReader.getProperty("enovia.pdm.integration.password"));
        serviceRequesterModel.setRequestHeaders("Authorization", PropertyReader.getProperty("enovia.pdm.integration.authorization.key"));
        serviceRequesterModel.setContentType("application/json");

        return serviceRequesterModel;
    }

    @Override
    public String sendRequest(RequestModel serviceRequesterModel) throws Exception {
        URLConnection URLConnection = new URLConnection();
        try {
            String responseData = URLConnection.callService(PropertyReader.getProperty("enovia.pdm.integration.service.url"), serviceRequesterModel);
            return responseData;
        } catch (Exception ex) {
            MASTER_SHIP_CHANGE_REQUEST_LOGGER.error(ex);
            throw ex;
        }
    }

    @Override
    public String sendRequest(RequestModel serviceRequesterModel, Boolean isMockResponse) throws Exception {

        return !isMockResponse ? sendRequest(serviceRequesterModel) : "{\n"
                + "  \"status\": \"OK\",\n"
                + "  \"items\": [\n"
                + "    {\n"
                + "      \"tnr\": {\n"
                + "        \"name\": \"RAU0103111\",\n"
                + "        \"revision\": \"02\",\n"
                + "        \"type\": \"Own design item\"\n"
                + "      }\n"
                + "    },\n"
                + "    {\n"
                + "      \"tnr\": {\n"
                + "        \"name\": \"RAUZ184677\",\n"
                + "        \"revision\": \"01\",\n"
                + "        \"type\": \"Own design item\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"messages\": null,\n"
                + "  \"systemErrors\": null\n"
                + "}";
    }
}
