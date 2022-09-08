package com.bjit.common.rest.app.service.comos.models.comosxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement( name = "RFLP")
@XmlType(propOrder = {"logicalReference", "logicalInstance", "logicalPort"})
public class RFLP {
    private LogicalReference logicalReference;
    private LogicalInstance logicalInstance;
    private LogicalPort logicalPort;

    public LogicalReference getLogicalReference() {
        return logicalReference;
    }

    @XmlElement(name = "LogicalReference")
    public void setLogicalReference(LogicalReference logicalReference) {
        this.logicalReference = logicalReference;
    }

    public LogicalInstance getLogicalInstance() {
        return logicalInstance;
    }

    @XmlElement(name = "LogicalInstance")
    public void setLogicalInstance(LogicalInstance logicalInstance) {
        this.logicalInstance = logicalInstance;
    }

    public LogicalPort getLogicalPort() {
        return logicalPort;
    }

    @XmlElement(name = "LogicalPort")
    public void setLogicalPort(LogicalPort logicalPort) {
        this.logicalPort = logicalPort;
    }
}
