package com.bjit.common.rest.app.service.controller.itemhistory.model;

import java.util.ArrayList;
import java.util.List;

public class ItemSearchRequestModel {
    private List<Data> data = new ArrayList<>();
    private Constraint constraint = new Constraint();
    private String source;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
