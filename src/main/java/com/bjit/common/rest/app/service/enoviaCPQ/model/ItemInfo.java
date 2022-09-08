package com.bjit.common.rest.app.service.enoviaCPQ.model;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "iteminfo")
@XmlType(propOrder = { "id", "name", "revision", "currentState", "nextState", "message" })
public class ItemInfo {
 private String id;
    private String name;
    private String revision;
    private String currentState;
    private String nextState;
    private String message;

    @XmlElement(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "revision")
    public void setRevision(String revision) {
        this.revision = revision;
    }
    
    @XmlElement(name = "currentState")
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    
    @XmlElement(name = "nextState")
    public void setNextState(String nextState) {
        this.nextState = nextState;
    }
    
    @XmlElement(name = "message")
    public void setMessage(String message) {
        this.message = message;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRevision() {
        return revision;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getNextState() {
        return nextState;
    }

    public String getMessage() {
        return message;
    }
 
    @Override
    public String toString() {
        return "ItemInfo{" + "id=" + id + ", name=" + name + ", revision=" + revision + ", currentState=" + currentState + ", nextState=" + nextState + ", message=" + message + '}';
    }
  
}
