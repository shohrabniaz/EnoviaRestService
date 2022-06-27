/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
public class DataFormat {

    private String sourceFormat;
    private String timezone;
    private String destinationFormat;

    public String getSourceFormat() {
        return sourceFormat;
    }

    @XmlAttribute
    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    public String getTimezone() {
        return timezone;
    }

    @XmlAttribute
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDestinationFormat() {
        return destinationFormat;
    }

    @XmlValue
    public void setDestinationFormat(String destinationFormat) {
        this.destinationFormat = destinationFormat;
    }
}
