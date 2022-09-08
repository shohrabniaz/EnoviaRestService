package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import javax.xml.bind.annotation.XmlAttribute;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
