package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

public class Product {

    private String physicalid;
    private String revision;
    private String mODItemCode;

    public String getPhysicalid() {
        return physicalid;
    }

    public void setPhysicalid(String physicalid) {
        this.physicalid = physicalid;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getMODItemCode() {
        return mODItemCode;
    }

    public void setMODItemCode(String mODItemCode) {
        this.mODItemCode = mODItemCode;
    }

}
