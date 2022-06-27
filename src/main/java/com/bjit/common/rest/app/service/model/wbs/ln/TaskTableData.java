package com.bjit.common.rest.app.service.model.wbs.ln;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author BJIT
 */
public class TaskTableData {
    @SerializedName("TableData")
    private TaskBean tableData;

    public TaskBean getTableData() {
        return tableData;
    }

    public void setTableData(TaskBean tableData) {
        this.tableData = tableData;
    }
}
