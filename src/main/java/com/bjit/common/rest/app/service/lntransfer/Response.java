/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.lntransfer;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class Response {

    private Status status;
    private String source;
    private List<Object> messages;
    private List<String> systemErrors;
    private Object data;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
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
