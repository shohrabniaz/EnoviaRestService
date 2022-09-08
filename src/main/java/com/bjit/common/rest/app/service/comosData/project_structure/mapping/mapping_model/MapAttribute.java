/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author BJIT
 */
@XmlType(propOrder = {"sourceName", "destinationName", "fixedValue", "rangeValues", "dataType", "dataLength", "dateFormat", "dataDateFormat", "date", "description", "isProperty", "selectable", "dataFormat"})
public class MapAttribute {

    private String sourceName;
    private String destinationName;
    private String fixedValue;
    private RangeValues rangeValues;
    private String dataType;
    private Integer dataLength;
    private String dataDateFormat;
    private String dateFormat;
    private String date;
    private String description;
    private String isProperty;
    private String selectable;
    private String dataFormat;

    public String getSelectable() {
        return selectable;
    }

    @XmlElement(name = "selectable")
    public void setSelectable(String selectable) {
        this.selectable = selectable;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    @XmlElement(name = "data-format")
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @XmlElement(name = "date-format")
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDataDateFormat() {
        return dataDateFormat;
    }

    @XmlElement(name = "data-date-format")
    public void setDataDateFormat(String dataDateFormat) {
        this.dataDateFormat = dataDateFormat;
    }

    public String getDate() {
        return date;
    }

    @XmlElement(name = "date")
    public void setDate(String date) {
        this.date = date;
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

    public String getFixedValue() {
        return fixedValue;
    }

    @XmlElement(name = "fixed_value")
    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public RangeValues getRangeValues() {
        return rangeValues;
    }

    @XmlElement(name = "values")
    public void setRangeValues(RangeValues rangeValues) {
        this.rangeValues = rangeValues;
    }

    public String getDataType() {
        return dataType;
    }

    @XmlElement(name = "data-type")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    
    public Integer getDataLength() {
        return dataLength;
    }

    @XmlElement(name = "data-length")
    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
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
