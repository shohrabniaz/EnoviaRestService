/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.lntransfer;

import com.bjit.common.rest.app.service.lntransfer.ILNResponse;
import com.bjit.common.rest.app.service.payload.common_response.CommonResponse;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */
@Component("lnResponseBuilder")
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestScope
public class LNCustomResponseBuilder implements ILNResponse {

    private List<Object> errorMessages;
    private Object responseData;
    private String responseStr;
    private Status operationStatus;
    private String source;
    private List<String> systemErrors;
    private final HashMap<String, Object> newPropertyMap = new HashMap<>();
    private static final org.apache.log4j.Logger RESPONSE_BUILDER_LOGGER = org.apache.log4j.Logger.getLogger(LNCustomResponseBuilder.class);

    @Override
    public ILNResponse setLNStatus(Status status) {
        this.operationStatus = status;
        return this;
    }
   @Override
    public ILNResponse setLNSource(String source) {
        this.source = source;
        return this;
    }
    @Override
    public ILNResponse setLNErrorMessage(List<Object> errorMessage) {
        errorMessage.forEach(this::addLNErrorMessage);
        //this.errorMessages = errorMessage;
        return this;
    }

    @Override
    public ILNResponse addLNErrorMessage(Object errorMessage) {

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
    public <T> ILNResponse addLNErrorMessage(List<T> errorMessage) {
        errorMessage.forEach(this::addLNErrorMessage);

        return this;
    }

  
    @Override
    public ILNResponse setLNData(Object data) {
        this.responseData = data;
        return this;
    }


    @Override
    public String buildLNResponse() {
        return buildLNResponse(Boolean.FALSE);
    }

    @Override
    public String buildLNResponse(Boolean serializeNulls) {
        JSON responseJson = new JSON(serializeNulls);

        return buildJsonResponse(responseJson);
    }

    @Override
    public String buildLNResponse(Boolean serializeNulls, Boolean isJsonString) {
        if (!serializeNulls) {
            return buildLNResponse();
        }

        if (!isJsonString) {
            return buildLNResponse(isJsonString);
        }

        JSON responseJson = new JSON(serializeNulls);
        this.responseData = responseJson.deserialize(this.responseData.toString(), JsonObject.class);

        return buildJsonResponse(responseJson);
    }

    private String buildJsonResponse(JSON responseJson) {
        CommonResponse response = setLNResponseData();
        response.setData(responseJson.deserialize(responseJson.serialize(response.getData(), this.newPropertyMap), Object.class));

        return responseJson.serialize(response);
    }

    private CommonResponse setLNResponseData() {
        CommonResponse response = new CommonResponse();
        response.setData(this.responseData);
            response.setSource(this.source);
        response.setStatus(this.operationStatus);
        response.setMessages(this.errorMessages);
        response.setSystemErrors(this.systemErrors);

        return response;
    }
    

    @Override
    public String buildLNResponse(String responseType) {

        if (responseType.equalsIgnoreCase("application/xml")) {
            this.operationStatus = null;

            String buildResponse = buildLNResponse();
            JSONObject json = new JSONObject(buildResponse);
            buildResponse = XML.toString(json);

            return buildResponse;
        }
        return buildLNResponse();
    }

    @Override
    public <T> String buildLNResponse(Class<T> classType, Object object, String mediaType) {
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
