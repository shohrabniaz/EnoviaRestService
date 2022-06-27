package com.bjit.common.rest.app.service.model.wbs.ln;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectDataBean {

    @SerializedName("TableData")
    @Expose
    private List<ActivitiesBean> tableData = null;
    
    @SerializedName("Error")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ActivitiesBean> getTableData() {
        return tableData;
    }

    public void setTableData(List<ActivitiesBean> tableData) {
        this.tableData = tableData;
    }

}
