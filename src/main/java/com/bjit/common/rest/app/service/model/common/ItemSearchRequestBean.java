package com.bjit.common.rest.app.service.model.common;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class ItemSearchRequestBean {
    private String requester;
    private String ftsSearch = "true";
    private List<ItemSearchParamBean> params;
    
    public String getFtsSearch() {
        return ftsSearch;
    }

    public void setFtsSearch(String ftsSearch) {
        this.ftsSearch = ftsSearch;
    }
    
    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public List<ItemSearchParamBean> getParams() {
        return params;
    }

    public void setParams(List<ItemSearchParamBean> params) {
        this.params = params;
    }
}
