/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author BJIT
 */
@XmlType(propOrder = {"sourceName", "fieldLabel", "dataType", "description", "isProperty", "selectable", "dataFormat", "altLabel", "isPreSelected", "isVisibleInList"})
public class XmlMapElementAttribute {
    private String sourceName;
    private String fieldLabel;
    private String dataType;
    private String description;
    private String isProperty;
    private String selectable;
    private String dataFormat;
    private String altLabel;
    private String isPreSelected;
    private String isVisibleInList;

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

    @XmlElement(name = "source_name")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    @XmlElement(name = "field_label")
    public void setFieldLabel(String destinationName) {
        this.fieldLabel = destinationName;
    }

    public String getDataType() {
        return dataType;
    }

    @XmlElement(name = "data-type")
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

    public String getIsVisibleInList() {
        return isVisibleInList;
    }

    @XmlAttribute(name="isVisibleInList")
    public void setIsVisibleInList(String isVisibleInList) {
        this.isVisibleInList = isVisibleInList;
    }

    public String getAltLabel() {
        return altLabel;
    }

    @XmlAttribute(name="alt-label")
    public void setAltLabel(String altLabel) {
        this.altLabel = altLabel;
    }

    public String getIsPreSelected() {
        return isPreSelected;
    }

    @XmlAttribute(name="isPreSelected")
    public void setIsPreSelected(String isPreSelected) {
        this.isPreSelected = isPreSelected;
    }

}
