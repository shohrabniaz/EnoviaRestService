package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse;

import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenModel;
import java.util.List;

public class DocumentResponseModel {

    private String success;
    private String error;
    private String statusCode;
    private CSRFTokenModel csrf;
    private List<DocumentDataModel> data;
    private List<Object> definitions;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public CSRFTokenModel getCsrf() {
        return csrf;
    }

    public void setCsrf(CSRFTokenModel csrf) {
        this.csrf = csrf;
    }

    public List<DocumentDataModel> getData() {
        return data;
    }

    public void setData(List<DocumentDataModel> data) {
        this.data = data;
    }

    public List<Object> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Object> definitions) {
        this.definitions = definitions;
    }
}
