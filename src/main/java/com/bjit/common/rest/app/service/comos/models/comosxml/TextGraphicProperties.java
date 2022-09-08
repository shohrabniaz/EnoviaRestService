package com.bjit.common.rest.app.service.comos.models.comosxml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class TextGraphicProperties {
    private String instanceId;
    private String displayName;

    public String getInstanceId() {
        return instanceId;
    }

    @XmlAttribute(name = "InstanceID")
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @XmlElement(name = "DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
