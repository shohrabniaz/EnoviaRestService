package com.bjit.common.rest.app.service.comos.models.comosxml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

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
