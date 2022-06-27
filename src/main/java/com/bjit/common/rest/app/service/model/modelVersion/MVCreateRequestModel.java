/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import java.util.List;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVCreateRequestModel {
    private List<MVItemsModel> items;

    public MVCreateRequestModel(List<MVItemsModel> items) {
        this.items = items;
    }
    
    public List<MVItemsModel> getItems() {
        return items;
    }

    public void setItems(List<MVItemsModel> items) {
        this.items = items;
    }
    
    
}
