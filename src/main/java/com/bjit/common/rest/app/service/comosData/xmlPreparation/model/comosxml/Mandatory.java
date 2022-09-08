package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlType(propOrder = {"plmExternalId", "direction", "relation", "firstPosition", "secondPosition", "boundingBox"})
public class Mandatory {
    private BoundingBox boundingBox;
    private Relation relation;
    private Attribute plmExternalId;
    private Attribute direction;
    private PositionSize firstPosition;
    private PositionSize secondPosition;

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @XmlElement(name = "BoundingBox")
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Relation getRelation() {
        return relation;
    }

    @XmlElement(name = "Relation")
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Attribute getPlmExternalId() {
        return plmExternalId;
    }

    @XmlElement(name = "PLM_ExternalID")
    public void setPlmExternalId(Attribute plmExternalId) {
        this.plmExternalId = plmExternalId;
    }

    public Attribute getDirection() {
        return direction;
    }

    @XmlElement(name = "V_Direction")
    public void setDirection(Attribute direction) {
        this.direction = direction;
    }

    public PositionSize getFirstPosition() {
        return firstPosition;
    }

    @XmlElement(name = "PositionSize")
    public void setFirstPosition(PositionSize firstPosition) {
        this.firstPosition = firstPosition;
    }

    public PositionSize getSecondPosition() {
        return secondPosition;
    }

    @XmlElement(name = "PositionSize")
    public void setSecondPosition(PositionSize secondPosition) {
        this.secondPosition = secondPosition;
    }
}
