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
 * @author Tanzir
 */
public class MultilevelPDMStructure implements BOMData {

    @Expose
    @SerializedName("source")
    private String source;

    @Expose
    @SerializedName("structure")
    private PDMItem structure;

    public MultilevelPDMStructure() {
        setSource("PDM");
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setStructure(PDMItem lnItem) {
        this.structure = lnItem;
    }

    public PDMItem getStructure() {
        return structure;
    }

}
