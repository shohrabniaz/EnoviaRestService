/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.model.tnr.TNR;

/**
 *
 * @author BJIT
 */
public class ResponseTNR {

    private TNR tnr;

    public ResponseTNR() {
    }

    public ResponseTNR(TNR tnr) {
        this.tnr = tnr;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    @Override
    public String toString() {
        return "ResponseTNR{" + "tnr=" + tnr + '}';
    }

}
