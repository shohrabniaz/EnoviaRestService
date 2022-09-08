/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models.xml;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author BJIT
 */
@XmlRootElement
public class Item implements Serializable {
    private String type;
    private String name;
    private String revision;
    private String id;
    private String event;
    private String currentState;
    private String nextState;
    private String username;

    public Item() {
    }

    public Item(String type, String name, String revision, String id, String event, String currentState, String nextState, String username) {
        this.type = type;
        this.name = name;
        this.revision = revision;
        this.id = id;
        this.event = event;
        this.currentState = currentState;
        this.nextState = nextState;
        this.username = username;
    }

    public String getType() {
        return type;
    }

    @XmlElement(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    @XmlElement(name = "revision")
    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getId() {
        return id;
    }

    @XmlElement(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    @XmlElement(name = "event")
    public void setEvent(String event) {
        this.event = event;
    }
    
    public String getCurrentState() {
        return currentState;
    }

    @XmlElement(name = "currentState")
    public void setCurrentState(String currentState) {
        if(currentState==null)this.currentState="";
        else this.currentState = currentState;
    }

    public String getNextState() {
        return nextState;
    }

    @XmlElement(name = "nextState")
    public void setNextState(String nextState) {
        if(nextState==null)this.nextState="";
        else this.nextState = nextState;
    }
    
    @XmlElement(name = "username")
    public void setusername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Item{" + "type=" + type + ", name=" + name + ", revision=" + revision + ", id=" + id + ", event=" + event + ", currentState=" + currentState + ", nextState=" + nextState + ", username=" + username + '}';
    }
    
    
}
