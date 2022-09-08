/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.model;

/**
 *
 * @author BJIT
 */
public class ParentRelationshipModel {

    public String parentType;
    public String relationName;

    public ParentRelationshipModel() {
    }

    public ParentRelationshipModel(String parentType, String relationName) {
        this.parentType = parentType;
        this.relationName = relationName;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
}
