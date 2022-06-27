package com.bjit.common.rest.app.service.controller.modelVersion.atoncontext;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Touhidul Islam
 */
public class AtonRequestModel {

    private List<TNR> models = new ArrayList<>();

    /**
     * @return the models
     */
    public List<TNR> getModels() {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(List<TNR> models) {
        this.models = models;
    }
}
