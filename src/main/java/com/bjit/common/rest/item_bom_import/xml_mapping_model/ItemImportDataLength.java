/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
public class ItemImportDataLength {

    private Integer value;
    private String paddingChar;

    public Integer getValue() {
        return value;
    }

    @XmlValue
    public void setValue(Integer value) {
        this.value = value;
    }

    public String getPaddingChar() {
        return paddingChar;
    }
    
    @XmlAttribute
    public void setPaddingChar(String paddingChar) {
        this.paddingChar = paddingChar;
    }
}
