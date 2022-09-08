/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class XmlMapElementBOMRelationship {

    private String relName;
    private XmlMapElementAttributes xmlMapElementAttributes;

    public String getRelName() {
        return relName;
    }

    @XmlAttribute
    public void setRelName(String relName) {
        this.relName = relName;
    }

    public XmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
