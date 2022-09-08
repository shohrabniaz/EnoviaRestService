/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Sajjad
 */
public class Attribute {

    private String sourceName;
    private String destinationName;
    private DataType dataType;
    private String selectable;
    private DataFormat dataFormat;
    private String description;
    private Boolean isRequired;
    private String attributeInterfaces;
    private Boolean delInterfaceIfDataNotInReq;
    private Boolean doNotAddDataIfReqIsEmpty;

    public String getSourceName() {
        return sourceName;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    @XmlElement(name = "data_format")
    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
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

    public DataType getDataType() {
        return dataType;
    }

    @XmlElement(name = "data_type")
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getSelectable() {
        return selectable;
    }

    @XmlElement(name = "selectable")
    public void setSelectable(String selectable) {
        this.selectable = selectable;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement(name = "description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    @XmlElement(name = "is_required", defaultValue = "false")
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getAttributeInterfaces() {
        return attributeInterfaces;
    }

    @XmlElement(name = "attribute_interfaces")
    public void setAttributeInterfaces(String attributeInterfaces) {
        this.attributeInterfaces = attributeInterfaces;
    }

    public Boolean getDelInterfaceIfDataNotInReq() {
        return delInterfaceIfDataNotInReq;
    }

    @XmlElement(name = "del_interface_if_data_not_in_req")
    public void setDelInterfaceIfDataNotInReq(Boolean delInterfaceIfDataNotInReq) {
        this.delInterfaceIfDataNotInReq = delInterfaceIfDataNotInReq;
    }

    public Boolean getDoNotAddDataIfReqIsEmpty() {
        return doNotAddDataIfReqIsEmpty;
    }

    @XmlElement(name = "do_not_add_data_if_req_is_empty")
    public void setDoNotAddDataIfReqIsEmpty(Boolean doNotAddDataIfReqIsEmpty) {
        this.doNotAddDataIfReqIsEmpty = doNotAddDataIfReqIsEmpty;
    }
}
