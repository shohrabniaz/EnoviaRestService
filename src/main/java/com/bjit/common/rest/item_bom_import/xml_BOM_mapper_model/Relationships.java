/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sajjad
 */
@XmlRootElement
public class Relationships {
    private List<Relationship> relationshipList;

    public List<Relationship> getRelationshipList() {
        return relationshipList;
    }
    @XmlElement(name="relationship")
    public void setRelationshipList(List<Relationship> relationshipList) {
        this.relationshipList = relationshipList;
    }
    
    
}
