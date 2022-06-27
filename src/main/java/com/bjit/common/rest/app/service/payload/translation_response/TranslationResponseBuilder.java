/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.translation_response;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.google.gson.JsonObject;
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
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.MediaType;

/**
 *
 * @author BJIT
 */
public class TranslationResponseBuilder implements ITranslationResponse {

    private List<Object> errorMessages;
    private String messages;
    private Object responseData;
    private String responseStr;
    private Status operationStatus;
    private List<String> systemErrors;
    private final HashMap<String, Object> newPropertyMap = new HashMap<>();
    private static final org.apache.log4j.Logger RESPONSE_BUILDER_LOGGER = org.apache.log4j.Logger.getLogger(TranslationResponseBuilder.class);

    @Override
    public ITranslationResponse setStatus(Status status) {
        this.operationStatus = status;
        return this;
    }

    @Override
    public ITranslationResponse setErrorMessage(List<Object> errorMessage) {
        errorMessage.forEach(this::addErrorMessage);
        return this;
    }
     @Override
    public ITranslationResponse setMessage(String message) {
        this.messages = message;
        return this;
    }

    @Override
    public ITranslationResponse addErrorMessage(Object errorMessage) {

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
    public <T> ITranslationResponse addErrorMessage(List<T> errorMessage) {
        errorMessage.forEach(this::addErrorMessage);

        return this;
    }

    @Override
    public ITranslationResponse cleanErrorMessage() {
        if (!NullOrEmptyChecker.isNull(this.errorMessages)) {
            this.errorMessages.clear();
        }
        return this;
    }

    @Override
    public ITranslationResponse setData(Object data) {
        this.responseData = data;
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
        TranslationResponse response = setResponseData();
        response.setData(responseJson.deserialize(responseJson.serialize(response.getData(), this.newPropertyMap), Object.class));

        return responseJson.serialize(response);
    }

    private TranslationResponse setResponseData() {
        TranslationResponse response = new TranslationResponse();
        response.setData(this.responseData);
        response.setStatus(this.operationStatus);
        response.setMessages(this.messages);
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

}
