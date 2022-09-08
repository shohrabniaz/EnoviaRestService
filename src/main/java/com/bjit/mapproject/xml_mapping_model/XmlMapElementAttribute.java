/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author BJIT
 */
@XmlType(propOrder = {"sourceName", "destinationName", "dataType", "description", "isProperty","dataFromat"})
public class XmlMapElementAttribute {
    private String sourceName;
    private String destinationName;
    private String dataType;
    private String description;
    private String isProperty;
    private String dataFromat;

    public String getDataFromat() {
        return dataFromat;
    }

    @XmlElement(name = "format")
    public void setDataFromat(String dataFromat) {
        this.dataFromat = dataFromat;
    }
    public String getSourceName() {
        return sourceName;
    }

    @XmlElement(name = "source_name")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    @XmlElement(name = "destination_name")
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDataType() {
        return dataType;
    }

    @XmlElement(name = "type")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsProperty() {
        return isProperty;
    }

    @XmlAttribute
    public void setIsProperty(String isProperty) {
        this.isProperty = isProperty;
    }
}
