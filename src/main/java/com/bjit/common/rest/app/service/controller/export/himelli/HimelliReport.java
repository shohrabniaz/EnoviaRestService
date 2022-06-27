package com.bjit.common.rest.app.service.controller.export.himelli;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

/**
 * @author Ashikur / BJIT
 *
 */
public class HimelliReport implements ReportTemplate {

    private HimelliInputData himelliInputData;
    private Set<Attribute> notIncludedAttributes;

    public HimelliReport(HimelliInputData himelliInputData) {
        super();
        this.himelliInputData = himelliInputData;
        this.notIncludedAttributes = new HashSet<>();
    }

    @Override
    public String addHeader() {
        return "vLOC Enovia\r\n" + "vBOM E All\r\n" + "vEXP Y\r\n" + "vCUS /\r\n" + "vCUR EUR,EUR 1.0\r\n"
                + "vLAN EN,FI";
    }

    @Override
    public String addBody() throws URISyntaxException, JAXBException, FileNotFoundException {
        return new HimelliReportUtility().prepareReportTable(himelliInputData.getJson(), this.notIncludedAttributes);
    }

    @Override
    public String addFooter() {
        return "";
    }

    @Override
    public void setNotIncludedAttributes(Set<Attribute> notIncludedAttributes) {
        this.notIncludedAttributes = notIncludedAttributes;
    }

}
