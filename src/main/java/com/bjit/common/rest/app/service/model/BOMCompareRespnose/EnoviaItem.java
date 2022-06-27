package com.bjit.common.rest.app.service.model.BOMCompareRespnose;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tahmid
 */
public class EnoviaItem {

    private String type;
    private String name;
    @SerializedName(value = "revision")
    private String revision;
    private String qty;
    private String position;
    @SerializedName(value = "Level")
    private String level;
    @SerializedName(value = "ERP Item Type")
    private String itemType;
    @SerializedName(value = "Release Purpose")
    private String releasePurpose;
    @SerializedName(value = "Drawing Number")
    private String drawingNumber;
    @SerializedName(value = "Title")
    private String title;
    @SerializedName(value = "PDM revision")
    private String pdmRevision;
    private String physicalid;
    @SerializedName(value = "Short Name")
    private String shortName;
    @SerializedName(value = "Weight")
    private String weight;
    @SerializedName(value = "Size")
    private String size;
    @SerializedName(value = "Technical Designation")
    private String technicalDesignation;
    @SerializedName(value = "Material")
    private String material;
    @SerializedName(value = "Unit")
    private String unit;
    @SerializedName(value = "Standard")
    private String standard;
    @SerializedName(value = "DistributionList")
    private String distributionList;
    @SerializedName(value = "Length")
    private String length;
    @SerializedName(value = "Width")
    private String width;
    @SerializedName(value = "Status")
    private String status;
    @SerializedName(value = "Source Item")
    private String sourceItem;
    @SerializedName(value = "Transfer To ERP")
    private String transferToERP;
    @SerializedName(value = "item common text")
    private String itemCommonText;
    @SerializedName(value = "item purchasing text")
    private String itemPurchasingText;
    @SerializedName(value = "bom common text")
    private String bomCommonText;
    @SerializedName(value = "bom purchasing text")
    private String bomPurchasingText;
    @SerializedName(value = "bom manufacturing text")
    private String bomManufacturingText;
    @SerializedName(value = "Mastership")
    private String mastership;
    @SerializedName(value = "bomLines")
    private List<EnoviaItem> bomLines;

    public EnoviaItem() {
    }

    public EnoviaItem(String position) {
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getReleasePurpose() {
        return releasePurpose;
    }

    public void setReleasePurpose(String releasePurpose) {
        this.releasePurpose = releasePurpose;
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

    public String getPdmRevision() {
        return pdmRevision;
    }

    public void setPdmRevision(String pdmRevision) {
        this.pdmRevision = pdmRevision;
    }

    public List<EnoviaItem> getBomLines() {
        return bomLines;
    }

    public void setBomLines(List<EnoviaItem> bomLines) {
        this.bomLines = bomLines;
    }

    public String getPhysicalid() {
        return physicalid;
    }

    public void setPhysicalid(String physicalid) {
        this.physicalid = physicalid;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTechnicalDesignation() {
        return technicalDesignation;
    }

    public void setTechnicalDesignation(String technicalDesignation) {
        this.technicalDesignation = technicalDesignation;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getDistributionList() {
        return distributionList;
    }

    public void setDistributionList(String distributionList) {
        this.distributionList = distributionList;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(String sourceItem) {
        this.sourceItem = sourceItem;
    }

    public String getTransferToERP() {
        return transferToERP;
    }

    public void setTransferToERP(String transferToERP) {
        this.transferToERP = transferToERP;
    }

    public String getItemCommonText() {
        return itemCommonText;
    }

    public void setItemCommonText(String itemCommonText) {
        this.itemCommonText = itemCommonText;
    }

    public String getItemPurchasingText() {
        return itemPurchasingText;
    }

    public void setItemPurchasingText(String itemPurchasingText) {
        this.itemPurchasingText = itemPurchasingText;
    }

    public String getBomCommonText() {
        return bomCommonText;
    }

    public void setBomCommonText(String bomCommonText) {
        this.bomCommonText = bomCommonText;
    }

    public String getBomPurchasingText() {
        return bomPurchasingText;
    }

    public void setBomPurchasingText(String bomPurchasingText) {
        this.bomPurchasingText = bomPurchasingText;
    }

    public String getBomManufacturingText() {
        return bomManufacturingText;
    }

    public void setBomManufacturingText(String bomManufacturingText) {
        this.bomManufacturingText = bomManufacturingText;
    }

    public String getMastership() {
        return mastership;
    }

    public void setMastership(String mastership) {
        this.mastership = mastership;
    }
}
