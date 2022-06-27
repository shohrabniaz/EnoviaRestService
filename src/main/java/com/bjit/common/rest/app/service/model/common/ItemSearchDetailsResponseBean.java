package com.bjit.common.rest.app.service.model.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author BJIT
 */
public class ItemSearchDetailsResponseBean {

    private String name;
    private String searchStr;
    private String found;
    private Map<String, Object> message;
    private List<HashMap<String, String>> detailResult;

    public ItemSearchDetailsResponseBean() {
    }

    public ItemSearchDetailsResponseBean(String name, String found, List<HashMap<String, String>> detailResult) {
        this.name = name;
        this.found = found;
        this.message = new HashMap<>();
        this.detailResult = detailResult;
    }

    public ItemSearchDetailsResponseBean(String searchStr, String found) {
        this.searchStr = searchStr;
        this.found = found;
    }

    public ItemSearchDetailsResponseBean(String searchStr, List<HashMap<String, String>> detailResult) {
        this.searchStr = searchStr;
        this.detailResult = detailResult;
    }

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

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public List<HashMap<String, String>> getDetailResult() {
        return detailResult;
    }

    public void setDetailResult(List<HashMap<String, String>> detailResult) {
        this.detailResult = detailResult;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String, Object> message) {
        this.message = message;
    }
}
