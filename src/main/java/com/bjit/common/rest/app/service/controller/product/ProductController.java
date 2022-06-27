/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.product;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjit.common.rest.app.service.model.modelVersion.MVObjectDataBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVResponseMessageFormatterBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMImportFactoryProducer;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.validator.ProductAPIValidator;

import matrix.db.Context;

/**
 *
 * @author BJIT
 */
@Controller
@RequestMapping(path = "/item/product/v1")
public class ProductController {

    private static final org.apache.log4j.Logger PRODUCT_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ProductController.class);

    /**
     * This service endpoint is to import mass hardware product for create or
     * update and adding classification path to created hardware product
     *
     * @param httpRequest API http request which includes jwt token info
     * @param hPObjectDataBean This is data bean which holds api data
     * @return
     */
    @RequestMapping(value = "/hardware/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createHardwareProduct(HttpServletRequest httpRequest, @RequestBody final MVObjectDataBean hPObjectDataBean) {
        ProductAPIValidator validator = new ProductAPIValidator();
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        PRODUCT_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER BEGIN ||| ----------------------------------------");
        PRODUCT_CONTROLLER_LOGGER.info("##################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            String source = hPObjectDataBean.getSource().toLowerCase();
            ItemOrBOMAbstractFactory itemFactory = ItemOrBOMImportFactoryProducer.getFactory(source);
            ItemOrBOMImport itemImport = itemFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_HARDWARE_PRODUCT);

            HashMap<String, List<MVResponseMessageFormatterBean>> tnrListMap = itemImport.doImport(context, hPObjectDataBean);

            List<MVResponseMessageFormatterBean> successfulItemList = tnrListMap.get("successFullList");
            List<MVResponseMessageFormatterBean> errorItemList = tnrListMap.get("errorList");

            PRODUCT_CONTROLLER_LOGGER.info("Validating Hardware Product");

            validator.checkTheImportedItems(context, successfulItemList, errorItemList);

            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successfulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder.setData(successfulItemList).setStatus(Status.FAILED).addErrorMessage(errorItemList).buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder.setData(successfulItemList).setStatus(Status.OK).buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
            } else {
                PRODUCT_CONTROLLER_LOGGER.fatal("Unknown exception occurred");
                throw new RuntimeException("Unknown exception occurred");
            }
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            PRODUCT_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            PRODUCT_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            PRODUCT_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER END ||| ----------------------------------------");
            PRODUCT_CONTROLLER_LOGGER.info("################################################################################################################################\n");
        }
    }

    /**
     * This service endpoint is to import different types of product, Multiple
     * products of similar type can be import (create or update) in same
     * request, Model is created with the product and classification path is
     * also created alongside the product creation
     *
     * @param httpRequest API http request which includes jwt token info
     * @param objectDataBeanList This is data bean which holds api data
     * @param productType Defines which type of product is to be imported
     * @return
     */
    @RequestMapping(value = "/{productType}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createProduct(HttpServletRequest httpRequest, @RequestBody final MVObjectDataBean objectDataBeanList, @PathVariable String productType) {
        ProductAPIValidator validator = new ProductAPIValidator();
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        PRODUCT_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT PRODUCT CONTROLLER BEGIN ||| ----------------------------------------");
        PRODUCT_CONTROLLER_LOGGER.info("##################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            String source = objectDataBeanList.getSource().toLowerCase();
            ItemOrBOMAbstractFactory itemFactory = ItemOrBOMImportFactoryProducer.getFactory(source);
            if (itemFactory == null) {
                throw new RuntimeException("Provided Source '" + source + "' is not supported by the system");
            }
            ItemOrBOMImport itemImport = itemFactory.getImportType(productType.toUpperCase());
            if (itemImport == null) {
                throw new RuntimeException("Provided Type '" + productType + "' is not supported by the system");
            }
            if (!validator.isProductTypeAndSourceValid(productType, source)) {
                throw new RuntimeException("System could not recognize '" + productType + "' for source : '" + source + "'");
            }
            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = itemImport.doImport(context, objectDataBeanList);

            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");

            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.FAILED).addErrorMessage(errorItemList).buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
            } else {
                PRODUCT_CONTROLLER_LOGGER.fatal("Unknown exception occurred");
                throw new RuntimeException("Unknown exception occurred");
            }

            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            PRODUCT_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            PRODUCT_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            PRODUCT_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            PRODUCT_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT PRODUCT CONTROLLER END ||| ----------------------------------------");
            PRODUCT_CONTROLLER_LOGGER.info("################################################################################################################################\n");
        }
    }

}
