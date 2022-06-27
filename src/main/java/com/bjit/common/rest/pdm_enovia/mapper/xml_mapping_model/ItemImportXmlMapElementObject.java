package com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ItemImportXmlMapElementObject {

    private String type;
    private ItemImportXmlMapElementAttributes xmlMapElementAttributes;
    private String runTimeInterfaceList;

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

    public ItemImportXmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(ItemImportXmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }

    public String getRunTimeInterfaceList() {
        return runTimeInterfaceList;
    }

    @XmlElement(name = "runTimeInterfaceList")
    public void setRunTimeInterfaceList(String runTimeInterfaceList) {
        this.runTimeInterfaceList = runTimeInterfaceList;
    }
}
