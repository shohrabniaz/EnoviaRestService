/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.bom.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ashikur Rahman
 */
public class Obj {
    private List<Attr> attr;

    public Obj(List<Attr> attr) {
        this.attr = attr;
    }

    public List<Attr> getAttr() {
        return attr;
    }

    public void setAttr(List<Attr> attr) {
        this.attr = attr;
    }

    

    
    
}
