package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest;

import com.bjit.common.rest.app.service.dsservice.models.fcsjob.DataElementsModel;


public class Brochure {
    private DataElementsModel dataelements;
    private String updateAction;

    public DataElementsModel getDataelements() {
        return dataelements;
    }

    public void setDataelements(DataElementsModel dataelements) {
        this.dataelements = dataelements;
    }
    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
}
