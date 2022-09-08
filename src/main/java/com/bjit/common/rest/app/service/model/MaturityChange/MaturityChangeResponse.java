/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.MaturityChange;

/**
 *
 * @author BJIT
 */
public class MaturityChangeResponse {

    private String id;
    private String state;
    private String message;

    public MaturityChangeResponse(String id, String state, String message) {
        this.id = id;
        this.state = state;
        this.message = message;
    }

    public MaturityChangeResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
