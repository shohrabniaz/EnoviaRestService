/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
public class LazyInterfaceIsValue {

    private Boolean reverse;
    private String condition;

    public Boolean getReverse() {
        return reverse;
    }

    @XmlAttribute
    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public String getCondition() {
        return condition;
    }

    @XmlValue
    public void setCondition(String condition) {
        this.condition = condition;
    }
}
