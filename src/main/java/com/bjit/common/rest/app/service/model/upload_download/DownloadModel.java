/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.upload_download;


/**
 *
 * @author Tomal
 */
public class DownloadModel {

    private String fileName;

    private String location;
//    private List<String> listOfFileName;

//    public List<String> getListOfFileName() {
//        return listOfFileName;
//    }
//
//    public void setListOfFileName(List<String> listOfFileName) {
//        this.listOfFileName = listOfFileName;
//    }

    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
}
//UploadModel