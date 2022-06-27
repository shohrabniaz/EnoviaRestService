/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.lntransfer;

import com.bjit.common.rest.app.service.payload.common_response.*;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author BJIT
 */
public interface ILNResponse {
    ILNResponse setLNStatus(Status status);
    ILNResponse setLNSource(String source);
    ILNResponse setLNErrorMessage(List<Object> errorMessage);
    ILNResponse addLNErrorMessage(Object errorMessage);
    <T> ILNResponse addLNErrorMessage(List<T> errorMessage);
    ILNResponse setLNData(Object data);
    String buildLNResponse();
    String buildLNResponse(Boolean doNotSerialize);
    String buildLNResponse(Boolean doNotSerialize, Boolean isJsonString);
    String buildLNResponse(String responseType);
    <T>String buildLNResponse(Class<T> classType, Object object, String mediaType);
   
}
