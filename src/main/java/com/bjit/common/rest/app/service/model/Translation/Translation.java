/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.Translation;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */

public class Translation {


    private String source;
   
    @JsonProperty(value = "bundle_and_text")
    private List<HashMap<String,BundleAndText>> bundleAndText;

    public Translation() {
    }

    public Translation(String source, List<HashMap<String,BundleAndText>> bundleAndText) {

        this.source = source;
        this.bundleAndText = bundleAndText;

    }
   
    public String getSource() {
        return source;
    }

    public void setSources(String source) {
        this.source = source;
    }

   
    public List<HashMap<String,BundleAndText>> getBundleAndText() {
        return bundleAndText;
    }

    public void setBundleAndText(List<HashMap<String,BundleAndText>> bundleAndText) {
        this.bundleAndText = bundleAndText;
    }

}
