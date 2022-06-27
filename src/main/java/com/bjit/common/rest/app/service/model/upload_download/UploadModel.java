package com.bjit.common.rest.app.service.model.upload_download;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class UploadModel {

    private String extraField;

    private MultipartFile[] files;

    public String getExtraField() {
       
        return extraField;
    }

    public void setExtraField(String extraField) {
         System.out.println("SETTING ExTRA FIELD");
        this.extraField = extraField;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "UploadModel{" +
                "extraField='" + extraField + '\'' +
                ", files=" + Arrays.toString(files) +
                '}';
    }
}
