package com.bjit.common.rest.app.service.dsservice.stores;

import java.util.HashMap;
import java.util.Optional;

public class PropertyStore {
//    HashMap<String, HashMap<String, String>> properties;
    HashMap<String, String> properties;

//    public HashMap<String, HashMap<String, String>> getProperties() {
//        this.properties = Optional.ofNullable(this.properties).orElseGet(() -> new HashMap<>());
//        return properties;
//    }
    public HashMap<String, String> getProperties() {
        this.properties = Optional.ofNullable(this.properties).orElseGet(() -> new HashMap<>());
        return properties;
    }

//    public void setProperties(HashMap<String, HashMap<String, String>> properties) {
//        this.properties = properties;
//    }
    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }
}