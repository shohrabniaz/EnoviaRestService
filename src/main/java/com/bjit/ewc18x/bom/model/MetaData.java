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
public class MetaData {
    private List<Data> metaData = new ArrayList<>();

    public MetaData(List<Data> metData) {
        this.metaData = metData;
    }

    public List<Data> getMetData() {
        return metaData;
    }

    public void setMetData(List<Data> metData) {
        this.metaData = metData;
    }
    
    
}
