/**
 *
 */
package com.bjit.mapper.mapproject.report_mapping_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author TAREQ SEFATI
 *
 */
@XmlRootElement(name = "report")
public class ReportMapper {

    private String reportName;
    private String templateLang;
    private String drawingInfo;
    private String summaryReport;
    private String format;
    private String level;
    private boolean printStartEndPage;

    public String getReportName() {
        return reportName;
    }

    @XmlElement(name = "report-name")
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTemplateLang() {
        return templateLang;
    }

    @XmlElement(name = "template-lang")
    public void setTemplateLang(String templateLang) {
        this.templateLang = templateLang;
    }

    public String getDrawingInfo() {
        return drawingInfo;
    }

    @XmlElement(name = "drawing-info")
    public void setDrawingInfo(String drawingInfo) {
        this.drawingInfo = drawingInfo;
    }

    public String getSummaryReport() {
        return summaryReport;
    }

    @XmlElement(name = "summary-report")
    public void setSummaryReport(String summaryReport) {
        this.summaryReport = summaryReport;
    }

    public String getFormat() {
        return format;
    }

    @XmlElement(name = "format")
    public void setFormat(String format) {
        this.format = format;
    }

    public String getLevel() {
        return level;
    }

    @XmlElement(name = "level")
    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isPrintStartEndPage() {
        return printStartEndPage;
    }

    @XmlElement(name = "printStartEndPage")
    public void setPrintStartEndPage(boolean printStartEndPage) {
        this.printStartEndPage = printStartEndPage;
    }
}
