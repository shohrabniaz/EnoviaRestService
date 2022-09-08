package com.bjit.common.rest.app.service.controller.export.report.single_level.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ReportDetailDataModel {
    private List<HashMap<String, Object>> detailBOM;
    private String rootObjectName;
    private String rootObjectRev;
    private String rootObjectDrNum;
    private String rootObjectState;
    private String rootObjectDes;
    private String rootObjectReleasedBy;
    private String rootObjectReleasedDate;
    private String singleReportUID;
    
    public ReportDetailDataModel() {
        this.detailBOM = new ArrayList<>();
    }

    public void setDetailBOM(List<HashMap<String, Object>> detailBOM) {
        this.detailBOM = detailBOM;
    }

    public void setRootObjectName(String rootObjectName) {
        this.rootObjectName = rootObjectName;
    }

    public void setRootObjectRev(String rootObjectRev) {
        this.rootObjectRev = rootObjectRev;
    }

    public void setRootObjectDrNum(String rootObjectDrNum) {
        this.rootObjectDrNum = rootObjectDrNum;
    }

    public void setRootObjectState(String rootObjectState) {
        this.rootObjectState = rootObjectState;
    }

    public void setRootObjectDes(String rootObjectDes) {
        this.rootObjectDes = rootObjectDes;
    }

    public void setRootObjectReleasedBy(String rootObjectReleasedBy) {
        this.rootObjectReleasedBy = rootObjectReleasedBy;
    }

    public void setRootObjectReleasedDate(String rootObjectReleasedDate) {
        this.rootObjectReleasedDate = rootObjectReleasedDate;
    }

    public void setSingleReportUID(String singleReportUID) {
        this.singleReportUID = singleReportUID;
    }

    public List<HashMap<String, Object>> getDetailBOM() {
        return detailBOM;
    }

    public String getRootObjectName() {
        return rootObjectName;
    }

    public String getRootObjectRev() {
        return rootObjectRev;
    }

    public String getRootObjectDrNum() {
        return rootObjectDrNum;
    }

    public String getRootObjectState() {
        return rootObjectState;
    }

    public String getRootObjectDes() {
        return rootObjectDes;
    }

    public String getRootObjectReleasedBy() {
        return rootObjectReleasedBy;
    }

    public String getRootObjectReleasedDate() {
        return rootObjectReleasedDate;
    }

    public String getSingleReportUID() {
        return singleReportUID;
    }
}
