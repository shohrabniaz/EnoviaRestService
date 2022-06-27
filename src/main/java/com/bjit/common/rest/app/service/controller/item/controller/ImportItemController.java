/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.controller;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMImportFactoryProducer;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping(path = "/importItem")
public class ImportItemController {

    private static final org.apache.log4j.Logger IMPORT_ITEM_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ImportItemController.class);
    @Autowired
    CommonPropertyReader commonPropertyReader;

    /**
     *
     * End point for PDM 'Own design item' import as
     * CreateAssembly/ProcessContinuousCreateMaterial
     */
    @RequestMapping(value = "/createManItem", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity create(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MAN ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory MANFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_ODI);
            ItemOrBOMImport MANItemImport = MANFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM);

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = MANItemImport.doImport(context, objectDataBeanList);

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
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MAN ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/createMopazItem", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity createMopazItem(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MOPAZ ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemOrBOMCreateFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_CREATEASSEMBLY_PROCESS_CONT_CREATE_MAT);
            ItemOrBOMImport commonItemImportFactory = itemOrBOMCreateFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_COMMON_ITEM);

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = commonItemImportFactory.doImport(context, objectDataBeanList);

            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");
            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.PARTIAL)
                        .setData(successFulItemList)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setData(successFulItemList)
                        .buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else {
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MOPAZ ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    @RequestMapping(value = "/createProductConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createProductConfiguration(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT PRODUCT CONFIGURATION CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("#######################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_IMPORT);
            ItemOrBOMImport itemImport = itemFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM);

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
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT PRODUCT CONFIGURATION CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("#####################################################################################################################################\n");
        }
    }

    @RequestMapping(value = "/createConfigurationFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createConfigurationFeature(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("##################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_IMPORT);
            ItemOrBOMImport itemImport = itemFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM);

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
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("################################################################################################################################\n");
        }
    }
    
    @RequestMapping(value = "/createConfigurationOption", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createConfigurationOption(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("##################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_IMPORT);
            ItemOrBOMImport itemImport = itemFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM);

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
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT HARDWARE PRODUCT CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("################################################################################################################################\n");
        }
    }

    @RequestMapping(value = "/createValComponentItem", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createValItem(HttpServletRequest httpRequest, @RequestBody final List<CreateObjectBean> createObjectBeanList) {
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT VAL COMPONENT ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("####################################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory importFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.IMPORT_ITEM_TYPE_VAL_COMPONENT);
            ItemOrBOMImport VALItemImport = importFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM); //Vejal begins

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = VALItemImport.doImport(context, createObjectBeanList);

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
                throw new RuntimeException("Unknown excepiton occurred");
            }

            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT VAL COMPONENT ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    @RequestMapping(value = "/createValComponentMaterialItem", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createVALComponentMaterialItem(HttpServletRequest httpRequest, @RequestBody final List<CreateObjectBean> createObjectBeanList) {
        IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT VAL COMPONENT MATERIAL ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory importFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.IMPORT_ITEM_TYPE_VAL_COMPONENT_MATERIAL);
            ItemOrBOMImport VALItemImport = importFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM); //Vejal begins

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = VALItemImport.doImport(context, createObjectBeanList);

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
                throw new RuntimeException("Unknown exception occurred");
            }

            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT VAL COMPONENT MATERIAL ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/manufacture/v1/{itemType}/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity importItems(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList, @PathVariable String itemType) {
        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        try {
            ItemOrBOMAbstractFactory itemOrBOMCreateFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_CREATEASSEMBLY_PROCESS_CONT_CREATE_MAT);
            ItemOrBOMImport commonItemImportFactory = itemOrBOMCreateFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_COMMON_ITEM);

            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = commonItemImportFactory.doImport(context, objectDataBeanList);

            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");
            Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
            Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

            if (hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.PARTIAL)
                        .setData(successFulItemList)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else if (hasSuccessfulList && !hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setData(successFulItemList)
                        .buildResponse();
            } else if (!hasSuccessfulList && hasErrorList) {
                buildResponse = responseBuilder
                        .setStatus(Status.FAILED)
                        .addErrorMessage(errorItemList)
                        .buildResponse();
            } else {
                IMPORT_ITEM_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
                throw new RuntimeException("Unknown excepiton occurred");
            }

            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (NullPointerException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (RuntimeException exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            Date controllerEndTime = DateTimeUtils.getTime(new Date());
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for the object type 'createManItem' service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null));

            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

            IMPORT_ITEM_CONTROLLER_LOGGER.info("ODI Item Import Process has taken : '" + duration + "' milli-seconds");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("---------------------------------------- ||| IMPORT MOPAZ ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }
}
