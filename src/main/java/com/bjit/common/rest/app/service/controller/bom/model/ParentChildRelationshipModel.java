/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.model;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ParentChildRelationshipModel {

    HashMap<String, List<ParentRelationshipModel>> parentChildRelationshipModel;

    public ParentChildRelationshipModel() {
    }

    public ParentChildRelationshipModel(HashMap<String, List<ParentRelationshipModel>> parentChildRelationshipModel) {
        this.parentChildRelationshipModel = parentChildRelationshipModel;
    }

    public HashMap<String, List<ParentRelationshipModel>> getParentChildRelationshipModel() {
        return parentChildRelationshipModel;
    }

    public void setParentChildRelationshipModel(HashMap<String, List<ParentRelationshipModel>> parentChildRelationshipModel) {
        this.parentChildRelationshipModel = parentChildRelationshipModel;
    }
}
