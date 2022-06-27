package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse;

import java.util.List;

public class FileRelatedData {
    private List<InfoModel> ownerInfo;
    private List<Object> children;

    public List<InfoModel> getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(List<InfoModel> ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }
}
