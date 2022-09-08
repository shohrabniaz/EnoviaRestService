/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author omour faruq
 */

@Component
@Scope("prototype")
@Data
@ToString
public class ComosTaskBean {

    @SerializedName("Project")
    @JsonProperty("Project")
    private String project;

    @SerializedName("Activity")
    @JsonProperty("Activity")
    private String activity;

    @SerializedName("ActivityTitle")
    @JsonProperty("ActivityTitle")
    private String activityTitle;

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

//    @SerializedName("EXT_UID")
//    @JsonProperty("EXT_UID")
//    private String EXT_UID;


    @SerializedName("ERPType")
    @JsonProperty("ERPType")
    private String erpType;

    @SerializedName("ParentComosActivityUID")
    @JsonProperty("ParentComosActivityUID")
    private String parentComosActivityUID;

    @SerializedName("ComosActivityUID")
    @JsonProperty("ComosActivityUID")
    private String comosActivityUID;

    @SerializedName("baselineStartDate")
    @JsonProperty("baselineStartDate")
    private String baseLineStartDate;

    @SerializedName("baselineEndDate")
    @JsonProperty("baselineEndDate")
    private String baseLineEndDate;

    @SerializedName("plannedStartDate")
    @JsonProperty("plannedStartDate")
    private String plannedStartDate;

    @SerializedName("plannedEndDate")
    @JsonProperty("plannedEndDate")
    private String plannedEndDate;

    @SerializedName("actualStartDate")
    @JsonProperty("actualStartDate")
    private String actualStartDate;

    @SerializedName("actualEndDate")
    @JsonProperty("actualEndDate")
    private String actualEndDate;

    @SerializedName("lastModifiedInTable")
    @JsonProperty("lastModifiedInTable")
    private String lastModifiedInTable;

    @SerializedName("plannedDeliveryDate")
    @JsonProperty("plannedDeliveryDate")
    private String plannedDeliveryDate;
}
