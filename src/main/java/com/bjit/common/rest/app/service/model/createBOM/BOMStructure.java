/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.createBOM;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class BOMStructure {
    private String source;
    private List<CreateBOMBean> createBomBeanList;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<CreateBOMBean> getCreateBomBeanList() {
        return createBomBeanList;
    }

    public void setCreateBomBeanList(List<CreateBOMBean> createBomBeanList) {
        this.createBomBeanList = createBomBeanList;
    }
}
