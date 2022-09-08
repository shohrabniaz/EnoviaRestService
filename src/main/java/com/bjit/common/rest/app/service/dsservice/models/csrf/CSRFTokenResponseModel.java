package com.bjit.common.rest.app.service.dsservice.models.csrf;

import java.util.List;

public class CSRFTokenResponseModel {

    private String success;
    private String statusCode;
    private String error;
    private CSRFTokenModel csrf;
    private List<String> data;
    private List<String> definitions;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CSRFTokenModel getCsrf() {
        return csrf;
    }

    public void setCsrf(CSRFTokenModel csrf) {
        this.csrf = csrf;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }
}
