package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlType(propOrder = {"XMin", "YMin", "XMax", "YMax", "value"})
public class BoundingBox {
    private String xMax;
    private String xMin;
    private String yMax;
    private String yMin;
    private String value;

    public String getXMax() {
        return xMax;
    }

    @XmlAttribute(name = "XMax")
    public void setXMax(String xMax) {
        this.xMax = xMax;
    }

    public String getXMin() {
        return xMin;
    }

    @XmlAttribute(name = "XMin")
    public void setXMin(String xMin) {
        this.xMin = xMin;
    }

    public String getYMax() {
        return yMax;
    }

    @XmlAttribute(name = "YMax")
    public void setYMax(String yMax) {
        this.yMax = yMax;
    }

    public String getYMin() {
        return yMin;
    }

    @XmlAttribute(name = "YMin")
    public void setYMin(String yMin) {
        this.yMin = yMin;
    }

    public String getValue() {
        return value;
    }

    @XmlValue
    public void setValue(String value) {
        this.value = value;
    }
}