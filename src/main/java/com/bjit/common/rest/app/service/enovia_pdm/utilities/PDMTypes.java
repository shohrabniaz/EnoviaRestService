package com.bjit.common.rest.app.service.enovia_pdm.utilities;

public enum PDMTypes {
    ProcessContinuousCreateMaterial("Own design item"),
    CreateAssembly("Own design item"),
    VAL_VALComponent("commercial items"),
    VAL_VALComponentMaterial("commercial items");

    private final String value;

    PDMTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
