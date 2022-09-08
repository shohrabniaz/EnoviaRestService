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
public class ItemImportValue {

    private String src;
    private String value;
    private String hasAny;

    private String runTimeInterfaceList;

    public String getSrc() {
        return src;
    }

    @XmlAttribute
    public void setSrc(String src) {
        this.src = src;
    }

    public String getValue() {
        return value;
    }

    @XmlValue
    public void setValue(String value) {
        this.value = value;
    }

    public String getHasAny() {
        return hasAny;
    }

    @XmlAttribute
    public void setHasAny(String hasAny) {
        this.hasAny = hasAny;
    }

    public String getRunTimeInterfaceList() {
        return runTimeInterfaceList;
    }

    @XmlAttribute(name = "runTimeInterfaceList")
    public void setRunTimeInterfaceList(String runTimeInterfaceList) {
        this.runTimeInterfaceList = runTimeInterfaceList;
    }
}
