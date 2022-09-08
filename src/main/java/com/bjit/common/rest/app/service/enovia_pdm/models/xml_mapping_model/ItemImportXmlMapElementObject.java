package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ItemImportXmlMapElementObject {

    private String type;
    private XmlMapElementAttributes xmlMapElementAttributes;
    private String runTimeInterfaceList;

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

    public XmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
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
