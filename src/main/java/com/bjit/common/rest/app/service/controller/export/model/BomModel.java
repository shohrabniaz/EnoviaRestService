/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.model;

import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationships;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BJIT
 */
@XmlRootElement
public class BomModel {

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
