package com.bjit.common.rest.app.service.comos.models.comosxml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"instanceId", "xAxis", "yAxis", "size"})
public class PositionSize {
    private String instanceId;
    private String xAxis;
    private String yAxis;
    private String size;

    public String getInstanceId() {
        return instanceId;
    }

    @XmlAttribute(name = "InstanceID")
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getxAxis() {
        return xAxis;
    }

    @XmlAttribute(name = "X")
    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    @XmlAttribute(name = "Y")
    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }


    public String getSize() {
        return size;
    }

    @XmlAttribute(name = "Size")
    public void setSize(String size) {
        this.size = size;
    }
}