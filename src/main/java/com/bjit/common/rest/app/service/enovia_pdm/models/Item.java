package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;

public class Item extends TNR {
    private String owner;
    private HashMap<String, String> attributes;

    public Item() {
    }

    public Item(TNR tnr) {
        super(tnr.getType(), tnr.getName(), tnr.getRevision());
    }

    public Item(TNR tnr, String owner) {
        this(tnr);
        this.owner = owner;
    }

    public Item(TNR tnr, String owner, HashMap<String, String> attributes) {
        this(tnr, owner);
        this.attributes = attributes;
    }

    public Item(String owner, HashMap<String, String> attributes) {
        this.owner = owner;
        this.attributes = attributes;
    }

    public Item(String type, String name, String revision, String owner, HashMap<String, String> attributes) {
        super(type, name, revision);
        this.owner = owner;
        this.attributes = attributes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }


    @Override
    public String toString() {
        return "Item{" +
                "tnr=" + super.toString() +
                "owner='" + owner + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
