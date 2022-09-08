/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mastershipChange;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author BJIT
 */

//@Lazy
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MCMailConfig {

    private String host;
    private String transportProtocol;
    private String sender;
    private String subject;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String textContent;
    private String filePath;
    private Boolean debug;
    private String template;

    public MCMailConfig() {
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, List<String> to, List<String> cc, List<String> bcc, String textContent, String filePath, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.textContent = textContent;
        this.filePath = filePath;
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, List<String> to, List<String> cc, List<String> bcc, String textContent, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.textContent = textContent;
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, String to, String cc, String bcc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.setTo(to);
        this.setCc(cc);
        this.setBcc(bcc);
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, List<String> to, List<String> cc, List<String> bcc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, String cc, String bcc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.setCc(cc);
        this.setBcc(bcc);
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, List<String> cc, List<String> bcc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.cc = cc;
        this.bcc = bcc;
        this.debug = debug;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, String cc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.setCc(cc);
        this.debug = debug;
        this.template = template;
    }

    public MCMailConfig(String host, String transportProtocol, String sender, String subject, List<String> cc, Boolean debug, String template) {
        this.host = host;
        this.transportProtocol = transportProtocol;
        this.sender = sender;
        this.subject = subject;
        this.cc = cc;
        this.debug = debug;
        this.template = template;
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(String transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public void setTo(String to) {
        this.to = Optional.ofNullable(this.to).orElse(new ArrayList<>());
        this.to.add(to);
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public void setCc(String cc) {
        this.cc = Optional.ofNullable(this.cc).orElse(new ArrayList<>());
        this.cc.add(cc);
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = Optional.ofNullable(this.bcc).orElse(new ArrayList<>());
        this.bcc.add(bcc);
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
