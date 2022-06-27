/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

/**
 *
 * @author Ashikur Rahman
 */
public class ResponseForReport {
    private String objectName;
    private Status status;
    private String requestId;
    private String filepath;

    public ResponseForReport(String objectName, Status status, String requestId, String filepath) {
        this.objectName = objectName;
        this.status = status;
        this.requestId = requestId;
        this.filepath = filepath;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    

    @Override
    public String toString() {
        return "ResponseForReport{" + "objectName=" + objectName + ", status=" + status + ", requestId=" + requestId + ", filePath=" + filepath + '}';
    }
}
