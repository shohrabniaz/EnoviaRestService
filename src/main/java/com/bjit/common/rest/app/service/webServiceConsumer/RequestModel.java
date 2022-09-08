/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.webServiceConsumer;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.io.Serializable;
import java.util.HashMap;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public final class RequestModel implements Serializable {

    private String serviceMethodType;
    private String contentType;
    private String accepts;
    private HashMap<String, String> requestHeaders;
    private String bodyData;
    private Boolean disableSSL;
    private Boolean cacheControl;

    public RequestModel() {
        serviceMethodType = null;
        contentType = null;
        requestHeaders = null;
        bodyData = null;
        disableSSL = null;
        cacheControl = null;
    }

    public RequestModel(String serviceMethodType, String contentType, String accepts, HashMap<String, String> requestHeaders, String bodyData, Boolean disableSSL, Boolean cacheControl) {
        this.serviceMethodType = serviceMethodType;
        this.contentType = contentType;
        this.accepts = accepts;
        this.requestHeaders = requestHeaders;
        this.bodyData = bodyData;
        this.disableSSL = disableSSL;
        this.cacheControl = cacheControl;
    }

    public String getServiceMethodType() {
        return serviceMethodType;
    }

    public void setServiceMethodType(String serviceMethodType) {
        this.serviceMethodType = serviceMethodType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAccepts() {
        return accepts;
    }

    public void setAccepts(String accepts) {
        this.accepts = accepts;
    }

    public HashMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HashMap<String, String> requestHeaders) {
        if (NullOrEmptyChecker.isNullOrEmpty(this.requestHeaders)) {
            this.requestHeaders = requestHeaders;
        } else {
            this.requestHeaders.putAll(requestHeaders);
        }
    }

    public void setRequestHeaders(String headerName, String headerValue) {
        this.requestHeaders = NullOrEmptyChecker.isNullOrEmpty(this.requestHeaders) ? new HashMap<>() : this.requestHeaders;
        this.requestHeaders.put(headerName, headerValue);
    }

    public String getBodyData() {
        return bodyData;
    }

    public void setBodyData(String bodyData) {
        this.bodyData = bodyData;
    }

    public Boolean getDisableSSL() {
        disableSSL = false;
        return disableSSL;
    }

    public void setDisableSSL(Boolean disableSSL) {
        this.disableSSL = disableSSL;
    }

    public Boolean getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(Boolean cacheControl) {
        this.cacheControl = cacheControl;
    }

    @Override
    public String toString() {
        return "ServiceRequesterModel{" + "serviceMethodType=" + serviceMethodType + ", contentType=" + contentType + ", accepts=" + accepts + ", requestHeaders=" + requestHeaders + ", bodyData=" + bodyData + ", disableSSL=" + disableSSL + ", cacheControl=" + cacheControl + '}';
    }
}
