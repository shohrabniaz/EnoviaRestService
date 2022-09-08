package com.bjit.common.rest.app.service.comos.models.comosxml;

import javax.xml.bind.annotation.XmlAttribute;

public class Relation {
    private String ownerReference;
    private String reference;

    public String getOwnerReference() {
        return ownerReference;
    }

    @XmlAttribute(name = "OwnerReference")
    public void setOwnerReference(String ownerReference) {
        this.ownerReference = ownerReference;
    }

    public String getReference() {
        return reference;
    }

    @XmlAttribute(name = "Reference")
    public void setReference(String reference) {
        this.reference = reference;
    }
}
