/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.itemImport;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class ObjectDataBean {

    private String source;
    private List<DataTree> dataTree;

    public ObjectDataBean() {

    }

    public ObjectDataBean(List<DataTree> dataTree, String source) {
        this.dataTree = dataTree;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<DataTree> geDataTree() {
        return dataTree;
    }

    public void setDataTree(List<DataTree> dataTree) {
        this.dataTree = dataTree;
    }
}
