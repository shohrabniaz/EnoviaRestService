/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.create_update_checkin;

import com.bjit.common.rest.app.service.controller.createcheckin.models.DocumentsInfo;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class CreateCheckinBean {

    private CreateObjectBean itemInfo;
    private List<DocumentsInfo> docInfo;
    private List<String> interfaceList;

    public CreateObjectBean getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(CreateObjectBean itemInfo) {
        this.itemInfo = itemInfo;
    }

    public List<DocumentsInfo> getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(List<DocumentsInfo> docInfo) {
        this.docInfo = docInfo;
    }

    public List<String> getInterfaceList() {
        return interfaceList;
    }

    public void setInterfaceList(List<String> interfaceList) {
        this.interfaceList = interfaceList;
    }
}
