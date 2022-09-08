/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author BJIT
 */
@XmlType(propOrder = {"sourceName", "destinationName", "values", "dataType", "description", "isProperty", "requestAttribute", "selectable", "dataLength", "isRequired", "dataFormat", "insertable", "lazyInterface"})
public class ItemImportXmlMapElementAttribute {

    private String sourceName;
    private String destinationName;
    private ItemImportValues values;
    private String dataType;
    private String description;
    private String isProperty;
    private Boolean requestAttribute;
    private String selectable;
    private ItemImportDataLength dataLength;
    private Boolean isRequired;
    private DataFormat dataFormat;
    private String insertable;
    private LazyInterface lazyInterface;

    public String getSelectable() {
        return selectable;
    }

    @XmlElement(name = "selectable")
    public void setSelectable(String selectable) {
        this.selectable = selectable;
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

    public ItemImportValues getValues() {
        return values;
    }

    @XmlElement(name = "values")
    public void setValues(ItemImportValues values) {
        this.values = values;
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

    public Boolean getRequestAttribute() {
        return requestAttribute;
    }

    @XmlAttribute
    public void setRequestAttribute(Boolean requestAttribute) {
        this.requestAttribute = requestAttribute;
    }

    public ItemImportDataLength getDataLength() {
        return dataLength;
    }

    @XmlElement(name = "data_length")
    public void setDataLength(ItemImportDataLength dataLength) {
        this.dataLength = dataLength;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    @XmlElement(name = "is_required", defaultValue = "false")
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    @XmlElement(name = "data-format")
    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getInsertable() {
        return insertable;
    }

    @XmlElement(name = "insertable")
    public void setInsertable(String insertable) {
        this.insertable = insertable;
    }

    public LazyInterface getLazyInterface() {
        return lazyInterface;
    }

    @XmlElement(name = "lazy-interface")
    public void setLazyInterface(LazyInterface lazyInterface) {
        this.lazyInterface = lazyInterface;
    }
}
