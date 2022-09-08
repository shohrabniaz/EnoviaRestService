/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
@XmlType(propOrder = {"type", "value"})
public class Template {

    private String type;
    private String value;

    public String getType() {
        return type;
    }

    @XmlAttribute(name="type")
    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    @XmlValue
    public void setValue(String value) {
        this.value = value;
    }
}
