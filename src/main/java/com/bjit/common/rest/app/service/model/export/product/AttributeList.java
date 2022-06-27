/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */

package com.bjit.common.rest.app.service.model.export.product;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Suvonkar Kudnu
 */
@XmlRootElement(name = "attributes")
@XmlAccessorType (XmlAccessType.FIELD)
public class AttributeList {
    
    @XmlElement(name = "attribute")
    private List<Attribute> list;

    public AttributeList(){
        list = new ArrayList<Attribute>();
    }

    public void add(Attribute p){
        list.add(p);
    }

    public List<Attribute> getList() {
        return list;
    }
}
