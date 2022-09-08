/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.MaturityChange;

/**
 *
 * @author Tahmid
 */
public class MaturityChangeBean {

    private String id;
    private String type;
    private String name;
    private String rev;
    private String tostate;
//    private String securityContext;

    public String getTostate() {
        return tostate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTostate(String tostate) {
        this.tostate = tostate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

//    public String getSecurityContext() {
//        return securityContext;
//    }
//
//    public void setSecurityContext(String securityContext) {
//        this.securityContext = securityContext;
//    }
}