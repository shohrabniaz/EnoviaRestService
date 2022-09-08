package com.bjit.mapproject.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class XmlMapElementProperties {
    private List<XmlMapElementProperty> properties;

    
    public List<XmlMapElementProperty> getProperties() {
        return properties;
    }
    @XmlElement(name = "property")
    public void setProperties(List<XmlMapElementProperty> properties) {
        this.properties = properties;
        System.out.println("SETTING NO ERROR:"+properties.toString());
    }

    public XmlMapElementProperty getProperty(String source) {
        for (XmlMapElementProperty property : properties) {
            if (source.equals(property.getSource())) {
                return property;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Properties{" + "properties=" + properties + '}';
    }
}
