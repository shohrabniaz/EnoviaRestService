package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlType(propOrder = {"value", "type", "mandatory", "vName", "vDescription", "logDevicePosition", "revision", "vFillingRation", "vOperationPressure", "vOperatingTemperature", "vIsFlexible", "originated", "policy", "modified", "current", "reservedBy", "owner", "organization", "project", "vDiscipline", "textGraphicProperties", "secondTextGraphicProperties"})
public class RFLVPMItem {
    private String value;
    private String type;
    private Mandatory mandatory;
    private Attribute vName;
    private Attribute vDescription;
    private Attribute logDevicePosition;
    private Attribute revision;
    private Attribute vFillingRation;
    private Attribute vOperationPressure;
    private Attribute vOperatingTemperature;
    private Attribute vIsFlexible;
    private Attribute originated;
    private Attribute policy;
    private Attribute modified;
    private Attribute current;
    private Attribute reservedBy;
    private Attribute owner;
    private Attribute organization;
    private Attribute project;
    private Attribute vDiscipline;
    private TextGraphicProperties textGraphicProperties;
    private TextGraphicProperties secondTextGraphicProperties;

    public String getValue() {
        return value;
    }

    @XmlAttribute(name = "Value")
    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute(name = "Type")
    public void setType(String type) {
        this.type = type;
    }

    public Mandatory getMandatory() {
        return mandatory;
    }

    @XmlElement(name = "Mandatory")
    public void setMandatory(Mandatory mandatory) {
        this.mandatory = mandatory;
    }

    public Attribute getvName() {
        return vName;
    }

    @XmlElement(name = "V_Name")
    public void setvName(Attribute vName) {
        this.vName = vName;
    }

    public Attribute getvDescription() {
        return vDescription;
    }

    @XmlElement(name = "V_description")
    public void setvDescription(Attribute vDescription) {
        this.vDescription = vDescription;
    }

    public Attribute getLogDevicePosition() {
        return logDevicePosition;
    }

    @XmlElement(name = "LOG_Device_Position")
    public void setLogDevicePosition(Attribute logDevicePosition) {
        this.logDevicePosition = logDevicePosition;
    }

    public Attribute getRevision() {
        return revision;
    }

    @XmlElement(name = "revision")
    public void setRevision(Attribute revision) {
        this.revision = revision;
    }


    public Attribute getvFillingRation() {
        return vFillingRation;
    }

    @XmlElement(name = "V_FillingRatio")
    public void setvFillingRation(Attribute vFillingRation) {
        this.vFillingRation = vFillingRation;
    }

    public Attribute getvOperationPressure() {
        return vOperationPressure;
    }

    @XmlElement(name = "V_OperatingPressure")
    public void setvOperationPressure(Attribute vOperationPressure) {
        this.vOperationPressure = vOperationPressure;
    }

    public Attribute getvOperatingTemperature() {
        return vOperatingTemperature;
    }

    @XmlElement(name = "V_OperatingTemperature")
    public void setvOperatingTemperature(Attribute vOperatingTemperature) {
        this.vOperatingTemperature = vOperatingTemperature;
    }


    public Attribute getvIsFlexible() {
        return vIsFlexible;
    }

    @XmlElement(name = "V_IsFlexible")
    public void setvIsFlexible(Attribute vIsFlexible) {
        this.vIsFlexible = vIsFlexible;
    }

    public Attribute getOriginated() {
        return originated;
    }

    @XmlElement(name = "originated")
    public void setOriginated(Attribute originated) {
        this.originated = originated;
    }

    public Attribute getPolicy() {
        return policy;
    }

    @XmlElement(name = "policy")
    public void setPolicy(Attribute policy) {
        this.policy = policy;
    }

    public Attribute getModified() {
        return modified;
    }

    @XmlElement(name = "modified")
    public void setModified(Attribute modified) {
        this.modified = modified;
    }

    public Attribute getCurrent() {
        return current;
    }

    @XmlElement(name = "current")
    public void setCurrent(Attribute current) {
        this.current = current;
    }

    public Attribute getReservedBy() {
        return reservedBy;
    }

    @XmlElement(name = "reservedby")
    public void setReservedBy(Attribute reservedBy) {
        this.reservedBy = reservedBy;
    }

    public Attribute getOwner() {
        return owner;
    }

    @XmlElement(name = "owner")
    public void setOwner(Attribute owner) {
        this.owner = owner;
    }

    public Attribute getOrganization() {
        return organization;
    }

    @XmlElement(name = "organization")
    public void setOrganization(Attribute organization) {
        this.organization = organization;
    }

    public Attribute getProject() {
        return project;
    }

    @XmlElement(name = "project")
    public void setProject(Attribute project) {
        this.project = project;
    }

    public Attribute getvDiscipline() {
        return vDiscipline;
    }

    @XmlElement(name = "V_discipline")
    public void setvDiscipline(Attribute vDiscipline) {
        this.vDiscipline = vDiscipline;
    }

    public TextGraphicProperties getTextGraphicProperties() {
        return textGraphicProperties;
    }

    @XmlElement(name = "TextGraphicProperties")
    public void setTextGraphicProperties(TextGraphicProperties textGraphicProperties) {
        this.textGraphicProperties = textGraphicProperties;
    }

    public TextGraphicProperties getSecondTextGraphicProperties() {
        return secondTextGraphicProperties;
    }

    @XmlElement(name = "TextGraphicProperties")
    public void setSecondTextGraphicProperties(TextGraphicProperties secondTextGraphicProperties) {
        this.secondTextGraphicProperties = secondTextGraphicProperties;
    }
}
