package com.bjit.common.rest.app.service.controller.lntransfer;

import com.bjit.common.rest.app.service.model.itemTransfer.LNTransferRequestModel;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import org.springframework.stereotype.Controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import com.bjit.common.rest.app.service.controller.lntransfer.Util.LNRequestUtil;
import com.bjit.common.rest.app.service.background.rnp.RnPResponseHandler;
import com.bjit.common.rest.app.service.lntransfer.ILNResponse;
import com.bjit.common.rest.app.service.lntransfer.LNCustomResponseBuilder;
import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import com.bjit.common.rest.app.service.model.itemTransfer.LNTransferRequestModel;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ex.integration.transfer.actions.GTSNightlyUpdateTransferAction;
import com.bjit.ex.integration.transfer.actions.LNTransferAction;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@Controller
@RestController
@RequestMapping(path = "/valmet/enovia/api/v1/export")
public class LNTransferController {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LNTransferController.class);

    @ResponseBody
    @PostMapping(value = {"/ln/transfer/item-bom-transfer/{transfer-type}", "/ln/transfer/item-bom-transfer/{transfer-type}/{level}"}, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> itemAndBOMTransfer(@RequestBody LNTransferRequestModel itemTransferModel,
            @PathVariable("transfer-type") String transferType,
            @PathVariable(value = "level", required = false) String level
    ) throws Exception {
        LNTransferAction lNTransferAction = new LNTransferAction();
        ILNResponse lnResponseBuilder = new LNCustomResponseBuilder();
        Map<LNResponseMessageFormater, String> transferResultMap = new HashMap<>();
        String expandLevel = "99";
        String buildResponse = "";
        String source = "";
        if (transferType.equalsIgnoreCase("item")) {
            source = "itemService";
        } else {
            source = "bomService";

        }
        if (!NullOrEmptyChecker.isNullOrEmpty(level)) {

            expandLevel = level;
        }
        try {
            LNRequestUtil.validateRequest(itemTransferModel);

            List<LNResponseMessageFormater> lnSucResponseMessageFormater = new ArrayList<>();
            Set<LNResponseMessageFormater> lnErResponseMessageFormater = new HashSet<>();

            transferResultMap = getTransferResultMap(itemTransferModel, transferType, expandLevel);

            if (transferResultMap.size() > 0) {

                transferResultMap.forEach((key, value) -> {

                    if (value.equalsIgnoreCase(LNTransferServiceImpl.SUCCESSFUL_ITEM_LIST)) {
                        if (!NullOrEmptyChecker.isNull(key.getItem())) {
                            lnSucResponseMessageFormater.add(key);
                        }

                    }
                    if (value.equalsIgnoreCase(LNTransferServiceImpl.FAILED_ITEM_LIST)) {
                        if (!NullOrEmptyChecker.isNull(key.getItem())) {
                            lnErResponseMessageFormater.add(key);
                        }

                    }
                    if (value.equalsIgnoreCase(LNTransferServiceImpl.SUCCESSFUL_BOM_LIST)) {
                        if (!NullOrEmptyChecker.isNull(key.getBom())) {
                            lnSucResponseMessageFormater.add(key);
                        }

                    }
                    if (value.equalsIgnoreCase(LNTransferServiceImpl.FAILED_BOM_LIST)) {
                        if (!NullOrEmptyChecker.isNull(key.getBom())) {
                            lnErResponseMessageFormater.add(key);
                        }

                    }

                });

            }
            if (lnSucResponseMessageFormater.size() > 0 && lnErResponseMessageFormater.size() > 0) {
                buildResponse = lnResponseBuilder.setLNData(lnSucResponseMessageFormater)
                        .setLNSource(source)
                        .addLNErrorMessage(lnErResponseMessageFormater)
                        .setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildLNResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else {
                if (lnErResponseMessageFormater.size() > 0) {
                    buildResponse = lnResponseBuilder.addLNErrorMessage(lnErResponseMessageFormater)
                            .setLNSource(source)
                            .setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                            .buildLNResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                } else if (lnSucResponseMessageFormater.size() > 0) {
                    buildResponse = lnResponseBuilder.setLNData(lnSucResponseMessageFormater)
                            .setLNSource(source)
                            .setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK)
                            .buildLNResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                }
            }
            buildResponse = lnResponseBuilder.setLNSource(source).setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK).buildLNResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            buildResponse = lnResponseBuilder.addLNErrorMessage(ex.getMessage()).setLNSource(source).setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildLNResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.BAD_REQUEST);
        } catch (ConnectException ex) {
            LOGGER.error(ex);
            buildResponse = lnResponseBuilder.addLNErrorMessage(ex.getMessage()).setLNSource(source).setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildLNResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            LOGGER.error(ex);
            buildResponse = lnResponseBuilder.addLNErrorMessage(ex.getMessage()).setLNSource(source).setLNStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildLNResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    Map<LNResponseMessageFormater, String> getTransferResultMap(LNTransferRequestModel itemTransferModel, String type, String expandLevel) throws Exception {

        LNTransferService lnTransferService = new LNTransferServiceImpl();
        Map<LNResponseMessageFormater, String> transferResultMap = new HashMap<>();

        if (type.equalsIgnoreCase("item")) {
            transferResultMap = lnTransferService.itemTransfer(itemTransferModel, type, expandLevel);
        } else {
            transferResultMap = lnTransferService.bomTransfer(itemTransferModel, type, expandLevel);

        }
        return transferResultMap;
    }

    @ResponseBody
    @GetMapping(value = "/ln/items/nightly", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> gtsNightlyUpdateToLN() {
        ILNResponse responseBuilder = new LNCustomResponseBuilder();
        String buildResponse = "";
        boolean isService = true;
        try {
            GTSNightlyUpdateTransferAction action = new GTSNightlyUpdateTransferAction(isService);
            return new ResponseEntity<>("Successfully transferred gts update to LN", HttpStatus.OK);
        } catch (Exception exp) {
            return new ResponseEntity<>("Error occured transferring one or more item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
