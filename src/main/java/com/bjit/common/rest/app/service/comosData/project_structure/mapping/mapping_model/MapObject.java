package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MapObject {

    private String type;
    private String operation;

    private MapAttributes xmlMapElementAttributes;
    private String runtimeInterfaceList;

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

    public MapAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(MapAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }

    public String getRuntimeInterfaceList() {
        return runtimeInterfaceList;
    }

    @XmlElement(name = "runtimeInterfaceList")
    public void setRuntimeInterfaceList(String runtimeInterfaceList) {
        this.runtimeInterfaceList = runtimeInterfaceList;
    }

    public String getOperation() {
        return operation;
    }

    @XmlAttribute(name = "operation")
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
