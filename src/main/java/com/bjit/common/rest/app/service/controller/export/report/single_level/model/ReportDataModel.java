package com.bjit.common.rest.app.service.controller.export.report.single_level.model;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class ReportDataModel {
    private List<ReportDetailDataModel> detailReportData;
    private ReportSummaryDataModel summaryReportData;

    public void setDetailReportData(List<ReportDetailDataModel> detailReportData) {
        this.detailReportData = detailReportData;
    }

    public void setSummaryReportData(ReportSummaryDataModel summaryReportData) {
        this.summaryReportData = summaryReportData;
    }

    public List<ReportDetailDataModel> getDetailReportData() {
        return detailReportData;
    }

    public ReportSummaryDataModel getSummaryReportData() {
        return summaryReportData;
    }
}
