/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Omour Faruq
 */
public class ComosItems {
    private String type;
    private List<Item> itemList; 

    public String getType() {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    @XmlElement(name = "item")
    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
