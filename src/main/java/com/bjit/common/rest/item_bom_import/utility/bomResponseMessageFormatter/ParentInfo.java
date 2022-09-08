/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author Sajjad
 */
public class ParentInfo {

    private TNR tnr;
    private String status;
    private String errorMessage;
    private List<String> errorMessages;
    //private List<LineInfo> lineList;

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*public List<LineInfo> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineInfo> lineList) {
        this.lineList = lineList;
    }*/
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
