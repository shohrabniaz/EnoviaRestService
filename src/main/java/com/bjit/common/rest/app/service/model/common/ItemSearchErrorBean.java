package com.bjit.common.rest.app.service.model.common;

/**
 *
 * @author BJIT
 */
public class ItemSearchErrorBean {
    private String name;
    private String error;
    
    public ItemSearchErrorBean() {}
    
    public ItemSearchErrorBean(String name, String error) {
        this.name = name;
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
