/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author BJIT
 */
public class DataFormat {
    private String sourceFormat;
    private String destinationFormat;

    public String getSourceFormat() {
        return sourceFormat;
    }

    @XmlAttribute(name = "source_format")
    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    public String getDestinationFormat() {
        return destinationFormat;
    }

    @XmlValue
    public void setDestinationFormat(String destinationFormat) {
        this.destinationFormat = destinationFormat;
    }
}
