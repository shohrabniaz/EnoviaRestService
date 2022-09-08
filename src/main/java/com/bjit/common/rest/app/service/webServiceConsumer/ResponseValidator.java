/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.webServiceConsumer;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author BJIT
 */
public class ResponseValidator {

    private static final org.apache.log4j.Logger RESPONSE_VALIDATOR_LOGGER = org.apache.log4j.Logger.getLogger(ResponseValidator.class);

    public String responseCreator(String responseData) {
        IResponse responseBuilder = new CustomResponseBuilder();
        responseData = responseBuilder.addErrorMessage(responseData).setStatus(Status.FAILED).buildResponse();
        return responseData;
    }

    public String validateResponseData(String responseMessage) {
        RESPONSE_VALIDATOR_LOGGER.debug("Response from service : '" + responseMessage + "'");
        try {

            String status = new JSONObject(responseMessage).getString("status");
            if (status.equalsIgnoreCase("ok") || status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("partial")) {
                return responseMessage;
            } else {
                throw new RuntimeException(responseMessage);
            }

        } catch (JSONException exp) {
            RESPONSE_VALIDATOR_LOGGER.error(exp);
            return responseCreator(responseMessage);
        } catch (NullPointerException exp) {
            String error = NullOrEmptyChecker.isNullOrEmpty(exp.getMessage()) ? "No data found from the service" : exp.getMessage();
            RESPONSE_VALIDATOR_LOGGER.error(exp);
            return responseCreator(error);
        } catch (RuntimeException exp) {
            RESPONSE_VALIDATOR_LOGGER.error(exp);
            return responseCreator(exp.getMessage());
        } catch (Exception exp) {
            RESPONSE_VALIDATOR_LOGGER.error(exp);
            return responseCreator(exp.getMessage());
        }
    }

    public String getErrorMessage(Exception exp)  {
        String exceptionMessage = NullOrEmptyChecker.isNullOrEmpty(exp.getMessage()) ? "Data not found from the service" : exp.getMessage();
        RESPONSE_VALIDATOR_LOGGER.error(exp);
        return validateResponseData(exceptionMessage);
    }
}
