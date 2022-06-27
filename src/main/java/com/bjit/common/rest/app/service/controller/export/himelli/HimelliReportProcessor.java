package com.bjit.common.rest.app.service.controller.export.himelli;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ashikur / BJIT
 *
 */
public class HimelliReportProcessor {

    private Object valconBOMObject = null;
    private Set<Attribute> notIncludedAttributes;

    public HimelliReportProcessor() {
        this.notIncludedAttributes = new HashSet<>();
    }

    public HimelliReportProcessor(Object valconBOMObject) {
        this();
        this.valconBOMObject = valconBOMObject;
    }

    public byte[] himelliDataProcessing() throws Exception {
        StringBuilder himelliReportContent = new StringBuilder();
        HimelliInputData himelliInputData = new HimelliInputData(this.valconBOMObject);
        ReportTemplate himelliReport = new HimelliReport(himelliInputData);
        himelliReport.setNotIncludedAttributes(this.notIncludedAttributes);
        himelliReportContent.append(himelliReport.addHeader()).append(himelliReport.addBody());
        System.out.println(himelliReportContent.toString());
        return himelliReportContent.toString().getBytes();
    }

    /**
     * @return the notIncludedAttributes
     */
    public Set<Attribute> getNotIncludedAttributes() {
        return notIncludedAttributes;
    }

    /**
     * @param notIncludedAttributes the notIncludedAttributes to set
     */
    public void setNotIncludedAttributes(Set<Attribute> notIncludedAttributes) {
        this.notIncludedAttributes = notIncludedAttributes;
    }

    public void addToNotIncludedAttributes(Attribute notIncludedAttribute) {
        this.notIncludedAttributes.add(notIncludedAttribute);
    }

    public void addToNotIncludedAttributes(String source) {
        Attribute attr = new Attribute();
        attr.setSource(source);
        this.notIncludedAttributes.add(attr);
    }

    public void addToNotIncludedAttributes(String source, String dest) {
        Attribute attr = new Attribute();
        attr.setSource(source);
        attr.setDest(dest);
        this.notIncludedAttributes.add(attr);
    }
}
