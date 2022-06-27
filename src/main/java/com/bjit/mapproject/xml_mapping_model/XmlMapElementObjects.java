package com.bjit.mapproject.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class XmlMapElementObjects {

    private List<XmlMapElementObject> xmlMapElementObject;

    public List<XmlMapElementObject> getXmlMapElementObject() {
        return xmlMapElementObject;
    }
    public XmlMapElementObject getXmlMapElementObjectFromType(String type) {
        for (XmlMapElementObject object : xmlMapElementObject) {
            if (type.equals(object.getType())) {
                return object;
            }
        }
        return null;
    }
    //@XmlElementWrapper(name="objects")
    @XmlElement(name = "object")
    public void setXmlMapElementObject(List<XmlMapElementObject> xmlMapElementObject) {
        this.xmlMapElementObject = xmlMapElementObject;
    }
}
