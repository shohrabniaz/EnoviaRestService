/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.himelli;


import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.ewc18x.validator.BOMExportValidation;
import com.bjit.mapper.mapproject.util.Constants;
import com.bjit.plmkey.ws.controller.expandobject.ExpandObjectUtil;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Horidas Roy
 */
public class HimelliRequestValidator {
    
    private static final org.apache.log4j.Logger HIMELLI_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(HimelliRequestValidator.class);
    private final String serviceRequestURLPattern = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private final String mandatoryRequestParams = "name|attrs";
    private final String mandatoryAttrs = "name|Title";
   
    
    public boolean validateHimelliServiceRequest(HttpServletRequest httpRequest, String attributes) throws Exception{
    try {
        
            
            HIMELLI_UTIL_LOGGER.info("Validating request");
            Boolean isRequestValid = validateHimelliServiceRequestCall(httpRequest, attributes);
            if (!isRequestValid) {
                HIMELLI_UTIL_LOGGER.info("Request is invalid");
                //throw new Exception(Constants.ATTRIBUTION_EXCEPTION);
            } 
           return isRequestValid;
        } catch (Exception exp) {
            HIMELLI_UTIL_LOGGER.error(exp.getMessage());
            throw exp;
        }
        
    }
    
    public boolean validateHimelliServiceRequestCall(HttpServletRequest httpRequest, String attributes) throws Exception{
        
        try {
            boolean isUrlValid = isValidURL(httpRequest.getRequestURL().toString());
            
            boolean isMandatoryParamsFound = isMandatoryRequestParamsFound(httpRequest.getParameterMap());
            boolean isMandatoryAttrsFound = isMandatoryAttrsFound(attributes);
            
            if(isMandatoryParamsFound && isUrlValid && isMandatoryAttrsFound){
                return true;
            }
                
        } catch (Exception ex) {
             HIMELLI_UTIL_LOGGER.error(ex.getMessage());
            throw ex;
        }
        return false;
       
    }

    private boolean isValidURL(String URL) throws Exception {
       return ExpandObjectUtil.hasPatternMatched(URL, serviceRequestURLPattern);
    }

    private boolean isMandatoryRequestParamsFound(Map<String, String[]> requestParamMap) throws Exception {
        if (requestParamMap != null && !requestParamMap.isEmpty()) {
            List<String> mandatoryParamList = new LinkedList<>(Arrays.asList(mandatoryRequestParams.split("\\|")));
            requestParamMap.forEach((key, value) -> {
                mandatoryParamList.removeIf(p -> p.equals(key));
            });
            return mandatoryParamList.isEmpty();
        }
        return false;
    }

    private boolean isMandatoryAttrsFound(String attributes) throws Exception{
         if (attributes != null && attributes.length()>0) {
            List<String> mandatoryParamList = new LinkedList<>(Arrays.asList(mandatoryAttrs.split("\\|")));
            Arrays.stream(attributes.split(",")).forEach(attr -> {
                mandatoryParamList.removeIf(a -> a.equals(attr.trim()));
            });
            return mandatoryParamList.isEmpty();
        }
        return false;
    }
}
