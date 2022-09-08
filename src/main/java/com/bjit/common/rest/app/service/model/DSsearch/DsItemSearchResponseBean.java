/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.DSsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class DsItemSearchResponseBean {
    
    @JsonProperty(value = "totalItems")
    private int totalItems; 
    
    @JsonProperty(value = "member")
    private List<HashMap<String,String>> member;
    
    @JsonProperty(value = "nlsLabel")
    private HashMap<String,String> nlsLabel;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<HashMap<String, String>> getMember() {
        return member;
    }

    public void setMember(List<HashMap<String, String>> member) {
        this.member = member;
    }

    public HashMap<String, String> getNlsLabel() {
        return nlsLabel;
    }

    public void setNlsLabel(HashMap<String, String> nlsLabel) {
        this.nlsLabel = nlsLabel;
    }
}
