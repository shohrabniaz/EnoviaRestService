/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
public class DataType {

    private String dataType;
    private Integer precision;
    private Double divisor;

    public String getDataType() {
        return dataType;
    }

    
    @XmlValue
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getPrecision() {
        return precision;
    }

    @XmlAttribute(name = "precision")
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Double getDivisor() {
        return divisor;
    }

    @XmlAttribute(name = "divisor")
    public void setDivisor(Double divisor) {
        this.divisor = divisor;
    }
}
