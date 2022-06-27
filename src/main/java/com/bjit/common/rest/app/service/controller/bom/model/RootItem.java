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
public class RootItem implements Serializable, Cloneable, Comparable<RootItem> {

    private TNR tnr;
    private String id;
    private List<ChildItem> lines;
    private HashMap<String, String> attributes;

    public RootItem() {
    }

    public RootItem(TNR tnr, String id, List<ChildItem> lines) {
        this.tnr = tnr;
        this.id = id;
        this.lines = lines;
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

    public List<ChildItem> getLines() {
        return lines;
    }

    public void setLines(List<ChildItem> lines) {
        this.lines = lines;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int compareTo(RootItem o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
