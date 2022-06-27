/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Omour Faruq
 */
public class ComosAttributeList {
    private ItemAttributeList itemAttributes;
    private RelationalAttributeList relationalAttributes;

    public ItemAttributeList getItemAttributes() {
        return itemAttributes;
    }

    @XmlElement(name = "item-attribute-list")
    public void setItemAttributes(ItemAttributeList itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public RelationalAttributeList getRelationalAttributes() {
        return relationalAttributes;
    }

    @XmlElement(name = "relational-attribute-list")
    public void setRelationalAttributes(RelationalAttributeList relationalAttributes) {
        this.relationalAttributes = relationalAttributes;
    }
}
