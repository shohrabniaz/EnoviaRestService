/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVCreateUpdateResponseFormatter extends ResponseMessageFormaterBean {

    private String modelPhysicalId;
    private String mvPhysicalId;
    private String ctxMfgItemCode;
    private String mfgItemCode;
    private String sourceRevision;
    private String failedMessage;

    public String getCtxMfgItemCode() {
        return ctxMfgItemCode;
    }

    public void setCtxMfgItemCode(String ctxMfgItemCode) {
        this.ctxMfgItemCode = ctxMfgItemCode;
    }

    public String getMfgItemCode() {
        return mfgItemCode;
    }

    public void setMfgItemCode(String mfgItemCode) {
        this.mfgItemCode = mfgItemCode;
    }

    public String getModelPhysicalId() {
        return modelPhysicalId;
    }

    public void setModelPhysicalId(String modelPhysicalId) {
        this.modelPhysicalId = modelPhysicalId;
    }

    public String getMvPhysicalId() {
        return mvPhysicalId;
    }

    public void setMvPhysicalId(String mvPhysicalId) {
        this.mvPhysicalId = mvPhysicalId;
    }

    public String getSourceRevision() {
        return sourceRevision;
    }

    public void setSourceRevision(String sourceRevision) {
        this.sourceRevision = sourceRevision;
    }

    public String getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(String failedMessage) {
        this.failedMessage = "Failed: " + failedMessage;
    }

}
