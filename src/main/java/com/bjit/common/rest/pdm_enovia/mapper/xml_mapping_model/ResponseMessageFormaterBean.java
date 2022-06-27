/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResponseMessageFormaterBean {

    private TNR tnr;
    private String objectId;
    private String errorMessage;

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String exceptionInfo = "Exception: ";
        if (errorMessage.contains(exceptionInfo))
            errorMessage = errorMessage.substring(errorMessage.indexOf(exceptionInfo) + exceptionInfo.length());
        this.errorMessage = errorMessage;
    }
}
