/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.model;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ChildItem implements Serializable, Cloneable, Comparable<ChildItem> {

    private TNR tnr;
    private String id;
    private HashMap<String, String> attributes;
    private ChildItem selected;
    private List<ChildItem> childItems;

    public ChildItem() {
    }

    public ChildItem(TNR tnr, String id, HashMap<String, String> attributes) {
        this.tnr = tnr;
        this.id = id;
        this.attributes = attributes;
    }

    public ChildItem(TNR tnr, String id, HashMap<String, String> attributes, ChildItem selected) {
        this.tnr = tnr;
        this.id = id;
        this.attributes = attributes;
        this.selected = selected;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public ChildItem getSelected() {
        return selected;
    }

    public void setSelected(ChildItem selected) {
        this.selected = selected;
    }

    public List<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ChildItem> childItems) {
        this.childItems = childItems;
    }

    @Override
    public int compareTo(ChildItem o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
