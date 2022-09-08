package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

/**
 *
 * @author Touhidul Islam
 */
public class ManufacturingItemInfo {

    private String physicalid;

    public ManufacturingItemInfo(String physicalid) {
        this.physicalid = physicalid;
    }

    public String getPhysicalid() {
        return physicalid;
    }

    public void setPhysicalid(String physicalid) {
        this.physicalid = physicalid;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ManufacturingItemInfo [");
        if (physicalid != null) {
            builder.append("physicalid=");
            builder.append(physicalid);
        }
        builder.append("]");
        return builder.toString();
    }
}
