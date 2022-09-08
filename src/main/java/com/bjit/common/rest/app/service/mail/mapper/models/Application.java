/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Application {

    private Templates templates;
    private String type;

    public Templates getTemplates() {
        return templates;
    }

    @XmlElement(name = "templates")
    public void setTemplates(Templates templates) {
        this.templates = templates;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }
}
