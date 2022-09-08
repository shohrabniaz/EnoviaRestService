/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.model;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.io.Serializable;

/**
 *
 * @author BJIT
 */
public class ExpandedModel implements Serializable, Cloneable, Comparable<ExpandedModel> {

    private TNR itemTnr;
    private String itemId;
    private String relationshipName;
    private String relationshipId;

    public ExpandedModel() {
    }

    public ExpandedModel(TNR itemTnr, String itemId, String relationshipName, String relationshipId) {
        this.itemTnr = itemTnr;
        this.itemId = itemId;
        this.relationshipName = relationshipName;
        this.relationshipId = relationshipId;
    }

    public TNR getItemTnr() {
        return itemTnr;
    }

    public void setItemTnr(TNR itemTnr) {
        this.itemTnr = itemTnr;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    @Override
    public int compareTo(ExpandedModel o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
