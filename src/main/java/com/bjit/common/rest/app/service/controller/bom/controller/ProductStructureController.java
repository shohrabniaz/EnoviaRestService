/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.controller;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.controller.bom.model.StructureModel;
import com.bjit.common.rest.app.service.controller.bom.serviceInterfaces.IProductStructureService;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author BJIT
 */
@Controller
@RequestMapping(path = "/structure/product/v1")
public class ProductStructureController {

    String rootHardwareItemId;
    private static final org.apache.log4j.Logger STRUCTURE_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ProductStructureController.class);

    @Autowired
    IProductStructureService hardwareProductStructureServiceImpl;
    @Autowired
    IResponse responseBuilder;

    @LogExecutionTime
    @RequestMapping(value = "/{productType}/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> importProductConfigurationStructure(HttpServletRequest httpRequest, @RequestBody final StructureModel structureModel, @PathVariable String productType) {

        String buildResponse = "";
        rootHardwareItemId = null;

        final Context context = (Context) httpRequest.getAttribute("context");
        try {

            buildResponse = hardwareProductStructureServiceImpl.importStructure(context, structureModel, responseBuilder);

            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            try {
                /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
                if (ContextUtil.isTransactionActive(context)) {
                    STRUCTURE_CONTROLLER_LOGGER.error("Aborting for Transaction");
                    ContextUtil.abortTransaction(context);
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                    STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                }
            } catch (FrameworkException ex) {
                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            }
        }
        throw new RuntimeException("Unknown Exception occurred");
    }
}
