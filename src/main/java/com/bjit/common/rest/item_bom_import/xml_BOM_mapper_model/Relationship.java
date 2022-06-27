/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Sajjad
 */
public class Relationship {

    private String name;
    private String discriminator;
    private String FromType;
    private String ToType;
    private String Interfaces;
    private Attributes attributes;
    private Discriminators discriminators;

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    @XmlAttribute(name = "discriminator")
    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getFromType() {
        return FromType;
    }

    @XmlAttribute(name = "FromType")
    public void setFromType(String FromType) {
        this.FromType = FromType;
    }

    public String getToType() {
        return ToType;
    }

    @XmlAttribute(name = "ToType")
    public void setToType(String ToType) {
        this.ToType = ToType;
    }

    public String getInterfaces() {
        return Interfaces;
    }

    @XmlElement(name = "Interfaces")
    public void setInterfaces(String Interfaces) {
        this.Interfaces = Interfaces;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    @XmlElement(name = "attributes")
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Discriminators getDiscriminators() {
        return discriminators;
    }

    @XmlElement(name = "discriminators")
    public void setDiscriminators(Discriminators discriminators) {
        this.discriminators = discriminators;
    }
}
