package com.bjit.common.rest.app.service.model.wbs.ln;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author BJIT
 */
public class ProjectTableData {
    @SerializedName("TableData")
    @Expose
    private ActivitiesBean tableData;

    public ActivitiesBean getTableData() {
        return tableData;
    }

    public void setTableData(ActivitiesBean tableData) {
        this.tableData = tableData;
    }
}
