/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;

/**
 * Create Object Bean for Products and its sub type
 * Classification Path properties added
 * @author BJIT
 */
public class MVCreateObjectBean extends CreateObjectBean{
    private String classificationPath;
    public String getClassificationPath() {
        return classificationPath;
    }

    public void setClassificationPath(String classificationPath) {
        this.classificationPath = classificationPath;
    }
}
