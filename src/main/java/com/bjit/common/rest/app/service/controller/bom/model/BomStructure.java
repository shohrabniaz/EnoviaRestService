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
 * @author Sajjad
 */
public class BomStructure implements Cloneable, Comparable<BomStructure>, Serializable {

    private TNR item;
    private List<HashMap<Object, Object>> lines;

    public BomStructure() {
    }

    public BomStructure(TNR item, List<HashMap<Object, Object>> lines) {
        this.item = item;
        this.lines = lines;
    }

    public TNR getItem() {
        return item;
    }

    public void setItem(TNR item) {
        this.item = item;
    }

    public List<HashMap<Object, Object>> getLines() {
        return lines;
    }

    public void setLines(List<HashMap<Object, Object>> lines) {
        this.lines = lines;
    }

    @Override
    public int compareTo(BomStructure o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
