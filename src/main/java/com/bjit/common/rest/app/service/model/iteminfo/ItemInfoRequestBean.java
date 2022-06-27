/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.iteminfo;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author Tomal
 */
public class ItemInfoRequestBean {
    String item_Id;
    TNR type_name_revision;
    List<String> attribute_List;
    List<String> property_List;

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

    public List<String> getAttribute_List() {
        return attribute_List;
    }

    public void setAttribute_List(List<String> attribute_List) {
        this.attribute_List = attribute_List;
    }

    public List<String> getProperty_List() {
        return property_List;
    }

    public void setProperty_List(List<String> property_List) {
        this.property_List = property_List;
    }

    
    
}
