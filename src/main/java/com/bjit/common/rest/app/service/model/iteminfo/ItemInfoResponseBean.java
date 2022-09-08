/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.iteminfo;

import java.util.Map;
import com.bjit.common.rest.app.service.model.tnr.TNR;
/**
 *
 * @author Tomal
 */
public class ItemInfoResponseBean {
    String item_Id;
    TNR type_name_revision;
    Map<String,String> requested_information;

    public String getItem_Id() {
        return item_Id;
    }

    public void setItem_Id(String item_Id) {
        this.item_Id = item_Id;
    }

    public TNR getType_name_revision() {
        return type_name_revision;
    }

    public void setType_name_revision(TNR type_name_revision) {
        this.type_name_revision = type_name_revision;
    }

    public Map<String, String> getRequested_information() {
        return requested_information;
    }

    public void setRequested_information(Map<String, String> requested_information) {
        this.requested_information = requested_information;
    }
    
}
