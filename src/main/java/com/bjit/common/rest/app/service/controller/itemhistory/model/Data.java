package com.bjit.common.rest.app.service.controller.itemhistory.model;

/**
 * @author Touhidul Islam
 */

import com.bjit.common.rest.app.service.model.tnr.TNR;

import java.util.ArrayList;
import java.util.List;

public class Data implements Cloneable {

    private String itemId;
    private Integer descId;
    private TNR tnr;
    private List<String> histories = new ArrayList<>();
    private boolean isExists = true;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getDescId() {
        return descId;
    }

    public void setDescId(Integer descId) {
        this.descId = descId;
    }

    public TNR getTNR() {
        return tnr;
    }

    public void setTNR(TNR tNR) {
        this.tnr = tNR;
    }

    public List<String> getHistories() {
        return histories;
    }

    public void setHistories(List<String> histories) {
        if (histories != null) {
            this.histories = histories;
        }
    }

    public boolean isExists() {
        return isExists;
    }

    public void setExists(boolean exists) {
        isExists = exists;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Data{");
        sb.append("itemId='").append(itemId).append('\'');
        sb.append(", descId=").append(descId);
        sb.append(", tNR=").append(tnr);
        sb.append(", histories=").append(histories);
        sb.append(", isExists=").append(isExists);
        sb.append('}');
        return sb.toString();
    }
}
