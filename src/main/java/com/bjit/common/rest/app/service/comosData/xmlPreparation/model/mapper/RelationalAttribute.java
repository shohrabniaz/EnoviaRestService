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
public class RelationalAttribute {
    private String type;
    private String sourceType;
    private String destinationType;

    public String getType() {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public String getSourceType() {
        return sourceType;
    }

    @XmlElement(name = "source")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getDestinationType() {
        return destinationType;
    }

    @XmlElement(name = "destination")
    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }
}
