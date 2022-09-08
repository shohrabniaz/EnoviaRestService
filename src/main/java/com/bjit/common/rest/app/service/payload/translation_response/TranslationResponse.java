/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.payload.translation_response;


import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class TranslationResponse {

    private Status status;
    private String messages;
    private List<String> systemErrors;
    private Object data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public List<String> getSystemErrors() {
        return systemErrors;
    }

    public void setSystemErrors(List<String> systemErrors) {
        this.systemErrors = systemErrors;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
