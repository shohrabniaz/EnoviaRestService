
package com.bjit.common.rest.app.service.GTS;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Sarwar
 *
 */
public class Data {

    @SerializedName("bundle_id")
    @Expose
    private Integer bundleId;
    @SerializedName("bundle_state")
    @Expose
    private String bundleState;
    @SerializedName("data_list")
    @Expose
    private List<GTSInfo> dataList = null;

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleState() {
        return bundleState;
    }

    public void setBundleState(String bundleState) {
        this.bundleState = bundleState;
    }

    public List<GTSInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<GTSInfo> dataList) {
        this.dataList = dataList;
    }

}
