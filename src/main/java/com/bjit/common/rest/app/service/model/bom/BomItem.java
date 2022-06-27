/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tomal
 */
public class BomItem {
    Map<String,String> ItemID;
    List<Map<String,String>> Lines;

    public Map<String, String> getItemID() {
        return ItemID;
    }

    public void setItemID(Map<String, String> ItemID) {
        this.ItemID = ItemID;
    }

    public List<Map<String, String>> getLines() {
        return Lines;
    }

    public void setLines(List<Map<String, String>> Lines) {
        this.Lines = Lines;
    }
}
