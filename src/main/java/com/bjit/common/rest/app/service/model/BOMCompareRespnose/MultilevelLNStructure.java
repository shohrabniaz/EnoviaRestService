/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.BOMCompareRespnose;

import com.bjit.compareBOM.MultiLevelBomDataModel.BomLineBean;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Tahmid
 */
public class MultilevelLNStructure implements BOMData {

    @Expose
    @SerializedName("source")
    private String source;

    @Expose
    @SerializedName("structure")
    private BomLineBean structure;

    public MultilevelLNStructure() {
        setSource("LN");
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setStructure(BomLineBean lnItem) {
        this.structure = lnItem;
    }

    public BomLineBean getStructure() {
        return structure;
    }

}
