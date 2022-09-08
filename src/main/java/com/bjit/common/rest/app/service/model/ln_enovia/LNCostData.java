package com.bjit.common.rest.app.service.model.ln_enovia;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author BJIT
 */
public class LNCostData {
    @SerializedName("Item")
    private String item;
    @SerializedName("Cost")
    private String cost;
    @SerializedName("ProductType")
    private String productType;
    @SerializedName("TIMESTAMP")
    private String timestamp;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
