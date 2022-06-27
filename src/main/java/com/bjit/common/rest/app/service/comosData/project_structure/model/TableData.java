/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.model;

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
public class TableData {

    @SerializedName("Project")
    private String project;

    @SerializedName("Activity")
    private String activity;

    @SerializedName("ActivityDescription")
    private String activityDescription;

    @SerializedName("ActivityStatus")
    private String activityStatus;

    @SerializedName("ActivityType")
    private String activityType;

    @SerializedName("ParentActivity")
    private String parentActivity;

    @SerializedName("AcutalEndDate")
    private String acutalEndDate;

    @SerializedName("NetQuantity")
    private String netQuantity;

    @SerializedName("Unit")
    private String unit;

    @SerializedName("ProductType")
    private String productType;

    @SerializedName("ProductTypeDesc")
    private String productTypeDesc;

    @SerializedName("Pos")
    private String pos;

    @SerializedName("Selectable")
    private String selectable;

    @SerializedName("ContractDeliverableNo")
    private String contractDeliverableNo;
}
