/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.model;

import com.bjit.project_structure.utilities.NullOrEmptyChecker;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author omour faruq
 */

@Component
@Scope("prototype")
@Data
@ToString
public class ComosTaskDataBean {
    @SerializedName("TableData")
    @JsonProperty("TableData")
    @Expose
    private List<ComosTaskBean> tableData = null;

    public void mergeTaskData(ComosTaskDataBean taskDataBean){
        List<ComosTaskBean> newTableData = taskDataBean.getTableData();
        
        if(NullOrEmptyChecker.isNullOrEmpty(newTableData)){
            return;
        }
        
        if(NullOrEmptyChecker.isNullOrEmpty(tableData)){
            setTableData(newTableData);
        }
        else{
            this.getTableData().addAll(newTableData);
        }
    }
}
