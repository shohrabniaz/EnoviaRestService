/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.validators;

import com.bjit.common.rest.app.service.controller.bom.expand.CommonBOMExpand;
import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.bom.model.ExpandedModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.RootItem;
import com.bjit.common.rest.app.service.controller.bom.processor.HPStructureProcessor;
import com.bjit.common.rest.app.service.controller.bom.utilities.HardwareProductItemCreateUtilities;
import com.bjit.common.rest.app.service.controller.bom.utilities.HardwareProductJPOUtilities;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import matrix.db.Context;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */
@Component
//@RequestScope
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PCStructureValidator {

    private static final org.apache.log4j.Logger STRUCTURE_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(PCStructureValidator.class);
    private CommonBOMExpand commonBOMExpand;
    private HardwareProductJPOUtilities hardwareProductJPOUtilities;
    private HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities;

    public void initiator(CommonBOMExpand commonBOMExpand, HardwareProductJPOUtilities hardwareProductJPOUtilities, HardwareProductItemCreateUtilities hardwareProductItemCreateUtilities) {
        this.commonBOMExpand = commonBOMExpand;
        this.hardwareProductJPOUtilities = hardwareProductJPOUtilities;
        this.hardwareProductItemCreateUtilities = hardwareProductItemCreateUtilities;
    }

    public void validateAndImportChildItems(Context context, List<ChildItem> lines, RootItem rootItem, String source, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) {
        lines.stream().forEach((ChildItem childItem) -> {

            TNR childTnr = childItem.getTnr();
            Boolean itemExistsInTheSystem = validateChildItemsExistenceAndRelationship(context, rootItem.getTnr(), childItem, childParentRelationship, childTypeJoiner);
            try {
                prepareParentChildRelationship(context, rootItem.getTnr(), childItem, source, childParentRelationship, childTypeJoiner);
            } catch (Exception ex) {
                STRUCTURE_CONTROLLER_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }

            Optional.ofNullable(childItem.getChildItems()).ifPresent((List<ChildItem> childList) -> {
                childList.parallelStream().forEach((ChildItem grandChild) -> {
                    Boolean childOptions = validateChildItemsExistenceAndRelationship(context, childItem.getTnr(), grandChild, childParentRelationship, childTypeJoiner);
                    try {
                        prepareParentChildRelationship(context, childItem.getTnr(), grandChild, source, childParentRelationship, childTypeJoiner);
                    } catch (Exception exp) {
                        STRUCTURE_CONTROLLER_LOGGER.error(exp);
                        throw new RuntimeException(exp);
                    }
                });
            });

            Optional.ofNullable(childItem.getSelected()).ifPresent((ChildItem selectedOption) -> {
                Boolean selectedItemExistsInTheSystem = validateChildItemsExistenceAndRelationship(context, childItem.getTnr(), selectedOption, childParentRelationship, childTypeJoiner);
                try {
                    prepareParentChildRelationship(context, childItem.getTnr(), childItem.getSelected(), source, childParentRelationship, childTypeJoiner);
                } catch (Exception ex) {
                    STRUCTURE_CONTROLLER_LOGGER.error(ex);
                    throw new RuntimeException(ex);
                }
            });
        });
    }

    private Boolean validateChildItemsExistenceAndRelationship(Context context, TNR parentTnr, ChildItem childItem, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) throws RuntimeException {
        TNR childTnr = childItem.getTnr();
        HashMap<String, String> searchChildItem;
        try {
            searchChildItem = searchItem(context, childTnr).get(0);
        } catch (NullPointerException exp) {
            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            return false;
        } catch (Exception exp) {
            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            return false;
        }

        String childId = searchChildItem.get("id");
        childItem.setId(childId);

        List<ParentRelationshipModel> parentChildRelationshipModel;

        if (childParentRelationship.containsKey(childTnr.getType())) {

            String parentItemType = parentTnr.getType();
            String childItemType = childTnr.getType();

            childTypeJoiner.add(childItemType);

            parentChildRelationshipModel = childParentRelationship.get(childItemType);

            if (!parentChildRelationshipModel.stream().anyMatch(parentChildRelationship -> parentChildRelationship.getParentType().equalsIgnoreCase(parentItemType))) {
                throw new RuntimeException("Making relationship between '" + parentItemType + "' and '" + childItemType + "' is not possible");
            }
        }

        return true;
    }

    public HashMap<String, String> checkChildItemsInExpandedList(Context context, ChildItem childItem, String parentId, HashMap<String, ExpandedModel> expandedMap, HashMap<String, String> productConfigurationIdList, HPStructureProcessor productStructureService) throws FrameworkException {
        if (!expandedMap.containsKey(childItem.getId())) {
            if (childItem.getTnr().getType().equalsIgnoreCase("Product Configuration")) {
                productConfigurationIdList.put(childItem.getId(), Optional.ofNullable(childItem.getAttributes().get("Marketing Name")).orElseThrow(() -> new NullPointerException("Marketing Name should not be null")));
            } else {
                String mqlQuery = "";

                if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Feature")) {
                    STRUCTURE_CONTROLLER_LOGGER.info("Configuration Features " + childItem.getTnr().toString());
                    mqlQuery = "add connection 'Configuration Features' from " + parentId + " to " + childItem.getId() + " select id dump";
                } else if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Option")) {

                    List<ExpandedModel> expandedList;
                    try {
                        expandedList = commonBOMExpand.expandItem(context, parentId, "Configuration Options", "Configuration Option", (short) 1);
                        expandedList.stream().forEach(expandedIdtem -> expandedMap.put(expandedIdtem.getItemId(), expandedIdtem));
                    } catch (Exception exp) {
                        STRUCTURE_CONTROLLER_LOGGER.error(exp);
                        throw new RuntimeException(exp);
                    }

                    STRUCTURE_CONTROLLER_LOGGER.info("Configuration Options " + childItem.getTnr().toString());
                    mqlQuery = "add connection 'Configuration Options' from " + parentId + " to " + childItem.getId() + " select id dump";
                }

                try {

                    if (childItem.getTnr().getType().equalsIgnoreCase("Product Configuration")) {
                        String get = childItem.getAttributes().get("Marketing Name");
                        childItem.getAttributes().clear();
                        childItem.getAttributes().put("marketingName", get);
                    } else {
                        String get = childItem.getAttributes().get("Display Name");
                        childItem.getAttributes().clear();
                        childItem.getAttributes().put("Display Name", get);
                    }

                    if (!expandedMap.containsKey(childItem.getId())) {
                        STRUCTURE_CONTROLLER_LOGGER.info("Connection query : " + mqlQuery);
                        String queryResult = MqlUtil.mqlCommand(context, mqlQuery);

                        STRUCTURE_CONTROLLER_LOGGER.info("Result returned from server : " + queryResult);
                        hardwareProductJPOUtilities.prepareProductConfigurationJPOMap(queryResult, childItem.getAttributes());
                    } else {
                        hardwareProductJPOUtilities.prepareProductConfigurationJPOMap(expandedMap.get(childItem.getId()).getRelationshipId(), childItem.getAttributes());
                    }
                } catch (FrameworkException exp) {
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    throw exp;
                } catch (Exception exp) {
                    STRUCTURE_CONTROLLER_LOGGER.error(exp);
                    throw exp;
                }

            }
        } else {
            if (childItem.getTnr().getType().equalsIgnoreCase("Configuration Feature") || childItem.getTnr().getType().equalsIgnoreCase("Configuration Option")) {
                hardwareProductJPOUtilities.prepareProductConfigurationJPOMap(expandedMap.get(childItem.getId()).getRelationshipId(), childItem.getAttributes());
            }
        }

        return productConfigurationIdList;
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR parentItem) throws NullPointerException {
        List<HashMap<String, String>> searchItem = null;
        try {
            CommonSearch commonSearch = new CommonSearch();
            HashMap<String, String> whereClause = new HashMap<>();
            whereClause.put("type", parentItem.getType());
            searchItem = commonSearch.searchItem(context, parentItem, whereClause);
        } catch (Exception exp) {
            STRUCTURE_CONTROLLER_LOGGER.error(exp);
            throw new NullPointerException("Root itme " + parentItem.toString() + " not found in the system");
        }
        return searchItem;
    }

    private void prepareParentChildRelationship(Context context, TNR rootTnr, ChildItem childItem, String source, HashMap<String, List<ParentRelationshipModel>> childParentRelationship, StringJoiner childTypeJoiner) throws Exception {
        try {
            String objectId = hardwareProductItemCreateUtilities.createItem(childItem, source, context);
            childItem.setId(objectId);

            List<ParentRelationshipModel> parentRelationList;

            if (childParentRelationship.containsKey(childItem.getTnr().getType())) {

                String rootItemType = rootTnr.getType();
                String childType = childItem.getTnr().getType();
                childTypeJoiner.add(childType);

                parentRelationList = childParentRelationship.get(childType);

                if (!parentRelationList.stream().anyMatch(parentRelation -> parentRelation.getParentType().equalsIgnoreCase(rootItemType))) {
                    throw new RuntimeException("Making relationship between '" + rootItemType + "' and '" + childType + "' is not possible");
                }
            }
        } catch (Exception ex) {
            STRUCTURE_CONTROLLER_LOGGER.error(ex);
            throw ex;
        }
    }
}
