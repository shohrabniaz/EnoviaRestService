package com.bjit.common.rest.app.service.controller.itemhistory.model.constraint;

public enum OperationName {
    AND("&&"), EQ("=="), NE("!="), LT("<"), GT(">"), LE("<"), GE(">=");

    // declaring private variable for getting values
    private String action;

    // getter method
    public String getOperation() {
        return this.action;
    }

    private OperationName(String name) {
        this.action = action;
    }
}
