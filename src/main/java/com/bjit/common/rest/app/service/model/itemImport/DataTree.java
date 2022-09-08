/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.itemImport;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class DataTree {

    private CreateObjectBean item;
    private List<Document> documents;
    private List<Substitute> substitutes;
    private String rowSequenceIdentifier;

    public DataTree() {

    }

    public DataTree(CreateObjectBean item) {
        this.item = item;
    }

    public DataTree(CreateObjectBean item, List<Document> documents, List<Substitute> substitutes, String rowSequenceIdentifier) {
        this(item);
        this.documents = documents;
        this.substitutes = substitutes;
        this.rowSequenceIdentifier = rowSequenceIdentifier;
    }

    public CreateObjectBean getItem() {
        return item;
    }

    public void setItem(CreateObjectBean item) {
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
