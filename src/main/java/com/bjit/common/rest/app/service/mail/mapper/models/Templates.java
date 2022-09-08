/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class Templates {

    private List<Template> templateList;

    public List<Template> getTemplateList() {
        return templateList;
    }

    @XmlElement(name = "template")
    public void setTemplateList(List<Template> templateList) {
        this.templateList = templateList;
    }
}
