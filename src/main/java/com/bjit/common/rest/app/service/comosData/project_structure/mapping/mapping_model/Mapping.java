/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BJIT
 */
@XmlRootElement
public class Mapping {

    private MapObjects xmlMapElementObjects;
    private MapBOMRelationships xmlMapElementBOMRelationships;

    public MapObjects getXmlMapElementObjects() {
        return xmlMapElementObjects;
    }

    @XmlElement(name = "objects")
    public void setXmlMapElementObjects(MapObjects xmlMapElementObjects) {
        this.xmlMapElementObjects = xmlMapElementObjects;
    }

    public MapBOMRelationships getXmlMapElementBOMRelationships() {
        return xmlMapElementBOMRelationships;
    }

    @XmlElement(name = "relationships")
    public void setXmlMapElementBOMRelationships(MapBOMRelationships xmlMapElementBOMRelationships) {
        this.xmlMapElementBOMRelationships = xmlMapElementBOMRelationships;
    }
}
