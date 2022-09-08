package com.bjit.ewc18x.model;

/**
 * @author Ashikur Rahman
 * @Description This class is a Response model
 * Sent from the Server after a service is called.
 */
public class Response {
    private String objectName;
    private Status status;
    private String requestid;
    private String message;

    public Response(String objectName, Status status, String requestId, String message) {
        this.objectName = objectName;
        this.status = status;
        this.requestid = requestId;
        this.message = message;
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

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" + "objectName=" + objectName + ", status=" + status + ", requestId=" + requestid + ", message=" + message + '}';
    }
}
