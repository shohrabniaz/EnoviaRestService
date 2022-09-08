/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

import com.bjit.ewc18x.message.EWMessages;

/**
 *
 * @author Kayum-603
 */
public class UiStatusMessageForm {
    private String status;
    private String message;
    private EWMessages ewMessages;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EWMessages getEwMessages() {
        return ewMessages;
    }

    public void setEwMessages(EWMessages ewMessages) {
        this.ewMessages = ewMessages;
    }

    @Override
    public String toString() {
        return "ApplicationMessageForm{" + "status=" + status + ", message=" + message + '}';
    }
    
}
