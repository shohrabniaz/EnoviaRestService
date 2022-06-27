package com.bjit.common.rest.app.service.model.checkin;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;
import java.util.Map;
public class CheckinBean {
    private String baseObjectId;
    private List<DocumentInfoBean> documentInfoList;
    private List<Map<String,String>> documentIds;
    private TNR tnr;

    public List<DocumentInfoBean> getDocumentInfoList() {
        return documentInfoList;
    }

    public void setDocumentInfoList(List<DocumentInfoBean> documentInfoList) {
        this.documentInfoList = documentInfoList;
    }

    public String getBaseObjectId() {
        return baseObjectId;
    }

    public void setBaseObjectId(String baseObjectId) {
        this.baseObjectId = baseObjectId;
    }

    public List<Map<String,String>> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Map<String,String>> documentIds) {
        this.documentIds = documentIds;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }
}