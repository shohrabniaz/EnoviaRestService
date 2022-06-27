package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse;

import com.bjit.common.rest.app.service.dsservice.models.fcsjob.DataElementsModel;


public class FileInfo {
    private String id;
    private String type;
    private String relId;
    private String cestamp;
    private DataElementsModel dataelements;
    private FileRelatedData relateddata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getCestamp() {
        return cestamp;
    }

    public void setCestamp(String cestamp) {
        this.cestamp = cestamp;
    }

    public DataElementsModel getDataelements() {
        return dataelements;
    }

    public void setDataelements(DataElementsModel dataelements) {
        this.dataelements = dataelements;
    }

    public FileRelatedData getRelateddata() {
        return relateddata;
    }

    public void setRelateddata(FileRelatedData relateddata) {
        this.relateddata = relateddata;
    }
}
