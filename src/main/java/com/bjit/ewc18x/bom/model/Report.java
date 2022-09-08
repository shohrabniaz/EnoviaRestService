/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.bom.model;

/**
 *
 * @author Ashikur Rahman
 */
public class Report {
    private String name;
    private ObjectTree objTree;
    private SummaryReport summaryReport;
    private MetaData metaData;
    
    
    public Report(String name, ObjectTree objTree, SummaryReport summaryReport, MetaData metaData) {
        this.name = name;
        this.objTree = objTree;
        this.summaryReport = summaryReport;
        this.metaData = metaData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectTree getObjTree() {
        return objTree;
    }

    public void setObjTree(ObjectTree objTree) {
        this.objTree = objTree;
    }

    public SummaryReport getSummaryReport() {
        return summaryReport;
    }

    public void setSummaryReport(SummaryReport summaryReport) {
        this.summaryReport = summaryReport;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
    

    
}
