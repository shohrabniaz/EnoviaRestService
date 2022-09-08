/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.ObjectAttribute;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Tahmid
 */
@XmlRootElement(name = "object")
public class ObjectAttributeBean {

    private String type;
    private String name;
    private String rev;
    // Initialized to enable proper response even after removal of these 2 nodes in the request body.
    @XmlElement(name = "property")
    private List<String> properties = new ArrayList<>();
    @XmlElement(name = "attribute")
    private List<String> attributes = new ArrayList<>();
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public List<String> getAttributes() {
        ArrayList<String> dummyAttrList = new ArrayList<>();
        attributes.forEach(attribute -> {
            dummyAttrList.add("attribute[" + attribute + "]");
        });
        attributes.clear();
        attributes.addAll(dummyAttrList);
        return attributes;
    }
    
    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }
}
