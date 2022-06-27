/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BJIT
 */
@XmlRootElement
public class Mapping {

    private XmlMapElementObjects xmlMapElementObjects;
    private XmlMapElementBOMRelationships xmlMapElementBOMRelationships;

    public XmlMapElementObjects getXmlMapElementObjects() {
        return xmlMapElementObjects;
    }

    @XmlElement(name = "objects")
    public void setXmlMapElementObjects(XmlMapElementObjects xmlMapElementObjects) {
        this.xmlMapElementObjects = xmlMapElementObjects;
    }

    public XmlMapElementBOMRelationships getXmlMapElementBOMRelationships() {
        return xmlMapElementBOMRelationships;
    }

    @XmlElement(name = "relationships")
    public void setXmlMapElementBOMRelationships(XmlMapElementBOMRelationships xmlMapElementBOMRelationships) {
        this.xmlMapElementBOMRelationships = xmlMapElementBOMRelationships;
    }
}
