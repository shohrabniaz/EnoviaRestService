package com.bjit.common.rest.app.service.model.wbs.ln;

import com.google.gson.annotations.SerializedName;

public class ActivitiesBean {

    @SerializedName("ProjectCode")
    private String projectCode;
    
    @SerializedName("Description")
    private String description;

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
