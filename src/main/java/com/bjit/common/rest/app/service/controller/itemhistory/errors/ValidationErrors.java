package com.bjit.common.rest.app.service.controller.itemhistory.errors;

import com.bjit.common.rest.app.service.controller.itemhistory.ItemHistoryController;
import com.bjit.common.rest.app.service.payload.common_response.Status;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Touhidul Islam
 */
public class ValidationErrors implements ItemHistoryErrorResponse {

    private static final Logger LOGGER = Logger.getLogger(ItemHistoryController.class);
    private ErrorMessageSetResponseBuilder responseBuilder = new ErrorMessageSetResponseBuilder();

    @Override
    public ResponseEntity getResponse(List<String> messages) {
        LOGGER.error(messages.toString());
        //responseBuilder.setErrorMessage(new ArrayList<Object>(messages));
        String errorResponseStr = responseBuilder.setErrorMessage(new ArrayList<Object>(messages)).setStatus(Status.FAILED).buildResponse();
        return new ResponseEntity<>(errorResponseStr, HttpStatus.NOT_ACCEPTABLE);
    }

}
