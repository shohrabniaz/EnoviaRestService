package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import java.util.List;

public class AtonProductsItemCodeResponse {

    private List<String> messages = null;
    private List<ModelProduct> modelProducts = null;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<ModelProduct> getModelProducts() {
        return modelProducts;
    }

    public void setModelProducts(List<ModelProduct> modelProducts) {
        this.modelProducts = modelProducts;
    }

}
