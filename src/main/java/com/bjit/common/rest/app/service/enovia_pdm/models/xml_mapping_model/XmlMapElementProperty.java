/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class XmlMapElementProperty {

    private XmlMapElementAttributes xmlMapElementAttributes;

    public XmlMapElementAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(XmlMapElementAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
