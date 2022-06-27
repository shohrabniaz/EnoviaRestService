/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.BOMCompareRespnose;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Tahmid
 */
public class MultilevelEnoviaStructure implements BOMData{

    @Expose
    @SerializedName("source")
    private String source;

    
    @Expose
    @SerializedName("structure")
    private EnoviaItem structure;

    public MultilevelEnoviaStructure() {
        setSource("ENOVIA");
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSource() {
        return source;
    }

    public EnoviaItem getStructure() {
        return structure;
    }

    public void setStructure(EnoviaItem enoviaItem) {
        this.structure = enoviaItem;
    }

}
