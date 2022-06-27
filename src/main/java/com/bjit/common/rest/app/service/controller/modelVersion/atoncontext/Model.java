package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import com.bjit.common.rest.app.service.model.tnr.TNR;

/**
 *
 * @author Touhidul Islam
 */
public class Model {

    private TNR tnr;
    private Boolean modelExists;
    private ModelInfo modelInfo;
    private ManufacturingItemInfo manufacturingItemInfo;

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public Boolean getModelExists() {
        return modelExists;
    }

    public void setModelExists(Boolean modelExists) {
        this.modelExists = modelExists;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    public ManufacturingItemInfo getManufacturingItemInfo() {
        return manufacturingItemInfo;
    }

    public void setManufacturingItemInfo(ManufacturingItemInfo manufacturingItemInfo) {
        this.manufacturingItemInfo = manufacturingItemInfo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Model [");
        if (tnr != null) {
            builder.append("tnr=");
            builder.append(tnr);
            builder.append(", ");
        }
        if (modelExists != null) {
            builder.append("modelExists=");
            builder.append(modelExists);
            builder.append(", ");
        }
        if (modelInfo != null) {
            builder.append("modelInfo=");
            builder.append(modelInfo);
            builder.append(", ");
        }
        if (manufacturingItemInfo != null) {
            builder.append("manufacturingItemInfo=");
            builder.append(manufacturingItemInfo);
        }
        builder.append("]");
        return builder.toString();
    }

}
