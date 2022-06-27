/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Omour Faruq
 */
public class Item {

    private String type;
    private String constantClass;
    private String packageName;
    private ComosAttributeList comosAttributeList;

    public String getType() {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public String getConstantClass() {
        return constantClass;
    }

    @XmlAttribute(name = "constantClass")
    public void setConstantClass(String constantClass) {
        this.constantClass = constantClass;
    }
    
    public String getPackageName() {
        return packageName;
    }

    @XmlAttribute(name = "packageName")
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ComosAttributeList getComosAttributeList() {
        return comosAttributeList;
    }

    @XmlElement(name = "attribute-list")
    public void setComosAttributeList(ComosAttributeList comosAttributeList) {
        this.comosAttributeList = comosAttributeList;
    }

}
