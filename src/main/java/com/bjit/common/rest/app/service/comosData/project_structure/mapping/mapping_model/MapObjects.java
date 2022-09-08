package com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class MapObjects {

    private List<MapObject> xmlMapElementObject;

    public List<MapObject> getXmlMapElementObject() {
        return xmlMapElementObject;
    }

    //@XmlElementWrapper(name="objects")
    @XmlElement(name = "object")
    public void setXmlMapElementObject(List<MapObject> xmlMapElementObject) {
        this.xmlMapElementObject = xmlMapElementObject;
    }
}
