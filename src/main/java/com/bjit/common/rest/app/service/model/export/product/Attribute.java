/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */

package com.bjit.common.rest.app.service.model.export.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Suvonkar Kudndu
 */
@XmlRootElement(name = "attribute")
@XmlAccessorType (XmlAccessType.FIELD)
public class Attribute {
    
    /** UI attribute name */
    private String attributeName;
    
    /** UI attribute value or actual attribute name */
    private String attributeValue;
    
    /** is property */
    private boolean property;
    
    /** is updatable */
    private boolean updatable;
    
    /** is default selected */
    private boolean selected;

    /** is changable */
    private boolean changable;
    
    /** is not property and not attribute like classification path*/
    private boolean notPropertyAndAttribute;
    
//    RelationshipAttrIDHeader;
    /** relationship name */
    private String relationshipName;

    public Attribute() {
    }

    public Attribute(String attributeName, String attributeValue, boolean property) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.property = property;
    }
    
    public Attribute(String attributeName, String attributeValue, boolean property, boolean updatable, boolean selected, boolean changable, boolean notPropertyAndAttribute, String relationshipName) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.property = property;
        this.updatable = updatable;
        this.selected = selected;
        this.changable = changable;
        this.notPropertyAndAttribute = notPropertyAndAttribute;
        this.relationshipName = relationshipName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public boolean isProperty() {
        return property;
    }

    public void setProperty(boolean property) {
        this.property = property;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChangable() {
        return changable;
    }

    public void setChangable(boolean changable) {
        this.changable = changable;
    }

    public boolean isNotPropertyAndAttribute() {
        return notPropertyAndAttribute;
    }

    public void setNotPropertyAndAttribute(boolean notPropertyAndAttribute) {
        this.notPropertyAndAttribute = notPropertyAndAttribute;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

}
