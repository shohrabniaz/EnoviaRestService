/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import java.util.HashMap;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVItemsModel {
    private String versionName;
    private String type;
    private MVCreateAttributeModel attributes;

    public MVItemsModel(String versionName, String type, MVCreateAttributeModel attributes){
        this.versionName = versionName == null ? "" : versionName;
        this.type = type == null ? "" : type;
        this.attributes = attributes;
    }
    
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MVCreateAttributeModel getAttributes() {
        return attributes;
    }

    public void setAttributes(MVCreateAttributeModel attributes) {
        this.attributes = attributes;
    }
    
}
