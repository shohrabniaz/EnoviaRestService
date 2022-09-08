package com.bjit.common.rest.item_bom_import.bom_import;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.structure.ItemStructure;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.import_threads.BOMValidatorProcess;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationship;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationships;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.builder.MapperBuilder;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import org.apache.log4j.Priority;

public class StructureImport implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger STRUCTURE_IMPORT_LOGGER = org.apache.log4j.Logger.getLogger(StructureImport.class);
    public static HashMap<String, Relationships> bomRelationshipMap = new HashMap<>();

    @Override
    public <T, K> K doImport(Context context, T itemStructure) {
        ItemStructure structureData = (ItemStructure) itemStructure;
        List<CreateBOMBean> createBOMBeanList = structureData.getStrucuteList();
        List<Relationship> relationshipList = null;
        
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        
        try {
            String source = structureData.getSource().toLowerCase();
            String mappingFilePath = PropertyReader.getProperty(source + ".structure.import.mapping.xml.directory");

            Relationships relationshipMapping;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("bom.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(bomRelationshipMap.get(structureData.getSource()))) {
                    StructureImport.bomRelationshipMap.put(source, (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath));
                }

                relationshipMapping = StructureImport.bomRelationshipMap.get(structureData.getSource());
            } else {
                relationshipMapping = (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath);
            }

            relationshipList = relationshipMapping.getRelationshipList();
        } catch (Exception exp) {
            STRUCTURE_IMPORT_LOGGER.error(exp);
        }

        String relName = "";
        String interfaceName = "";
        HashMap<String, List<ParentInfo>> responseMsgMap = new HashMap<>();
        String responseStatus = "";
        List<ParentInfo> successfulParentInfoList = new ArrayList<>();
        List<ParentInfo> errorParentInfoList = new ArrayList<>();
        
        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();

        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("item.import.concurrent.total.thread.count"));
        STRUCTURE_IMPORT_LOGGER.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        try {
            boolean isBOMCreatedSuccessfully = true;
            HashMap<ParentInfo, List<ChildInfo>> parenChildInfoMap = new HashMap<>();
            List<HashMap<ParentInfo, List<ChildInfo>>> parentChildInfoMapList = new ArrayList<>();
            List<HashMap<String, String>> existingChildInfoRelMapList = new ArrayList<>();
            BOMValidatorProcess BOMValidation = new BOMValidatorProcess();
            for (CreateBOMBean createBOMBean : createBOMBeanList) {
                BOMDataCollector dataCollector = new BOMDataCollector();
                try {
                    //Callable odiImportProcess = new BOMValidatorProcess(createBOMBean, context, relationshipList, relName, interfaceName);
                    //Future<?> future = executorService.submit(odiImportProcess);
                    //futures.add(future);
                    dataCollector = BOMValidation.bomValidationAndDataCollection(businessObjectUtil, businessObjectOperations, createBOMBean, context, relationshipList, relName, interfaceName);
                    //dataCollector = (BOMDataCollector) future.get();
//                    parentChildInfoMapList.add(dataCollector.getRequestParentChildInfoMap());
//                    if (!NullOrEmptyChecker.isNullOrEmpty(dataCollector.getExistingChildInfoRelMap())) {
//                        existingChildInfoRelMapList.add(dataCollector.getExistingChildInfoRelMap());
//                    }
                    if (dataCollector.isBOMcontainError()) {
                        break;
                    }
                } catch (Exception ex) {
                    isBOMCreatedSuccessfully = false;
                    STRUCTURE_IMPORT_LOGGER.error(ex);
                    throw ex;
                }
            }
            ArrayList<String> disconnectRelationList = new ArrayList<>();
            boolean errorInStructure = false;

            //try {
            ContextUtil.startTransaction(context, true);

            /*for (HashMap<ParentInfo, List<ChildInfo>> parentChildInfoMap : parentChildInfoMapList) {
                for (ParentInfo parentInfoKey : parentChildInfoMap.keySet()) {
                    StringBuilder errorMsgBuilder = new StringBuilder();
                    ParentInfo responseParentInfo = new ParentInfo();
                    if (!NullOrEmptyChecker.isNullOrEmpty(parentInfoKey.getErrorMessage())) {
                        errorInStructure = true;
                        errorMsgBuilder.append(parentInfoKey.getErrorMessage());
                        responseParentInfo.setTnr(parentInfoKey.getTnr());
                        responseParentInfo.setErrorMessage(errorMsgBuilder.toString());
                        errorParentInfoList.add(responseParentInfo);
                        responseMsgMap.put("Error", errorParentInfoList);
                        return (K) responseMsgMap;
                    }
                    responseParentInfo.setTnr(parentInfoKey.getTnr());
                    for (ChildInfo childInfo : parentChildInfoMap.get(parentInfoKey)) {
                        if (!childInfo.getMessage().isEmpty() && childInfo.getMessage() != null) {
                            errorInStructure = true;
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

                        if (!NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelID())) {
                            Instant bomUpdateStartTime = Instant.now();
                            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

                            STRUCTURE_IMPORT_LOGGER.debug("Modifying connections");

                            String interfaceNames = childInfo.getInterfaceName();
                            if (!NullOrEmptyChecker.isNullOrEmpty(interfaceNames)) {

                                List<String> interfaceListFromMap = Arrays.asList(interfaceNames.split(","));

                                STRUCTURE_IMPORT_LOGGER.debug("Interface List: " + interfaceListFromMap);

                                List<String> relationShipInterfacesExistsInRelationship = getBusinessObjectInterfaces(context, childInfo.getRelID());
                                interfaceListFromMap.replaceAll(String::trim);

//                                List<String> relationsInterfaces = new ArrayList<>(relationShipInterfacesExistsInRelationship);
//                                Collections.sort(relationShipInterfacesExistsInRelationship);
//                                Collections.sort(interfaceListFromMap);
//                                interfaceListFromMap.removeAll(relationShipInterfacesExistsInRelationship);
                                List<String> uniqueInterfaces = removeAll(interfaceListFromMap, relationShipInterfacesExistsInRelationship);

                                STRUCTURE_IMPORT_LOGGER.info("New Interfaces are : " + interfaceListFromMap);

                                uniqueInterfaces.forEach((String connectionInterface) -> {
                                    try {
//                                        String mqlQuery = "mod connection " + childInfo.getRelID() + " add interface " + connectionInterface;
//                                        IMPORT_BOM_LOGGER.info("Connection modifying query : " + mqlQuery);
//                                        String queryResult = MqlUtil.mqlCommand(context, mqlQuery);
//                                        IMPORT_BOM_LOGGER.info("Result : " + queryResult);

                                        addInterfaceToTheRelationship("", connectionInterface, childInfo.getRelID(), context);

                                    } catch (FrameworkException ex) {
                                        STRUCTURE_IMPORT_LOGGER.warn(ex);
                                    } catch (Exception ex) {
                                        STRUCTURE_IMPORT_LOGGER.warn(ex);
                                    }
                                });
                            }

                            BusinessObjectUtil.modifyConnection(context, childInfo.getRelID(), relationshipAttributes, childInfo.getPropertyNameValueMap());

                            String parentTypeName = childInfo.getParentId();
                            String childTypeName = childInfo.getChildId();
                            if (Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info"))) {

                                BusinessObject parentBO = new BusinessObject(childInfo.getParentId());
                                parentBO.open(context);
                                parentTypeName = "Type : '" + parentBO.getTypeName() + "' Name : '" + parentBO.getName() + "'";
                                childTypeName = "Type : '" + childInfo.getChildTNR().getType() + "' Name : '" + childInfo.getChildTNR().getName() + "'";
                            }
                            Instant BomUpdateEndTime = Instant.now();
                            long bomUpdateDuration = DateTimeUtils.getDuration(bomUpdateStartTime, BomUpdateEndTime);
                            STRUCTURE_IMPORT_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Parent " + parentTypeName + " child " + childTypeName + " update relationship '" + childInfo.getRelID() + "' in " + bomUpdateDuration + " milli-seconds");

                        }
                        if (!errorInStructure && NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelID())) {
                            STRUCTURE_IMPORT_LOGGER.debug("Creating new connection");
                            String relationShipName = connectBusinessObjects(childInfo, relationshipAttributes, context);
                            STRUCTURE_IMPORT_LOGGER.debug("Relationship Name : " + relationShipName);
                        }
                    }
                    successfulParentInfoList.add(responseParentInfo);
                }
            }*/
            
            for (int i = 0; i < existingChildInfoRelMapList.size(); i++) {
                HashMap<String, String> existingChildInfoRelMap = existingChildInfoRelMapList.get(i);
                for (String itemNameRevPos : existingChildInfoRelMap.keySet()) {
                    String relID = existingChildInfoRelMap.get(itemNameRevPos);
                    try {
                        businessObjectUtil.disconnectRelationship(context, relID);
                    } catch (MatrixException ex) {
                        errorInStructure = true;
                        STRUCTURE_IMPORT_LOGGER.error(ex);
                        break;
                        //throw new RuntimeException(ex);
                    }
                }
            }
            /*existingChildInfoRelMapList.forEach((existingChildInfoRelMap)  -> {
                
                existingChildInfoRelMap.forEach((String itemNameRev, String relID) -> {
                    try {
                        BusinessObjectUtil.disconnectRelationship(context, relID);
                    } catch (MatrixException ex) {
                        
                        IMPORT_BOM_LOGGER.error(ex);
                        //throw new RuntimeException(ex);
                    }
                });
            });*/

            if (!errorInStructure) {
                STRUCTURE_IMPORT_LOGGER.debug("Committing Transaction");
                ContextUtil.commitTransaction(context);
            }

            if (errorParentInfoList.isEmpty()) {
                responseMsgMap.put("Successful", successfulParentInfoList);
            }
        } catch (FrameworkException | RuntimeException exp) {
            STRUCTURE_IMPORT_LOGGER.debug("Aborting Transaction");
            STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(exp);
        } catch (MatrixException exp) {
            STRUCTURE_IMPORT_LOGGER.debug("Aborting Transaction");
            STRUCTURE_IMPORT_LOGGER.error(exp);
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(exp);
        } catch (Exception exp) {
            STRUCTURE_IMPORT_LOGGER.debug("Aborting Transaction");
            STRUCTURE_IMPORT_LOGGER.error(exp);
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(exp);
        }
        STRUCTURE_IMPORT_LOGGER.debug("responseMsgMap :: " + responseMsgMap);
        return (K) responseMsgMap;
    }

    private List<String> removeAll(List<String> firstList, List<String> secondList) {
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

    private void addInterfaceToTheRelationship(String vault, String interfaceName, String relationshipId, Context context) throws MatrixException {
        try {
            matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);
            interfaceName = interfaceName.trim();

            STRUCTURE_IMPORT_LOGGER.info("Adding interface '" + interfaceName + "' to the relationship");

            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            relationShip.addBusinessInterface(context, businessInterface);
        } catch (MatrixException exp) {
            STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public List<String> getRelationshipInterfaces(Context context, String relationshipId) throws FrameworkException {
        try {
            String mqlRelationshipInterfaceQuery = "print connection " + relationshipId + " select interface dump";
            STRUCTURE_IMPORT_LOGGER.info(mqlRelationshipInterfaceQuery);
            String queryResult = MqlUtil.mqlCommand(context, mqlRelationshipInterfaceQuery);
            return Arrays.asList(queryResult.split(","));
        } catch (Exception exp) {
            STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public List<String> getBusinessObjectInterfaces(Context context, String relationshipId) throws MatrixException, RuntimeException {

        matrix.db.Relationship relationShip = new matrix.db.Relationship(relationshipId);

        relationShip.open(context);
        BusinessInterfaceList businessInterfaceList = relationShip.getBusinessInterfaces(context);
        List<String> businessObjectInterfaceList = new ArrayList<>();

        for (BusinessInterface businessObjectInterface : businessInterfaceList) {
            try {
                businessObjectInterface.open(context);
                String existingInterface = businessObjectInterface.getName();
                STRUCTURE_IMPORT_LOGGER.debug("Existing Interface : '" + existingInterface + "'");
                businessObjectInterfaceList.add(existingInterface);

            } catch (MatrixException exp) {
                STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            } catch (NullPointerException exp) {
                STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                STRUCTURE_IMPORT_LOGGER.error(exp.getMessage());
                throw exp;
            } finally {
                businessObjectInterface.close(context);
            }
        }
        return businessObjectInterfaceList;
    }

    private String connectBusinessObjects(ChildInfo childInfo, HashMap<String, String> relationshipAttributes, Context context) throws MatrixException, IOException, InterruptedException {

        String interfaceName;
        Instant bomCreateStartTime = Instant.now();
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        BusinessObject parentBO = new BusinessObject(childInfo.getParentId());
        BusinessObject childBO = new BusinessObject(childInfo.getChildId());
        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setName(childInfo.getRelName());
        AttributeList attributeList = createAttributeList(relationshipAttributes);
        interfaceName = childInfo.getInterfaceName();
        matrix.db.Relationship relationship = parentBO.connect(context, relationshipType, true, childBO);
        relationship.open(context);
        if (!NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {

            List<String> interfaceList = Arrays.asList(interfaceName.split(","));
            interfaceList.forEach((String relationshipInterface) -> {
                relationshipInterface = relationshipInterface.trim();

                STRUCTURE_IMPORT_LOGGER.info("Adding interface : '" + relationshipInterface + "' to the relationship");

                Vault vault = new Vault("");
                BusinessInterface businessInterface = new BusinessInterface(relationshipInterface, vault);
                try {
                    relationship.addBusinessInterface(context, businessInterface);
                } catch (MatrixException ex) {
                    STRUCTURE_IMPORT_LOGGER.warn(ex);
                }
            });

        }
        relationship.setAttributeValues(context, attributeList);
        relationship.close(context);
        String relationShipName = relationship.getName();

        
        BusinessObjectUtil BusinessObjectUtil = new BusinessObjectUtil();
        BusinessObjectUtil.modifyConnection(context, relationShipName, relationshipAttributes, childInfo.getPropertyNameValueMap());

        String parentTypeName = childInfo.getParentId();
        String childTypeName = childInfo.getChildId();
        if (Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info"))) {
            parentBO.open(context);
            //childBO.open(context);

            parentTypeName = "Type : '" + parentBO.getTypeName() + "' Name : '" + parentBO.getName() + "'";
            childTypeName = "Type : '" + childInfo.getChildTNR().getType() + "' Name : '" + childInfo.getChildTNR().getName() + "'";
        }
        Instant BomCreateEndTime = Instant.now();
        long bomCreateDuration = DateTimeUtils.getDuration(bomCreateStartTime, BomCreateEndTime);
        STRUCTURE_IMPORT_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Parent " + parentTypeName + " child " + childTypeName + " created relationship '" + relationShipName + "' in " + bomCreateDuration + " milli-seconds");

        return relationShipName;
    }

//    private HashMap<String, String> relationShipsDefaultAttributes(HashMap<String, String> relationAttributeMap) {
//        if (NullOrEmptyChecker.isNullOrEmpty(relationAttributeMap)) {
//            relationAttributeMap = new HashMap<String, String>();
//        }
//        relationAttributeMap.put("MBOM_MBOMInstance.MBOM_AllowStructureTransferToERP", "true");
//
//        return relationAttributeMap;
//    }
    private HashMap<String, String> relationShipsDefaultAttributes(HashMap<String, String> relationAttributeMap, TNR tnr) throws IOException {
        if (NullOrEmptyChecker.isNullOrEmpty(relationAttributeMap)) {
            relationAttributeMap = new HashMap<String, String>();
        }

        String type = tnr.getType();
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HashMap<String, String> transferTorErpMap = commonPropertyReader.getPropertyValue("item.type.transfer", Boolean.TRUE);
        STRUCTURE_IMPORT_LOGGER.info("Transfer to ERP mapped : " + transferTorErpMap);
        STRUCTURE_IMPORT_LOGGER.debug("Item type : " + type);
        String attribute = transferTorErpMap.get(type);
        STRUCTURE_IMPORT_LOGGER.debug("Attribute name is : " + attribute);
        relationAttributeMap.put(attribute, "true");
        commonPropertyReader = null;
        return relationAttributeMap;
    }

    private AttributeList createAttributeList(HashMap<String, String> attributeNameValueMap) {
        AttributeList attributeList = new AttributeList();
        for (String attributeName : attributeNameValueMap.keySet()) {
            AttributeType at = new AttributeType(attributeName);
            Attribute attribute = new Attribute(at, attributeNameValueMap.get(attributeName));
            attributeList.addElement(attribute);
        }
        return attributeList;
    }
}
