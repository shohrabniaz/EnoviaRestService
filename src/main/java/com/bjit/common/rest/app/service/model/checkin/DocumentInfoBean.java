/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.checkin;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;

/**
 *
 * @author Tomal
 */
public class DocumentInfoBean {
    private CreateObjectBean createObjectBean;
    private String fileName;

    public CreateObjectBean getCreateObjectBean() {
        return createObjectBean;
    }

    public void setCreateObjectBean(CreateObjectBean createObjectBean) {
        this.createObjectBean = createObjectBean;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
