/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.mvEnoviaItem;

import com.bjit.common.rest.app.service.model.tnr.TNR;

/**
 *
 * @author BJIT
 */
public class ItemInfo {

    private String physicalid;
    private TNR tnr;

    public String getPhysicalid() {
        return physicalid;
    }

    public void setPhysicalid(String physicalid) {
        this.physicalid = physicalid;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    @Override
    public String toString() {
        return "ItemInfo{" + "physicalid=" + physicalid + ", tnr=" + tnr + '}';
    }

   

}
