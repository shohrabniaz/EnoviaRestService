/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models;

import lombok.*;

import java.util.HashMap;

/**
 * @author BJIT
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ParentChildModel {
    
    private ChildData childData;
    private ChildData parentData;
    protected String relationshipId;
    protected HashMap<String, String> itemRelationMap;
    protected Integer level;
    protected Integer netQuantity;
}
