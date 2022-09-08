/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.validators;

import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.app.service.controller.bom.validators.CommonBOMValidator;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Attribute;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationship;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
public class ComosBOMValidator extends CommonBOMValidator {

    private static final org.apache.log4j.Logger COMOS_BOM_VALIDATOR_LOGGER = org.apache.log4j.Logger.getLogger(ComosBOMValidator.class);

    /**
     *
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createBOMBean
     * @param context
     * @param commonBomImportVariables
     * @return
     * @throws IOException
     * @throws MatrixException
     */
    @Override
    public BOMDataCollector bomValidationAndDataCollection(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateBOMBean createBOMBean, CommonBOMImportParams commonBomImportVariables) throws IOException, MatrixException, Exception {
        List<Relationship> relationshipList = commonBomImportVariables.relationshipList;
        String relName = commonBomImportVariables.relName;
        String interfaceName = commonBomImportVariables.interfaceName;

        BOMDataCollector dataCollector = new BOMDataCollector();
        HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap = new HashMap<>();
        HashMap<String, ChildInfo> childInfoMap = new HashMap<>();
        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
        HashMap<String, ArrayList<String>> existingChildInfoRelMap = new HashMap<>();
        HashMap<String, String> childInfoIdMap = new HashMap<>();
        ParentInfo parentInfo = new ParentInfo();

        TNR parentItem = createBOMBean.getItem();

        String parentType = parentItem.getType();
        String parentName = parentItem.getName();

        COMOS_BOM_VALIDATOR_LOGGER.info("Parent BOM validation process has been started");

        TNR parentTNR = parentItemTNRValidation(parentItem);
        parentInfo.setTnr(parentTNR);

        String parentObjectId = "";

        CommonSearch commonSearch = new CommonSearch();
        BusinessObject parentBusinessObject = null;
        try {
            parentBusinessObject = searchParentItem(context, parentTNR, commonSearch);
            parentObjectId = parentBusinessObject.getObjectId();

            List<String> relList = Optional.ofNullable(relationshipList).orElseThrow(() -> new RuntimeException(parentTNR.getType() + " has not been allowed as a parent type")).stream().map((Relationship relationship) -> relationship.getName()).collect(Collectors.toList());

            Instant start_expand_time = Instant.now();
            COMOS_BOM_VALIDATOR_LOGGER.debug("Start expanding 1 level structure for parent : " + parentName);

            existingChildDataCollectorMap = businessObjectUtil.getExpandedDataFromParent(context, parentBusinessObject, null, relList, null, null, (short) 1);

            Instant end_expand_time = Instant.now();
            COMOS_BOM_VALIDATOR_LOGGER.info(" | Expand | " + parentName + " | " + DateTimeUtils.getDuration(start_expand_time, end_expand_time));

            existingChildInfoRelMap = existingChildDataCollectorMap.get("child-rel-info");
            childInfoIdMap = existingChildDataCollectorMap.get("child-id-info");

        } catch (MatrixException ex) {
            COMOS_BOM_VALIDATOR_LOGGER.error(ex);
            throw ex;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
            COMOS_BOM_VALIDATOR_LOGGER.error(ex);
            throw ex;
        } catch (Exception ex) {
            COMOS_BOM_VALIDATOR_LOGGER.error(ex);
            throw ex;
        }

        COMOS_BOM_VALIDATOR_LOGGER.info("Parent BOM validation process has been completed");
        COMOS_BOM_VALIDATOR_LOGGER.info("Lines validation process has been started");

        HashMap<String, String> duplicatePositionValidatorMap = new HashMap<>();

        for (HashMap<String, String> lineDataMap : createBOMBean.getLines()) {
//            setDefaultValue(lineDataMap, "Position", "1");
//            setDefaultValue(lineDataMap, "Net quantity", "0");
            setDefaultValue(lineDataMap, "revision", "1.1");

//            lineDataMap.put("type", PropertyReader.getProperty("import.type.map.Enovia.comos." + lineDataMap.get("type")));
            ChildInfo childInfo = new ChildInfo();

            TNR tempChildTNR = new TNR(lineDataMap.get("type"), lineDataMap.get("component"), Optional.ofNullable(lineDataMap.get("revision")).filter(revision -> !revision.isEmpty()).orElse("1.1"));
            
            String childType = validateChildAttirbutesInTheRequest("type", lineDataMap, tempChildTNR, parentTNR);
            String childName = validateChildAttirbutesInTheRequest("component", lineDataMap, tempChildTNR, parentTNR);
            String childRev = validateChildAttirbutesInTheRequest("revision", lineDataMap, tempChildTNR, parentTNR);
            String childPosition = validateChildAttirbutesInTheRequest("Position", lineDataMap, tempChildTNR, parentTNR);
            String childQuantity = validateChildAttirbutesInTheRequest("Net quantity", lineDataMap, tempChildTNR, parentTNR);
            float childQuantityFloat = Float.parseFloat(childQuantity);

            COMOS_BOM_VALIDATOR_LOGGER.debug(tempChildTNR.toString() + " Position " + childPosition);
            tempChildTNR = null;

            TNR childTNR = new TNR(childType, childName, childRev);

            childInfo.setChildTNR(childTNR);
            childInfo.setMessage("");

            String childId = "";
            HashMap<String, String> propertyNameValueMap = new HashMap<>();

            String childUniqueKey = "";

            childUniqueKey = childName + "-" + childRev + "-" + childPosition;
            if (existingChildInfoRelMap.containsKey(childUniqueKey)) {
                existingChildInfoRelMap.remove(childUniqueKey);
            }

            try {

                childInfoIdMap = searchChildItem(context, childTNR, commonSearch, childUniqueKey, childInfoIdMap);
                childId = childInfoIdMap.get(childUniqueKey);

                propertyNameValueMap.put("organization", parentBusinessObject.getOrganizationOwner(context).getName());
                propertyNameValueMap.put("project", parentBusinessObject.getProjectOwner(context).getName());

                childInfo.setPropertyNameValueMap(propertyNameValueMap);

            } catch (NullPointerException | MatrixException exp) {
                COMOS_BOM_VALIDATOR_LOGGER.error(exp);
                throw exp;
            } catch (Exception exp) {
                COMOS_BOM_VALIDATOR_LOGGER.error(exp);
                throw exp;
            }

            String positionAttribute = "";
            try {

                List<Attribute> relAttributeList = Collections.EMPTY_LIST;
                for (int i = 0; i < relationshipList.size(); i++) {
                    Relationship rel = relationshipList.get(i);
                    if (rel.getFromType().equalsIgnoreCase(parentType) && rel.getToType().equalsIgnoreCase(childType)) {
                        relName = rel.getName();
                        interfaceName = rel.getInterfaces();
                        
                        if(rel.getAttributes() != null){
                            relAttributeList = rel.getAttributes().getAttributeList();
                        }

                        for (Attribute attribute : relAttributeList) {
                            if (attribute.getSourceName().equalsIgnoreCase("Position")) {
                                positionAttribute = attribute.getDestinationName();
                            }
                        }
                    }
                }

                Optional.ofNullable(relAttributeList).orElseThrow(() -> new RuntimeException(parentTNR.getName() + " has not been allowed as a parent type"));

                List relListWithQuantity = Arrays.asList(PropertyReader.getProperty("BOM.rel.with.quantity.att.list").split("\\|"));

                childQuantityValidation(relListWithQuantity, relName, childQuantity, childName, parentName);
                childPositionValidation(childPosition, parentName, duplicatePositionValidatorMap, childInfo);

                ArrayList<String> connectedChildRelIDlist = new ArrayList<>();
                COMOS_BOM_VALIDATOR_LOGGER.debug("Check connection for relationship name" + relName);
                try {
                    //relID = BusinessObjectUtil.checkToRelationship(context, parentID, childId, relName);
                    connectedChildRelIDlist = businessObjectUtil.checkToRelationshipWithPosition(context, parentObjectId, childId, relName, positionAttribute, childPosition);
                    COMOS_BOM_VALIDATOR_LOGGER.debug("Total connection found between " + parentName + " and Child " + childName + " is " + connectedChildRelIDlist.size());
                } catch (MatrixException ex) {
                    COMOS_BOM_VALIDATOR_LOGGER.error(ex);
                }

                HashMap<String, String> attributeNameValueMap = prepareAttributeNameValueMap(relAttributeList, lineDataMap, childName, parentName);

                /*
                If a line contain same child with same position, then considering as same BOM. 
                 */
                if (childInfoMap.containsKey(childName + childPosition)) {
                    ChildInfo child = childInfoMap.get(childName + childPosition);
                    int previousQuantity = child.getChildQuantity();
                    childInfo.setChildQuantity(Math.round(childQuantityFloat) + previousQuantity);
                } else {
                    childInfo.setChildQuantity(Math.round(childQuantityFloat));
                }

                if (!NullOrEmptyChecker.isNullOrEmpty(connectedChildRelIDlist)) {
                    childInfo.setRelIDList(connectedChildRelIDlist);
                } else {
                    childInfo.setRelIDList(null);
                }
                childInfo.setRelName(relName);
                childInfo.setInterfaceName(interfaceName);
                childInfo.setParentId(parentObjectId);
                childInfo.setChildId(childId);
                childInfo.setAttributeNameValueMap(attributeNameValueMap);

                childInfoMap.put(childName + childPosition, childInfo);
            } catch (RuntimeException exp) {
                COMOS_BOM_VALIDATOR_LOGGER.error(exp);
                throw exp;
            } catch (Exception exp) {
                COMOS_BOM_VALIDATOR_LOGGER.error(exp);
                throw exp;
            }
        }

        requestParentChildInfoMap.put(parentInfo, childInfoMap);
        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);

        COMOS_BOM_VALIDATOR_LOGGER.info("Lines validation process has been completed");
        return dataCollector;
    }

    private void setDefaultValue(HashMap<String, String> lineDataMap, String mapKey, String defaultValue) {
        lineDataMap.put(mapKey, Optional.ofNullable(lineDataMap.get(mapKey)).filter(position -> !position.isEmpty()).orElse(defaultValue));
    }

    @Override
    protected BusinessObject searchParentItem(Context context, TNR parentTNR, CommonSearch commonSearch) throws MatrixException, Exception {
        try {
            parentTNR.setRevision("1.1");

//            TNR clonedParentTNR = (TNR) parentTNR.clone();
            String comosType = parentTNR.getType();
            String v6Type = PropertyReader.getProperty("import.type.map.Enovia.comos." + comosType);
            parentTNR.setType(v6Type);

            Instant start_find_time = Instant.now();
            List<HashMap<String, String>> parentItemSearchedAttributes = commonSearch.searchItem(context, parentTNR);

            parentTNR.setType(comosType);

            String parentBusinessObjectId = parentItemSearchedAttributes.get(0).get("id");
            BusinessObject parentBusinessObject = new BusinessObject(parentBusinessObjectId);

            Instant end_find_time = Instant.now();
            COMOS_BOM_VALIDATOR_LOGGER.info(" | Search | " + parentTNR.getName() + " | " + DateTimeUtils.getDuration(start_find_time, end_find_time));

            return parentBusinessObject;
        } catch (MatrixException ex) {
            COMOS_BOM_VALIDATOR_LOGGER.error(ex);
            throw ex;
        } catch (Exception ex) {
            COMOS_BOM_VALIDATOR_LOGGER.error(ex);
            throw ex;
        }
    }

    @Override
    protected HashMap<String, String> searchChildItem(Context context, TNR childTNR, CommonSearch commonSearch, String childUniqueKey, HashMap<String, String> childInfoIdMap) throws Exception {
        String childId;
        if (!childInfoIdMap.containsKey(childUniqueKey)) {
            try {
                Instant start_child_find_time = Instant.now();

                String comosType = childTNR.getType();
                String v6Type = PropertyReader.getProperty("import.type.map.Enovia.comos." + comosType);
                childTNR.setType(v6Type);

                List<HashMap<String, String>> childItemSearchedAttributes = commonSearch.searchItem(context, childTNR);
                childId = childItemSearchedAttributes.get(0).get("id");

                childTNR.setType(comosType);

                Instant end_child_find_time = Instant.now();
                COMOS_BOM_VALIDATOR_LOGGER.info(" | Search | " + childTNR.getName() + " | " + DateTimeUtils.getDuration(start_child_find_time, end_child_find_time));

                childInfoIdMap.put(childUniqueKey, childId);
            } catch (Exception ex) {
                COMOS_BOM_VALIDATOR_LOGGER.error(ex);
                throw ex;
            }
        }
        return childInfoIdMap;
    }

    @Override
    protected void childPositionValidation(String childPosition, String parentName, HashMap<String, String> duplicatePositionValidatorMap, ChildInfo childInfo) {

    }

    @Override
    protected void childQuantityValidation(List relListWithQuantity, String relName, String childQuantity, String childName, String parentName) {

    }

    @Override
    protected void childDuplicatePositionValidation(HashMap<String, String> duplicatePositionValidatorMap, String childPosition, ChildInfo childInfo, String parentName) throws RuntimeException {

    }

    @Override
    protected TNR parentItemTNRValidation(TNR parentItem) {
        TNR parentTNR = new TNR(
                Optional.ofNullable(parentItem.getType()).orElseThrow(() -> new RuntimeException("Type is missing for parent " + parentItem.getType() + " " + parentItem.getName() + " " + parentItem.getRevision())),
                Optional.ofNullable(parentItem.getName()).orElseThrow(() -> new RuntimeException("Name is missing for parent " + parentItem.getType() + " " + parentItem.getName() + " " + parentItem.getRevision())),
                parentItem.getRevision());

        return parentTNR;
    }

    @Override
    public String validateChildAttirbutesInTheRequest(String requestAttirbute, HashMap<String, String> lineDataMap, TNR tempChildTNR, TNR parentTNR) {
//        return Optional.ofNullable(lineDataMap.get(requestAttirbute)).filter(mapKeyData -> !mapKeyData.isEmpty()).orElseThrow(() -> new RuntimeException("'" + requestAttirbute + "' can't be null or empty for " + tempChildTNR.toString() + " object under parent " + parentTNR));
        return Optional.ofNullable(lineDataMap.get(requestAttirbute)).filter(mapKeyData -> !mapKeyData.isEmpty()).orElse("1");
    }
}
