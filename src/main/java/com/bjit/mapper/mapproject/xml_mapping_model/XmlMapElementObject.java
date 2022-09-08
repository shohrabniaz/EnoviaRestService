package com.bjit.mapper.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlMapElementObject {

    private String type;
    private String typeShortName;
    private XmlMapElementAttributes xmlMapElementAttributes;

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

    public String getTypeShortName() {
        return typeShortName;
    }

    @XmlAttribute
    public void setTypeShortName(String typeShortName) {
        this.typeShortName = typeShortName;
    }

    public XmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name="attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
