package com.bjit.common.rest.app.service.controller.itemhistory.errors;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessageSetResponseBuilder extends CustomResponseBuilder {

    public IResponse setErrorMessage(List<Object> errorMessage) {
        super.setErrorMessages(new ArrayList<Object>(errorMessage));
        return this;
    }

}
