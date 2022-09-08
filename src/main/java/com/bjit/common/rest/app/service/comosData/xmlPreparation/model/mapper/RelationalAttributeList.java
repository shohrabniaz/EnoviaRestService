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
public class RelationalAttributeList {
    private List<RelationalAttribute> relationalAttributeList;

    public List<RelationalAttribute> getRelationalAttributeList() {
        return relationalAttributeList;
    }

    @XmlElement(name = "relational-attribute")
    public void setRelationalAttributeList(List<RelationalAttribute> relationalAttributeList) {
        this.relationalAttributeList = relationalAttributeList;
    }
}
