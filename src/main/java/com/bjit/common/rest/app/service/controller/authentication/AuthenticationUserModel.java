/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication;

/**
 *
 * @author BJIT
 */
public class AuthenticationUserModel {
    private String userId;
    private String password;
    private String host;
    private String isCasContext;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIsCasContext() {
        return isCasContext;
    }

    public void setIsCasContext(String isCasContext) {
        this.isCasContext = isCasContext;
    }
}
