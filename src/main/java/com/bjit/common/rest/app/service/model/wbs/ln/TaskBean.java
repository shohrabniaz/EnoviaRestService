package com.bjit.common.rest.app.service.model.wbs.ln;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author BJIT
 */
public class TaskBean {

    @SerializedName("Project")
    @JsonProperty("Project")
    private String project;

    @SerializedName("Activity")
    @JsonProperty("Activity")
    private String activity;

    @SerializedName("ActivityDescription")
    @JsonProperty("ActivityDescription")
    private String activityDescription;

    @SerializedName("ActivityStatus")
    @JsonProperty("ActivityStatus")
    private String activityStatus;

    @SerializedName("ActivityType")
    @JsonProperty("ActivityType")
    private String activityType;

    @SerializedName("ParentActivity")
    @JsonProperty("ParentActivity")
    private String parentActivity;

    @SerializedName("AcutalEndDate")
    @JsonProperty("AcutalEndDate")
    private String acutalEndDate;

    @SerializedName("NetQuantity")
    @JsonProperty("NetQuantity")
    private String netQuantity;

    @SerializedName("Unit")
    @JsonProperty("Unit")
    private String unit;

    @SerializedName("ProductType")
    @JsonProperty("ProductType")
    private String productType;

    @SerializedName("ProductTypeDesc")
    @JsonProperty("ProductTypeDesc")
    private String productTypeDesc;

    @SerializedName("Pos")
    @JsonProperty("Pos")
    private String pos;

    @SerializedName("Selectable")
    @JsonProperty("Selectable")
    private String selectable;

    @SerializedName("ContractDeliverableNo")
    @JsonProperty("ContractDeliverableNo")
    private String contractDeliverableNo;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(String parentActivity) {
        this.parentActivity = parentActivity;
    }

    public String getAcutalEndDate() {
        return acutalEndDate;
    }

    public void setAcutalEndDate(String acutalEndDate) {
        this.acutalEndDate = acutalEndDate;
    }

    public String getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(String netQuantity) {
        this.netQuantity = netQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductTypeDesc() {
        return productTypeDesc;
    }

    public void setProductTypeDesc(String productTypeDesc) {
        this.productTypeDesc = productTypeDesc;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getSelectable() {
        return selectable;
    }

    public void setSelectable(String selectable) {
        this.selectable = selectable;
    }

    public String getContractDeliverableNo() {
        return contractDeliverableNo;
    }

    public void setContractDeliverableNo(String contractDeliverableNo) {
        this.contractDeliverableNo = contractDeliverableNo;
    }
}
