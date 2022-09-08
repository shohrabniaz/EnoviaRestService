/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.itemHistoryLine;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author BJIT
 */
public class HistoryOrder {
    
    private String historyOrder;

    public String getHistoryOrder() {
        return historyOrder;
    }

    public void setHistoryOrder(String historyOrder) {
        this.historyOrder = historyOrder;
    }

    @Override
    public String toString() {
        return "HistoryOrder{" + "historyOrder=" + historyOrder + '}';
    }
    
    
    
    
    
}
