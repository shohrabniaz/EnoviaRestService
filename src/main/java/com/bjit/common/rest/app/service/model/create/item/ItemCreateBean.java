/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.create.item;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Tomal
 */
public class ItemCreateBean {
    List <HashMap<String,Object>> items;

    public List<HashMap<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<HashMap<String, Object>> items) {
        this.items = items;
    }
    
}
