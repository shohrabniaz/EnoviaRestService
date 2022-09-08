package com.bjit.common.rest.app.service.controller.itemhistory.model.constraint;

public class Condition {
    private String key;
    private String value;
    private OperationName operation;
    private OperationName linkBy = OperationName.AND;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OperationName getOperation() {
        return operation;
    }

    public void setOperation(OperationName operation) {
        this.operation = operation;
    }

    public OperationName getLinkBy() {
        return linkBy;
    }

    public void setLinkBy(OperationName linkBy) {
        this.linkBy = linkBy;
    }
}
