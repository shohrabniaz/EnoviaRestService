/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.BOMCompareRespnose;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tahmid
 */
public class PDMItem {

    @SerializedName(value = "type")
    private String type;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "revision")
    private String revision;
    @SerializedName(value = "position")
    private String position;
    @SerializedName(value = "qty")
    private String qty;
    @SerializedName(value = "signalCode")
    private String signalCode;
    @SerializedName(value = "itemType")
    private String itemType;
    @SerializedName(value = "drawingNumber")
    private String drawingNumber;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "Transferred to ERPs")
    private String transferToERP;
    @SerializedName(value = "bomLines")
    private List<PDMItem> bomLines;

    public PDMItem() {
    }

    public PDMItem(String position) {
        this.position = position;
        this.type = "dummy_type";
        this.name = "dummy_obj";
        this.bomLines = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSignalCode() {
        return signalCode;
    }

    public void setSignalCode(String signalCode) {
        this.signalCode = signalCode;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTransferToERP() {
        return transferToERP;
    }

    public void setTransferToERP(String transferToERP) {
        this.transferToERP = transferToERP;
    }

    public List<PDMItem> getBomLines() {
        return bomLines;
    }

    public void setBomLines(List<PDMItem> bomLines) {
        this.bomLines = bomLines;
    }

}
