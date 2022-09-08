/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ResponseModel {
    private Status status;
    private List<ResponseTNR> items;
    private List<Message> messages;
    private List<String> systemErrors;
    private String mail;

    public ResponseModel() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public ResponseModel(Status status, List<ResponseTNR> items, List<Message> messages, List<String> systemErrors) {
        this.status = status;
        this.items = items;
        this.messages = messages;
        this.systemErrors = systemErrors;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ResponseTNR> getItems() {
        return items;
    }

    public void setItems(List<ResponseTNR> items) {
        this.items = items;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getSystemErrors() {
        return systemErrors;
    }

    public void setSystemErrors(List<String> systemErrors) {
        this.systemErrors = systemErrors;
    }
}
