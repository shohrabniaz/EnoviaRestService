/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sajjad
 */
public class BOMDataCollector {
    
    private HashMap<ParentInfo, HashMap<String,ChildInfo>> requestParentChildInfoMap ;
    private HashMap<String,ArrayList<String>> existingChildInfoRelMap ;
    private boolean BOMcontainError = false; 

    public boolean isBOMcontainError() {
        return BOMcontainError;
    }

    public void setBOMcontainError(boolean BOMcontainError) {
        this.BOMcontainError = BOMcontainError;
    }
 
    public HashMap<ParentInfo, HashMap<String,ChildInfo>> getRequestParentChildInfoMap() {
        return requestParentChildInfoMap;
    }

    public void setRequestParentChildInfoMap(HashMap<ParentInfo, HashMap<String,ChildInfo>> requestParentChildInfoMap) {
        this.requestParentChildInfoMap = requestParentChildInfoMap;
    }

    public HashMap<String, ArrayList<String>> getExistingChildInfoRelMap() {
        return existingChildInfoRelMap;
    }

    public void setExistingChildInfoRelMap(HashMap<String, ArrayList<String>> existingChildInfoRelMap) {
        this.existingChildInfoRelMap = existingChildInfoRelMap;
    }
    
    
    
}
