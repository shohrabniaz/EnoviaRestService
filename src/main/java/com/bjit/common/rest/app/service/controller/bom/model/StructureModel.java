/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class StructureModel implements Serializable, Cloneable, Comparable<StructureModel> {

    private String source;
    List<RootItem> item;

    public StructureModel() {
    }

    public StructureModel(String source, List<RootItem> item) {
        this.source = source;
        this.item = item;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<RootItem> getItem() {
        return item;
    }

    public void setItem(List<RootItem> item) {
        this.item = item;
    }

    @Override
    public int compareTo(StructureModel o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
