/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.model;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ExportModel {

    private TNR tnr;
    private List<String> attributes;
    private String reportName;
    private String requestId;
    private String reportFormat;
    private Boolean includeFiles;
    private String exportType;

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String format) {
        this.reportFormat = format;
    }

    public Boolean getIncludeFiles() {
        return includeFiles;
    }

    public void setIncludeFiles(Boolean includeFiles) {
        this.includeFiles = includeFiles;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}
