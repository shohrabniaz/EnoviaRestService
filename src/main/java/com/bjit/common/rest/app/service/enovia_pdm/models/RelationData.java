package com.bjit.common.rest.app.service.enovia_pdm.models;

import java.util.HashMap;

public class RelationData {
    private String relType;
    private HashMap<String, String> attributes;

    public RelationData(){}

    public RelationData(String relType, HashMap<String, String> attributes) {
        this.relType = relType;
        this.attributes = attributes;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "RelationData{" +
                "relType='" + relType + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
