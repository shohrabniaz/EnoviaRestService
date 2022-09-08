/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Omour Faruq
 */
public class ItemAttributeList {
    private List<ItemAttribute> itemAttributeList;

    public List<ItemAttribute> getItemAttributeList() {
        return itemAttributeList;
    }

    @XmlElement(name = "item-attribute")
    public void setItemAttributeList(List<ItemAttribute> itemAttributeList) {
        this.itemAttributeList = itemAttributeList;
    }
}
