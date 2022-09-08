/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class XmlMapElementBOMRelationship {

    private String name;
    private XmlMapElementAttributes xmlMapElementAttributes;

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public XmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
