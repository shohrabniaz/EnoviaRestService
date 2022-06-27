/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.ObjectAttribute;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Tahmid
 */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "value", "properties" })
public class Version {
    private String value;
    private CustomMap properties;

    public Version() {
    }

    public Version(String value, CustomMap properties) {
        this.value = value;
        this.properties = properties;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute
    public void setValue(String value) {
        this.value = value;
    }

    public CustomMap getProperties() {
        return properties;
    }

    @XmlElement
    public void setProperties(CustomMap properties) {
        this.properties = properties;
    }
}
