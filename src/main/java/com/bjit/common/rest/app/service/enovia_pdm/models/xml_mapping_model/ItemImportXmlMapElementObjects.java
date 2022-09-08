package com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class ItemImportXmlMapElementObjects {

    private List<ItemImportXmlMapElementObject> xmlMapElementObject;
    private Boolean expandUp;
    private Boolean expandDown;
    private Integer expandLevel;
    private Integer dataFetchLimit;
    private String expandRelWhereClause;
    private String expandBusWhereClause;

    public List<ItemImportXmlMapElementObject> getXmlMapElementObject() {
        return xmlMapElementObject;
    }

    @XmlElement(name = "object")
    public void setXmlMapElementObject(List<ItemImportXmlMapElementObject> xmlMapElementObject) {
        this.xmlMapElementObject = xmlMapElementObject;
    }

    public Boolean getExpandUp() {
        return expandUp;
    }

    @XmlElement(name = "expand-up")
    public void setExpandUp(Boolean expandUp) {
        this.expandUp = expandUp;
    }

    public Boolean getExpandDown() {
        return expandDown;
    }

    @XmlElement(name = "expand-down")
    public void setExpandDown(Boolean expandDown) {
        this.expandDown = expandDown;
    }

    public Integer getExpandLevel() {
        return expandLevel;
    }

    @XmlElement(name = "expand-level")
    public void setExpandLevel(Integer expandLevel) {
        this.expandLevel = expandLevel;
    }

    public Integer getDataFetchLimit() {
        return dataFetchLimit;
    }

    @XmlElement(name = "data-fetch-limit")
    public void setDataFetchLimit(Integer dataFetchLimit) {
        this.dataFetchLimit = dataFetchLimit;
    }

    public String getExpandRelWhereClause() {
        return expandRelWhereClause;
    }

    @XmlElement(name = "expand-rel-where")
    public void setExpandRelWhereClause(String expandRelWhereClause) {
        this.expandRelWhereClause = expandRelWhereClause;
    }

    public String getExpandBusWhereClause() {
        return expandBusWhereClause;
    }

    @XmlElement(name = "expand-bus-where")
    public void setExpandBusWhereClause(String expandBusWhereClause) {
        this.expandBusWhereClause = expandBusWhereClause;
    }
}
