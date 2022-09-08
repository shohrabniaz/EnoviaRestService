package com.bjit.common.rest.app.service.model.common;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author BJIT
 */
public class ItemSearchResponseBean {

    private String name;
    private String found;
    private Map<String, Object> message;

    public ItemSearchResponseBean() {
    }

    public ItemSearchResponseBean(String name, String found) {
        this.name = name;
        this.found = found;
        this.message = new HashMap<>();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String, Object> message) {
        this.message = message;
    }

    public void addMessage(String messageKey, String messageValue) {
        if (NullOrEmptyChecker.isNull(message)) {
            message = new HashMap<>();
        }
        message.put(messageKey, messageValue);
    }
}
