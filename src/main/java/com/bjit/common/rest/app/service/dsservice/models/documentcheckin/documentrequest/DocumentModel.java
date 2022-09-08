package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest;

import com.bjit.common.rest.app.service.dsservice.models.fcsjob.DataElementsModel;

public class DocumentModel {

    private String type;
    private String id;
    private DataElementsModel dataelements;
    private DocumentRequestRelatedData relateddata;
    private String tempid;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataElementsModel getDataelements() {
        return dataelements;
    }

    public void setDataelements(DataElementsModel dataelements) {
        this.dataelements = dataelements;
    }

    public DocumentRequestRelatedData getRelateddata() {
        return relateddata;
    }

    public void setRelateddata(DocumentRequestRelatedData relateddata) {
        this.relateddata = relateddata;
    }

    public String getTempid() {
        return tempid;
    }

    public void setTempid(String tempid) {
        this.tempid = tempid;
    }
}
