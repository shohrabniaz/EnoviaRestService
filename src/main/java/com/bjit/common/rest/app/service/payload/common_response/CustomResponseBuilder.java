/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.common_response;

import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.google.gson.JsonObject;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * @author BJIT
 */
@Service
@RequestScope
@Qualifier("CustomResponseBuilder")
public class CustomResponseBuilder implements IResponse {

    private static final org.apache.log4j.Logger RESPONSE_BUILDER_LOGGER = org.apache.log4j.Logger.getLogger(CustomResponseBuilder.class);
    private final HashMap<String, Object> newPropertyMap = new HashMap<>();
    private List<Object> errorMessages;
    private Object responseData;
    private String responseStr;
    private Status operationStatus;
    private String source;
    private List<String> systemErrors;

    @Override
    public IResponse setStatus(Status status) {
        this.operationStatus = status;
        return this;
    }

    @Override
    public IResponse setSource(String source) {
        this.source = source;
        return this;
    }

    @Override
    public IResponse setErrorMessage(List<Object> errorMessage) {
        errorMessage.forEach(this::addErrorMessage);
        //this.errorMessages = errorMessage;
        return this;
    }

    @Override
    public IResponse addErrorMessage(Object errorMessage) {

        if (addStringErrorMessage(errorMessage)) {
            return this;
        }

        if (NullOrEmptyChecker.isNull(this.errorMessages)) {
            this.errorMessages = new ArrayList<>();
        }

        this.errorMessages.add(errorMessage);
        return this;
    }

    private boolean addStringErrorMessage(Object errorMessage) {
        if (errorMessage instanceof String) {
            if (NullOrEmptyChecker.isNullOrEmpty(this.systemErrors)) {
                this.systemErrors = new ArrayList<>();
            }
            this.systemErrors.add(CommonUtilities.removeExceptions(errorMessage.toString()));
            return true;
        }
        return false;
    }

    @Override
    public <T> IResponse addErrorMessage(List<T> errorMessage) {
        errorMessage.forEach(this::addErrorMessage);

        return this;
    }

    @Override
    public IResponse cleanErrorMessage() {
        if (!NullOrEmptyChecker.isNull(this.errorMessages)) {
            this.errorMessages.clear();
        }
        return this;
    }

    @Override
    public IResponse setData(Object data) {
        this.responseData = data;
        return this;
    }

    @Override
    public IResponse addNewProperty(String propertyName, String propertyValue) {
        newPropertyMap.put(propertyName, propertyValue);
        return this;
    }

    @Override
    public IResponse addNewProperty(String propertyName, HashMap<String, Object> propertyValue) {
        this.newPropertyMap.put(propertyName, propertyValue);
        return this;
    }

    @Override
    public IResponse addNewProperty(String propertyName, List<Object> propertyValue) {
        this.newPropertyMap.put(propertyName, propertyValue);
        return this;
    }

    @Override
    public String buildResponse() {
        return buildResponse(Boolean.FALSE);
    }

    @Override
    public String buildResponse(Boolean serializeNulls) {
        JSON responseJson = new JSON(serializeNulls);

        return buildJsonResponse(responseJson);
    }

    @Override
    public String buildResponse(Boolean serializeNulls, Boolean isJsonString) {
        if (!serializeNulls) {
            return buildResponse();
        }

        if (!isJsonString) {
            return buildResponse(isJsonString);
        }

        JSON responseJson = new JSON(serializeNulls);
        this.responseData = responseJson.deserialize(this.responseData.toString(), JsonObject.class);

        return buildJsonResponse(responseJson);
    }

    private String buildJsonResponse(JSON responseJson) {
        CommonResponse response = setResponseData();
        response.setData(responseJson.deserialize(responseJson.serialize(response.getData(), this.newPropertyMap), Object.class));

        return responseJson.serialize(response);
    }

    private CommonResponse setResponseData() {
        CommonResponse response = new CommonResponse();
        response.setData(this.responseData);

        response.setStatus(this.operationStatus);
        response.setMessages(this.errorMessages);
        response.setSystemErrors(this.systemErrors);

        return response;
    }


    @Override
    public String buildResponse(String responseType) {

        if (responseType.equalsIgnoreCase("application/xml")) {
            this.operationStatus = null;

            String buildResponse = buildResponse();
            JSONObject json = new JSONObject(buildResponse);
            buildResponse = XML.toString(json);

            return buildResponse;
        }
        return buildResponse();
    }

    @Override
    public <T> String buildResponse(Class<T> classType, Object object, String mediaType) {
        try {
            Map<String, Object> properties = new HashMap<>();
            if (mediaType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
                properties.put(JAXBContextProperties.MEDIA_TYPE, mediaType);
                properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{classType}, properties);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sb = new StringWriter();
            jaxbMarshaller.marshal(object, sb);
            responseStr = sb.toString();
        } catch (PropertyException ex) {
            RESPONSE_BUILDER_LOGGER.error(ex.getMessage());
        } catch (JAXBException ex) {
            RESPONSE_BUILDER_LOGGER.error(ex.getMessage());
        }
        return responseStr;
    }

    @Override
    public IResponse addNewSummaryProperty(String propertyName, List<HashMap<String, String>> propertyValue) {
        this.newPropertyMap.put(propertyName, propertyValue);
        return this;
    }

    /**
     * prepare JSONObject response.
     *
     * @param message   is response status
     * @param jsonArray contain final response data
     * @return JSONObject
     */
    @Override
    public JSONObject createJsonObjectResponse(Status message, JSONArray jsonArray) {
        JSONObject response = new JSONObject();
        response.put("status", message);
        response.put("data", jsonArray);
        return response;
    }
    public void setErrorMessages(List<Object> errorMessage) {
        this.errorMessages = errorMessage;
    }
}
