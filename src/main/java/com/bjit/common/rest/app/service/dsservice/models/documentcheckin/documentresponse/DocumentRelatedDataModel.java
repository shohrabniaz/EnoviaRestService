package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse;

import java.util.List;

public class DocumentRelatedDataModel {
    private List<FileInfo> files;
    private List<InfoModel> ownerInfo;
    private List<InfoModel> originatorInfo;

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public List<InfoModel> getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(List<InfoModel> ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public List<InfoModel> getOriginatorInfo() {
        return originatorInfo;
    }

    public void setOriginatorInfo(List<InfoModel> originatorInfo) {
        this.originatorInfo = originatorInfo;
    }
}
