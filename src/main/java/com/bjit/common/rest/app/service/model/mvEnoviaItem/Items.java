/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.mvEnoviaItem;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class Items {

    List<ItemInfo> items;

    public List<ItemInfo> getItems() {
        return items;
    }

    public void setItems(List<ItemInfo> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Items{" + "items=" + items + '}';
    }

}
