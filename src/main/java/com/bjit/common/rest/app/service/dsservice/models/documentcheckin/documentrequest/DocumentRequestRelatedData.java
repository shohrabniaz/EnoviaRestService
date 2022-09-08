package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest;

import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.InfoModel;
import java.util.List;

public class DocumentRequestRelatedData {
    List<Brochure> files;
    List<InfoModel> ownerInfo;
    private List<Object> children;

    public List<Brochure> getFiles() {
        return files;
    }

    public void setFiles(List<Brochure> files) {
        this.files = files;
    }

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
