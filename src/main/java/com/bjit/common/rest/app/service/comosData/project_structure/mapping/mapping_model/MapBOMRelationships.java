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
public class MapBOMRelationships {
    private List<MapBOMRelationship> xmlMapElementBOMRelationship;

    public List<MapBOMRelationship> getXmlMapElementBOMRelationship() {
        return xmlMapElementBOMRelationship;
    }

    @XmlElement(name = "relationship")
    public void setXmlMapElementBOMRelationship(List<MapBOMRelationship> Relationship) {
        this.xmlMapElementBOMRelationship = Relationship;
    }
}
