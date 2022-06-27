/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.structure;

import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ItemStructure {
    List<CreateBOMBean> strucuteList;
    String source;

    public List<CreateBOMBean> getStrucuteList() {
        return strucuteList;
    }

    public void setStrucuteList(List<CreateBOMBean> strucuteList) {
        this.strucuteList = strucuteList;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
