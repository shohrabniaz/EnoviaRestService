/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.bom.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ashikur Rahman
 */
public class ObjectTree {
   private String objectInfoAsString;

    public ObjectTree(String objectInfoAsString) {
        this.objectInfoAsString = objectInfoAsString;
    }
    
    public String getObjectInfoAsString() {
        return objectInfoAsString;
    }

    public void setObjectInfoAsString(String objectInfoAsString) {
        this.objectInfoAsString = objectInfoAsString;
    }
   
   
}
