/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.ref.item;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.refItem.RefItemExportServiceImp;
import com.bjit.common.rest.app.service.refItems.utilities.RefItemExportUtil;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import java.time.Instant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
@RequestMapping("/export")
public class RefItemExportController {

    private static final org.apache.log4j.Logger Export_REF_Item_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(RefItemExportController.class);
    RefItemExportServiceImp refItemExportServiceImp = new RefItemExportServiceImp();
    RefItemExportUtil refItemExportUtil = new RefItemExportUtil();
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
    @GetMapping(value = "/ref/items", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity exportRefItem(HttpServletRequest httpRequest,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "code_status", required = false) String codeStatus,
            @RequestParam(value = "format", required = false) String format
    ) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Export_REF_Item_CONTROLLER_LOGGER.debug("---------------------- ||| Export REF Item ||| ----------------------");
        Export_REF_Item_CONTROLLER_LOGGER.debug("####################################################################");
        Instant itemImportStartTime = Instant.now();
        try {
            finalResponse = refItemExportServiceImp.getJsonArrayData(code, codeStatus);
            if (format != null) {
                if (format.equalsIgnoreCase("XML")) {
                    return new ResponseEntity<>(refItemExportServiceImp.convertJsonToXml(refItemExportUtil.buildXMLResponse(finalResponse, Status.OK)), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(responseBuilder.createJsonObjectResponse(Status.OK, new JSONArray(finalResponse)).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(responseBuilder.createJsonObjectResponse(Status.OK, new JSONArray(finalResponse)).toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Export_REF_Item_CONTROLLER_LOGGER.debug(ex);
            String buildResponse = responseBuilder.addErrorMessage(ex.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            Export_REF_Item_CONTROLLER_LOGGER.debug("Time elapsed for the Export REF Items is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            Export_REF_Item_CONTROLLER_LOGGER.info("REF Items Export: '" + duration + "' milli-seconds");
            Export_REF_Item_CONTROLLER_LOGGER.info("---------------------------------------- ||| REF Items Export END ||| ----------------------------------------");
            Export_REF_Item_CONTROLLER_LOGGER.info("################################################################################################################################\n");
        }

    }
}
