package com.bjit.common.rest.app.service.dsservice.models.fcsjob;

import java.util.List;

public class DataModel {
    private DataElementsModel dataelements;
    private List<String> children;

    public DataElementsModel getDataelements() {
        return dataelements;
    }

    public void setDataelements(DataElementsModel dataelements) {
        this.dataelements = dataelements;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }
}
