/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BJIT
 */
@XmlRootElement
public class MailTemplateMapper {

    private Applications applications;

    public Applications getApplications() {
        return applications;
    }

    @XmlElement(name = "applications")
    public void setApplications(Applications applications) {
        this.applications = applications;
    }
}
