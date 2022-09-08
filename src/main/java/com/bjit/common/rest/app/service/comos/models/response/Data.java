package com.bjit.common.rest.app.service.comos.models.response;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("MillId")
    private String millId;
    @SerializedName("EquipmentId")
    private String equipmentId;
    @SerializedName("ComosModel")
    private Child comosModel;

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

    public Child getComosModel() {
        return comosModel;
    }

    public void setComosModel(Child comosModel) {
        this.comosModel = comosModel;
    }
}
