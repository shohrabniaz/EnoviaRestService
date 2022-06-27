/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.import_threads;

import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Attribute;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.DataType;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationship;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public final class BOMValidatorProcess {

    private static final org.apache.log4j.Logger BOM_IMPORT_VALIDATOR_LOGGER = org.apache.log4j.Logger.getLogger(BOMValidatorProcess.class);
    private static final String POSITIVE_INTEGER_REGEX = "[1-9]\\d*";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile(POSITIVE_INTEGER_REGEX);

    public BOMValidatorProcess() {
    }

    /**
     *
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createBOMBean
     * @param context
     * @param relationshipList
     * @param relName
     * @param interfaceName
     * @return
     * @throws IOException
     * @throws MatrixException
     */
    public BOMDataCollector bomValidationAndDataCollection(BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateBOMBean createBOMBean, Context context, List<Relationship> relationshipList, String relName, String interfaceName) throws IOException, MatrixException {
        BOMDataCollector dataCollector = new BOMDataCollector();
        HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap = new HashMap<>();
        HashMap<String, ChildInfo> childInfoMap = new HashMap<>();
        //existingChildDataCollectorMap map will contain existing child information after first level expansion of parent.
        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
        HashMap<String, ArrayList<String>> existingChildInfoRelMap = new HashMap<>();
        HashMap<String, String> childInfoIdMap = new HashMap<>();
        ParentInfo parentInfo = new ParentInfo();
//        boolean parentStatus = true;
        ArrayList<String> missingTNRs = new ArrayList<>();

        String parentProject = "";
        String parentOrganization = "";

        TNR parentTNR = new TNR();
        String parentType = createBOMBean.getItem().getType();
        String parentName = createBOMBean.getItem().getName();
        String parentRevision = createBOMBean.getItem().getRevision();
        Boolean materialTypeItem = false;
        Boolean negativeQuantity = false;
        Boolean materialsNegativeQuantity = false;

        BOM_IMPORT_VALIDATOR_LOGGER.debug("Start BOM Validation !!! ");
        BOM_IMPORT_VALIDATOR_LOGGER.info(" | Validation | " + parentType + " | Parent name | " + parentName + " | PDM Revision | " + parentRevision);

        if (NullOrEmptyChecker.isNullOrEmpty(parentType)) {
            missingTNRs.add("'Type'");
        }
        if (NullOrEmptyChecker.isNullOrEmpty(parentName)) {
            missingTNRs.add("'Name'");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(parentType)
                && parentType.equals("Own design item")
                && NullOrEmptyChecker.isNullOrEmpty(parentRevision)) {
            missingTNRs.add("'Revision'");
        }

        parentTNR.setType(parentType);
        parentTNR.setName(parentName);
        parentTNR.setRevision(NullOrEmptyChecker.isNull(parentRevision) ? "" : parentRevision);
        parentInfo.setTnr(parentTNR);
        if (missingTNRs.size() > 0) {
            String errorMessage = missingTNRs + " " + PropertyReader.getProperty("missing.parent.tnr.exception");
            return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }
        String parentID = "";
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        try {
            XmlParse xmlParse = new XmlParse();

            String enoviaParentType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                    commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    parentType);

            if (enoviaParentType.toLowerCase().contains("val*")) {
                throw new RuntimeException("Parent item ('" + parentName + "') can't be a VAL item");
            }

            if (NullOrEmptyChecker.isNullOrEmpty(enoviaParentType)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.parent.type"), "'" + parentType + "'");
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }
            parentType = enoviaParentType;
            BOM_IMPORT_VALIDATOR_LOGGER.debug("ENOVIA Parent type : " + enoviaParentType);
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.parent.type"), "'" + parentType + "'");
            return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        try {
            Instant start_find_time = Instant.now();
            BusinessObject parentBO = businessObjectUtil.findPDMItem(context, parentType, parentName, parentRevision);

            if (NullOrEmptyChecker.isNull(parentBO)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.parent.in.ennovia.exception"), "'" + parentName + "'");
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            parentBO.open(context);
            parentType = parentBO.getTypeName();
            parentBO.close(context);

            Instant end_find_time = Instant.now();
            BOM_IMPORT_VALIDATOR_LOGGER.info(" | Search | " + parentName + " | " + DateTimeUtils.getDuration(start_find_time, end_find_time));

            if (parentBO != null) {
                parentID = parentBO.getObjectId();
                parentProject = parentBO.getProjectOwner(context).getName();
                parentOrganization = parentBO.getOrganizationOwner(context).getName();

                ArrayList<String> relList = new ArrayList<>();
                for (int i = 0; i < relationshipList.size(); i++) {
                    Relationship rel = relationshipList.get(i);
                    relName = rel.getName();
                    relList.add(relName);
                }
                Instant start_expand_time = Instant.now();
                //to sync BOM with PDM gather first level child data 
                BOM_IMPORT_VALIDATOR_LOGGER.debug("Start expanding 1 level structure for parent : " + parentName);

                existingChildDataCollectorMap = businessObjectUtil.getExistingChildInfoByExpandingParent(context, parentBO, null, relList, null, null, new Short("1"));

                Instant end_expand_time = Instant.now();
                BOM_IMPORT_VALIDATOR_LOGGER.info(" | Expand | " + parentName + " | " + DateTimeUtils.getDuration(start_expand_time, end_expand_time));

                existingChildInfoRelMap = existingChildDataCollectorMap.get("child-rel-info");
                childInfoIdMap = existingChildDataCollectorMap.get("child-id-info");
            } else {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.parent.in.ennovia.exception"), "'" + parentName + "'");
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            //parentID = BusinessObjectUtility.searchByTypeName(context, parentType, parentName);
        } catch (MatrixException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (NullPointerException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (Exception ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        }
//        boolean isSuccessfulTransaction = true;
        StringBuilder errorMsgBilder = new StringBuilder();

        HashMap<String, String> duplicatePositionValidatorMap = new HashMap<>();

        for (HashMap<String, String> lineDataMap : createBOMBean.getLines()) {
            ChildInfo childInfo = new ChildInfo();
            TNR childTNR = new TNR();
            String errorMsg = "";
            String childType = lineDataMap.get("type");
            String childName = lineDataMap.get("component");
            String childRev = lineDataMap.get("revision");
            String PDMChildType = childType;
            String childPosition = lineDataMap.get("Position");
            String childQuantity = lineDataMap.get("Net quantity");
            Double childQuantityFloat = Double.parseDouble(childQuantity);
            String childLength = lineDataMap.get("Length");
            String childWidth = lineDataMap.get("Width");
            String childNoOfUnit = lineDataMap.get("Number of units");
            BOM_IMPORT_VALIDATOR_LOGGER.debug(" PDM Child type : " + childType + " Child name " + childName + " PDM Revision " + childRev + " Position " + childPosition);
            if (NullOrEmptyChecker.isNullOrEmpty(childType)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                        "'type'",
                        "'" + parentName + "'");
                return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }
            try {
                XmlParse xmlParse = new XmlParse();

                String enoviaChildType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                        commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        childType);

                if (NullOrEmptyChecker.isNullOrEmpty(enoviaChildType)) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.child.type"),
                            "'" + childType + "'");
                    return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }
                childType = enoviaChildType;

                BOM_IMPORT_VALIDATOR_LOGGER.debug("ENOVIA Child type : " + childType + " Child name " + childName);

            } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.child.type"),
                        "'" + childType + "'");
                return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            //String childName = lineDataMap.get("component");
            if (NullOrEmptyChecker.isNullOrEmpty(childName)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                        "'name'",
                        "'" + parentName + "'");
                return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            if (childType.equals("Own design item") && NullOrEmptyChecker.isNullOrEmpty(childRev)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                        "'revision'",
                        "'" + parentName + "'");
                return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            childTNR.setName(childName);
            childTNR.setType(childType);
            childTNR.setRevision(childRev);
            childInfo.setChildTNR(childTNR);
            childInfo.setMessage("");

            String childId = "";
            String project = "";
            String organization = "";
            HashMap<String, String> propertyNameValueMap = new HashMap<>();
            String position = lineDataMap.get("Position");

            /*if existingChildInfoRelMap contains children in line (request from PDM) then remove those children from the Map. 
            later children will be disconnected from BOM which are not present in current request. 
             */
            String childUniqueKey = "";
            if (PDMChildType.equalsIgnoreCase("commercial items")) {
                childUniqueKey = childName + "-" + "" + "-" + childPosition;
                if (existingChildInfoRelMap.containsKey(childUniqueKey)) {
                    existingChildInfoRelMap.remove(childUniqueKey);
                }
            } else {
                childUniqueKey = childName + "-" + childRev + "-" + childPosition;
                if (existingChildInfoRelMap.containsKey(childUniqueKey)) {
                    existingChildInfoRelMap.remove(childUniqueKey);
                }
            }
            //End Sync BOM structure with PDM

            BusinessObject childBusinessObject = null;
            try {
                /*
                if a child already connected in a structure then the system will not find it. 
                getting existing child id from existingChildInfoIdMap map.  
                 */

                if (!childInfoIdMap.containsKey(childUniqueKey)) {

                    Instant start_child_find_time = Instant.now();
                    childId = findChildBusinessObject(businessObjectUtil, businessObjectOperations, commonPropertyReader, childRev, context, childType, childName, errorMsgBilder, parentName, position, childInfo, childId);
                    Instant end_child_find_time = Instant.now();
                    BOM_IMPORT_VALIDATOR_LOGGER.info(" | Search | " + childName + " | " + DateTimeUtils.getDuration(start_child_find_time, end_child_find_time));

                    if (NullOrEmptyChecker.isNullOrEmpty(childId)) {
                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.in.ennovia.exception"),
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        return childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }
                    childInfoIdMap.put(childUniqueKey, childId);
                } else {
                    childId = childInfoIdMap.get(childUniqueKey);
                }

                childBusinessObject = new BusinessObject(childId);
                childBusinessObject.open(context);
                childType = childBusinessObject.getTypeName();
                childInfo.getChildTNR().setType(childType);

                childBusinessObject.close(context);

                if (!NullOrEmptyChecker.isNullOrEmpty(parentOrganization)) {
                    propertyNameValueMap.put("organization", parentOrganization);
                }
                if (!NullOrEmptyChecker.isNullOrEmpty(parentProject)) {
                    propertyNameValueMap.put("project", parentProject);
                }

                childInfo.setPropertyNameValueMap(propertyNameValueMap);

                //relationShipsDefaultAttributes(childInfo.getAttributeNameValueMap(), childType);
            } catch (NullPointerException | MatrixException exp) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
                childErrorMessageBuilder(exp.getMessage(), childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            } catch (Exception exp) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
                childErrorMessageBuilder(exp.getMessage(), childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            String positionAttribute = "";
            try {

                List<Attribute> relAttributeList = null;
                for (int i = 0; i < relationshipList.size(); i++) {
                    Relationship rel = relationshipList.get(i);
                    if (rel.getFromType().equalsIgnoreCase(parentType) && rel.getToType().equalsIgnoreCase(childType)) {
                        relName = rel.getName();
                        interfaceName = rel.getInterfaces();
                        relAttributeList = rel.getAttributes().getAttributeList();
                        for (Attribute attribute : relAttributeList) {
                            if (attribute.getSourceName().equalsIgnoreCase("Position")) {
                                positionAttribute = attribute.getDestinationName();
                            }
                        }

                    }
                }

                List relListWithQuantity = Arrays.asList(commonPropertyReader.getPropertyValue("BOM.rel.with.quantity.att.list").split("\\|"));

                if (relListWithQuantity.contains(relName)) {
                    if ("0.0".equalsIgnoreCase(childQuantity) || "0".equalsIgnoreCase(childQuantity)) {
                        BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has zero quantity");

                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.quantity.can.not.zero.or.fraction"),
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }
                } else {

                    if (childQuantityFloat < 0) {
                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.non.material.item.quantity.can.not.be.negative"),
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }

                    if ("0.0".equalsIgnoreCase(childQuantity) || "0".equalsIgnoreCase(childQuantity) || childQuantityFloat % 1 != 0) {
                        BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has zero quantity");

                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.quantity.can.not.zero.or.fraction"),
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }
                }

                if (!isPositiveInteger(childPosition)) {
                    BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has negative position");
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.position.validation"),
                            "'" + childPosition + "'",
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }

                if (duplicatePositionValidatorMap.containsKey(childPosition)) {
                    String itemNameRev = duplicatePositionValidatorMap.get(childPosition);
                    if (!itemNameRev.equalsIgnoreCase(childName + "|" + childRev)) {
                        String[] duplicateChildInfo = duplicatePositionValidatorMap.get(childPosition).split("\\|");
                        String duplicateChildName = "";
                        String duplicateChildRev = "";
                        if (duplicateChildInfo != null) {
                            duplicateChildName = duplicateChildInfo[0];
                            if (duplicateChildInfo.length == 2) {
                                duplicateChildRev = duplicateChildInfo[1];
                            }
                        }
                        BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has duplicate position");
                        childInfo.setMessage(MessageFormat.format(PropertyReader.getProperty("duplicate.position.not.allowed"),
                                "'" + childPosition + "'",
                                "'" + childName + "'",
                                "'" + childRev + "'",
                                "'" + duplicateChildName + "'",
                                "'" + duplicateChildRev + "'",
                                "'" + parentName + "'"));
                        //childInfo.setMessage("Duplicate position "+childPosition+" for Child "+childName+" and "+duplicatePositionValidatorMap.get(childPosition)+" under "+parentName);
                        childInfoMap.put(childName + childPosition, childInfo);
                        requestParentChildInfoMap.put(parentInfo, childInfoMap);
                        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
                        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
                        dataCollector.setBOMcontainError(true);
//                        return dataCollector;
                    }
                } else {
                    duplicatePositionValidatorMap.put(childPosition, childName + "|" + childRev);
                }
                ArrayList<String> connectedChildRelIDlist = new ArrayList<>();
                BOM_IMPORT_VALIDATOR_LOGGER.debug("Check connection for relationship name" + relName);
                try {
                    //relID = BusinessObjectUtil.checkToRelationship(context, parentID, childId, relName);
                    connectedChildRelIDlist = businessObjectUtil.checkToRelationshipWithPosition(context, parentID, childId, relName, positionAttribute, childPosition);
                    BOM_IMPORT_VALIDATOR_LOGGER.debug("Total connection found between " + parentName + " and Child " + childName + " is " + connectedChildRelIDlist.size());
                } catch (MatrixException ex) {
                    BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
                }
                /*
                Calculating Usage Co-efficent for VALComponentMaterial with inventory unit Length(m,in,ft) and Area(m2,in2,ft2)
                 */
                double usageCoEfficent = 0;
                String inventoryUnit = "";
                List valComponentMaterialLengthUnitList = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
                List valComponentMaterialAreaUnitList = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
                List valComponentMaterialMassUnitList = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));
                List valComponentMaterialVolumeUnitList = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
                List forbiddenImperialUnits = Arrays.asList(PropertyReader.getProperty("forbidden.imperial.units").split("\\|"));

                CommonUtilities commonUtilities = new CommonUtilities();

                if (childBusinessObject != null) {
                    childType = childBusinessObject.getTypeName();

                    if (childType.equalsIgnoreCase("VAL_VALComponentMaterial") || childType.equalsIgnoreCase("ProcessContinuousCreateMaterial")) {
                        double noOfUnit = Double.parseDouble(childNoOfUnit);
                        materialTypeItem = true;
                        if (childQuantityFloat < 0) {
                            negativeQuantity = true;
                            childQuantityFloat = childQuantityFloat * (-1);
                        }

                        if (noOfUnit - Math.floor(noOfUnit) != 0) {
                            BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has negative position");
                            String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.NoOfUnit.validation"),
                                    "'" + childNoOfUnit + "'",
                                    "'" + childName + "'",
                                    "'" + parentName + "'");
                            childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                        }
                        BOM_IMPORT_VALIDATOR_LOGGER.debug("Calculating usage co-efficent for : " + childName);
                        inventoryUnit = childBusinessObject.getAttributeValues(context, PropertyReader.getProperty("inventory.unit.VAL.component.mateial")).getValue();
                        childInfo.setChildInventoryUnit(inventoryUnit);

                        if (forbiddenImperialUnits.contains(inventoryUnit)) {
                            /**
                             * Forbid imperial units
                             */
                            String errorMessage = MessageFormat.format(PropertyReader.getProperty("bom.invalid.imperial.unit.error.message"),
                                    "'" + childName + "'",
                                    "'" + parentName + "'");
                            childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                        }

                        if (valComponentMaterialAreaUnitList.contains(inventoryUnit)) {
                            double length = 0.0;
                            double width = 0.0;
                            if (!NullOrEmptyChecker.isNullOrEmpty(childLength)) {
                                length = Double.parseDouble(childLength);
                            }

                            if (!NullOrEmptyChecker.isNullOrEmpty(childWidth)) {
                                width = Double.parseDouble(childWidth);
                            }

                            //double areaCalculation = (length / 1000) * (width / 1000);
                            //double areaCalculation = length * width;
                            //double areaCalculation = commonUtilities.unitConversion(inventoryUnit, length * width);
                            usageCoEfficent = quantityCalculationForLengthAndArea(commonUtilities.unitConversion("mm2", length * width), noOfUnit, commonUtilities.unitConversion(inventoryUnit, (double) childQuantityFloat), usageCoEfficent, childInfo, parentName);
                        } else if (valComponentMaterialLengthUnitList.contains(inventoryUnit)) {
                            double length = 0.0;
                            if (!NullOrEmptyChecker.isNullOrEmpty(childLength)) {
                                length = Double.parseDouble(childLength);
                            }

//                            length = commonUtilities.unitConversion(inventoryUnit, length);
                            usageCoEfficent = quantityCalculationForLengthAndArea(commonUtilities.unitConversion("mm", length), noOfUnit, commonUtilities.unitConversion(inventoryUnit, (double) childQuantityFloat), usageCoEfficent, childInfo, parentName);
                        }
                    }
                }

                HashMap<String, String> attributeNameValueMap = new HashMap<>();

                if (materialTypeItem) {
                    if (negativeQuantity) {
                        attributeNameValueMap.put(PropertyReader.getProperty("attribute.material.items.by.product"), negativeQuantity.toString());
                    }

                    materialsNegativeQuantity = negativeQuantity;
                    materialTypeItem = false;
                    negativeQuantity = false;
                }

                for (Attribute attribute : relAttributeList) {
                    attribute.setIsRequired(NullOrEmptyChecker.isNull(attribute.getIsRequired()) ? Boolean.FALSE : attribute.getIsRequired());
                    if (attribute.getIsRequired() && NullOrEmptyChecker.isNullOrEmpty(lineDataMap.get(attribute.getSourceName()))) {

                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.attribute.exception"),
                                "'" + attribute.getSourceName() + "'",
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }

                    String jsonAttributeValue = lineDataMap.get(attribute.getSourceName());
                    if (attribute.getSourceName().equalsIgnoreCase("Net quantity") && usageCoEfficent != 0) {
                        jsonAttributeValue = Double.toString(usageCoEfficent);
                        attributeNameValueMap.put(attribute.getDestinationName(), jsonAttributeValue);
                    } else {
                        if (attribute.getSourceName().equalsIgnoreCase("Net quantity")) {
                            Double usageCoefficientForMassAndVolume = Double.parseDouble(jsonAttributeValue);
                            if (valComponentMaterialMassUnitList.contains(inventoryUnit) || valComponentMaterialVolumeUnitList.contains(inventoryUnit)) {

                                if (materialsNegativeQuantity) {
                                    usageCoefficientForMassAndVolume = usageCoefficientForMassAndVolume * (-1);
                                    materialsNegativeQuantity = false;
                                }

                                usageCoefficientForMassAndVolume = commonUtilities.unitConversion(inventoryUnit, usageCoefficientForMassAndVolume);
                            }

                            jsonAttributeValue = usageCoefficientForMassAndVolume.toString();
                        }

                        jsonAttributeValue = convertToRealValues(attribute, jsonAttributeValue);
                        attributeNameValueMap.put(attribute.getDestinationName(), jsonAttributeValue);
                    }
                }

                /*
                If a line contain same child with same position, then considering as same BOM. 
                 */
                if (childInfoMap.containsKey(childName + childPosition)) {
                    ChildInfo child = childInfoMap.get(childName + childPosition);
                    int previousQuantity = child.getChildQuantity();
                    childInfo.setChildQuantity((int) (Math.round(childQuantityFloat) + previousQuantity));
                } else {
                    childInfo.setChildQuantity((int) Math.round(childQuantityFloat));
                }

                if (!NullOrEmptyChecker.isNullOrEmpty(connectedChildRelIDlist)) {
                    childInfo.setRelIDList(connectedChildRelIDlist);
                } else {
                    childInfo.setRelIDList(null);
                }
                childInfo.setRelName(relName);
                childInfo.setInterfaceName(interfaceName);
                childInfo.setParentId(parentID);
                childInfo.setChildId(childId);
                childInfo.setAttributeNameValueMap(attributeNameValueMap);

                childInfoMap.put(childName + childPosition, childInfo);
            } catch (Exception exp) {
                throw exp;
            }
        }

        requestParentChildInfoMap.put(parentInfo, childInfoMap);
        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
        return dataCollector;
    }

    /**
     *
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createBOMBean
     * @param context
     * @param relationshipList
     * @param relName
     * @param interfaceName
     * @return
     * @throws IOException
     * @throws MatrixException
     */
    public BOMDataCollector bomValidationAndDataCollection(BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateBOMBean createBOMBean, Context context, List<Relationship> relationshipList, String relName, String interfaceName, HashMap<String, List<ParentInfo>> responseMsgMap) throws IOException, MatrixException {
        BOMDataCollector dataCollector = new BOMDataCollector();
        HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap = new HashMap<>();
        HashMap<String, ChildInfo> childInfoMap = new HashMap<>();
        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
        HashMap<String, ArrayList<String>> existingChildInfoRelMap = new HashMap<>();
        HashMap<String, String> childInfoIdMap = new HashMap<>();
        ParentInfo parentInfo = new ParentInfo();
        ArrayList<String> missingTNRs = new ArrayList<>();

        String parentProject = "";
        String parentOrganization = "";

        TNR parentTNR = new TNR();
        String parentType = createBOMBean.getItem().getType();
        String parentName = createBOMBean.getItem().getName();
        String parentRevision = createBOMBean.getItem().getRevision();
        Boolean materialTypeItem = false;
        Boolean negativeQuantity = false;
        Boolean materialsNegativeQuantity = false;

        BOM_IMPORT_VALIDATOR_LOGGER.debug("Start BOM Validation !!! ");
        BOM_IMPORT_VALIDATOR_LOGGER.info(" | Validation | " + parentType + " | Parent name | " + parentName + " | PDM Revision | " + parentRevision);

        if (NullOrEmptyChecker.isNullOrEmpty(parentType)) {
            missingTNRs.add("'Type'");
        }
        if (NullOrEmptyChecker.isNullOrEmpty(parentName)) {
            missingTNRs.add("'Name'");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(parentType)
                && parentType.equals("Own design item")
                && NullOrEmptyChecker.isNullOrEmpty(parentRevision)) {
            missingTNRs.add("'Revision'");
        }

        parentTNR.setType(parentType);
        parentTNR.setName(parentName);
        parentTNR.setRevision(NullOrEmptyChecker.isNull(parentRevision) ? "" : parentRevision);
        parentInfo.setTnr(parentTNR);
        if (missingTNRs.size() > 0) {
            String errorMessage = missingTNRs + " " + PropertyReader.getProperty("missing.parent.tnr.exception");
            return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }
        String parentID = "";
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        try {
            XmlParse xmlParse = new XmlParse();

            String enoviaParentType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                    commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    parentType);

            if (enoviaParentType.toLowerCase().contains("val*")) {
                String errorMessage = "Parent item ('" + parentName + "') can't be a VAL item";
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            if (NullOrEmptyChecker.isNullOrEmpty(enoviaParentType)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.parent.type"), "'" + parentType + "'");
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }
            parentType = enoviaParentType;
            BOM_IMPORT_VALIDATOR_LOGGER.debug("ENOVIA Parent type : " + enoviaParentType);
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.parent.type"), "'" + parentType + "'");
            return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        try {
            Instant start_find_time = Instant.now();
            BusinessObject parentBO = businessObjectUtil.findPDMItem(context, parentType, parentName, parentRevision);

            if (!NullOrEmptyChecker.isNull(parentBO)) {
                parentBO.open(context);
                parentType = parentBO.getTypeName();
                parentBO.close(context);

                Instant end_find_time = Instant.now();
                BOM_IMPORT_VALIDATOR_LOGGER.info(" | Search | " + parentName + " | " + DateTimeUtils.getDuration(start_find_time, end_find_time));

                if (parentBO != null) {
                    parentID = parentBO.getObjectId();
                    parentProject = parentBO.getProjectOwner(context).getName();
                    parentOrganization = parentBO.getOrganizationOwner(context).getName();

                    ArrayList<String> relList = new ArrayList<>();
                    for (int i = 0; i < relationshipList.size(); i++) {
                        Relationship rel = relationshipList.get(i);
                        relName = rel.getName();
                        relList.add(relName);
                    }
                    Instant start_expand_time = Instant.now();
                    BOM_IMPORT_VALIDATOR_LOGGER.debug("Start expanding 1 level structure for parent : " + parentName);

                    existingChildDataCollectorMap = businessObjectUtil.getExistingChildInfoByExpandingParent(context, parentBO, null, relList, null, null, new Short("1"));

                    Instant end_expand_time = Instant.now();
                    BOM_IMPORT_VALIDATOR_LOGGER.info(" | Expand | " + parentName + " | " + DateTimeUtils.getDuration(start_expand_time, end_expand_time));

                    existingChildInfoRelMap = existingChildDataCollectorMap.get("child-rel-info");
                    childInfoIdMap = existingChildDataCollectorMap.get("child-id-info");
                } else {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.parent.in.ennovia.exception"), "'" + parentName + "'");
                    return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }
            } else {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.parent.in.ennovia.exception"), "'" + parentName + "'");
                return parentErrorMessageBuilder(errorMessage, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            //parentID = BusinessObjectUtility.searchByTypeName(context, parentType, parentName);
        } catch (MatrixException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (NullPointerException ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (Exception ex) {
//            parentStatus = false;
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        }
        StringBuilder errorMsgBilder = new StringBuilder();

        HashMap<String, String> duplicatePositionValidatorMap = new HashMap<>();

        try {
            validateChildItems(context, relationshipList, businessObjectOperations, businessObjectUtil, commonPropertyReader, createBOMBean, parentType, parentName, parentID, parentInfo, relName, interfaceName, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector, childInfoIdMap, parentOrganization, parentProject, errorMsgBilder, duplicatePositionValidatorMap, materialTypeItem, materialsNegativeQuantity, negativeQuantity, responseMsgMap);
        } catch (Exception ex) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        }

        requestParentChildInfoMap.put(parentInfo, childInfoMap);
        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
        return dataCollector;
    }

    private BOMDataCollector validateChildItems(Context context, List<Relationship> relationshipList, BusinessObjectOperations businessObjectOperations, BusinessObjectUtil businessObjectUtil, CommonPropertyReader commonPropertyReader, CreateBOMBean createBOMBean, String parentType, String parentName, String parentID, ParentInfo parentInfo, String relName, String interfaceName, HashMap<String, ChildInfo> childInfoMap, HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap, HashMap<String, ArrayList<String>> existingChildInfoRelMap, BOMDataCollector dataCollector, HashMap<String, String> childInfoIdMap, String parentOrganization, String parentProject, StringBuilder errorMsgBilder, HashMap<String, String> duplicatePositionValidatorMap, Boolean materialTypeItem, Boolean materialsNegativeQuantity, Boolean negativeQuantity, HashMap<String, List<ParentInfo>> responseMsgMap) throws Exception {
        final int parallelism = Integer.parseInt(PropertyReader.getProperty("bom.validation.import.concurrent.total.thread.count"));
        ForkJoinPool childValidatorJoinPool = new ForkJoinPool(parallelism);

        try {
            childValidatorJoinPool.submit(() -> {
                createBOMBean.getLines().parallelStream().forEach((HashMap<String, String> lineDataMap) -> {
                    try {
                        validateChildItems(context, relationshipList, businessObjectOperations, businessObjectUtil, commonPropertyReader, lineDataMap, parentType, parentName, parentID, parentInfo, relName, interfaceName, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector, childInfoIdMap, parentOrganization, parentProject, errorMsgBilder, duplicatePositionValidatorMap, materialTypeItem, materialsNegativeQuantity, negativeQuantity, responseMsgMap);
                    } catch (Exception ex) {
                        BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
                    }
                });
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
            throw new RuntimeException(ex);
        }
        return dataCollector;
    }

    private BOMDataCollector validateChildItems(Context context, List<Relationship> relationshipList, BusinessObjectOperations businessObjectOperations, BusinessObjectUtil businessObjectUtil, CommonPropertyReader commonPropertyReader, HashMap<String, String> lineDataMap, String parentType, String parentName, String parentID, ParentInfo parentInfo, String relName, String interfaceName, HashMap<String, ChildInfo> childInfoMap, HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap, HashMap<String, ArrayList<String>> existingChildInfoRelMap, BOMDataCollector dataCollector, HashMap<String, String> childInfoIdMap, String parentOrganization, String parentProject, StringBuilder errorMsgBilder, HashMap<String, String> duplicatePositionValidatorMap, Boolean materialTypeItem, Boolean materialsNegativeQuantity, Boolean negativeQuantity, HashMap<String, List<ParentInfo>> responseMsgMap) throws Exception {
        ChildInfo childInfo = new ChildInfo();
        TNR childTNR = new TNR();
        String errorMsg = "";
        String childType = lineDataMap.get("type");
        String childName = lineDataMap.get("component");
        String childRev = lineDataMap.get("revision");
        String PDMChildType = childType;
        String childPosition = lineDataMap.get("Position");
        String childQuantity = lineDataMap.get("Net quantity");
        Double childQuantityFloat = Double.parseDouble(childQuantity);
        String childLength = lineDataMap.get("Length");
        String childWidth = lineDataMap.get("Width");
        String childNoOfUnit = lineDataMap.get("Number of units");

        BOM_IMPORT_VALIDATOR_LOGGER.debug(" PDM Child type : " + childType + " Child name " + childName + " PDM Revision " + childRev + " Position " + childPosition);
        if (NullOrEmptyChecker.isNullOrEmpty(childType)) {
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                    "'type'",
                    "'" + parentName + "'");
            /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        try {
            childType = searchAndGetChildType(childType, childName, commonPropertyReader);
        } catch (Exception exp) {
            /*return*/ childErrorMessageBuilder(exp.getMessage(), childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        //String childName = lineDataMap.get("component");
        if (NullOrEmptyChecker.isNullOrEmpty(childName)) {
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                    "'name'",
                    "'" + parentName + "'");
            /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        if (childType.equals("Own design item") && NullOrEmptyChecker.isNullOrEmpty(childRev)) {
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.tnr.exception"),
                    "'revision'",
                    "'" + parentName + "'");
            /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
        }

        childTNR.setName(childName);
        childTNR.setType(childType);
        childTNR.setRevision(childRev);
        childInfo.setChildTNR(childTNR);
        childInfo.setMessage("");

        String childId = "";
        String project = "";
        String organization = "";
        HashMap<String, String> propertyNameValueMap = new HashMap<>();
        String position = lineDataMap.get("Position");

        /*if existingChildInfoRelMap contains children in line (request from PDM) then remove those children from the Map. 
            later children will be disconnected from BOM which are not present in current request. 
         */
        String childUniqueKey = "";
        if (PDMChildType.equalsIgnoreCase("commercial items")) {
            childUniqueKey = childName + "-" + "" + "-" + childPosition;
            if (existingChildInfoRelMap.containsKey(childUniqueKey)) {
                existingChildInfoRelMap.remove(childUniqueKey);
            }
        } else {
            childUniqueKey = childName + "-" + childRev + "-" + childPosition;
            if (existingChildInfoRelMap.containsKey(childUniqueKey)) {
                existingChildInfoRelMap.remove(childUniqueKey);
            }
        }
        //End Sync BOM structure with PDM

        BusinessObject childBusinessObject = null;
        try {
            /*
                if a child already connected in a structure then the system will not find it. 
                getting existing child id from existingChildInfoIdMap map.  
             */

            if (!childInfoIdMap.containsKey(childUniqueKey)) {

                Instant start_child_find_time = Instant.now();
                childId = findChildBusinessObject(businessObjectUtil, businessObjectOperations, commonPropertyReader, childRev, context, childType, childName, errorMsgBilder, parentName, position, childInfo, childId);
                Instant end_child_find_time = Instant.now();
                BOM_IMPORT_VALIDATOR_LOGGER.info(" | Search | " + childName + " | " + DateTimeUtils.getDuration(start_child_find_time, end_child_find_time));

                if (NullOrEmptyChecker.isNullOrEmpty(childId)) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.in.ennovia.exception"),
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }
                childInfoIdMap.put(childUniqueKey, childId);
            } else {
                childId = childInfoIdMap.get(childUniqueKey);
            }

            childBusinessObject = new BusinessObject(childId);
            childBusinessObject.open(context);
            childType = childBusinessObject.getTypeName();
            childInfo.getChildTNR().setType(childType);

            childBusinessObject.close(context);

            if (!NullOrEmptyChecker.isNullOrEmpty(parentOrganization)) {
                propertyNameValueMap.put("organization", parentOrganization);
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(parentProject)) {
                propertyNameValueMap.put("project", parentProject);
            }

            childInfo.setPropertyNameValueMap(propertyNameValueMap);

            //relationShipsDefaultAttributes(childInfo.getAttributeNameValueMap(), childType);
        } catch (NullPointerException | MatrixException exp) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
        } catch (Exception exp) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
        }

        String positionAttribute = "";
        try {

            List<Attribute> relAttributeList = null;
            for (int i = 0; i < relationshipList.size(); i++) {
                Relationship rel = relationshipList.get(i);
                if (rel.getFromType().equalsIgnoreCase(parentType) && rel.getToType().equalsIgnoreCase(childType)) {
                    relName = rel.getName();
                    interfaceName = rel.getInterfaces();
                    relAttributeList = rel.getAttributes().getAttributeList();
                    for (Attribute attribute : relAttributeList) {
                        if (attribute.getSourceName().equalsIgnoreCase("Position")) {
                            positionAttribute = attribute.getDestinationName();
                        }
                    }

                }
            }

            List relListWithQuantity = Arrays.asList(commonPropertyReader.getPropertyValue("BOM.rel.with.quantity.att.list").split("\\|"));

            if (relListWithQuantity.contains(relName)) {
                if ("0.0".equalsIgnoreCase(childQuantity) || "0".equalsIgnoreCase(childQuantity)) {
                    BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has zero quantity");

                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.quantity.can.not.zero.or.fraction"),
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }
            } else {

                if (childQuantityFloat < 0) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.non.material.item.quantity.can.not.be.negative"),
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }

                if ("0.0".equalsIgnoreCase(childQuantity) || "0".equalsIgnoreCase(childQuantity) || childQuantityFloat % 1 != 0) {
                    BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has zero quantity");

                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.quantity.can.not.zero.or.fraction"),
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }
            }

            if (!isPositiveInteger(childPosition)) {
                BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has negative position");
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.position.validation"),
                        "'" + childPosition + "'",
                        "'" + childName + "'",
                        "'" + parentName + "'");
                /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
            }

            if (duplicatePositionValidatorMap.containsKey(childPosition)) {
                String itemNameRev = duplicatePositionValidatorMap.get(childPosition);
                if (!itemNameRev.equalsIgnoreCase(childName + "|" + childRev)) {
                    String[] duplicateChildInfo = duplicatePositionValidatorMap.get(childPosition).split("\\|");
                    String duplicateChildName = "";
                    String duplicateChildRev = "";
                    if (duplicateChildInfo != null) {
                        duplicateChildName = duplicateChildInfo[0];
                        if (duplicateChildInfo.length == 2) {
                            duplicateChildRev = duplicateChildInfo[1];
                        }
                    }
                    BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has duplicate position");
                    childInfo.setMessage(MessageFormat.format(PropertyReader.getProperty("duplicate.position.not.allowed"),
                            "'" + childPosition + "'",
                            "'" + childName + "'",
                            "'" + childRev + "'",
                            "'" + duplicateChildName + "'",
                            "'" + duplicateChildRev + "'",
                            "'" + parentName + "'"));
                    //childInfo.setMessage("Duplicate position "+childPosition+" for Child "+childName+" and "+duplicatePositionValidatorMap.get(childPosition)+" under "+parentName);
                    childInfoMap.put(childName + childPosition, childInfo);
                    requestParentChildInfoMap.put(parentInfo, childInfoMap);
                    dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
                    dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
                    dataCollector.setBOMcontainError(true);
//                    return dataCollector;
                }
            } else {
                duplicatePositionValidatorMap.put(childPosition, childName + "|" + childRev);
            }
            ArrayList<String> connectedChildRelIDlist = new ArrayList<>();
            BOM_IMPORT_VALIDATOR_LOGGER.debug("Check connection for relationship name" + relName);
            try {
                //relID = BusinessObjectUtil.checkToRelationship(context, parentID, childId, relName);
                connectedChildRelIDlist = businessObjectUtil.checkToRelationshipWithPosition(context, parentID, childId, relName, positionAttribute, childPosition);
                BOM_IMPORT_VALIDATOR_LOGGER.debug("Total connection found between " + parentName + " and Child " + childName + " is " + connectedChildRelIDlist.size());
            } catch (Exception ex) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
            }
            /*
                Calculating Usage Co-efficent for VALComponentMaterial with inventory unit Length(m,in,ft) and Area(m2,in2,ft2)
             */
            double usageCoEfficent = 0;
            String inventoryUnit = "";
            List valComponentMaterialLengthUnitList = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
            List valComponentMaterialAreaUnitList = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
            List valComponentMaterialMassUnitList = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));
            List valComponentMaterialVolumeUnitList = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
            List forbiddenImperialUnits = Arrays.asList(PropertyReader.getProperty("forbidden.imperial.units").split("\\|"));

            CommonUtilities commonUtilities = new CommonUtilities();

            if (childBusinessObject != null) {
                childType = childBusinessObject.getTypeName();

                if (childType.equalsIgnoreCase("VAL_VALComponentMaterial") || childType.equalsIgnoreCase("ProcessContinuousCreateMaterial")) {
                    double noOfUnit = Double.parseDouble(childNoOfUnit);
                    materialTypeItem = true;
                    if (childQuantityFloat < 0) {
                        negativeQuantity = true;
                        childQuantityFloat = childQuantityFloat * (-1);
                    }

                    if (noOfUnit - Math.floor(noOfUnit) != 0) {
                        BOM_IMPORT_VALIDATOR_LOGGER.error("'" + childInfo.getChildTNR().getType() + "' '" + childInfo.getChildTNR().getName() + "' has negative position");
                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("BOM.NoOfUnit.validation"),
                                "'" + childNoOfUnit + "'",
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }
                    BOM_IMPORT_VALIDATOR_LOGGER.debug("Calculating usage co-efficent for : " + childName);
                    inventoryUnit = childBusinessObject.getAttributeValues(context, PropertyReader.getProperty("inventory.unit.VAL.component.mateial")).getValue();
                    childInfo.setChildInventoryUnit(inventoryUnit);

                    if (forbiddenImperialUnits.contains(inventoryUnit)) {
                        /**
                         * Forbid imperial units
                         */
                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("bom.invalid.imperial.unit.error.message"),
                                "'" + childName + "'",
                                "'" + parentName + "'");
                        /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }

                    try {
                        if (valComponentMaterialAreaUnitList.contains(inventoryUnit)) {
                            double length = 0.0;
                            double width = 0.0;
                            if (!NullOrEmptyChecker.isNullOrEmpty(childLength)) {
                                length = Double.parseDouble(childLength);
                            }

                            if (!NullOrEmptyChecker.isNullOrEmpty(childWidth)) {
                                width = Double.parseDouble(childWidth);
                            }

                            usageCoEfficent = quantityCalculationForLengthAndArea(commonUtilities.unitConversion("mm2", length * width), noOfUnit, commonUtilities.unitConversion(inventoryUnit, (double) childQuantityFloat), usageCoEfficent, childInfo, parentName);
                        } else if (valComponentMaterialLengthUnitList.contains(inventoryUnit)) {
                            double length = 0.0;
                            if (!NullOrEmptyChecker.isNullOrEmpty(childLength)) {
                                length = Double.parseDouble(childLength);
                            }

                            usageCoEfficent = quantityCalculationForLengthAndArea(commonUtilities.unitConversion("mm", length), noOfUnit, commonUtilities.unitConversion(inventoryUnit, (double) childQuantityFloat), usageCoEfficent, childInfo, parentName);
                        }
                    } catch (Exception exp) {
                        childErrorMessageBuilder(exp.getMessage(), childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                    }
                }
            }

            HashMap<String, String> attributeNameValueMap = new HashMap<>();

            if (materialTypeItem) {
                if (negativeQuantity) {
                    attributeNameValueMap.put(PropertyReader.getProperty("attribute.material.items.by.product"), negativeQuantity.toString());
                }

                materialsNegativeQuantity = negativeQuantity;
                materialTypeItem = false;
                negativeQuantity = false;
            }

            for (Attribute attribute : relAttributeList) {
                attribute.setIsRequired(NullOrEmptyChecker.isNull(attribute.getIsRequired()) ? Boolean.FALSE : attribute.getIsRequired());
                if (attribute.getIsRequired() && NullOrEmptyChecker.isNullOrEmpty(lineDataMap.get(attribute.getSourceName()))) {

                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.child.attribute.exception"),
                            "'" + attribute.getSourceName() + "'",
                            "'" + childName + "'",
                            "'" + parentName + "'");
                    /*return*/ childErrorMessageBuilder(errorMessage, childName, childPosition, childInfo, parentInfo, childInfoMap, requestParentChildInfoMap, existingChildInfoRelMap, dataCollector);
                }

                String jsonAttributeValue = lineDataMap.get(attribute.getSourceName());
                if (attribute.getSourceName().equalsIgnoreCase("Net quantity") && usageCoEfficent != 0) {
                    jsonAttributeValue = Double.toString(usageCoEfficent);
                    attributeNameValueMap.put(attribute.getDestinationName(), jsonAttributeValue);
                } else {
                    if (attribute.getSourceName().equalsIgnoreCase("Net quantity")) {
                        Double usageCoefficientForMassAndVolume = Double.parseDouble(jsonAttributeValue);
                        if (valComponentMaterialMassUnitList.contains(inventoryUnit) || valComponentMaterialVolumeUnitList.contains(inventoryUnit)) {

                            if (materialsNegativeQuantity) {
                                usageCoefficientForMassAndVolume = usageCoefficientForMassAndVolume * (-1);
                                materialsNegativeQuantity = false;
                            }

                            usageCoefficientForMassAndVolume = commonUtilities.unitConversion(inventoryUnit, usageCoefficientForMassAndVolume);
                        }

                        jsonAttributeValue = usageCoefficientForMassAndVolume.toString();
                    }

                    jsonAttributeValue = convertToRealValues(attribute, jsonAttributeValue);
                    attributeNameValueMap.put(attribute.getDestinationName(), jsonAttributeValue);
                }
            }

            /*
                If a line contain same child with same position, then considering as same BOM. 
             */
            if (childInfoMap.containsKey(childName + childPosition)) {
                ChildInfo child = childInfoMap.get(childName + childPosition);
                int previousQuantity = child.getChildQuantity();
                childInfo.setChildQuantity((int) (Math.round(childQuantityFloat) + previousQuantity));
            } else {
                childInfo.setChildQuantity((int) Math.round(childQuantityFloat));
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(connectedChildRelIDlist)) {
                childInfo.setRelIDList(connectedChildRelIDlist);
            } else {
                childInfo.setRelIDList(null);
            }
            childInfo.setRelName(relName);
            childInfo.setInterfaceName(interfaceName);
            childInfo.setParentId(parentID);
            childInfo.setChildId(childId);
            childInfo.setAttributeNameValueMap(attributeNameValueMap);

            childInfoMap.put(childName + childPosition, childInfo);
        } catch (Exception exp) {
            parentInfo.setErrorMessage(exp.getMessage());
            List<ParentInfo> errorParentInfoList = new ArrayList<>();
            errorParentInfoList.add(parentInfo);

            if (responseMsgMap.containsKey("Error")) {
                responseMsgMap.get("Error").addAll(errorParentInfoList);
            } else {
                responseMsgMap.put("Error", errorParentInfoList);
            }
            throw exp;
        }
        return dataCollector;
    }

    private String searchAndGetChildType(String childType, String childName, CommonPropertyReader commonPropertyReader) {
        try {
            XmlParse xmlParse = new XmlParse();

            String enoviaChildType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                    commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                    childType);

            if (NullOrEmptyChecker.isNullOrEmpty(enoviaChildType)) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.child.type"),
                        "'" + childType + "'");
                throw new RuntimeException(errorMessage);
            }
            childType = enoviaChildType;

            BOM_IMPORT_VALIDATOR_LOGGER.debug("ENOVIA Child type : " + childType + " Child name " + childName);
            return childType;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("invalid.child.type"),
                    "'" + childType + "'");
            throw new RuntimeException(errorMessage);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private double quantityCalculationForLengthAndArea(double lengthOrArea, double noOfUnit, double childQuantityFloat, double usageCoEfficent, ChildInfo childInfo, String parentName) throws NumberFormatException, RuntimeException {
        double quantityCalculationResult = lengthOrArea * noOfUnit;
        Integer preciseValue = Integer.parseInt(PropertyReader.getProperty("bom.quantityCalculation.and.netQuantity.precision"));
        quantityCalculationResult = new BigDecimal(quantityCalculationResult).setScale(preciseValue, RoundingMode.HALF_UP).doubleValue();
        double childQuantityDouble = new BigDecimal(childQuantityFloat).setScale(preciseValue, RoundingMode.HALF_UP).doubleValue();

        BOM_IMPORT_VALIDATOR_LOGGER.info(childInfo.getChildTNR().getName() + "'s Calculated Quantity : " + quantityCalculationResult + " net quantity is :" + childQuantityDouble);

        if (quantityCalculationResult == 0) {
            usageCoEfficent = childQuantityFloat;
            childInfo.setChildNoOfUnit(1);
        } else if (quantityCalculationResult == childQuantityDouble) {
            usageCoEfficent = lengthOrArea;
            childInfo.setChildNoOfUnit((int) Math.round(noOfUnit));
        } else {
            throw new RuntimeException(MessageFormat.format(PropertyReader.getProperty("bom.quantityCalculationResult.and.netQuantity.is.inEqual"), parentName, childInfo.getChildTNR().getName()));
        }
        return usageCoEfficent;
    }

    /**
     *
     * @param attribute
     * @param jsonAttributeValue
     * @return
     */
    private String convertToRealValues(Attribute attribute, String jsonAttributeValue) {
        BOM_IMPORT_VALIDATOR_LOGGER.debug("Converting attribute to real values !! ");
        String sourceName = attribute.getSourceName();
        DataType attributeDataType = attribute.getDataType();
        String dataType = attributeDataType.getDataType();
        Double divisor = attributeDataType.getDivisor();
        Integer precision = attributeDataType.getPrecision();

        if (!NullOrEmptyChecker.isNullOrEmpty(dataType) && dataType.equalsIgnoreCase("real")) {
            try {
                double parsedValue = Double.parseDouble(jsonAttributeValue);

                if (!NullOrEmptyChecker.isNull(divisor)) {
                    parsedValue = parsedValue / divisor;
                    if (!NullOrEmptyChecker.isNullOrEmpty(precision)) {
                        parsedValue = new BigDecimal(parsedValue).setScale(precision, RoundingMode.HALF_UP).doubleValue();

                        BOM_IMPORT_VALIDATOR_LOGGER.debug("Precised value is : '" + parsedValue + "'");
                    } else {
                        BOM_IMPORT_VALIDATOR_LOGGER.debug("Precision has not been set for '" + sourceName + "'");
                    }
                } else {
                    BOM_IMPORT_VALIDATOR_LOGGER.debug("Divisor has not been set for '" + sourceName + "'");
                }
                BOM_IMPORT_VALIDATOR_LOGGER.debug("Updated value is : '" + parsedValue + "'");
                return Double.toString(parsedValue);
            } catch (NumberFormatException exp) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
            } catch (NullPointerException exp) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
            } catch (Exception exp) {
                BOM_IMPORT_VALIDATOR_LOGGER.error(exp);
            }
        }
        return jsonAttributeValue;
    }

    /*private HashMap<String, String> relationShipsDefaultAttributes(HashMap<String, String> relationAttributeMap, String type) throws IOException {
        if (NullOrEmptyChecker.isNullOrEmpty(relationAttributeMap)) {
            relationAttributeMap = new HashMap<String, String>();
        }

        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HashMap<String, String> transferTorErpMap = commonPropertyReader.getPropertyValue("item.type.transfer", Boolean.TRUE);
        ODI_BOM_IMPORT_THREAD_LOGGER.info("Transfer to ERP mapped : " + transferTorErpMap);
        ODI_BOM_IMPORT_THREAD_LOGGER.debug("Item type : " + type);
        String attribute = transferTorErpMap.get(type);
        ODI_BOM_IMPORT_THREAD_LOGGER.debug("Attribute name is : " + attribute);
        relationAttributeMap.put(attribute, "true");
        commonPropertyReader = null;
        return relationAttributeMap;
    }*/
    /**
     *
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param commonPropertyReader
     * @param childRev
     * @param context1
     * @param childType
     * @param childName
     * @param errorMsgBilder
     * @param parentName
     * @param position
     * @param childInfo
     * @param childId
     * @return
     */
    //private String findChildBusinessObject(CommonPropertyReader commonPropertyReader, String childRev, Context context1, String childType, String childName, StringBuilder errorMsgBilder, String parentName, String position, ChildInfo childInfo, String childId) {
    private String findChildBusinessObject(BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CommonPropertyReader commonPropertyReader, String childRev, Context context1, String childType, String childName, StringBuilder errorMsgBilder, String parentName, String position, ChildInfo childInfo, String childId) {
        BOM_IMPORT_VALIDATOR_LOGGER.debug("Finding Child Item !! ");
        //boolean parentStatus;
        //boolean isSuccessfulTransaction;
        String errorMsg;
        try {
            BusinessObject childBO = businessObjectUtil.findPDMItem(context1, childType, childName, childRev);
            if (childBO == null) {
            } else {
                childId = childBO.getObjectId();
            }
        } catch (NullPointerException ex) {
            errorMsg = ex.getMessage();
            errorMsgBilder.append(parentName)
                    .append("/")
                    .append(position)
                    .append("/")
                    .append(childName)
                    .append(" : ")
                    .append(errorMsg)
                    .append("\n");
            childInfo.setMessage(errorMsgBilder.toString());
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (MatrixException ex) {
            errorMsg = ex.getMessage();
            errorMsgBilder.append(parentName)
                    .append("/")
                    .append(position)
                    .append("/")
                    .append(childName)
                    .append(" : ")
                    .append(errorMsg)
                    .append("\n");
            childInfo.setMessage(errorMsgBilder.toString());
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
            errorMsgBilder.append(parentName)
                    .append("/")
                    .append(position)
                    .append("/")
                    .append(childName)
                    .append(" : ")
                    .append(errorMsg)
                    .append("\n");
            childInfo.setMessage(errorMsgBilder.toString());
            BOM_IMPORT_VALIDATOR_LOGGER.error(ex);
        }
        return childId;
    }

    public final boolean isPositiveInteger(String s) {
        return POSITIVE_INTEGER_PATTERN.matcher(s).matches();
    }

    /**
     *
     * @param errorMessage
     * @param childName
     * @param childPosition
     * @param childInfo
     * @param parentInfo
     * @param childInfoMap
     * @param requestParentChildInfoMap
     * @param existingChildInfoRelMap
     * @param dataCollector
     * @return
     */
    private BOMDataCollector childErrorMessageBuilder(String errorMessage, String childName, String childPosition, ChildInfo childInfo, ParentInfo parentInfo, HashMap<String, ChildInfo> childInfoMap, HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap, HashMap<String, ArrayList<String>> existingChildInfoRelMap, BOMDataCollector dataCollector) {
        BOM_IMPORT_VALIDATOR_LOGGER.debug("Building error message for child item " + childName);
        String childMessage = childInfo.getMessage();
        childInfo.setMessage(!NullOrEmptyChecker.isNullOrEmpty(childMessage) ? (childMessage + System.lineSeparator() + errorMessage) : errorMessage);

        childInfoMap.put(childName + childPosition, childInfo);
        requestParentChildInfoMap.put(parentInfo, childInfoMap);
        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
        dataCollector.setBOMcontainError(true);
        return dataCollector;
    }

    /**
     *
     * @param errorMessage
     * @param parentInfo
     * @param childInfoMap
     * @param requestParentChildInfoMap
     * @param existingChildInfoRelMap
     * @param dataCollector
     * @return
     */
    private BOMDataCollector parentErrorMessageBuilder(String errorMessage, ParentInfo parentInfo, HashMap<String, ChildInfo> childInfoMap, HashMap<ParentInfo, HashMap<String, ChildInfo>> requestParentChildInfoMap, HashMap<String, ArrayList<String>> existingChildInfoRelMap, BOMDataCollector dataCollector) {
        BOM_IMPORT_VALIDATOR_LOGGER.debug("Building error message for Parent item " + parentInfo.getTnr().getName());

        parentInfo.setErrorMessage(errorMessage);

        requestParentChildInfoMap.put(parentInfo, childInfoMap);
        dataCollector.setRequestParentChildInfoMap(requestParentChildInfoMap);
        dataCollector.setExistingChildInfoRelMap(existingChildInfoRelMap);
        dataCollector.setBOMcontainError(true);
        return dataCollector;
    }
}
