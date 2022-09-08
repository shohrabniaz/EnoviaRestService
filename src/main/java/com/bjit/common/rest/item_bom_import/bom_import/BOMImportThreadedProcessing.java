/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.bom_import;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.db.Vault;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 * @param <K>
 */
public final class BOMImportThreadedProcessing<K> implements Callable {

    private static final org.apache.log4j.Logger BOM_IMPORT_THREADED_PROCESSING_LOGGER = org.apache.log4j.Logger.getLogger(BOMImportThreadedProcessing.class);

    private final Context context;
    private final HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap;
    private final BOMImportVariables bomImportVariables;
    private final List<ParentInfo> errorParentInfoList;
    private final HashMap<String, List<ParentInfo>> responseMsgMap;
    private final BusinessObjectUtil businessObjectUtil;
    private final List<ParentInfo> successfulParentInfoList;
    private final HashMap<String, Long> bomValidationAndProcessingTimeForParentMap;

    private int disconnectCounter = 0;

    public BOMImportThreadedProcessing(Context context, HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap, BOMImportVariables bomImportVariables) {
        this.context = context;
        this.parentChildInfoMap = parentChildInfoMap;
        this.bomImportVariables = bomImportVariables;

        this.errorParentInfoList = bomImportVariables.errorParentInfoList;
        this.responseMsgMap = bomImportVariables.responseMsgMap;
        this.businessObjectUtil = bomImportVariables.businessObjectUtil;
        this.successfulParentInfoList = bomImportVariables.successfulParentInfoList;
        this.bomValidationAndProcessingTimeForParentMap = bomImportVariables.bomValidationAndProcessingTimeForParentMap;
    }

    @Override
    public Object call() throws Exception {
        for (ParentInfo parentInfoKey : parentChildInfoMap.keySet()) {
            try {
                StringBuilder errorMsgBuilder = new StringBuilder();
                ParentInfo responseParentInfo = new ParentInfo();
                if (!NullOrEmptyChecker.isNullOrEmpty(parentInfoKey.getErrorMessage())) {
                    bomImportVariables.errorInStructure = true;
                    errorMsgBuilder.append(parentInfoKey.getErrorMessage());
                    responseParentInfo.setTnr(parentInfoKey.getTnr());
                    responseParentInfo.setErrorMessage(errorMsgBuilder.toString());
                    errorParentInfoList.add(responseParentInfo);
                    responseMsgMap.put("Error", errorParentInfoList);
                    return (K) responseMsgMap;
                }

                String parentName = parentInfoKey.getTnr().getName();
                responseParentInfo.setTnr(parentInfoKey.getTnr());
                HashMap<String, ChildInfo> childInfoMap = parentChildInfoMap.get(parentInfoKey);

                int child_in_a_BOM = 0;
                int unprocessed_connection = 0;
                long start_BOM_process_time = System.currentTimeMillis();

                for (String childNamePositionKey : childInfoMap.keySet()) {
                    ChildInfo childInfo = childInfoMap.get(childNamePositionKey);
                    if (!childInfo.getMessage().isEmpty() && childInfo.getMessage() != null) {
                        bomImportVariables.errorInStructure = true;
                        errorMsgBuilder.append(childInfo.getMessage());
                        responseParentInfo.setErrorMessage(errorMsgBuilder.toString());
                        errorParentInfoList.add(responseParentInfo);
                        responseMsgMap.put("Error", errorParentInfoList);
                        return (K) responseMsgMap;
                    }

                    //HashMap<String, String> relationshipAttributes = relationShipsDefaultAttributes(childInfo.getAttributeNameValueMap());
                    HashMap<String, String> relationshipAttributes = relationShipsDefaultAttributes(childInfo.getAttributeNameValueMap(), childInfo.getChildTNR());
                    //HashMap<String, String> relationshipAttributes = childInfo.getAttributeNameValueMap();

                    childInfo.setAttributeNameValueMap(relationshipAttributes);
                    /*
                            Start: VSIX-3423 - MBOM import in Enovia
                     */
                    String childInventoryUnit = childInfo.getChildInventoryUnit();//Inventory Unit only available for Material Item
                    int noOfDuplicateChild = 0;

                    boolean isChildHasNumberOfUnits = true;
                    if (childInventoryUnit != null) {
                        List valComponentMaterialLengthUnitList = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
                        List valComponentMaterialAreaUnitList = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
                        /*
                            Number of Units is only considerable for VALComponentMaterial,if the inventory unit is Length(m,in,ft) 
                            or Area(m2,in2,ft2) then BOM will split based on Number of Units
                         */
                        if (valComponentMaterialLengthUnitList.contains(childInventoryUnit) || valComponentMaterialAreaUnitList.contains(childInventoryUnit)) {
                            noOfDuplicateChild = childInfo.getChildNoOfUnit();
                        } else {
                            /*
                                If VAlComponentMaterial's inventory unit is Mass(g,kg,lb,g) or volumn(m3,in3,ft3,l) then BOM will not split.
                             */
                            isChildHasNumberOfUnits = false;
                        }
                    } else {
                        /*
                            All type except VALComponentMaterial, BOM will split depending on child quantity. 
                         */
                        noOfDuplicateChild = childInfo.getChildQuantity();
                    }

                    // End: VSIX-3423 - MBOM import in Enovia
                    child_in_a_BOM = child_in_a_BOM + noOfDuplicateChild;

                    if (!NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelIDList())) {

                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Modifying relationship from parent :  " + parentName + " to child : " + childInfo.getChildTNR().getName());

                        if (!isChildHasNumberOfUnits) {

                            childInfo.getRelIDList().stream().parallel().forEach((String relID) -> {

                                bomImportVariables.modified_connection++;
                                Instant bomUpdateStartTime = Instant.now();
                                try {
                                    modifyRelationship(context, businessObjectUtil, relID, relationshipAttributes, childInfo);
                                } catch (MatrixException | InterruptedException ex) {
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                    throw new RuntimeException(ex);
                                }

                                Instant BomUpdateEndTime = Instant.now();
                                long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Modify | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relID + " | " + bomUpdateDuration);
                            });

                        } else {
                            int numOfExisitingChild = childInfo.getRelIDList().size(); //num of child with same name and position

                            if (numOfExisitingChild < noOfDuplicateChild) {
                                childInfo.getRelIDList().stream().parallel().forEach((String relID) -> {
                                    Instant bomUpdateStartTime = Instant.now();
                                    bomImportVariables.modified_connection++;
                                    try {
                                        modifyRelationship(context, businessObjectUtil, relID, relationshipAttributes, childInfo);
                                    } catch (MatrixException | InterruptedException ex) {
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                        throw new RuntimeException(ex);
                                    }
                                    Instant BomUpdateEndTime = Instant.now();
                                    long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Modify | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relID + " | " + bomUpdateDuration);
                                });

                                int noOfNewConnection = noOfDuplicateChild - numOfExisitingChild;
                                BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Requested new duplicate connection : No of duplicate connection " + noOfNewConnection);
                                IntStream.range(0, noOfNewConnection).parallel().forEach((int iterator) -> {
                                    bomImportVariables.new_connection++;
                                    Instant bomUpdateStartTime = Instant.now();
                                    String relationshipName;
                                    try {
                                        relationshipName = connectBusinessObjects(childInfo, businessObjectUtil, relationshipAttributes, context);
                                    } catch (MatrixException | InterruptedException | IOException ex) {
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                        throw new RuntimeException(ex);
                                    }
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug(" Relationship Name : " + relationshipName);
                                    Instant BomUpdateEndTime = Instant.now();
                                    long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Connect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relationshipName + " | " + bomUpdateDuration);
                                });

                                unprocessed_connection = numOfExisitingChild;
                            } else {
                                final int numOfChildToDisconnect = numOfExisitingChild - noOfDuplicateChild;
                                disconnectCounter = 0;

                                ArrayList<String> childRelIdList = childInfo.getRelIDList();
                                List<String> disconnectedRelIds = new ArrayList<>();
                                IntStream.range(0, numOfChildToDisconnect).parallel().forEach(iterator -> {
                                    try {
                                        Instant bomUpdateStartTime = Instant.now();
                                        String relId = childRelIdList.get(iterator);
                                        businessObjectUtil.disconnectRelationship(context, relId);
                                        bomImportVariables.deleted_connection++;
                                        Instant BomUpdateEndTime = Instant.now();
                                        disconnectedRelIds.add(relId);
                                        //childRelIdList.remove(iterator);
                                        long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Disconnect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID " + relId + " | " + bomUpdateDuration);
                                    } catch (MatrixException ex) {
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                        throw new RuntimeException(ex);
                                    } catch (Exception ex) {
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                        throw new RuntimeException(ex);
                                    }
                                });

                                childRelIdList.removeAll(disconnectedRelIds);
                                childRelIdList.stream().parallel().forEach(relId -> {
                                    try {
                                        Instant bomUpdateStartTime = Instant.now();
                                        bomImportVariables.modified_connection++;
                                        modifyRelationship(context, businessObjectUtil, relId, relationshipAttributes, childInfo);
                                        Instant BomUpdateEndTime = Instant.now();
                                        long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Modify | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relId + " | " + bomUpdateDuration);
                                    } catch (MatrixException | InterruptedException ex) {
                                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                        throw new RuntimeException(ex);
                                    }

                                });
                            }
                        }

                    }
                    if (!bomImportVariables.errorInStructure && NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelIDList())) {
                        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Creating relationship from parent ::  " + parentName + " to child :: " + childInfo.getChildTNR().getName());

                        if (!isChildHasNumberOfUnits) {
                            Instant bomUpdateStartTime = Instant.now();
                            String relationShipName = connectBusinessObjects(childInfo, businessObjectUtil, relationshipAttributes, context);
                            bomImportVariables.new_connection++;
                            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug(" Relationship Name : " + relationShipName);
                            Instant BomUpdateEndTime = Instant.now();
                            long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                            BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Connect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relationShipName + " | " + bomUpdateDuration);
                        } else {

                            IntStream.range(0, noOfDuplicateChild).parallel().forEach(iterator -> {
                                try {
                                    Instant bomUpdateStartTime = Instant.now();
                                    String relationShipName = connectBusinessObjects(childInfo, businessObjectUtil, relationshipAttributes, context);
                                    bomImportVariables.new_connection++;
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Relationship Name : " + relationShipName);
                                    Instant BomUpdateEndTime = Instant.now();
                                    long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Connect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relationShipName + " | " + bomUpdateDuration);
                                } catch (MatrixException | InterruptedException | IOException ex) {
                                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                                    throw new RuntimeException(ex);
                                }
                            });
                        }

                    }
                }
                long end_BOM_process_time = System.currentTimeMillis();
                long total_BOM_process_time = end_BOM_process_time - start_BOM_process_time;

                BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Data | Parent | " + parentName + " | Total child=" + child_in_a_BOM);
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.info(" | Process Time | Single BOM | " + parentName + " | " + total_BOM_process_time);
                bomImportVariables.total_child_in_ENOVIA = bomImportVariables.total_child_in_ENOVIA + child_in_a_BOM;

                if (bomValidationAndProcessingTimeForParentMap.containsKey(parentName)) {
                    bomValidationAndProcessingTimeForParentMap.put(parentName, bomValidationAndProcessingTimeForParentMap.get(parentName) + total_BOM_process_time);
                }
                successfulParentInfoList.add(responseParentInfo);
            } catch (Exception exp) {
                ParentInfo responseParentInfo = new ParentInfo();
                responseParentInfo.setTnr(parentInfoKey.getTnr());
                responseParentInfo.setErrorMessage(exp.getMessage());
                errorParentInfoList.add(responseParentInfo);
                responseMsgMap.put("Error", errorParentInfoList);
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp);
                throw exp;
            }
        }

        return true;
    }

    private String connectBusinessObjects(ChildInfo childInfo, BusinessObjectUtil businessObjectUtil, HashMap<String, String> relationshipAttributes, Context context) throws MatrixException, IOException, InterruptedException {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Start creating new relationship !! ");
        String interfaceName;
        Instant bomCreateStartTime = Instant.now();
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        BusinessObject parentBO = new BusinessObject(childInfo.getParentId());
        BusinessObject childBO = new BusinessObject(childInfo.getChildId());
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName(childInfo.getRelName());
        AttributeList attributeList = createAttributeList(relationshipAttributes);
        interfaceName = childInfo.getInterfaceName();

        if (Optional.ofNullable(relationshipAttributes.get(PropertyReader.getProperty("attribute.material.items.by.product"))).isPresent()) {
            interfaceName = interfaceName + ", " + PropertyReader.getProperty("attribute.material.items.by.product.interface");
        }

        matrix.db.Relationship relationship = parentBO.connect(context, relationshipType, true, childBO);
        //relationship.open(context);
        if (!NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {

            List<String> interfaceList = Arrays.asList(interfaceName.split(","));
            interfaceList.forEach((String relationshipInterface) -> {
                relationshipInterface = relationshipInterface.trim();

                BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Adding interface : '" + relationshipInterface + "' to the relationship");

                Vault vault = new Vault("vplm");
                BusinessInterface businessInterface = new BusinessInterface(relationshipInterface, vault);
                try {
                    relationship.addBusinessInterface(context, businessInterface);
                } catch (MatrixException ex) {
                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                }
            });

        }
        relationship.setAttributeValues(context, attributeList);
        //relationship.close(context);
        String relationShipName = relationship.getName();
        businessObjectUtil.modifyConnection(context, relationShipName, relationshipAttributes, childInfo.getPropertyNameValueMap());

        return relationShipName;
    }

    private AttributeList createAttributeList(HashMap<String, String> attributeNameValueMap) {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Create Attribute list !! ");
        AttributeList attributeList = new AttributeList();
        for (String attributeName : attributeNameValueMap.keySet()) {
            AttributeType at = new AttributeType(attributeName);
            Attribute attribute = new Attribute(at, attributeNameValueMap.get(attributeName));
            attributeList.addElement(attribute);
        }
        return attributeList;
    }

    private void modifyRelationship(Context context, BusinessObjectUtil businessObjectUtil, String relID, HashMap<String, String> relationshipAttributes, ChildInfo childInfo) throws MatrixException, InterruptedException {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Start modify relationship !! ");
        String interfaceNames = childInfo.getInterfaceName();
        if (Optional.ofNullable(relationshipAttributes.get(PropertyReader.getProperty("attribute.material.items.by.product"))).isPresent()) {
            interfaceNames = interfaceNames + ", " + PropertyReader.getProperty("attribute.material.items.by.product.interface");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(interfaceNames)) {
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Start adding interface ::  " + interfaceNames);
            List<String> interfaceListFromMap = Arrays.asList(interfaceNames.split(","));

            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Interface List: " + interfaceListFromMap);

            List<String> relationShipInterfacesExistsInRelationship = getBusinessObjectInterfaces(context, relID);
            interfaceListFromMap.replaceAll(String::trim);

            List<String> uniqueInterfaces = removeAll(interfaceListFromMap, relationShipInterfacesExistsInRelationship);

            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("New Interfaces are : " + uniqueInterfaces);

            uniqueInterfaces.forEach((String connectionInterface) -> {
                try {
                    addInterfaceToTheRelationship("", connectionInterface, relID, context);
                } catch (FrameworkException ex) {
                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                } catch (Exception ex) {
                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                }
            });

            List<String> uniqueRemovableInterfaces = removeAll(relationShipInterfacesExistsInRelationship, interfaceListFromMap);
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Removable Interfaces are : " + uniqueRemovableInterfaces);

            uniqueRemovableInterfaces.forEach((String connectionInterface) -> {
                try {
                    removeInterfaceFromTheRelationship("", connectionInterface, relID, context);
                } catch (FrameworkException ex) {
                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                } catch (Exception ex) {
                    BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(ex);
                }
            });
        }

        businessObjectUtil.modifyConnection(context, relID, relationshipAttributes, childInfo.getPropertyNameValueMap());
    }

    private void addInterfaceToTheRelationship(String vault, String interfaceName, String relationshipId, Context context) throws MatrixException {
        try {
            matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);
            interfaceName = interfaceName.trim();

            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Adding interface '" + interfaceName + "' to the relationship");

            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            relationShip.addBusinessInterface(context, businessInterface);
        } catch (MatrixException exp) {
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private void removeInterfaceFromTheRelationship(String vault, String interfaceName, String relationshipId, Context context) throws MatrixException {
        try {
            matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);
            interfaceName = interfaceName.trim();

            BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Removing interface '" + interfaceName + "' to the relationship");

            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            relationShip.removeBusinessInterface(context, businessInterface);
        } catch (MatrixException exp) {
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private List<String> removeAll(List<String> firstList, List<String> secondList) {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Remove List (Generic method to remove item from a list) !!");
        List<String> resultantList = new ArrayList<>();
        firstList.forEach(item -> {
            boolean notFound = true;
            for (int index = 0; index < secondList.size(); index++) {
                if (item.trim().equalsIgnoreCase(secondList.get(index).trim())) {
                    notFound = false;
                    break;
                }
            }

            if (notFound) {
                resultantList.add(item.trim());
            }
        });

        return resultantList;
    }

    public List<String> getBusinessObjectInterfaces(Context context, String relationshipId) throws MatrixException, RuntimeException {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Get Interface from BusinessObject !!");
        matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);

        //relationShip.open(context);
        BusinessInterfaceList businessInterfaceList = relationShip.getBusinessInterfaces(context, true);
        List<String> businessObjectInterfaceList = new ArrayList<>();

        for (BusinessInterface businessObjectInterface : businessInterfaceList) {
            try {
                //businessObjectInterface.open(context);
                String existingInterface = businessObjectInterface.getName();
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Existing Interface : '" + existingInterface + "'");
                businessObjectInterfaceList.add(existingInterface);

            } catch (NullPointerException exp) {
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                BOM_IMPORT_THREADED_PROCESSING_LOGGER.error(exp.getMessage());
                throw exp;
            } finally {
                //businessObjectInterface.close(context);
            }
        }
        return businessObjectInterfaceList;
    }

    private HashMap<String, String> relationShipsDefaultAttributes(HashMap<String, String> relationAttributeMap, TNR tnr) throws IOException {
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Preparing relationship default attribute !! ");
        if (NullOrEmptyChecker.isNullOrEmpty(relationAttributeMap)) {
            relationAttributeMap = new HashMap<String, String>();
        }

        String type = tnr.getType();
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HashMap<String, String> transferTorErpMap = commonPropertyReader.getPropertyValue("item.type.transfer", Boolean.TRUE);
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Transfer to ERP mapped : " + transferTorErpMap);
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Item type : " + type);
        String attribute = transferTorErpMap.get(type);
        BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Attribute name is : " + attribute);
        relationAttributeMap.put(attribute, "true");
        commonPropertyReader = null;
        return relationAttributeMap;
    }
}
