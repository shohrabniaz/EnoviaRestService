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
public class MVCreateUpdateResponse {
    @JsonProperty(value = "totalItems")
    private int totalItems; 
    
    @JsonProperty(value = "member")
    private List<MVCreateUpdateMemberResponse> member;
    
    @JsonProperty(value = "nlsLabel")
    private HashMap<String,String> nlsLabel;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<MVCreateUpdateMemberResponse> getMember() {
        return member;
    }

    public void setMember(List<MVCreateUpdateMemberResponse> member) {
        this.member = member;
    }

    public HashMap<String, String> getNlsLabel() {
        return nlsLabel;
    }

    public void setNlsLabel(HashMap<String, String> nlsLabel) {
        this.nlsLabel = nlsLabel;
    }
}
