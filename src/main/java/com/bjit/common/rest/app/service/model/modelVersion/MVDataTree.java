/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.bjit.common.rest.app.service.model.itemImport.Document;
import com.bjit.common.rest.app.service.model.itemImport.Substitute;
import java.util.List;

/**
 * Data tree for Products and its subtypes
 * @author BJIT
 */
public class MVDataTree {
    private MVCreateObjectBean item;
    private List<Document> documents;
    private List<Substitute> substitutes;
    private String rowSequenceIdentifier;

    public MVDataTree() {

    }

    public MVDataTree(MVCreateObjectBean item) {
        this.item = item;
    }

    public MVDataTree(MVCreateObjectBean item, List<Document> documents, List<Substitute> substitutes, String rowSequenceIdentifier) {
        this(item);
        this.documents = documents;
        this.substitutes = substitutes;
        this.rowSequenceIdentifier = rowSequenceIdentifier;
    }

    public MVCreateObjectBean getItem() {
        return item;
    }

    public void setItem(MVCreateObjectBean item) {
        this.item = item;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Substitute> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(List<Substitute> substitutes) {
        this.substitutes = substitutes;
    }

    public String getRowSequenceIdentifier() {
        return rowSequenceIdentifier;
    }

    public void setRowSequenceIdentifier(String rowSequenceIdentifier) {
        this.rowSequenceIdentifier = rowSequenceIdentifier;
    }
}
