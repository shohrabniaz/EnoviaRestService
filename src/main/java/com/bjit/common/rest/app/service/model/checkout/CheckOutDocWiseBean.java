/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.checkout;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class CheckOutDocWiseBean {

    private String itemId;
    //private HashMap<String, List<String>> filesInDoc;
    
    
    private List<HashMap<String,List<String>>> documents;
    
    
    private String errorMessage;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /*public HashMap<String, List<String>> getFilesInDoc() {
        return filesInDoc;
    }

    public void setFilesInDoc(HashMap<String, List<String>> filesInDoc) {
        this.filesInDoc = filesInDoc;
    }*/
    
    public List<HashMap<String, List<String>>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<HashMap<String, List<String>>> documents) {
        this.documents = documents;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
