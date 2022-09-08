/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.itemHistoryLine;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class ItemHistoryReqModel {

    private List<ItemsInfo> data;
    private HistoryOrder constraint;

    public List<ItemsInfo> getData() {
        return data;
    }

    public void setData(List<ItemsInfo> data) {
        this.data = data;
    }

    public HistoryOrder getConstraint() {
        return constraint;
    }

    public void setConstraint(HistoryOrder constraint) {
        this.constraint = constraint;
    }

    @Override
    public String toString() {
        return "ItemHistoryReqModel{" + "data=" + data + ", constraint=" + constraint + '}';
    }

}
