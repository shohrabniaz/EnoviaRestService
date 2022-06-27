/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class MapAttributes {
    private List<MapAttribute> xmlMapElementAttribute;

    public List<MapAttribute> getXmlMapElementAttribute() {
        return xmlMapElementAttribute;
    }

    @XmlElement(name="attribute")
    public void setXmlMapElementAttribute(List<MapAttribute> xmlMapElementAttribute) {
        this.xmlMapElementAttribute = xmlMapElementAttribute;
    }
}
