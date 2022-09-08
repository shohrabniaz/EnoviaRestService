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
public class ActivityModel {
    private String activityName;
    private String activityType;
    private String activityId;
    private String relationShipName;
    private String parentName;
    private String parentId;
    private String relationshipId;
    private String comosActivityUID;
    private String parentComosActivityUID;
}
