package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class RangeValues {

    private List<RangeValue> value;
    private String defaultValue;

    public List<RangeValue> getValue() {
        return value;
    }

    @XmlElement(name = "value")
    public void setValue(List<RangeValue> value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @XmlAttribute
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
