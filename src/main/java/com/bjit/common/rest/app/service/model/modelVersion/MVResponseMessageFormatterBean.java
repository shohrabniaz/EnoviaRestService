/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;

/**
 * Model Version Import Response Formatter Bean extended from
 * 'ResponseMessageFormatterBean' and classification path properties included
 *
 * @author BJIT
 */
public class MVResponseMessageFormatterBean extends ResponseMessageFormaterBean {

    private String classificationPath;

    public String getClassificationPath() {
        return classificationPath;
    }

    public void setClassificationPath(String classificationPath) {
        this.classificationPath = classificationPath;
    }
}
