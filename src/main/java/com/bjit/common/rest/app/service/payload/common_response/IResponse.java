/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.common_response;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author BJIT
 */
public interface IResponse {
    IResponse setStatus(Status status);
    IResponse setSource(String source);
    IResponse setErrorMessage(List<Object> errorMessage);
    IResponse addErrorMessage(Object errorMessage);
    <T> IResponse addErrorMessage(List<T> errorMessage);
    IResponse cleanErrorMessage();
    IResponse setData(Object data);
    IResponse addNewProperty(String propertyName, String propertyValue);
    IResponse addNewProperty(String propertyName, HashMap<String, Object> propertyValue);
    IResponse addNewProperty(String propertyName, List<Object> propertyValue);
    IResponse addNewSummaryProperty(String propertyName, List<HashMap<String,String>> propertyValue);
    String buildResponse();
    String buildResponse(Boolean doNotSerialize);
    String buildResponse(Boolean doNotSerialize, Boolean isJsonString);
    String buildResponse(String responseType);
    <T>String buildResponse(Class<T> classType, Object object, String mediaType);
    JSONObject createJsonObjectResponse(Status message, JSONArray jsonArray);
}
