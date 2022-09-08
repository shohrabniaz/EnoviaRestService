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
public class SummaryReport {
    private List<Obj> obj = new ArrayList<>();

    public SummaryReport(List<Obj> obj) {
        this.obj = obj;
    }

    public List<Obj> getObj() {
        return obj;
    }

    public void setObj(List<Obj> obj) {
        this.obj = obj;
    }
}
