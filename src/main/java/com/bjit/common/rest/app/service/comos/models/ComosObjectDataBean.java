/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.models;

import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;

/**
 *
 * @author BJIT
 */
public class ComosObjectDataBean extends ObjectDataBean {
    private String millId;
    private String equipmentId;

    public String getMillId() {
        return millId;
    }

    public void setMillId(String millId) {
        this.millId = millId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
}
