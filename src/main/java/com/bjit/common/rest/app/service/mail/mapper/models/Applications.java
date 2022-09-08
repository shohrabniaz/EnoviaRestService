/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.models;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author BJIT
 */
public class Applications {
    private List<Application> applicationList;

    public List<Application> getApplicationList() {
        return applicationList;
    }

    @XmlElement(name="application")
    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }
}
