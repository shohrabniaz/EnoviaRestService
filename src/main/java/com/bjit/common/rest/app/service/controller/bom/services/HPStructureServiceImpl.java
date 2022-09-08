/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.services;

import com.bjit.common.rest.app.service.controller.bom.expand.CommonBOMExpand;
import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.bom.model.ExpandedModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentChildRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.RootItem;
import com.bjit.common.rest.app.service.controller.bom.model.StructureModel;
import com.bjit.common.rest.app.service.controller.bom.processor.HPStructureProcessor;
import com.bjit.common.rest.app.service.controller.bom.serviceInterfaces.IProductStructureService;
import com.bjit.common.rest.app.service.controller.bom.utilities.HardwareProductItemCreateUtilities;
import com.bjit.common.rest.app.service.controller.bom.utilities.HardwareProductJPOUtilities;
import com.bjit.common.rest.app.service.controller.bom.validators.PCStructureValidator;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
//import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
//import org.springframework.stereotype.Service;

/**
 *
 * @author BJIT
 */
@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestScope
public class HPStructureServiceImpl implements IProductStructureService {

    private static final org.apache.log4j.Logger STRUCTURE_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(HPStructureServiceImpl.class);

//    @Autowired
//    HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities;

//    @Autowired
//    CommonBOMExpand commonBOMExpand;

//    @Autowired
//    HPStructureProcessor productStructureService;
//    @Autowired
//    PCStructureValidator productConfigurationStructureValidator;
    @Autowired
    BeanFactory beanFactory;

    IResponse responseBuilder;

//    String rootHardwareItemId;
    HashMap<String, String> rootHardwareItemIdMap = new HashMap<>();

    @Override
    public <T, K> K importStructure(Context context, T model, IResponse responseBuilder) {

//        IResponse responseBuilder = new CustomResponseBuilder();
        this.responseBuilder = responseBuilder;
        String buildResponse;
//        rootHardwareItemId = null;

        try {

            StructureModel structureModel = (StructureModel) model;

            List<RootItem> rootItemList = structureModel.getItem();

            HashMap<String, List<ResponseMessageFormaterBean>> responseMessage = getResponseMap();

            rootItemList.stream().parallel().forEach((RootItem rootItem) -> {

                HardwareProductJPOUtilities hardwareProductJPOUtilities = beanFactory.getBean(HardwareProductJPOUtilities.class);
                PCStructureValidator productConfigurationStructureValidator = beanFactory.getBean(PCStructureValidator.class);
                HPStructureProcessor productStructureService = beanFactory.getBean(HPStructureProcessor.class);
                HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities = beanFactory.getBean(HardwareProductItemCreateUtilities.class);
                CommonBOMExpand commonBOMExpand = beanFactory.getBean(CommonBOMExpand.class);

                productConfigurationStructureValidator.initiator(commonBOMExpand, hardwareProductJPOUtilities, hardwareProductItemCreateUtilities);

                ResponseMessageFormaterBean responseData = beanFactory.getBean("responseMessageFormatterBean", ResponseMessageFormaterBean.class);//.getBean(ResponseMessageFormaterBean.class);
                responseData.setTnr(rootItem.getTnr());

                try {
                    /*---------------------------------------- ||| Start Transaction for Importing Product Configuration Structure ||| ----------------------------------------*/
                    new CommonUtilities().doStartTransaction(context);

                    String rootItemId = prepareProductConfigurationStructure(productStructureService, rootItem, context, hardwareProductJPOUtilities, structureModel.getSource(), productConfigurationStructureValidator, hardwareProductItemCreateUtilities, commonBOMExpand);
//                    String rootItemId = prepareProductConfigurationStructure(productStructureService, rootItem, context, hardwareProductJPOUtilities, structureModel.getSource(), productConfigurationStructureValidator);
//                    String rootItemId = prepareProductConfigurationStructure(productStructureService, rootItem, context, hardwareProductJPOUtilities, structureModel.getSource());

                    /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
                    STRUCTURE_CONTROLLER_LOGGER.info("Committing Transaction");
                    ContextUtil.commitTransaction(context);
                    responseData.setObjectId(rootItemId);
                    responseMessage.get("successFullList").add(responseData);

                } catch (FrameworkException | RuntimeException | InterruptedException exp) {
                    /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
                    STRUCTURE_CONTROLLER_LOGGER.error("Aborting for Transaction");
                    ContextUtil.abortTransaction(context);
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    responseData.setErrorMessage(exp.getMessage());
                    responseMessage.get("errorList").add(responseData);
                }
            });

            buildResponse = prepareServiceResponse(responseMessage);

        } catch (Exception exp) {
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            STRUCTURE_CONTROLLER_LOGGER.error("Aborting for Transaction");
            ContextUtil.abortTransaction(context);

            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            STRUCTURE_CONTROLLER_LOGGER.debug(buildResponse);
            return (K) buildResponse;
        }

        return (K) buildResponse;
    }

    private String prepareServiceResponse(HashMap<String, List<ResponseMessageFormaterBean>> responseMessage) {
        List<ResponseMessageFormaterBean> successFulItemList = responseMessage.get("successFullList");
        List<ResponseMessageFormaterBean> errorItemList = responseMessage.get("errorList");
        Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successFulItemList);
        Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

        if (hasSuccessfulList && hasErrorList) {
            return responseBuilder.setData(successFulItemList).setStatus(Status.FAILED).addErrorMessage(errorItemList).buildResponse();
        } else if (hasSuccessfulList && !hasErrorList) {
            return responseBuilder.setData(successFulItemList).setStatus(Status.OK).buildResponse();
        } else if (!hasSuccessfulList && hasErrorList) {
            return responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
        } else {
            STRUCTURE_CONTROLLER_LOGGER.fatal("Unknown excepiton occurred");
            throw new RuntimeException("Unknown excepiton occurred");
        }
    }

    private HashMap<String, List<ResponseMessageFormaterBean>> getResponseMap() {
        List<ResponseMessageFormaterBean> tnrSuccessfullList = new ArrayList<>();
        List<ResponseMessageFormaterBean> tnrErrorList = new ArrayList<>();
        HashMap<String, List<ResponseMessageFormaterBean>> responseMessage = new HashMap<>();
        responseMessage.put("successFullList", tnrSuccessfullList);
        responseMessage.put("errorList", tnrErrorList);
        return responseMessage;
    }

    private String prepareProductConfigurationStructure(HPStructureProcessor productStructureService, RootItem rootItem, final Context context, HardwareProductJPOUtilities hardwareProductJPOUtilities, final String source, PCStructureValidator productConfigurationStructureValidator, HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities, CommonBOMExpand commonBOMExpand) {
//    private String prepareProductConfigurationStructure(HPStructureProcessor productStructureService, RootItem rootItem, final Context context, HardwareProductJPOUtilities hardwareProductJPOUtilities, final String source, PCStructureValidator productConfigurationStructureValidator) {
//    private String prepareProductConfigurationStructure(HPStructureProcessor productStructureService, RootItem rootItem, final Context context, HardwareProductJPOUtilities hardwareProductJPOUtilities, final String source) {
        ParentChildRelationshipModel parentChildRelationshipModel = productStructureService.prepareHardwareProductRelationshipMap();
        HashMap<String, List<ParentRelationshipModel>> childParentRelationship = parentChildRelationshipModel.getParentChildRelationshipModel();

        StringJoiner parentRelationshipJoiner = new StringJoiner(",");
        StringJoiner childTypeJoiner = getChildTypeJoiner(childParentRelationship, parentRelationshipJoiner);

        HashMap<String, String> productConfigurationIdList = new HashMap();
        String rootItemKey = "";
        try {
            TNR parentItem = rootItem.getTnr();
            String rootId = getRootItem(context, parentItem, rootItem, productConfigurationStructureValidator, hardwareProductItemCreateUtilities);
//            String rootId = getRootItem(context, parentItem, rootItem);

            rootItem.setId(rootId);

            if (parentItem.getType().equalsIgnoreCase("Hardware Product")) {
                rootItemKey = parentItem.getType() + '_' + parentItem.getName() + '_' + parentItem.getRevision();
                rootHardwareItemIdMap.put(rootItemKey, rootId);
//                rootHardwareItemId = rootId;
            }

            List<ChildItem> lines = rootItem.getLines();
            productConfigurationStructureValidator.validateAndImportChildItems(context, lines, rootItem, source, childParentRelationship, childTypeJoiner);

            List<ExpandedModel> expandedList;
            try {
                expandedList = commonBOMExpand.expandItem(context, rootId, parentRelationshipJoiner.toString(), childTypeJoiner.toString(), (short) 2);
            } catch (Exception exp) {
                STRUCTURE_CONTROLLER_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }

            HashMap<String, ExpandedModel> expandedMap = new HashMap<>();
            expandedList.stream().forEach(expandedIdtem -> expandedMap.put(expandedIdtem.getItemId(), expandedIdtem));

            HashMap<String, ChildItem> childItemMap = new HashMap<>();
            lines.stream().forEach((ChildItem lineItem) -> {
                childItemMap.put(lineItem.getId(), lineItem);
                Optional.ofNullable(lineItem.getSelected()).ifPresent(selectedOption -> childItemMap.put(selectedOption.getId(), selectedOption));
            });

            final String structureRootId = rootId;

            prepareParentChildRelationship(context, lines, structureRootId, expandedMap, productConfigurationIdList, productStructureService, productConfigurationStructureValidator);
//            prepareParentChildRelationship(context, lines, structureRootId, expandedMap, productConfigurationIdList, productStructureService);

            final String rootItemId = rootHardwareItemIdMap.get(rootItemKey);
            Optional.ofNullable(rootItemId).orElseThrow(() -> new NullPointerException("Root Hardware Item not found"));
            Optional.ofNullable(productConfigurationIdList).orElseThrow(() -> new NullPointerException("No Configuration Feature or Configuration Options found for Product Configuration"));

            productConfigurationIdList.keySet().stream().parallel().forEach((String productConfigurationId) -> {
                try {
                    hardwareProductJPOUtilities.callProductConfigJPOMethod(context, rootItemId, productConfigurationId, Optional.ofNullable(productConfigurationIdList.get(productConfigurationId)).orElse("Default Marketing Name"));
                } catch (MatrixException exp) {
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    throw new RuntimeException(exp);
                }
            });

            ResponseMessageFormaterBean successfullImport = beanFactory.getBean(ResponseMessageFormaterBean.class);
            successfullImport.setTnr(parentItem);
            successfullImport.setObjectId(rootId);

            return rootId;

        } catch (RuntimeException exp) {
            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            throw exp;
        }
    }

    private void prepareParentChildRelationship(Context context, List<ChildItem> lines, final String structureRootId, HashMap<String, ExpandedModel> expandedMap, HashMap<String, String> productConfigurationIdList, HPStructureProcessor productStructureService, PCStructureValidator productConfigurationStructureValidator) {
//    private void prepareParentChildRelationship(Context context, List<ChildItem> lines, final String structureRootId, HashMap<String, ExpandedModel> expandedMap, HashMap<String, String> productConfigurationIdList, HPStructureProcessor productStructureService) {
        lines.stream().forEach((ChildItem childItem) -> {
            try {
                productConfigurationStructureValidator.checkChildItemsInExpandedList(context, childItem, structureRootId, expandedMap, productConfigurationIdList, productStructureService);

                Optional.ofNullable(childItem.getChildItems()).ifPresent((List<ChildItem> childList) -> {
                    childList.parallelStream().forEach((ChildItem grandChild) -> {
                        if (!Optional.ofNullable(expandedMap.get(grandChild.getId())).isPresent()) {
                            try {
                                String mqlQuery = "add connection 'Configuration Options' from " + childItem.getId() + " to " + grandChild.getId() + " select id dump";
                                STRUCTURE_CONTROLLER_LOGGER.info("Connection query : " + mqlQuery);
                                String queryResult = MqlUtil.mqlCommand(context, mqlQuery);
                            } catch (FrameworkException exp) {
                                STRUCTURE_CONTROLLER_LOGGER.error(exp);
                                throw new RuntimeException(exp);
                            }
                        }
                    });
                });

            } catch (FrameworkException exp) {
                STRUCTURE_CONTROLLER_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }

            Optional.ofNullable(childItem.getSelected()).ifPresent((ChildItem selectedOption) -> {
                try {
                    productConfigurationStructureValidator.checkChildItemsInExpandedList(context, selectedOption, childItem.getId(), expandedMap, productConfigurationIdList, productStructureService);
                } catch (FrameworkException exp) {
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    throw new RuntimeException(exp);
                }
            });
        });
    }

    private String getRootItem(Context context, TNR parentItem, RootItem rootItem, PCStructureValidator productConfigurationStructureValidator, HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities) throws RuntimeException {
//    private String getRootItem(Context context, TNR parentItem, RootItem rootItem, PCStructureValidator productConfigurationStructureValidator) throws RuntimeException {
//    private String getRootItem(Context context, TNR parentItem, RootItem rootItem) throws RuntimeException {
        String rootId = null;
        try {
            rootId = productConfigurationStructureValidator.searchItem(context, parentItem).get(0).get("id");
        } catch (NullPointerException exp) {
            try {
                rootId = getHardwareProduct(rootItem, exp, parentItem, context, hardwareProductItemCreateUtilities);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception exp) {
            try {
                rootId = getHardwareProduct(rootItem, exp, parentItem, context, hardwareProductItemCreateUtilities);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return rootId;
    }

    private String getHardwareProduct(RootItem rootItem, Exception exp, TNR parentItem, Context context, HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities) throws Exception {
//    private String getHardwareProduct(RootItem rootItem, Exception exp, TNR parentItem, Context context) throws Exception {
        String rootId;
        Optional.ofNullable(rootItem.getAttributes()).orElseThrow(() -> new RuntimeException(exp));
        CreateObjectBean CreateObjectBean = new CreateObjectBean(parentItem, rootItem.getAttributes());
        rootId = hardwareProductItemCreateUtilities.createHPItem(CreateObjectBean, context);
        return rootId;
    }

    private StringJoiner getChildTypeJoiner(HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner parentRelationshipJoiner) {
        StringJoiner childTypeJoiner = new StringJoiner(",");
        childParentRelationship
                .entrySet()
                .stream()
                .forEach(entry -> childParentRelationship.get(entry.getKey())
                .stream()
                .forEach(relationship -> parentRelationshipJoiner.add(relationship.getRelationName())));
        return childTypeJoiner;
    }
}
