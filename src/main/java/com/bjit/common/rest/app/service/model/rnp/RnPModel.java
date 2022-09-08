/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.rnp;

import java.io.File;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class RnPModel {

    private Context context;
    private String responseBomData;
    private HashMap rootItemParams;
    private HashMap deliveryParams;
    private String format;
    private String lang;
    private String primaryLang;
    private String secondaryLang;
    private String requestId;
    private String isSummaryRequired;
    private String type;
    private String name;
    private String rev;
    private String objectId;
    private boolean download;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private String expandLevel;
    private String isDrawingInfoRequired;
    private String attributeListString;
    private String printDelivery;
    private String mainProjTitle;
    private String psk;
    private String subTitle;
    private String product;
    private String baseUrl;
    private String receiverEmail;
    private Long numberOfChildInTheStructure;
    private File downloadableFile;
    private Boolean isFileGenerated;
    private Boolean isMBOMReport;
    private String docType;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getResponseBomData() {
        return responseBomData;
    }

    public void setResponseBomData(String responseBomData) {
        this.responseBomData = responseBomData;
    }

    public HashMap getRootItemParams() {
        return rootItemParams;
    }

    public void setRootItemParams(HashMap rootItemParams) {
        this.rootItemParams = rootItemParams;
    }

    public HashMap getDeliveryParams() {
        return deliveryParams;
    }

    public void setDeliveryParams(HashMap deliveryParams) {
        this.deliveryParams = deliveryParams;
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

    public String getIsSummaryRequired() {
        return isSummaryRequired;
    }

    public void setIsSummaryRequired(String isSummaryRequired) {
        this.isSummaryRequired = isSummaryRequired;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
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

    public String getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(String expandLevel) {
        this.expandLevel = expandLevel;
    }

    public String getIsDrawingInfoRequired() {
        return isDrawingInfoRequired;
    }

    public void setIsDrawingInfoRequired(String isDrawingInfoRequired) {
        this.isDrawingInfoRequired = isDrawingInfoRequired;
    }

    public String getAttributeListString() {
        return attributeListString;
    }

    public void setAttributeListString(String attributeListString) {
        this.attributeListString = attributeListString;
    }

    public String getPrintDelivery() {
        return printDelivery;
    }

    public void setPrintDelivery(String printDelivery) {
        this.printDelivery = printDelivery;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Long getNumberOfChildInTheStructure() {
        return numberOfChildInTheStructure;
    }

    public void setNumberOfChildInTheStructure(Long numberOfChildInTheStructure) {
        this.numberOfChildInTheStructure = numberOfChildInTheStructure;
    }
    
    public File getDownloadableFile() {
        return downloadableFile;
    }

    public void setDownloadableFile(File downloadableFile) {
        this.downloadableFile = downloadableFile;
    }

    public Boolean getIsFileGenerated() {
        return isFileGenerated;
    }

    public void setIsFileGenerated(Boolean isFileGenerated) {
        this.isFileGenerated = isFileGenerated;
    }

    public Boolean getIsMBOMReport() {
        return isMBOMReport;
    }

    public void setIsMBOMReport(Boolean isMBOMReport) {
        this.isMBOMReport = isMBOMReport;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
    
}
