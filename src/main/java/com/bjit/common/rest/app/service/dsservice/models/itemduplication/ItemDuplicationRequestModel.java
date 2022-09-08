package com.bjit.common.rest.app.service.dsservice.models.itemduplication;

import java.util.List;

public class ItemDuplicationRequestModel {

    private String affix = "CopyOf";
    private List<SkeletonItemInfo> data;

    public ItemDuplicationRequestModel(List<SkeletonItemInfo> data) {
        this.data = data;
    }

    /**
     * @return the affix
     */
    public String getAffix() {
        return affix;
    }

    /**
     * @param affix the affix to set
     */
    public void setAffix(String affix) {
        this.affix = affix;
    }

    /**
     * @return the data
     */
    public List<SkeletonItemInfo> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<SkeletonItemInfo> data) {
        this.data = data;
    }
}
