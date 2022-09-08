/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sajjad
 */
public class ChildInfo {

    private String childId;
    private TNR childTNR;
    private String relName;
    private ArrayList<String> relIDList;
    private String interfaceName;
    private String message;
    private String parentId;
    private int childQuantity;
    private int childNoOfUnit;
    private String childInventoryUnit;
    private HashMap<String, String> AttributeNameValueMap;
    private HashMap<String, String> propertyNameValueMap;
    private String length;
    private String width;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public TNR getChildTNR() {
        return childTNR;
    }

    public void setChildTNR(TNR childTNR) {
        this.childTNR = childTNR;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public int getChildQuantity() {
        return childQuantity;
    }

    public void setChildQuantity(int childQuantity) {
        this.childQuantity = childQuantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getRelIDList() {
        return relIDList;
    }

    public void setRelIDList(ArrayList<String> relIDList) {
        this.relIDList = relIDList;
    }

    public HashMap<String, String> getAttributeNameValueMap() {
        return AttributeNameValueMap;
    }

    public void setAttributeNameValueMap(HashMap<String, String> AttributeNameValueMap) {
        this.AttributeNameValueMap = AttributeNameValueMap;
    }

    public HashMap<String, String> getPropertyNameValueMap() {
        return propertyNameValueMap;
    }

    public void setPropertyNameValueMap(HashMap<String, String> propertyNameValueMap) {
        this.propertyNameValueMap = propertyNameValueMap;
    }

    public int getChildNoOfUnit() {
        return childNoOfUnit;
    }

    public void setChildNoOfUnit(int childNoOfUnit) {
        this.childNoOfUnit = childNoOfUnit;
    }

    public String getChildInventoryUnit() {
        return childInventoryUnit;
    }

    public void setChildInventoryUnit(String childInventoryUnit) {
        this.childInventoryUnit = childInventoryUnit;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

}
