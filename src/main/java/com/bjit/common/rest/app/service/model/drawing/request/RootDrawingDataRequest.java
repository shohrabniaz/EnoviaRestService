package com.bjit.common.rest.app.service.model.drawing.request;

import java.util.List;

public class RootDrawingDataRequest {

    private String source;
    private List<DrawingDataRequest> params;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<DrawingDataRequest> getParams() {
        return params;
    }

    public void setParams(List<DrawingDataRequest> params) {
        this.params = params;
    }
}
