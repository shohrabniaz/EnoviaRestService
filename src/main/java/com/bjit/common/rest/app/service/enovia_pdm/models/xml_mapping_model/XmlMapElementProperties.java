/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class XmlMapElementProperties {

    private List<XmlMapElementProperty> xmlMapElementProperty;

    public List<XmlMapElementProperty> getXmlMapElementProperty() {
        return xmlMapElementProperty;
    }

    @XmlElement(name = "property")
    public void setXmlMapElementProperty(List<XmlMapElementProperty> xmlMapElementProperty) {
        this.xmlMapElementProperty = xmlMapElementProperty;
    }
}
