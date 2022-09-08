package com.bjit.common.rest.app.service.controller.export.himelli;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"source", "dest"})
@XmlRootElement(name = "attribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute {

    private String source;
    private String dest;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "Attribute [source=" + source + ", dest=" + dest + "]";
    }
}
