package com.bjit.common.rest.app.service.comos.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class Child {
    @SerializedName("Id")
    private String id;
    @SerializedName("Type")
    private String type;
    @SerializedName("Code")
    private String code;
    @SerializedName("Level")
    private String level;
    @SerializedName("HasChild")
    private Boolean hasChild;
    @SerializedName("Description")
    private String description;
    @SerializedName("Attributes")
    private HashMap<String, String> attributes;
    @SerializedName("Childs")
    private List<Child> childs;
    private Long sequence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Child> getChilds() {
        return childs;
    }

    public void setChilds(List<Child> childs) {
        this.childs = childs;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}
