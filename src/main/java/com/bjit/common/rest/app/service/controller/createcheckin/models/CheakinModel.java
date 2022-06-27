/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin.models;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class CheakinModel {

    private String baseObjectId;
    private List<DocumentsInfo> documentInfoList;
    private TNR tnr;

    public String getBaseObjectId() {
        return baseObjectId;
    }

    public void setBaseObjectId(String baseObjectId) {
        this.baseObjectId = baseObjectId;
    }

    public List<DocumentsInfo> getDocumentInfoList() {
        return documentInfoList;
    }

    public void setDocumentInfoList(List<DocumentsInfo> documentInfoList) {
        this.documentInfoList = documentInfoList;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }
}
