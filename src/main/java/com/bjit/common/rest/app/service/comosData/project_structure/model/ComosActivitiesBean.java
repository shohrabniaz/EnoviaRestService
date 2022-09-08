package com.bjit.common.rest.app.service.comosData.project_structure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author omour faruq
 */

@Component
@Scope("prototype")
@Data
@ToString
public class ComosActivitiesBean {

    @SerializedName("ProjectCode")
    @JsonProperty("ProjectCode")
    private String projectCode;

    @SerializedName("Description")
    @JsonProperty("Description")
    private String description;

    @SerializedName("Title")
    @JsonProperty("Title")
    private String title;

    @SerializedName("CompassId")
    @JsonProperty("CompassId")
    private String compassId;

    @SerializedName("ERPSubProject")
    @JsonProperty("ERPSubProject")
    private String erpSubProject;

    @SerializedName("ComosProjectUID")
    @JsonProperty("ComosProjectUID")
    private String comosProjectUID;

    @SerializedName("ComosMillId")
    @JsonProperty("ComosMillId")
    private String millId;

    @SerializedName("ComosEquipmentId")
    @JsonProperty("ComosEquipmentId")
    private String equipmentId;

    @SerializedName("ComosPlantId")
    @JsonProperty("ComosPlantId")
    private String plantId;

    @SerializedName("ComosProjectId")
    @JsonProperty("ComosProjectId")
    private String projectId;

    @SerializedName("ComosLayerId")
    @JsonProperty("ComosLayerId")
    private String layerId;

    @SerializedName("ComosMillHierarchyId")
    @JsonProperty("ComosMillHierarchyId")
    private String millHierarchyId;
}
