/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.createobject_response;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ResponseMessage {

    private String responseStatus;
    private String responseMessage;

    
    private String objectId;
    //private List<ObjectOperationStatus> operationsList;
    //private List<Object> resultList;
    private HashMap<String, String> resultMap;

    /*public HashMap<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(HashMap<String, String> resultMap) {
        this.resultMap = resultMap;
    }*/
    
    private String objectName;
    private TNR typeNameRevision;
    
    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /*public String getTemplateBusinessObjectId() {
        return templateBusinessObjectId;
    }

    public void setTemplateBusinessObjectId(String templateBusinessObjectId) {
        this.templateBusinessObjectId = templateBusinessObjectId;
    }*/
    
    /*public String geObjectId() {
        return objectId;
    }

    public void setObjectId(String businessObjectId) {
        this.objectId = businessObjectId;
    }*/

    /*public List<ObjectOperationStatus> getOperationsList() {
        return operationsList;
    }

    public void setOperationsList(List<ObjectOperationStatus> operationsList) {
        this.operationsList = operationsList;
    }*/
    
    public HashMap<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(HashMap<String, String> resultMap) {
        this.resultMap = resultMap;
    }
    
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public String getObjectId() {
        return objectId;
    }

    //private String templateBusinessObjectId;
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public TNR getTypeNameRevision() {
        return typeNameRevision;
    }

    public void setTypeNameRevision(TNR typeNameRevision) {
        this.typeNameRevision = typeNameRevision;
    }
}
