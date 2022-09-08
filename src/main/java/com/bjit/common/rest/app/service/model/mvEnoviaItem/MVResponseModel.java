/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.mvEnoviaItem;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class MVResponseModel {

    private List<MVInfo> items;

    public List<MVInfo> getItems() {
        return items;
    }

    public void setItems(List<MVInfo> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "MVResponseModel{" + "items=" + items + '}';
    }

}
