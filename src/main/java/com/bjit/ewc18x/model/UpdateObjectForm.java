/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
//import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Kayum-603
 */
public class UpdateObjectForm extends UiStatusMessageForm{

//    @NotEmpty
    private String userID;
//
   // @NotEmpty
    private String password;

//    @NotEmpty
    private MultipartFile file;

    private Map<Integer, String> defaultItem;

    private String output = "";
    private String iContextURI;
    private String iValidityURI;
    private String port;
    private String securityContext;
    private String relationshipPattern;
    private String typePattern;
    private Boolean getTo;
    private Boolean getFrom;
    private int recursionLevel;
    private int limit;
    private String tableName;
    private Boolean objectIdFlag;
    private Boolean depthFlag;
    private String demoexception = "";
    private String successMessage = "";
    private String cbpKey;

    public String getCbpKey() {
        return cbpKey;
    }

    public void setCbpKey(String cbpKey) {
        this.cbpKey = cbpKey;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }  

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getIContextURI() {
        return iContextURI;
    }

    public void setIContextURI(String iContextURI) {
        this.iContextURI = iContextURI;
    }

    public String getIValidityURI() {
        return iValidityURI;
    }

    public void setIValidityURI(String iValidityURI) {
        this.iValidityURI = iValidityURI;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(String securityContext) {
        this.securityContext = securityContext;
    }

    public String getRelationshipPattern() {
        return relationshipPattern;
    }

    public void setRelationshipPattern(String relationshipPattern) {
        this.relationshipPattern = relationshipPattern;
    }

    public String getTypePattern() {
        return typePattern;
    }

    public void setTypePattern(String typePattern) {
        this.typePattern = typePattern;
    }

    public Boolean getGetTo() {
        return getTo;
    }

    public void setGetTo(Boolean getTo) {
        this.getTo = getTo;
    }

    public Boolean getGetFrom() {
        return getFrom;
    }

    public void setGetFrom(Boolean getFrom) {
        this.getFrom = getFrom;
    }

    public int getRecursionLevel() {
        return recursionLevel;
    }

    public void setRecursionLevel(int recursionLevel) {
        this.recursionLevel = recursionLevel;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Boolean getObjectIdFlag() {
        return objectIdFlag;
    }

    public void setObjectIdFlag(Boolean objectIdFlag) {
        this.objectIdFlag = objectIdFlag;
    }

    public Boolean getDepthFlag() {
        return depthFlag;
    }

    public void setDepthFlag(Boolean depthFlag) {
        this.depthFlag = depthFlag;
    }

    /**
     * @return the demoexception
     */
    public String getDemoexception() {
        return demoexception;
    }

    /**
     * @param demoexception the demoexception to set
     */
    public void setDemoexception(String demoexception) {
        this.demoexception = demoexception;
    }

    public Map<Integer, String> getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Map<Integer, String> defaultItem) {
        this.defaultItem = defaultItem;
    }

    public String getiContextURI() {
        return iContextURI;
    }

    public void setiContextURI(String iContextURI) {
        this.iContextURI = iContextURI;
    }

    public String getiValidityURI() {
        return iValidityURI;
    }

    public void setiValidityURI(String iValidityURI) {
        this.iValidityURI = iValidityURI;
    }

//	@Override
//	public String toString() {
//		return "UpdateObjectForm{" + "userID=" + userID + ", password=" + password + ", file=" + file + ", defaultItem=" + defaultItem + ", output=" + output + ", iContextURI=" + iContextURI + ", iValidityURI=" + iValidityURI + ", port=" + port + ", securityContext=" + securityContext + ", relationshipPattern=" + relationshipPattern + ", typePattern=" + typePattern + ", getTo=" + getTo + ", getFrom=" + getFrom + ", recursionLevel=" + recursionLevel + ", limit=" + limit + ", tableName=" + tableName + ", objectIdFlag=" + objectIdFlag + ", depthFlag=" + depthFlag + ", demoexception=" + demoexception + '}';
//	}

    @Override
    public String toString() {
        return "UpdateObjectForm{" + "userID=" + userID + ", password=" + password + ", file=" + file + ", defaultItem=" + defaultItem + ", output=" + output + ", iContextURI=" + iContextURI + ", iValidityURI=" + iValidityURI + ", port=" + port + ", securityContext=" + securityContext + ", relationshipPattern=" + relationshipPattern + ", typePattern=" + typePattern + ", getTo=" + getTo + ", getFrom=" + getFrom + ", recursionLevel=" + recursionLevel + ", limit=" + limit + ", tableName=" + tableName + ", objectIdFlag=" + objectIdFlag + ", depthFlag=" + depthFlag + ", demoexception=" + demoexception + ", successMessage=" + successMessage + '}';
    }

}
