/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.salesforce.intregation;

import com.bjit.common.rest.app.service.controller.ref.item.*;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.refItem.RefItemExportServiceImp;
import com.bjit.common.rest.app.service.refItems.utilities.RefItemExportUtil;
import com.bjit.common.rest.app.service.salesforce.SalseforceIntregationExportServiceImp;
import com.bjit.common.rest.app.service.salseforce.utilities.SalseforceIntregationExportUtil;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import java.time.Instant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Suvonkar Kundu
 */
@RestController
@RequestMapping("/salesforce")
public class SalesforceIntregationController {

    private static final org.apache.log4j.Logger Salesforce_Intregation_Controller_Logger = org.apache.log4j.Logger.getLogger(SalesforceIntregationController.class);
    SalseforceIntregationExportServiceImp SalseforceIntregationExportServiceImp = new SalseforceIntregationExportServiceImp();
    SalseforceIntregationExportUtil salseforceIntregationExportUtil = new SalseforceIntregationExportUtil();
    String finalResponse;
    IResponse responseBuilder = new CustomResponseBuilder();

    /**
     * Build JSON or XML response
     *
     * @param code is reference item code
     * @param codestatus is reference item code status
     * @param format define XML and JSON response format
     * @return ResponseEntity
     */
    @ResponseBody
    @GetMapping(value = "/mv/intregation", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity salseforceMVIntregation(HttpServletRequest httpRequest
    ) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Salesforce_Intregation_Controller_Logger.debug("---------------------- ||| Salseforce Intregation ||| ----------------------");
        Salesforce_Intregation_Controller_Logger.debug("####################################################################");
        Instant itemImportStartTime = Instant.now();
        try {
            finalResponse = SalseforceIntregationExportServiceImp.getIntregatedMVItemId();
            return new ResponseEntity<>(responseBuilder.createJsonObjectResponse(Status.OK, new JSONArray(finalResponse)).toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Salesforce_Intregation_Controller_Logger.debug(ex);
            String buildResponse = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            Salesforce_Intregation_Controller_Logger.debug("Time elapsed for the salseforce intregation  is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            Salesforce_Intregation_Controller_Logger.info("Model Version Salseforce Intregation: '" + duration + "' milli-seconds");
            Salesforce_Intregation_Controller_Logger.info("---------------------------------------- |||Model Version Salseforce Intregation ||| ----------------------------------------");
            Salesforce_Intregation_Controller_Logger.info("################################################################################################################################\n");
        }

    }
}
