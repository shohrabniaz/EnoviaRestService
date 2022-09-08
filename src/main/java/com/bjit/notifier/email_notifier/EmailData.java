/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier.email_notifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class EmailData {

    private String from;
    private List<String> toList;
    private List<String> ccList;
    private List<String> bccList;

    private String subject;
    private String data;
    private List<String> attachment;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return toList;
    }

    public void setTo(List<String> toList) {
        if (this.toList == null) {
            this.toList = new ArrayList<>();
        }
        this.toList.addAll(toList);
    }

    public void setTo(String... toList) {
        this.setTo(Arrays.asList(toList));
    }

    public void setTo(String to) {
        this.setTo(Arrays.asList(to.split("\\s*,\\s*")));
    }

    public List<String> getCc() {
        return ccList;
    }

    public void setCc(List<String> ccList) {
        if (this.ccList == null) {
            this.ccList = new ArrayList<>();
        }
        this.ccList.addAll(ccList);
    }

    public void setCc(String... ccList) {
        this.setCc(Arrays.asList(ccList));
    }

    public void setCc(String ccList) {
        this.setCc(Arrays.asList(ccList.split("\\s*,\\s*")));
    }

    public List<String> getBcc() {
        return bccList;
    }

    public void setBcc(List<String> bccList) {
        if (this.bccList == null) {
            this.bccList = new ArrayList<>();
        }
        this.bccList.addAll(bccList);
    }

    public void setBcc(String... bccList) {
        this.setBcc(Arrays.asList(bccList));
    }

    public void setBcc(String bccList) {
        this.setBcc(Arrays.asList(bccList.split("\\s*,\\s*")));
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<String> attachment) {
        if (this.attachment == null) {
            this.attachment = new ArrayList<>();
        }

        this.attachment.addAll(attachment);
    }

    public void setAttachment(String... attachment) {
        this.setAttachment(Arrays.asList(attachment));
    }

    public void setAttachment(String attachment) {
        this.setAttachment(Arrays.asList(attachment.split("\\s*,\\s*")));
    }
}
