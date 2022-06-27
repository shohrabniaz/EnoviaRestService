/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.Translation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author BJIT
 */
public class BundleAndText {

    @JsonProperty(value = "text")
    private String Text;
    @JsonProperty(value = "abbreviation")
    private String Abbreviation;

    public BundleAndText() {
    }

    public BundleAndText(String English, String Abbreviation) {

        this.Text = Text;
        this.Abbreviation = Abbreviation;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public String getAbbreviation() {
        return Abbreviation;
    }

    public void setAbbreviation(String Abbreviation) {
        this.Abbreviation = Abbreviation;
    }

}
