package com.bjit.common.rest.app.service.model.wbs.ln;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Mashuk
 */
public class WBSImportBean extends TaskDataBean {
    
    @SerializedName("Project")
    @JsonProperty("Project")
    private String projectCode;
    
    @SerializedName("Description")
    @JsonProperty("Description")
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
