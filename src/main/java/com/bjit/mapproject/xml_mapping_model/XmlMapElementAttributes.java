/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class XmlMapElementAttributes {
    private List<XmlMapElementAttribute> xmlMapElementAttribute;

    public List<XmlMapElementAttribute> getXmlMapElementAttribute() {
        return xmlMapElementAttribute;
    }
    public XmlMapElementAttribute getXmlMapElementAttributeFromSource(String source) {
        for (XmlMapElementAttribute attribute : xmlMapElementAttribute) {
            if (source.equals(attribute.getSourceName())) {
                return attribute;
            }
        }
        return null;
    }
    @XmlElement(name="attribute")
    public void setXmlMapElementAttribute(List<XmlMapElementAttribute> xmlMapElementAttribute) {
        this.xmlMapElementAttribute = xmlMapElementAttribute;
    }
}
