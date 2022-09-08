package com.bjit.common.rest.app.service.controller.itemhistory.validator;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;

import java.util.List;

public abstract class Validator {
    private Validator nextValidator = null;
    protected List<Data> dataList;

    public void setNextValidator(Validator validator) {
        this.nextValidator = validator;
    }

    public List<String> validate(List<Data> dataList) {
        List<String> errors = this._validate(dataList);

        //this validator does not found any errors
        if (errors.isEmpty()) {
            //its time for next validator to validate dataList
            if (this.nextValidator != null) {
                return this.nextValidator.validate(dataList);
            }
        }
        return errors;
    }

    protected abstract List<String> _validate(List<Data> dataList);
}
