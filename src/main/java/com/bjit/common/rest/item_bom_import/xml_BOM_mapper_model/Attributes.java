/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Sajjad
 */
public class Attributes {

    private List<Attribute> attributeList;
    public List<Attribute> getAttributeList() {
        return attributeList;
    }
    @XmlElement(name="attribute")
    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }
}
