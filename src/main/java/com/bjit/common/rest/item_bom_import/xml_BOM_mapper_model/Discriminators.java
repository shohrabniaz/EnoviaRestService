/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class Discriminators {

    private List<Discriminator> discriminatorList;
    private String defaultRelationshipName;

    public List<Discriminator> getDiscriminatorList() {
        return discriminatorList;
    }

    @XmlElement(name = "discriminator")
    public void setDiscriminatorList(List<Discriminator> discriminatorList) {
        this.discriminatorList = discriminatorList;
    }

    public String getDefaultRelationshipName() {
        return defaultRelationshipName;
    }

    @XmlAttribute(name = "default-relationship-name")
    public void setDefaultRelationshipName(String defaultRelationshipName) {
        this.defaultRelationshipName = defaultRelationshipName;
    }
}
