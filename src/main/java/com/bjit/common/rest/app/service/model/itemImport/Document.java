/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.itemImport;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class Document {
    private CreateObjectBean documentItem;
    private String rel;
    private List<HashMap<String, String>> files;

    public CreateObjectBean getDocumentItem() {
        return documentItem;
    }

    public void setDocumentItem(CreateObjectBean documentItem) {
        this.documentItem = documentItem;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public List<HashMap<String, String>> getFiles() {
        return files;
    }

    public void setFiles(List<HashMap<String, String>> files) {
        this.files = files;
    }
}
