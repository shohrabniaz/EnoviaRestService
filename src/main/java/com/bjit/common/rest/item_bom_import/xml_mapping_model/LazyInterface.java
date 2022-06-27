/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_mapping_model;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author BJIT
 */
public class LazyInterface {

    private Boolean requestHasValue;
    private LazyInterfaceIsValue isValue;
    private String setValue;
    private String interfaces;

    public Boolean getRequestHasValue() {
        return requestHasValue;
    }

    @XmlElement(name = "request-has-value", defaultValue = "true")
    public void setRequestHasValue(Boolean requestHasValue) {
        this.requestHasValue = requestHasValue;
    }

    public LazyInterfaceIsValue getIsValue() {
        return isValue;
    }

    @XmlElement(name = "is-value")
    public void setIsValue(LazyInterfaceIsValue isValue) {
        this.isValue = isValue;
    }

    public String getSetValue() {
        return setValue;
    }

    @XmlElement(name = "set-value")
    public void setSetValue(String setValue) {
        this.setValue = setValue;
    }

    public String getInterfaces() {
        return interfaces;
    }

    @XmlElement(name = "interfaces")
    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }
}
