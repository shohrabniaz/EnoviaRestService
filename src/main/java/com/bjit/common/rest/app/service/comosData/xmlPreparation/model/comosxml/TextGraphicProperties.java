package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
