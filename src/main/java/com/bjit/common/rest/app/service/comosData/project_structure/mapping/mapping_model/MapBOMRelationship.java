/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class MapBOMRelationship {

    private String name;
    private MapAttributes xmlMapElementAttributes;

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public MapAttributes getXmlMapElementAttributes() {
        return xmlMapElementAttributes;
    }

    @XmlElement(name = "attributes")
    public void setXmlMapElementAttributes(MapAttributes xmlMapElementAttributes) {
        this.xmlMapElementAttributes = xmlMapElementAttributes;
    }
}
