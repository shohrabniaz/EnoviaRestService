/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class BomImportData {
    private String destinationValue;
    private HashMap<String, String> rangeValues;

    public String getDestinationValue() {
        return destinationValue;
    }

    public void setDestinationValue(String destinationValue) {
        this.destinationValue = destinationValue;
    }

    public HashMap<String, String> getRangeValues() {
        return rangeValues;
    }

    public void setRangeValues(HashMap<String, String> rangeValues) {
        this.rangeValues = rangeValues;
    }
}
