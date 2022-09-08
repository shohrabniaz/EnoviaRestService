package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Touhidul Islam
 */
public class AtonResponseModel {

    private List<String> messages = new ArrayList<>();
    private List<Model> models = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AtonResponseModel [");
        if (messages != null) {
            builder.append("messages=");
            builder.append(messages);
            builder.append(", ");
        }
        if (models != null) {
            builder.append("models=");
            builder.append(models);
        }
        builder.append("]");
        return builder.toString();
    }
}
