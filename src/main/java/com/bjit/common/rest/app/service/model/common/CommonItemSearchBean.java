/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author BJIT
 */
public class CommonItemSearchBean {

    private String type;
    private String name;
    private String revision;
    @JsonProperty(value = "attrs")
    private String[] attrs;
    @JsonProperty(value = "attr")
    private List<HashMap<String,String>> attr;

    public CommonItemSearchBean(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public CommonItemSearchBean(String type, String name, String revision) {
        this.type = type;
        this.name = name;
        this.revision = revision;

    }

    public CommonItemSearchBean() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String[] getAttrs() {
        return attrs;
    }

    public void setAttrs(String[] attrs) {
        this.attrs = attrs;
    }

    public List<HashMap<String,String>> getAttr() {
        return attr;
    }

    public void setAttr(List<HashMap<String,String>> attr) {
        this.attr = attr;
    }

}
