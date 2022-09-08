/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.model;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import java.util.List;

/**
 *
 * @author Tomal
 */
public class DocumentInfoBean {
    private CreateObjectBean createObjectBean;
    private List<String> files;

    public CreateObjectBean getCreateObjectBean() {
        return createObjectBean;
    }

    public void setCreateObjectBean(CreateObjectBean createObjectBean) {
        this.createObjectBean = createObjectBean;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
