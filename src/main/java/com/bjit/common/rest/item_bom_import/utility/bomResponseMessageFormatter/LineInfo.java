/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter;

import com.bjit.common.rest.app.service.model.tnr.TNR;

/**
 *
 * @author Sajjad
 */
public class LineInfo {
    private TNR tnr;
    private String position;
    private String message;

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
