/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.bom.model;

/**
 * @author Ashikur Rahman
 */
public class GenerateReport {
    private Report report;

    public GenerateReport(Report report) {
        this.report = report;
    }
    
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
