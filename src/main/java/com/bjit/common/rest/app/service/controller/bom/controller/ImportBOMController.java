/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.controller;

import com.bjit.common.rest.app.service.controller.bom.processor.HPStructureProcessor;
import com.bjit.common.rest.app.service.controller.bom.model.ParentChildRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.bom.model.ExpandedModel;
import com.bjit.common.rest.app.service.controller.bom.model.RootItem;
import com.bjit.common.rest.app.service.controller.bom.model.StructureModel;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.structure.ItemStructure;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory;
import com.bjit.common.rest.item_bom_import.factories.ItemOrBOMImportFactoryProducer;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.import_threads.ItemImportProcess;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Sajjad
 */
@Controller
@RequestMapping(path = "/importBom")
public class ImportBOMController {

    String rootHardwareItemId;
    private static final org.apache.log4j.Logger IMPORT_BOM_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ImportBOMController.class);

    @RequestMapping(value = "/createManBom", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> createBOM(HttpServletRequest httpRequest, @RequestBody final List<CreateBOMBean> createBOMBeanList) {
        Instant itemImportStartTime = Instant.now();
        IMPORT_BOM_CONTROLLER_LOGGER.debug("---------------------- ||| MAN BOM IMPORT ||| ----------------------");
        IMPORT_BOM_CONTROLLER_LOGGER.debug("####################################################################");
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = "";
        final Context context = (Context) httpRequest.getAttribute("context");

        try {
            ItemOrBOMAbstractFactory MANFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_ODI);
            ItemOrBOMImport MAN_BOMImport = MANFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_BOM);

            HashMap<String, List<ParentInfo>> responseMsgMap = MAN_BOMImport.doImport(context, createBOMBeanList);

            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
            List<ParentInfo> errorItemList = responseMsgMap.get("Error");

            List<ParentInfo> uniqueerrorItemList = errorItemList;
            if (!NullOrEmptyChecker.isNullOrEmpty(errorItemList)) {
                uniqueerrorItemList = errorItemList.stream().distinct().collect(Collectors.toList());

                uniqueerrorItemList.stream().forEach((ParentInfo parentInfo) -> {
                    parentInfo.setErrorMessage(String.join(" | ", parentInfo.getErrorMessages()));
                    parentInfo.setErrorMessages(null);
                });
            }

            if ((successFulItemList != null && successFulItemList.size() > 0) && (errorItemList != null && errorItemList.size() > 0)) {
                buildResponse = responseBuilder.setData(successFulItemList).addErrorMessage(uniqueerrorItemList).setStatus(Status.FAILED).buildResponse();
            } else if (errorItemList != null && errorItemList.size() > 0) {
                buildResponse = responseBuilder.addErrorMessage(uniqueerrorItemList).setStatus(Status.FAILED).buildResponse();
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

//    @RequestMapping(value = "/createStructure", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//    @ResponseBody
//    public ResponseEntity<?> createStructure(HttpServletRequest httpRequest, @RequestBody final ItemStructure itemStructure) {
//        Instant itemImportStartTime = Instant.now();
//        IMPORT_BOM_CONTROLLER_LOGGER.debug("---------------------- ||| MAN BOM IMPORT ||| ----------------------");
//        IMPORT_BOM_CONTROLLER_LOGGER.debug("####################################################################");
//        IResponse responseBuilder = new CustomResponseBuilder();
//        String buildResponse = "";
//        final Context context = (Context) httpRequest.getAttribute("context");
//
//        try {
//            ItemOrBOMAbstractFactory MANFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_ODI);
//            ItemOrBOMImport importStructure = MANFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_STRUCTURE);
//
//            HashMap<String, List<ParentInfo>> responseMsgMap = importStructure.doImport(context, itemStructure);
//
//            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
//            List<ParentInfo> errorItemList = responseMsgMap.get("Error");
//
//            if (errorItemList != null && errorItemList.size() > 0) {
//                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
//            } else if (successFulItemList != null && successFulItemList.size() > 0) {
//                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
//            }
//
//            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        } catch (Exception exp) {
//            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        } finally {
//            Instant itemImportEndTime = Instant.now();
//            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
//            IMPORT_BOM_CONTROLLER_LOGGER.info("BOM Import Process has taken : '" + duration + "' milli-seconds");
//        }
//    }
//
//    @RequestMapping(value = "/importPCStructure", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//    @ResponseBody
//    public ResponseEntity<?> importPCStructure(HttpServletRequest httpRequest, @RequestBody final ItemStructure itemStructure) {
//        Instant itemImportStartTime = Instant.now();
//        IMPORT_BOM_CONTROLLER_LOGGER.debug("---------------------- ||| MAN BOM IMPORT ||| ----------------------");
//        IMPORT_BOM_CONTROLLER_LOGGER.debug("####################################################################");
//        IResponse responseBuilder = new CustomResponseBuilder();
//        String buildResponse = "";
//        final Context context = (Context) httpRequest.getAttribute("context");
//
//        try {
//
////            ItemOrBOMAbstractFactory MANFactory = ItemOrBOMImportFactoryProducer.getFactory(ItemOrBOMImportFactoryProducer.ITEM_TYPE_ODI);
////            ItemOrBOMImport importStructure = MANFactory.getImportType(ItemOrBOMAbstractFactory.IMPORT_TYPE_STRUCTURE);
////
////
////            HashMap<String, List<ParentInfo>> responseMsgMap = importStructure.doImport(context, itemStructure);
////
////
////            List<ParentInfo> successFulItemList = responseMsgMap.get("Successful");
////            List<ParentInfo> errorItemList = responseMsgMap.get("Error");
////
////            if (errorItemList != null && errorItemList.size() > 0) {
////                buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
////            } else if (successFulItemList != null && successFulItemList.size() > 0) {
////                buildResponse = responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
////            }
//            HPStructureProcessor importPCStructureProcessor = new HPStructureProcessor();
//            ParentChildRelationshipModel parentChildRelationshipModel = importPCStructureProcessor.prepareHardwareProductRelationshipMap();
//
//            String sourceEnvironment = itemStructure.getSource();
//            List<CreateBOMBean> strucuteList = itemStructure.getStrucuteList();
//
//            try {
//                ContextUtil.startTransaction(context, true);
//                strucuteList.forEach((CreateBOMBean structure) -> {
//                    try {
//
//                        importPCStructureProcessor.importStructure(context, parentChildRelationshipModel, structure);
//                    } catch (Exception exp) {
//                        IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                        throw new RuntimeException(exp);
//                    }
//                });
//                ContextUtil.commitTransaction(context);
//                buildResponse = responseBuilder.setData(strucuteList.get(0).getItem()).setStatus(Status.OK).buildResponse();
//            } catch (Exception exp) {
//                IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                ContextUtil.abortTransaction(context);
//                throw new RuntimeException(exp);
//            }
//
//            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        } catch (Exception exp) {
//            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        } finally {
//            Instant itemImportEndTime = Instant.now();
//            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
//            IMPORT_BOM_CONTROLLER_LOGGER.info("BOM Import Process has taken : '" + duration + "' milli-seconds");
//        }
//    }
//
//    @RequestMapping(value = "/importProductConfigurationStructure", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//    @ResponseBody
//    public ResponseEntity<?> importProductConfigurationStructure(HttpServletRequest httpRequest, @RequestBody final ItemStructure itemStructure, @RequestParam Optional<String> createPCIfNotExits) {
//
//        IResponse responseBuilder = new CustomResponseBuilder();
//        String buildResponse = "";
//        rootHardwareItemId = null;
//        HPStructureProcessor importPCStructureProcessor = new HPStructureProcessor();
//        Boolean createProductConfigurationIfNotExits = Boolean.parseBoolean(createPCIfNotExits.orElse("false"));
//
//        try {
//            final Context context = (Context) httpRequest.getAttribute("context");
//
//            try {
//
//                /*---------------------------------------- ||| Start Transaction for Importing Product Configuration Structure ||| ----------------------------------------*/
//                IMPORT_BOM_CONTROLLER_LOGGER.debug("Starting Transaction");
//                ContextUtil.startTransaction(context, true);
//
//                StructureModel structureModel = importPCStructureProcessor.convertItemStructureToStructureModel(itemStructure);
//                List<RootItem> rootItemList = structureModel.getItem();
//
//                ParentChildRelationshipModel parentChildRelationshipModel = importPCStructureProcessor.prepareHardwareProductRelationshipMap();
//                HashMap<String, List<ParentRelationshipModel>> childParentRelationship = parentChildRelationshipModel.getParentChildRelationshipModel();
//
//                StringJoiner parentRelationshipJoiner = new StringJoiner(",");
//                StringJoiner childTypeJoiner = new StringJoiner(",");
//                childParentRelationship
//                        .entrySet()
//                        .stream()
//                        .forEach(entry -> childParentRelationship.get(entry.getKey())
//                        .stream()
//                        .forEach(relationship -> parentRelationshipJoiner.add(relationship.getRelationName())));
//
//                HashMap<String, String> productConfigurationIdList = new HashMap();
//
//                BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
//                rootItemList.stream().forEach((RootItem rootItem) -> {
//
//                    TNR parentItem = rootItem.getTnr();
//                    ArrayList<BusinessObject> searchRootItem;
//                    try {
//                        searchRootItem = businessObjectUtil.findBO(context, parentItem);
//                    } catch (MatrixException exp) {
//                        IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                        throw new RuntimeException(exp);
//                    }
//
//                    if (!searchRootItem.isEmpty()) {
//                        BusinessObject rootBusinessObject = searchRootItem.get(0);
//                        String rootId = rootBusinessObject.getObjectId();
//                        rootItem.setId(rootId);
//
//                        if (parentItem.getType().equalsIgnoreCase("Hardware Product")) {
//                            rootHardwareItemId = rootId;
//                        }
//                        //HashMap<String, String> childIdMap = new HashMap<>();
//
//                        List<ChildItem> lines = rootItem.getLines();
//                        lines.stream().forEach((ChildItem childItem) -> {
//
//                            TNR childTnr = childItem.getTnr();
//                            ArrayList<BusinessObject> searchChildItem;
//                            try {
//                                searchChildItem = businessObjectUtil.findBO(context, childTnr);
//                            } catch (MatrixException exp) {
//                                IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                                throw new RuntimeException(exp);
//                            }
//
//                            if (!searchChildItem.isEmpty()) {
//
//                                BusinessObject childBusinessObject = searchChildItem.get(0);
//                                String childId = childBusinessObject.getObjectId();
//                                childItem.setId(childId);
//                                //childIdMap.put(childTnr.getType() + childTnr.getName() + childTnr.getRevision(), childId);
//
//                                List<ParentRelationshipModel> parentRelationList = null;
//
//                                if (childParentRelationship.containsKey(childItem.getTnr().getType())) {
//
//                                    String rootItemType = rootItem.getTnr().getType();
//                                    String childType = childItem.getTnr().getType();
//                                    childTypeJoiner.add(childType);
//
//                                    parentRelationList = childParentRelationship.get(childType);
//
//                                    if (!parentRelationList.stream().anyMatch(parentRelation -> parentRelation.getParentType().equalsIgnoreCase(rootItemType))) {
//                                        throw new RuntimeException("Making relationship between '" + rootItemType + "' and '" + childType + "' is not possible");
//                                    }
//                                }
//                            } else {
//                                if (createProductConfigurationIfNotExits) {
//                                    if (childTnr.getType().equalsIgnoreCase("Product Configuration")) {
//
//                                        HashMap<String, String> attributes = new HashMap<>();
//                                        attributes.put("Configuration Selection Type", "Single");
//                                        attributes.put("Display Name", "Automatically Generated");
//                                        attributes.put("project", "GLOBAL_COMPONENTS_INTERNAL");
//                                        attributes.put("organization", "VALMET_INTERNAL");
//
//                                        CreateObjectBean pcItemCreateModel = new CreateObjectBean(childTnr, attributes, structureModel.getSource());
//                                        ResponseMessageFormaterBean responseMessageFormaterBean = new ResponseMessageFormaterBean();
//                                        ItemImportProcess itemImportProcess = new ItemImportProcess(context, pcItemCreateModel, responseMessageFormaterBean, structureModel.getSource());
//                                        try {
//                                            String newPCObjectId = itemImportProcess.processItem(context, pcItemCreateModel, responseMessageFormaterBean);
//
//                                            childItem.setId(newPCObjectId);
//
//                                            List<ParentRelationshipModel> parentRelationList = null;
//
//                                            if (childParentRelationship.containsKey(childItem.getTnr().getType())) {
//
//                                                String rootItemType = rootItem.getTnr().getType();
//                                                String childType = childItem.getTnr().getType();
//                                                childTypeJoiner.add(childType);
//
//                                                parentRelationList = childParentRelationship.get(childType);
//
//                                                if (!parentRelationList.stream().anyMatch(parentRelation -> parentRelation.getParentType().equalsIgnoreCase(rootItemType))) {
//                                                    throw new RuntimeException("Making relationship between '" + rootItemType + "' and '" + childType + "' is not possible");
//                                                }
//                                            }
//
//                                        } catch (RuntimeException exp) {
//                                            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                                            throw new RuntimeException(exp);
//                                        } catch (Exception exp) {
//                                            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                                            throw new RuntimeException(exp);
//                                        }
//                                    } else {
//                                        IMPORT_BOM_CONTROLLER_LOGGER.error("Item not found");
//                                        throw new RuntimeException("Item not found");
//                                    }
//                                } else {
//                                    IMPORT_BOM_CONTROLLER_LOGGER.error("Item not found");
//                                    throw new RuntimeException("Item not found");
//                                }
//
//                            }
//                        });
//
//                        List<ExpandedModel> expandedList;
//                        try {
//                            expandedList = importPCStructureProcessor.expandItem(context, rootId, parentRelationshipJoiner.toString(), childTypeJoiner.toString(), (short) 1);
//                        } catch (Exception exp) {
//                            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                            throw new RuntimeException(exp);
//                        }
//
//                        HashMap<String, ExpandedModel> expandedMap = new HashMap<>();
//                        expandedList.stream().forEach(expandedIdtem -> expandedMap.put(expandedIdtem.getItemId(), expandedIdtem));
//
//                        HashMap<String, ChildItem> childItemMap = new HashMap<>();
//                        lines.stream().forEach(childItem -> childItemMap.put(childItem.getId(), childItem));
//
//                        lines.stream().forEach((ChildItem childItem) -> {
//
//                            if (!expandedMap.containsKey(childItem.getTnr())) {
//                                if (childItem.getTnr().getType().equalsIgnoreCase("Product Configuration")) {
//                                    productConfigurationIdList.put(childItem.getId(), childItem.getAttributes().get("marketingName"));
//                                } else {
//                                    /**
//                                     * Connect with parent item method(RootItem,
//                                     * ChildItem, ParentChildRelationshipMap)
//                                     */
//                                }
//                            } else {
//                                if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Feature") || childItem.getTnr().getType().equalsIgnoreCase("Configuration Option")) {
//                                    importPCStructureProcessor.prepareProductConfigurationJPOMap(expandedMap.get(childItem.getId()).getRelationshipId(), childItem.getAttributes());
//                                }
//                            }
//                        });
//                    } else {
//                        IMPORT_BOM_CONTROLLER_LOGGER.error("Item not found");
//                        throw new RuntimeException("Item not found");
//                    }
//                });
//
//                Optional.ofNullable(rootHardwareItemId).orElseThrow(() -> new NullPointerException("Root Hardware Item not found"));
//                Optional.ofNullable(productConfigurationIdList).orElseThrow(() -> new NullPointerException("No Configuration Feature or Configuration Options found for Product Configuration"));
//
//                productConfigurationIdList.keySet().stream().parallel().forEach((String productConfigurationId) -> {
//                    try {
//                        importPCStructureProcessor.callProductConfigJPOMethod(context, rootHardwareItemId, productConfigurationId, Optional.ofNullable(productConfigurationIdList.get(productConfigurationId)).orElse("Default Marketing Name"));
//
//                        /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
//                        IMPORT_BOM_CONTROLLER_LOGGER.info("Committing Transaction");
//                        ContextUtil.commitTransaction(context);
//                    } catch (MatrixException exp) {
//                        IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                        throw new RuntimeException(exp);
//                    }
//                });
//
//                buildResponse = responseBuilder.setData("Structure Imported Successfully").setStatus(Status.OK).buildResponse();
//
//            } catch (FrameworkException exp) {
//                /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//                IMPORT_BOM_CONTROLLER_LOGGER.error("Aborting for Transaction");
//                ContextUtil.abortTransaction(context);
//
//                IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//                IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//            } catch (Exception exp) {
//                /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//                IMPORT_BOM_CONTROLLER_LOGGER.error("Aborting for Transaction");
//                ContextUtil.abortTransaction(context);
//
//                IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//                IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//            }
//
//        } catch (Exception exp) {
//            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
//            IMPORT_BOM_CONTROLLER_LOGGER.error(exp);
//            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
//            IMPORT_BOM_CONTROLLER_LOGGER.debug(buildResponse);
//            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
//    }
}
