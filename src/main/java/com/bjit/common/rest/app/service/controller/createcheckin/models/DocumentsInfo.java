/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin.models;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class DocumentsInfo {
    private CreateObjectBean document;
    private List<String> fileName;

    public CreateObjectBean getDocument() {
        return document;
    }

    public void setDocument(CreateObjectBean document) {
        this.document = document;
    }
    
    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }
}
