package com.bjit.mapproject.xml_mapping_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"source", "destination", "type", "description","dataFormate"})
public class XmlMapElementProperty {
    private String source;
    private String destination;
    private String description;
    private String type;
    private String dataFormate;

    public String getDataFormate() {
        return dataFormate;
    }
    @XmlElement(name = "format")
    public void setDataFormate(String dataFormate) {
        this.dataFormate = dataFormate;
    }
    public String getSource() {
        return source;
    }

    @XmlElement(name = "source_name")
    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    @XmlElement(name = "destination_name")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Property{" + "source=" + source + ", destination=" + destination + ", description=" + description + ", type=" + type + '}';
    }
}
