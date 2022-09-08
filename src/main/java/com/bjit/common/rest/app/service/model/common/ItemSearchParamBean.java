package com.bjit.common.rest.app.service.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class ItemSearchParamBean {

    private String name;
    @JsonProperty("searchStr")
    private String searchStr;
    @JsonProperty("id_sequence")
    private String idSequence;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdSequence() {
        return idSequence;
    }

    public void setIdSequence(String idSequence) {
        this.idSequence = idSequence;
    }
    private HashMap<String, String> property;
    private HashMap<String, String> attribute;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public HashMap<String, String> getProperty() {
        return property;
    }

    public void setProperty(HashMap<String, String> property) {
        this.property = property;
    }

    public HashMap<String, String> getAttribute() {
        return attribute;
    }

    public void setAttribute(HashMap<String, String> attribute) {
        this.attribute = attribute;
    }
}
