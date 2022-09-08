/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.himelli;

import matrix.db.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author BJIT
 */
public class HimelliModel {

    private Context context;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private String type;
    private String name;
    private String rev;
    private boolean isLatest = false;
    private String objectId;
    private String format;
    private String lang;
    private String primaryLang;
    private String secondaryLang;
    private String requestId;
    private String expandLevel;
    private String requester;
    private String printDrawing;
    private String attrs;
    private boolean download = true;
    private String mainProjTitle;
    private String psk;
    private String subTitle;
    private String product;
    private String printDelivery;
    private String treeView;
    private String drawingNumber;
    private String drawingType;
    private Long numberOfChildInTheStructure;
    private String receiverEmail;
    private String baseUrl;
    private String rptFileName;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPrimaryLang() {
        return primaryLang;
    }

    public void setPrimaryLang(String primaryLang) {
        this.primaryLang = primaryLang;
    }

    public String getSecondaryLang() {
        return secondaryLang;
    }

    public void setSecondaryLang(String secondaryLang) {
        this.secondaryLang = secondaryLang;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(String expandLevel) {
        this.expandLevel = expandLevel;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getPrintDrawing() {
        return printDrawing;
    }

    public void setPrintDrawing(String printDrawing) {
        this.printDrawing = printDrawing;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public String getMainProjTitle() {
        return mainProjTitle;
    }

    public void setMainProjTitle(String mainProjTitle) {
        this.mainProjTitle = mainProjTitle;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrintDelivery() {
        return printDelivery;
    }

    public void setPrintDelivery(String printDelivery) {
        this.printDelivery = printDelivery;
    }

    public String getTreeView() {
        return treeView;
    }

    public void setTreeView(String treeView) {
        this.treeView = treeView;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getDrawingType() {
        return drawingType;
    }

    public void setDrawingType(String drawingType) {
        this.drawingType = drawingType;
    }

    public Long getNumberOfChildInTheStructure() {
        return numberOfChildInTheStructure;
    }

    public void setNumberOfChildInTheStructure(Long numberOfChildInTheStructure) {
        this.numberOfChildInTheStructure = numberOfChildInTheStructure;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRptFileName() {
        return rptFileName;
    }

    public void setRptFileName(String rptFileName) {
        this.rptFileName = rptFileName;
    }
}
