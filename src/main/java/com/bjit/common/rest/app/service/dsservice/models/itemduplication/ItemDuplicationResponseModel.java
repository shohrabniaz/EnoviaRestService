package com.bjit.common.rest.app.service.dsservice.models.itemduplication;

import java.util.List;

public class ItemDuplicationResponseModel {

    private List<CreatedItem> results;
    private List<CreatedItemError> errorReport;

    /**
     * @return the results
     */
    public List<CreatedItem> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<CreatedItem> results) {
        this.results = results;
    }

    /**
     * @return the errorReport
     */
    public List<CreatedItemError> getErrorReport() {
        return errorReport;
    }

    /**
     * @param errorReport the errorReport to set
     */
    public void setErrorReport(List<CreatedItemError> errorReport) {
        this.errorReport = errorReport;
    }
}
