/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.modelVersion;
import java.util.List;

/**
 * Baiic Object Data Bean to import Product
 * @author BJIT
 */
public class MVObjectDataBean {
    private String source;
    private List<MVDataTree> dataTree;

    public MVObjectDataBean() {

    }

    public MVObjectDataBean(List<MVDataTree> dataTree, String source) {
        this.dataTree = dataTree;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<MVDataTree> geDataTree() {
        return dataTree;
    }

    public void setDataTree(List<MVDataTree> dataTree) {
        this.dataTree = dataTree;
    }
}
