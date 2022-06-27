/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class Discriminator {
    private String name;
    private String value;
    private String relationShipName;

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @XmlElement(name = "value")
    public void setValue(String value) {
        this.value = value;
    }

    public String getRelationShipName() {
        return relationShipName;
    }

    @XmlElement(name = "relationship-name")
    public void setRelationShipName(String relationShipName) {
        this.relationShipName = relationShipName;
    }
}
