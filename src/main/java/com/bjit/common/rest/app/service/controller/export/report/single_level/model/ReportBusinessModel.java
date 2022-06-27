package com.bjit.common.rest.app.service.controller.export.report.single_level.model;

import java.util.ArrayList;
import java.util.HashMap;
import matrix.db.BusinessObject;

/**
 *
 * @author BJIT
 */
public class ReportBusinessModel {
    private ReportParameterModel parameter;
    private String mapAbsoluteDirectory;
    private ArrayList<String> attributeList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> alternativeAttributeMap = new HashMap<>();
    private BusinessObject businessObject;
    private boolean isMBOMReport;
    private boolean isTitleRequired;
    private boolean childPropertiesRequired;

    public ReportParameterModel getParameter() {
        return parameter;
    }

    public void setParameter(ReportParameterModel parameter) {
        this.parameter = parameter;
    }

    public boolean isIsMBOMReport() {
        return isMBOMReport;
    }

    public void setIsMBOMReport(boolean isMBOMReport) {
        this.isMBOMReport = isMBOMReport;
    }

    public String getMapAbsoluteDirectory() {
        return mapAbsoluteDirectory;
    }

    public void setMapAbsoluteDirectory(String mapAbsoluteDirectory) {
        this.mapAbsoluteDirectory = mapAbsoluteDirectory;
    }

    public ArrayList<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(ArrayList<String> attributeList) {
        this.attributeList = attributeList;
    }

    public HashMap<String, ArrayList<String>> getAlternativeAttributeMap() {
        return alternativeAttributeMap;
    }

    public void setAlternativeAttributeMap(HashMap<String, ArrayList<String>> alternativeAttributeMap) {
        this.alternativeAttributeMap = alternativeAttributeMap;
    }

    public BusinessObject getBusinessObject() {
        return businessObject;
    }

    public void setBusinessObject(BusinessObject businessObject) {
        this.businessObject = businessObject;
    }

    public boolean isIsTitleRequired() {
        return isTitleRequired;
    }

    public void setIsTitleRequired(boolean isTitleRequired) {
        this.isTitleRequired = isTitleRequired;
    }

    public boolean isChildPropertiesRequired() {
        return childPropertiesRequired;
    }

    public void setChildPropertiesRequired(boolean childPropertiesRequired) {
        this.childPropertiesRequired = childPropertiesRequired;
    }
}
