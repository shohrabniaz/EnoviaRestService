package com.bjit.common.rest.app.service.model.drawing.request;

public class DrawingDataRequest {

    private String type;
    private String name;
    private String revision;
    private String objectId;

    public DrawingDataRequest(String type, String name, String revision, String objectId) {
        this.type = type;
        this.name = name;
        this.revision = revision;
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
