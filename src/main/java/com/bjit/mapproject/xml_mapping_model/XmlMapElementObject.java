package com.bjit.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlMapElementObject {
    private String type;
    
    private XmlMapElementAttributes xmlMapElementAttributes;
    private XmlMapElementProperties xmlMapElementProperteis;
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
    public XmlMapElementProperties XmlMapElementProperties() {
        return xmlMapElementProperteis;
    }
     @XmlElement(name="properties")
    public void setXmlMapElementProperties(XmlMapElementProperties properties) {
        this.xmlMapElementProperteis = properties;
    }
    @XmlElement(name="attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
