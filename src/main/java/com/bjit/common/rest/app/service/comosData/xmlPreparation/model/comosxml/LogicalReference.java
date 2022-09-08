package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LogicalReference {
    private List<RFLVPMItem> id;

    public List<RFLVPMItem> getId() {
        return id;
    }

    @XmlElement(name = "ID")
    public void setId(List<RFLVPMItem> id) {
        this.id = id;
    }
}
