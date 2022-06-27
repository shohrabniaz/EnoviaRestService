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
@XmlRootElement(name = "data")
public class ObjectAttributeValueBean extends MapWrapper {
    
    private List<Version> version;

    public List<Version> getVersion() {
        if (version == null)
            version = new ArrayList<>();
        return version;
    }

    @XmlElement
    public void setVersion(List<Version> version) {
        this.version = version;
    }
    
    

}
