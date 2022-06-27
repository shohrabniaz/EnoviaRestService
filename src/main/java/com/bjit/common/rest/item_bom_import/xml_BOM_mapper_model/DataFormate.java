/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Sajjad
 */
public class DataFormate {
     private String src;
    private String value;

    public String getSrc() {
        return src;
    }

    @XmlAttribute
    public void setSrc(String src) {
        this.src = src;
    }
    
}
