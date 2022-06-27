package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import java.util.List;

public class ModelProduct {

    private ModelInfo modelInfo;
    private List<Product> products = null;

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
