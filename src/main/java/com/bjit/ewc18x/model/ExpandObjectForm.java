/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;
//import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Kayum-603
 */
@Component
public class ExpandObjectForm extends UiStatusMessageForm{

    private ArrayList<String> columnList = new ArrayList<String>();

   // @NotEmpty
    private String name;// = "testA003611C000015485742A3E900067630";
    //@NotEmpty
    private String type;// = "ENOCLG_KeywordReference";
    private String revision;// = "A.1";
    private String vault;

    //@NotEmpty
    private String userID;// = "jklalrahab";

//    @NotEmpty
    private String password;

   // @NotEmpty
    private String outputFileFormat;

    //private Map<Integer, String> defaultItem;
    private Map<String, String> defaultItem;

    //@NotEmpty
    private ArrayList<String> selectedItem;
    
    private List<String> defaultTypeList;
    //@NotEmpty
    private List<String> selectedTypeList;

    private String output = "";
    private String SendResult="";
    private String iContextURI;
    private String iValidityURI;
    private String port;
    private String securityContext;
    private String relationshipPattern;
    private String typePattern = "*";
    private Boolean getTo;
    private Boolean getFrom;
    //@NotNull
   // @Range(min=1, max=50)
    private Integer recursionLevel;
    private String cbpKey;
    @NotEmpty
    private String serviceName;

    private String currentUrl;

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCbpKey() {
        return cbpKey;
    }

    public void setCbpKey(String cbpKey) {
        this.cbpKey = cbpKey;
    }

    public String getSendResult() {
        return SendResult;
    }

    public void setSendResult(String SendResult) {
        this.SendResult = SendResult;
    }
    private int limit;
    private String tableName;
    private Boolean objectIdFlag;
    private Boolean depthFlag;
    private String demoexception;

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

    public String getOutputFileFormat() {
        return outputFileFormat;
    }

    public void setOutputFileFormat(String outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
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

    public Integer getRecursionLevel() {
        return recursionLevel;
    }

    public void setRecursionLevel(Integer recursionLevel) {
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return the vault
     */
    public String getVault() {
        return vault;
    }

    /**
     * @param vault the vault to set
     */
    public void setVault(String vault) {
        this.vault = vault;
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

    public Map<String, String> getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(Map<String, String> defaultItem) {
        this.defaultItem = defaultItem;
    }

    public ArrayList<String> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ArrayList<String> selectedItem) {
        this.selectedItem = selectedItem;
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

    public List<String> getDefaultTypeList() {
        return defaultTypeList;
    }

    public void setDefaultTypeList(List<String> defaultTypeList) {
        this.defaultTypeList = defaultTypeList;
    }

    public List<String> getSelectedTypeList() {
        return selectedTypeList;
    }

    public void setSelectedTypeList(List<String> selectedTypeList) {
        this.selectedTypeList = selectedTypeList;
    }

//    @Override
//    public String toString() {
//        return "ExpandObjectForm{" + "columnList=" + columnList + ", name=" + name + ", type=" + type + ", revision=" + revision + ", vault=" + vault + ", userID=" + userID + ", password=" + password + ", outputFileFormat=" + outputFileFormat + ", defaultItem=" + defaultItem + ", selectedItem=" + selectedItem + ", defaultTypeList=" + defaultTypeList + ", selectedTypeList=" + selectedTypeList + ", output=" + output + ", iContextURI=" + iContextURI + ", iValidityURI=" + iValidityURI + ", port=" + port + ", securityContext=" + securityContext + ", relationshipPattern=" + relationshipPattern + ", typePattern=" + typePattern + ", getTo=" + getTo + ", getFrom=" + getFrom + ", recursionLevel=" + recursionLevel + ", limit=" + limit + ", tableName=" + tableName + ", objectIdFlag=" + objectIdFlag + ", depthFlag=" + depthFlag + ", demoexception=" + demoexception + '}';
//    }

    @Override
    public String toString() {
        return "ExpandObjectForm{" + "columnList=" + columnList + ", name=" + name + ", type=" + type + ", revision=" + revision + ", vault=" + vault + ", userID=" + userID + ", password=" + password + ", outputFileFormat=" + outputFileFormat + ", defaultItem=" + defaultItem + ", selectedItem=" + selectedItem + ", defaultTypeList=" + defaultTypeList + ", selectedTypeList=" + selectedTypeList + ", output=" + output + ", SendResult=" + SendResult + ", iContextURI=" + iContextURI + ", iValidityURI=" + iValidityURI + ", port=" + port + ", securityContext=" + securityContext + ", relationshipPattern=" + relationshipPattern + ", typePattern=" + typePattern + ", getTo=" + getTo + ", getFrom=" + getFrom + ", recursionLevel=" + recursionLevel + ", limit=" + limit + ", tableName=" + tableName + ", objectIdFlag=" + objectIdFlag + ", depthFlag=" + depthFlag + ", demoexception=" + demoexception + '}';
    }

//    public Map<Integer, String> tableColumnFields() throws FileNotFoundException {
//        Scanner input = new Scanner(System.in);
//        File file = new File(getClass().getResource("/ExpandObjectTableFieldName.txt").getFile());
//        input = new Scanner(file);
//        ArrayList<String> arrayList = new ArrayList<String>();
//        while (input.hasNextLine()) {
//            String line = input.nextLine();
//            arrayList.add(line);
//        }
//        input.close();
//        columnList = arrayList;
//        //for( int i=0; i<columnList.size(); i++ ) System.out.println("Coloumn Fields are "+columnList.get(i));
//        Map<Integer, String> tableColumnFieldList;
//        tableColumnFieldList = new LinkedHashMap<Integer, String>();
//        for (int i = 0; i < columnList.size(); i++) {
//            tableColumnFieldList.put(i, columnList.get(i));
//        }
//
//        return tableColumnFieldList;
//    }
//
//    public ExpandObjectForm() throws FileNotFoundException {
//        super();
//        //setDefaultItem(AttributeList.getInstance().getAttributeName());
//    }

}
