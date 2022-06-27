/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.translation_response;

import java.util.HashMap;
import java.util.List;
import com.bjit.common.rest.app.service.payload.common_response.Status;
/**
 *
 * @author BJIT
 */
public interface ITranslationResponse {
    ITranslationResponse setStatus(Status status);
    ITranslationResponse setMessage(String message);
    ITranslationResponse setErrorMessage(List<Object> errorMessage);
    ITranslationResponse addErrorMessage(Object errorMessage);
    <T> ITranslationResponse addErrorMessage(List<T> errorMessage);
    ITranslationResponse cleanErrorMessage();
    ITranslationResponse setData(Object data);
    String buildResponse();
    String buildResponse(Boolean doNotSerialize);
    String buildResponse(Boolean doNotSerialize, Boolean isJsonString);
    String buildResponse(String responseType);
    <T>String buildResponse(Class<T> classType, Object object, String mediaType);
}
