/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVCreateUpdateModelResponse {
    @JsonProperty(value = "totalItems")
    private int totalItems; 
    
    @JsonProperty(value = "member")
    private List<HashMap<String,String>> member;

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
}
