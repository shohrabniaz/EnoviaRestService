package com.bjit.common.rest.app.service.background.processors;

public class BackgroundResponse<T> {
    private String response;
    private T object;

    public BackgroundResponse() {
    }

    public BackgroundResponse(String response, T object) {
        this.response = response;
        this.object = object;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
