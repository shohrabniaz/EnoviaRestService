/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class ItemImportValues {

    private List<ItemImportValue> value;
    private String defaultValue;

    public List<ItemImportValue> getValue() {
        return value;
    }

    @XmlElement(name = "value")
    public void setValue(List<ItemImportValue> value) {
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
