/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.utilities;

import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.db.Vault;
import matrix.util.MatrixException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommonBOMUtilities {

    private static final org.apache.log4j.Logger COMMON_BOM_UTILITIES_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMUtilities.class);

    private Context context;
    private BusinessObjectUtil businessObjectUtil;
    private CommonBOMImportParams mopazBomImportVariables;
    private List<String> processedConnections;

    public CommonBOMUtilities() {
    }

    public CommonBOMUtilities(Context context, BusinessObjectUtil businessObjectUtil, CommonBOMImportParams mopazBomImportVariables) {
        this.context = context;
        this.businessObjectUtil = businessObjectUtil;
        this.mopazBomImportVariables = mopazBomImportVariables;
        this.processedConnections = mopazBomImportVariables.processedConnections;
    }

    public void disconnectChildFromParent(int iterator, ArrayList<String> childRelIdList, List<String> disconnectedRelIds, ChildInfo childInfo, String parentName) throws RuntimeException {
        try {
            Instant bomUpdateStartTime = Instant.now();
            String relationshipName = childRelIdList.get(iterator);
            businessObjectUtil.disconnectRelationship(context, relationshipName);
            mopazBomImportVariables.deleted_connection++;
            Instant BomUpdateEndTime = Instant.now();
            disconnectedRelIds.add(relationshipName);
            this.processedConnections.add(relationshipName);
            //childRelIdList.remove(iterator);
            long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
            COMMON_BOM_UTILITIES_LOGGER.info(" | Disconnect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID " + relationshipName + " | " + bomUpdateDuration);
        } catch (MatrixException ex) {
            COMMON_BOM_UTILITIES_LOGGER.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public void updateExistingConnectionAttributes(ChildInfo childInfo, String relationshipId, HashMap<String, String> relationshipAttributes, String parentName) throws RuntimeException {
        Instant bomUpdateStartTime = Instant.now();
        mopazBomImportVariables.modified_connection++;
        try {
            modifyRelationship(context, businessObjectUtil, relationshipId, relationshipAttributes, childInfo);
        } catch (MatrixException | InterruptedException ex) {
            COMMON_BOM_UTILITIES_LOGGER.error(ex);
            throw new RuntimeException(ex);
        }
        Instant BomUpdateEndTime = Instant.now();
        long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
        this.processedConnections.add(relationshipId);
        COMMON_BOM_UTILITIES_LOGGER.info(" | Modify | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relationshipId + " | " + bomUpdateDuration);
    }

    public void createNewConnection(ChildInfo childInfo, HashMap<String, String> relationshipAttributes, String parentName) throws RuntimeException {
        mopazBomImportVariables.new_connection++;
        Instant bomUpdateStartTime = Instant.now();
        String relationshipName;
        try {
            relationshipName = connectBusinessObjects(childInfo, businessObjectUtil, relationshipAttributes, context);
            this.processedConnections.add(relationshipName);
        } catch (MatrixException | InterruptedException | IOException ex) {
            COMMON_BOM_UTILITIES_LOGGER.error(ex);
            throw new RuntimeException(ex);
        }
        COMMON_BOM_UTILITIES_LOGGER.debug(" Relationship Name : " + relationshipName);
        Instant BomUpdateEndTime = Instant.now();
        long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
        COMMON_BOM_UTILITIES_LOGGER.info(" | Connect | " + parentName + " | " + childInfo.getChildTNR().getName() + " | Qty=" + childInfo.getChildQuantity() + " | rel_ID=" + relationshipName + " | " + bomUpdateDuration);
    }

    public String connectBusinessObjects(ChildInfo childInfo, BusinessObjectUtil businessObjectUtil, HashMap<String, String> relationshipAttributes, Context context) throws MatrixException, IOException, InterruptedException {
        COMMON_BOM_UTILITIES_LOGGER.debug("Start creating new relationship !! ");
        String interfaceName;
        Instant bomCreateStartTime = Instant.now();

        BusinessObject parentBO = new BusinessObject(childInfo.getParentId());
        BusinessObject childBO = new BusinessObject(childInfo.getChildId());
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName(childInfo.getRelName());
        AttributeList attributeList = createAttributeList(relationshipAttributes);
        interfaceName = childInfo.getInterfaceName();

        if (Optional.ofNullable(relationshipAttributes.get(PropertyReader.getProperty("attribute.material.items.by.product"))).isPresent()) {
            interfaceName = interfaceName + ", " + PropertyReader.getProperty("attribute.material.items.by.product.interface");
        }

        parentBO.open(context);
        COMMON_BOM_UTILITIES_LOGGER.info("Connecting child " + childInfo.getChildTNR().toString() + " from Parent " + parentBO.getName());
        parentBO.close(context);

        matrix.db.Relationship relationship = parentBO.connect(context, relationshipType, true, childBO);
        //relationship.open(context);
        if (!NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {

            List<String> interfaceList = Arrays.asList(interfaceName.split(","));
            interfaceList.forEach((String relationshipInterface) -> {
                relationshipInterface = relationshipInterface.trim();

                COMMON_BOM_UTILITIES_LOGGER.debug("Adding interface : '" + relationshipInterface + "' to the relationship");

                Vault vault = new Vault("vplm");
                BusinessInterface businessInterface = new BusinessInterface(relationshipInterface, vault);
                try {
                    relationship.addBusinessInterface(context, businessInterface);
                } catch (MatrixException ex) {
                    COMMON_BOM_UTILITIES_LOGGER.error(ex);
                }
            });

        }
        relationship.setAttributeValues(context, attributeList);
        //relationship.close(context);
        String relationShipName = relationship.getName();
        businessObjectUtil.modifyConnection(context, relationShipName, relationshipAttributes, childInfo.getPropertyNameValueMap());

        return relationShipName;
    }

    public AttributeList createAttributeList(HashMap<String, String> attributeNameValueMap) {
        COMMON_BOM_UTILITIES_LOGGER.debug("Create Attribute list !! ");
        AttributeList attributeList = new AttributeList();
        for (String attributeName : attributeNameValueMap.keySet()) {
            AttributeType at = new AttributeType(attributeName);
            Attribute attribute = new Attribute(at, attributeNameValueMap.get(attributeName));
            attributeList.addElement(attribute);
        }
        return attributeList;
    }

    public void modifyRelationship(Context context, BusinessObjectUtil businessObjectUtil, String relID, HashMap<String, String> relationshipAttributes, ChildInfo childInfo) throws MatrixException, InterruptedException {
        COMMON_BOM_UTILITIES_LOGGER.debug("Start modify relationship !! ");
        String interfaceNames = childInfo.getInterfaceName();
        if (Optional.ofNullable(relationshipAttributes.get(PropertyReader.getProperty("attribute.material.items.by.product"))).isPresent()) {
            interfaceNames = interfaceNames + ", " + PropertyReader.getProperty("attribute.material.items.by.product.interface");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(interfaceNames)) {
            COMMON_BOM_UTILITIES_LOGGER.debug("Start adding interface ::  " + interfaceNames);
            List<String> interfaceListFromMap = Arrays.asList(interfaceNames.split(","));

            COMMON_BOM_UTILITIES_LOGGER.debug("Interface List: " + interfaceListFromMap);

            List<String> relationShipInterfacesExistsInRelationship = getBusinessObjectInterfaces(context, relID);
            interfaceListFromMap.replaceAll(String::trim);

            List<String> uniqueInterfaces = removeAll(interfaceListFromMap, relationShipInterfacesExistsInRelationship);

            COMMON_BOM_UTILITIES_LOGGER.debug("New Interfaces are : " + uniqueInterfaces);

            uniqueInterfaces.forEach((String connectionInterface) -> {
                try {
                    addInterfaceToTheRelationship("", connectionInterface, relID, context);
                } catch (FrameworkException ex) {
                    COMMON_BOM_UTILITIES_LOGGER.error(ex);
                } catch (Exception ex) {
                    COMMON_BOM_UTILITIES_LOGGER.error(ex);
                }
            });

            List<String> uniqueRemovableInterfaces = removeAll(relationShipInterfacesExistsInRelationship, interfaceListFromMap);
            COMMON_BOM_UTILITIES_LOGGER.debug("Removable Interfaces are : " + uniqueRemovableInterfaces);

            uniqueRemovableInterfaces.forEach((String connectionInterface) -> {
                try {
                    removeInterfaceFromTheRelationship("", connectionInterface, relID, context);
                } catch (FrameworkException ex) {
                    COMMON_BOM_UTILITIES_LOGGER.error(ex);
                } catch (Exception ex) {
                    COMMON_BOM_UTILITIES_LOGGER.error(ex);
                }
            });
        }

        businessObjectUtil.modifyConnection(context, relID, relationshipAttributes, childInfo.getPropertyNameValueMap());
    }

    public void addInterfaceToTheRelationship(String vault, String interfaceName, String relationshipId, Context context) throws MatrixException {
        try {
            matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);
            interfaceName = interfaceName.trim();

            COMMON_BOM_UTILITIES_LOGGER.debug("Adding interface '" + interfaceName + "' to the relationship");

            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            relationShip.addBusinessInterface(context, businessInterface);
        } catch (MatrixException exp) {
            COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void removeInterfaceFromTheRelationship(String vault, String interfaceName, String relationshipId, Context context) throws MatrixException {
        try {
            matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);
            interfaceName = interfaceName.trim();

            COMMON_BOM_UTILITIES_LOGGER.debug("Removing interface '" + interfaceName + "' to the relationship");

            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            relationShip.removeBusinessInterface(context, businessInterface);
        } catch (MatrixException exp) {
            COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public List<String> removeAll(List<String> firstList, List<String> secondList) {
        COMMON_BOM_UTILITIES_LOGGER.debug("Remove List (Generic method to remove item from a list) !!");
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
        COMMON_BOM_UTILITIES_LOGGER.debug("Get Interface from BusinessObject !!");
        matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);

        //relationShip.open(context);
        BusinessInterfaceList businessInterfaceList = relationShip.getBusinessInterfaces(context);
        List<String> businessObjectInterfaceList = new ArrayList<>();

        for (BusinessInterface businessObjectInterface : businessInterfaceList) {
            try {
                //businessObjectInterface.open(context);
                String existingInterface = businessObjectInterface.getName();
                COMMON_BOM_UTILITIES_LOGGER.debug("Existing Interface : '" + existingInterface + "'");
                businessObjectInterfaceList.add(existingInterface);

            } catch (NullPointerException exp) {
                COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
                throw exp;
            } finally {
                //businessObjectInterface.close(context);
            }
        }
        return businessObjectInterfaceList;
    }

    public HashMap<String, String> relationShipsDefaultAttributes(HashMap<String, String> relationAttributeMap, TNR tnr) throws IOException {
        COMMON_BOM_UTILITIES_LOGGER.debug("Preparing relationship default attribute !! ");
//        if (NullOrEmptyChecker.isNullOrEmpty(relationAttributeMap)) {
//            relationAttributeMap = new HashMap<String, String>();
//        }
//
//        String type = tnr.getType();
//        HashMap<String, String> transferTorErpMap = PropertyReader.getProperties("item.type.transfer", Boolean.TRUE);
//        MOPAZ_BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Transfer to ERP mapped : " + transferTorErpMap);
//        MOPAZ_BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Item type : " + type);
//        String attribute = transferTorErpMap.get(type);
//        MOPAZ_BOM_IMPORT_THREADED_PROCESSING_LOGGER.debug("Attribute name is : " + attribute);
//        relationAttributeMap.put(attribute, "true");
        return relationAttributeMap;
    }

    public String createNewConnection(Context context, String parentId, String childId, String relationshipName) throws MatrixException, InterruptedException {
        try {
            RelationshipType relationshipType = new RelationshipType();
            relationshipType.setName(relationshipName);

            BusinessObject parentBusinessObject = new BusinessObject(parentId);
            BusinessObject childBusinessObject = new BusinessObject(childId);
            parentBusinessObject.open(context);
            childBusinessObject.open(context);

            COMMON_BOM_UTILITIES_LOGGER.info("Connecting '" + childBusinessObject.getName() + "' as a child of '" + parentBusinessObject.getName() + "'");

            Relationship relationship = parentBusinessObject.connect(context, relationshipType, true, childBusinessObject);
            String newRelationshipId = relationship.getName();

            updateProjectAndOrganization(parentId, context, newRelationshipId);

            parentBusinessObject.close(context);
            childBusinessObject.close(context);

            return newRelationshipId;
        } catch (Exception exp) {
            COMMON_BOM_UTILITIES_LOGGER.error(exp.getMessage());
            throw exp;
        }

    }

    public String updateProjectAndOrganization(String parentId, Context context, String newRelationshipId) throws FrameworkException, MatrixException {
        BusinessObject parentBusinessObject = new BusinessObject(parentId);

        parentBusinessObject.open(context);

        String parentProject = parentBusinessObject.getProjectOwner(context).getName();
        String parentOrganization = parentBusinessObject.getOrganizationOwner(context).getName();

        String mqlQuery = "modify connection " + newRelationshipId + " project '" + parentProject + "' organization '" + parentOrganization + "'";

        COMMON_BOM_UTILITIES_LOGGER.info("Update query : " + mqlQuery);
        String queryResult = MqlUtil.mqlCommand(context, mqlQuery);
        COMMON_BOM_UTILITIES_LOGGER.info("Result returned from server : " + queryResult);

        parentBusinessObject.close(context);

        return newRelationshipId;
    }
}
