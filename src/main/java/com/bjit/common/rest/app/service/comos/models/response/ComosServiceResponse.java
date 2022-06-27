package com.bjit.common.rest.app.service.comos.models.response;

import com.google.gson.annotations.SerializedName;

public class ComosServiceResponse {
    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private String message;
    @SerializedName("Data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
