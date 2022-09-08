/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Omour Faruq
 */
@XmlRootElement(name = "comos")
//@XmlType(propOrder = {"logicalReference", "logicalInstance", "logicalPort"})
public class ComosMapper {
    private List<ComosItems> itemList;

    public List<ComosItems> getItemList() {
        return itemList;
    }

    @XmlElement(name = "item-list")
    public void setItemList(List<ComosItems> itemList) {
        this.itemList = itemList;
    }
}
