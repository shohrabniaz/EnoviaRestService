/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVCreateUpdateMemberResponse {
    @JsonProperty(value = "modelID")
    private String modelID; 
    
    @JsonProperty(value = "id")
    private String id;
    
    @JsonProperty(value = "name")
    private String name;
    
    @JsonProperty(value = "title")
    private String title;
    
    @JsonProperty(value = "type")
    private String type;
    
    @JsonProperty(value = "description")
    private String description;
    
    @JsonProperty(value = "revision")
    private String revision;
    
    @JsonProperty(value = "model")
    private MVCreateUpdateModelResponse model;
    
    @JsonProperty(value = "modelName")
    private String modelName; 
    
    @JsonProperty(value = "modified")
    private String modified; 
    
    @JsonProperty(value = "created")
    private String created; 
    
    @JsonProperty(value = "state")
    private String state; 
    
    @JsonProperty(value = "owner")
    private String owner; 
    @JsonProperty(value = "organization")
    private String organization; 
    @JsonProperty(value = "collabspace")
    private String collabspace; 
    @JsonProperty(value = "basePrice")
    private String basePrice; 
    @JsonProperty(value = "source")
    private String source; 
    @JsonProperty(value = "identifier")
    private String identifier; 
    @JsonProperty(value = "relativePath")
    private String relativePath; 
    @JsonProperty(value = "isRoot")
    private String isRoot; 
    @JsonProperty(value = "isLeaf")
    private String isLeaf; 
    
    

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public MVCreateUpdateModelResponse getModel() {
        return model;
    }

    public void setModel(MVCreateUpdateModelResponse model) {
        this.model = model;
    }
    
    
}
