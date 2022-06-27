/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.itemTransfer;

import com.bjit.ex.integration.model.webservice.Item;
import java.util.List;

/**
 *
 * @author Sarowar-221
 */
public class LNTransferRequestModel {
    private List<Item> items;
    private String target;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getSource() {
        return target;
    }

    public void setSource(String source) {
        this.target = source;
    }
    
    @Override
    public String toString() {
        return "ItemTransfer{" + "items=" + items + ", source=" + target + '}';
    }
}
