package com.bjit.common.rest.app.service.comos.models.comosxml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class LogicalPort {
    private List<RFLVPMItem> id;

    public List<RFLVPMItem> getId() {
        return id;
    }

    @XmlElement(name = "ID")
    public void setId(List<RFLVPMItem> id) {
        this.id = id;
    }
}
