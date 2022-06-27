/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.controller;

import com.bjit.common.rest.app.service.model.createBOM.BOMStructure;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMImportFactoryProducer;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
@RequestMapping("/import/bom")
public class CommonBOMImportController {

    private static final org.apache.log4j.Logger IMPORT_BOM_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMImportController.class);

//    @Autowired
//    IBOMImportImpl bomImport;
//
//    @Autowired
//    IResponse responseBuilder;
    @PostMapping("/{source}/create")
    public ResponseEntity createBOM(HttpServletRequest httpRequest, @RequestBody final List<CreateBOMBean> createBOMBeanList, @PathVariable("source") String source) {
        Instant itemImportStartTime = Instant.now();
        IMPORT_BOM_CONTROLLER_LOGGER.debug("---------------------- ||| BOM IMPORT ||| ----------------------");
        IMPORT_BOM_CONTROLLER_LOGGER.debug("####################################################################");
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = "";
        final Context context = (Context) httpRequest.getAttribute("context");
        
        source = source.toLowerCase();
        BOMStructure bomStructureModel = new BOMStructure();
        bomStructureModel.setSource(source);
        bomStructureModel.setCreateBomBeanList(createBOMBeanList);

        try {
            ItemOrBOMAbstractFactory bomFactory = ItemOrBOMImportFactoryProducer.getFactory(source);
            ItemOrBOMImport commonBomImport = bomFactory.getImportType(source);

            HashMap<String, List<ParentInfo>> responseMsgMap = commonBomImport.doImport(context, bomStructureModel);

            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
            List<ParentInfo> errorItemList = responseMsgMap.get("Error");

            if (errorItemList != null && errorItemList.size() > 0) {
                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
            } else if (successFulItemList != null && successFulItemList.size() > 0) {
                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
            }

            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception exp) {
            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } finally {
            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
            IMPORT_BOM_CONTROLLER_LOGGER.info(" | Process Time | Total BOM Import | " + duration);
        }
    }
}
