/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.importer;

import com.bjit.common.rest.app.service.dsservice.consumers.ConsumerContainers;
import com.bjit.common.rest.app.service.dsservice.consumers.IConsumer;
import com.bjit.common.rest.app.service.dsservice.consumers.ItemDuplicationConsumer;
import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.itemduplication.ItemDuplicationRequestModel;
import com.bjit.common.rest.app.service.dsservice.models.itemduplication.ItemDuplicationResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.itemduplication.SkeletonItemInfo;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.ItemImportUtil;
import com.bjit.common.rest.pdm_enovia.mapper.ItemMapper;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
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
import matrix.db.RelationshipList;
import matrix.util.MatrixException;
import org.apache.log4j.Priority;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
public class ItemImportProcessor {

    private static final org.apache.log4j.Logger OBJECT_CREATION_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(ItemImportProcessor.class);
    private String existingObjectId;
    private String businessObjectinterfaceName;
    private List<String> businessObjectinterfaceList;

    public String processCreateObjectOperation(Context context, CreateObjectBean createBean, BusinessObjectOperations businessObjectOperations, ResultUtil resultUtil, ConsumerContainers consumerContainers) throws FrameworkException, Exception {
        Instant itemImportStartTime = Instant.now();
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);

                //HashMap<String, String> existingItem = searchResults.stream().filter(valItem -> valItem.get("type").equals(createObjectBean.getTnr().getType()) && valItem.get("name").equals(createObjectBean.getTnr().getName())).findFirst().get();
//                String objectId = BusinessObjectUtility.searchByTypeName(context, createObjectBean.getTnr().getType(), createObjectBean.getTnr().getName());
                String objectId = "";

                if (!NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                    this.existingObjectId = objectId;

                    Instant itemImportEndTime = Instant.now();
                    long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);

                    OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "VAL Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + duration + "' milli-seconds to find out in the DB (Item exists)");

                    //String errorMessage = "Error in creating of object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "'. Object is exists";
                    String errorMessage = "Object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "' is already exists";
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }

            HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
            OBJECT_CREATION_PROCESSOR_LOGGER.info("ObjectCloningMap is : " + objectCloningMap);

            String typeVALComponent = PropertyReader.getProperty("dslc.itemservice.specific.creation.VAL_VALComponent");
            String typeVALComponentMaterial = PropertyReader.getProperty("dslc.itemservice.specific.creation.VAL_VALComponentMaterial");
            String clonedObjectId = null;
            // currently concerning only these type item creation using dslc service '@author Touhidul Islam'
            String type = createObjectBean.getTnr().getType();
            OBJECT_CREATION_PROCESSOR_LOGGER.info(type + "::" + typeVALComponent + "-" + typeVALComponentMaterial);

            if (typeVALComponent.equalsIgnoreCase(type)
                    || typeVALComponentMaterial.equalsIgnoreCase(type)) {
                Instant startTime = Instant.now();
                OBJECT_CREATION_PROCESSOR_LOGGER.info(" ------------------ START: DSLC duplicate service -----------");
                OBJECT_CREATION_PROCESSOR_LOGGER.debug(type + " type object going to be created");
                List<SkeletonItemInfo> data = new ArrayList<>();
                data.add(new SkeletonItemInfo(type, createObjectBean.getTemplateBusinessObjectId()));
                ItemDuplicationRequestModel model = new ItemDuplicationRequestModel(data);

                IConsumer<ItemDuplicationResponseModel> itemDuplicationConsumer = new ItemDuplicationConsumer();
                itemDuplicationConsumer.setBusinessObject(model);

                IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer = consumerContainers.getCsrfTokenResponseModelIConsumer();
                ItemDuplicationResponseModel response = csrfTokenResponseModelIConsumer.nextConsumer(itemDuplicationConsumer);
                try {
                    clonedObjectId = response.getResults().get(0).getId();
                } catch(Exception e) {
                    String errorMessage = response.getErrorReport().get(0).getErrorMessage();
                    OBJECT_CREATION_PROCESSOR_LOGGER.error("Error while duplicate items: " + errorMessage);
                    throw new Exception(errorMessage);
                }
                OBJECT_CREATION_PROCESSOR_LOGGER.info("createdItemPhysicalId:" + clonedObjectId);
                Instant endTime = Instant.now();
                long duration = DateTimeUtils.getDuration(startTime, endTime);
                OBJECT_CREATION_PROCESSOR_LOGGER.info(" ++++++ END: DSLC duplicate service: It took " + duration + " miliseconds");
            } else {
                clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
            }

            addInterfaceListToVALTypeItem(createObjectBean, context, clonedObjectId, businessObjectOperations);

            OBJECT_CREATION_PROCESSOR_LOGGER.info("ClonedObjectId is : " + clonedObjectId);

            Instant itemUpdateStartTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);

            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : VAL Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + duration + "' milli-seconds to Create in the DB (New Item Created)");

            if (resultUtil.itemListWith3dModels.contains(createObjectBean.getTnr().getName())) {
                resultUtil.successfulCreateList.add(createObjectBean.getTnr().getName());
            }

            updateObject(createObjectBean, context, clonedObjectId, businessObjectOperations);

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);

            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : VAL Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + updateDuration + "' milli-seconds to update the object");

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processImportItemOperation(Context context, CreateObjectBean createObjectBean, Boolean ifExistsThenReturnObjectId, BusinessObjectOperations businessObjectOperations, ResultUtil resultUtil, ConsumerContainers consumerContainers) throws FrameworkException, Exception {

        /**
         * For testing purpose here is checking the items existence
         */
        List<String> selectedAttributes = new ArrayList<>();
        selectedAttributes.add("type");
        selectedAttributes.add("name");
        selectedAttributes.add("revision");
        selectedAttributes.add("id");
        //selectedAttributes.add("interface");
        selectedAttributes.add("attribute[" + PropertyReader.getProperty("import.item.val.component.material.attr.inventory.unit") + "]");

        CommonSearch searchValItem = new CommonSearch();
        List<HashMap<String, String>> searchResults = Collections.<HashMap<String, String>>emptyList();

        try {
            searchResults = searchValItem.searchItem(context, new TNR(PropertyReader.getProperty("val.item.search.pattern"), createObjectBean.getTnr().getName(), "*"), selectedAttributes);
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);
        } catch (NullPointerException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);
        }

        if (searchResults.size() > 1) {
            searchResults.forEach(action -> action.keySet().removeIf(searchData -> !(searchData.contains("type") || searchData.contains("name"))));

            String error = MessageFormat.format(PropertyReader.getProperty("temp.query.search.error.response"), searchResults.toString());
            OBJECT_CREATION_PROCESSOR_LOGGER.error(error);
            throw new RuntimeException(error);
        }

        /**
         * searchResults.size() = 0 means item is total new searchResults.size()
         * = 1 any type val item exists searchResults.size() > 1 error
         */
        HashMap<String, String> existingItem = null;
        try {
            this.existingObjectId = null;
            if (searchResults.isEmpty()) {
                /**
                 * searchResults.isEmpty() means item is total new
                 *
                 * Creates a new VALComponent/VALComponentMaterial after
                 * searching the existence If the item already exists in the
                 * system then it throws an exception
                 */
                //return processCreateObjectOperation(context, createObjectBean, businessObjectOperations, searchResults);
                return processCreateObjectOperation(context, createObjectBean, businessObjectOperations, resultUtil, consumerContainers);
            } else {
                existingItem = searchResults.get(0);
                String errorMessage = " Item '" + existingItem.get("type") + "' '" + existingItem.get("name") + "' '" + existingItem.get("revision") + "' exists";

                this.existingObjectId = existingItem.get("id");
                ifExistsThenReturnObjectId = true;

                OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception exp) {
            if (ifExistsThenReturnObjectId) {
                //Check data into relationships
                BusinessObject existingBusinessObject = new BusinessObject(Optional.ofNullable(this.existingObjectId).orElseThrow(() -> new RuntimeException(exp)));
                RelationshipList toRelationship = existingBusinessObject.getToRelationship(context);
                boolean standaloneItem = toRelationship.isEmpty();

                String existingItemsType = existingItem.get("type");
                String requestedItemsType = createObjectBean.getTnr().getType();

                String existingItemsInventoryUnit = !NullOrEmptyChecker.isNullOrEmpty(existingItem.get("attribute[" + PropertyReader.getProperty("import.item.val.component.material.attr.inventory.unit") + "]")) ? existingItem.get("attribute[" + PropertyReader.getProperty("import.item.val.component.material.attr.inventory.unit") + "]") : PropertyReader.getProperty("pdm.default.inventory.unit");

                String requestedItemsInventoryUnit = !NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes().get(PropertyReader.getProperty("import.item.val.component.material.attr.inventory.unit"))) ? createObjectBean.getAttributes().get(PropertyReader.getProperty("import.item.val.component.material.attr.inventory.unit")) : PropertyReader.getProperty("pdm.default.inventory.unit");

                if (!existingItemsInventoryUnit.equalsIgnoreCase(requestedItemsInventoryUnit)) {
                    /**
                     * Get existing items interface and remove
                     */
                    if (!standaloneItem) {
                        String error = PropertyReader.getProperty("val.item.used.in.structure.error.message");
                        OBJECT_CREATION_PROCESSOR_LOGGER.error(error);
                        throw new RuntimeException(error);
                    }

                    ItemImportMapping itemImportMapping = Optional.ofNullable(ItemMapper.valMap.get(existingItemsType + "Map")).orElseGet(() -> getItemImportMapperIfNotExists(context, existingBusinessObject, existingItemsType));

                    ItemImportValue inventoryUnitAndItsValues = itemImportMapping
                            .getXmlMapElementObjects()
                            .getXmlMapElementObject()
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new NullPointerException(MessageFormat.format(PropertyReader.getProperty("val.item.xml.attribute.map.wrongly.formatted"), existingItemsType)))
                            .getXmlMapElementAttributes()
                            .getXmlMapElementAttribute()
                            .stream()
                            .filter(attributeSourceName -> attributeSourceName.getSourceName().equalsIgnoreCase("inventory unit"))
                            .findFirst()
                            .orElseThrow(() -> new NullPointerException(MessageFormat.format(PropertyReader.getProperty("val.item.xml.attribute.map.missing.inventory.unit"), existingItemsType)))
                            .getValues()
                            .getValue()
                            .stream()
                            .filter(rangeValue -> rangeValue.getSrc().equalsIgnoreCase(existingItemsInventoryUnit))
                            .findFirst()
                            .orElseThrow(() -> new NullPointerException(MessageFormat.format(PropertyReader.getProperty("val.item.xml.attribute.map.missing.inventory.unit.interface"), existingItemsType)));

                    String exisitingItemsRunTimeInterfaces = inventoryUnitAndItsValues.getRunTimeInterfaceList();

                    if (!existingItemsType.equalsIgnoreCase(requestedItemsType)) {
                        exisitingItemsRunTimeInterfaces = Optional.ofNullable(inventoryUnitAndItsValues.getRunTimeInterfaceList()).orElse("") + "," + itemImportMapping.getXmlMapElementObjects().getXmlMapElementObject().get(0).getRunTimeInterfaceList();
                        String requestedItemsInterfaces = ItemMapper.valMap.get(requestedItemsType + "Map").getXmlMapElementObjects().getXmlMapElementObject().get(0).getRunTimeInterfaceList() + Optional.ofNullable(createObjectBean.getAttributes().get("businessInterfaceList")).orElse("");

                        List<String> exisitingItemsRunTimeInterfaceList = Arrays.asList(exisitingItemsRunTimeInterfaces.split(",")).stream().filter(interfcaceName -> !NullOrEmptyChecker.isNullOrEmpty(interfcaceName)).map(interfaceName -> interfaceName.trim()).collect(Collectors.toList());
                        OBJECT_CREATION_PROCESSOR_LOGGER.debug("Existing items interfaces : " + exisitingItemsRunTimeInterfaceList);

                        List<String> requestedItemsInterfaceList = Arrays.asList(requestedItemsInterfaces.split(",")).stream().map(interfaceName -> interfaceName.trim()).collect(Collectors.toList());
                        OBJECT_CREATION_PROCESSOR_LOGGER.debug("Requested items interfaces : " + requestedItemsInterfaceList);

                        exisitingItemsRunTimeInterfaceList.removeAll(requestedItemsInterfaceList);
                        OBJECT_CREATION_PROCESSOR_LOGGER.info("Interfaces  will be removed from current items : " + exisitingItemsRunTimeInterfaceList);

                        exisitingItemsRunTimeInterfaces = String.join(",", exisitingItemsRunTimeInterfaceList);
                        OBJECT_CREATION_PROCESSOR_LOGGER.debug("Interfaces  will be removed from current items : " + exisitingItemsRunTimeInterfaces);

                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(exisitingItemsRunTimeInterfaces)) {
                        Arrays.asList(exisitingItemsRunTimeInterfaces.split(",")).stream().forEach((String interfaceName) -> {
                            try {
                                businessObjectOperations.removeInterface(context, existingBusinessObject, interfaceName.trim(), "");
                            } catch (MatrixException ex) {
                                OBJECT_CREATION_PROCESSOR_LOGGER.error(ex.getMessage());
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }

                if (!existingItemsType.equalsIgnoreCase(requestedItemsType)) {
                    if (!standaloneItem) {
                        String error = PropertyReader.getProperty("val.item.used.in.structure.error.message");
                        OBJECT_CREATION_PROCESSOR_LOGGER.error(error);
                        throw new RuntimeException(error);
                    }

                    // change the business objects type
                    // changeObjectType(context, businessObject, type, name, revision, vault, policy)
                    // BusinessObject changedBO = CommonUtil.changeObjectType(context, importObjectBO, Constants.typeComponent, importObjectBO.getName(), importObjectBO.getRevision(), importObjectBO.getVault(), importObjectBO.getPolicy(context).getName());
                    existingBusinessObject.open(context);
                    businessObjectOperations.changeObjectType(context, existingBusinessObject, requestedItemsType, createObjectBean.getTnr().getName(), existingBusinessObject.getRevision(), existingBusinessObject.getVault(), existingBusinessObject.getPolicy(context).getName());
                    existingBusinessObject.close(context);
                }

                if (!NullOrEmptyChecker.isNullOrEmpty(this.existingObjectId)) {
                    Instant itemUpdateStartTime = Instant.now();
                    addInterfaceListToVALTypeItem(createObjectBean, context, this.existingObjectId, businessObjectOperations);
                    CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
                    updateObject(createObjectBean, context, this.existingObjectId, businessObjectOperations);

                    if (resultUtil.itemListWith3dModels.contains(createObjectBean.getTnr().getName()) && !resultUtil.successfulCreateList.contains(createObjectBean.getTnr().getName())) {
                        resultUtil.successfulUpdateList.add(createObjectBean.getTnr().getName());
                    }

                    Instant itemUpdateEndTime = Instant.now();
                    long duration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);

                    OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : VAL Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + duration + "' milli-seconds to find out in the DB (Item exists)");
                    return this.existingObjectId;
                }
            }
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private ItemImportMapping getItemImportMapperIfNotExists(Context context, BusinessObject existingBusinessObject, String existingItemsType) throws RuntimeException {

        try {
            existingBusinessObject.open(context);
            TNR existingItemsTNR = new TNR(existingBusinessObject.getTypeName(), existingBusinessObject.getName(), existingBusinessObject.getRevision());
            existingBusinessObject.close(context);

            CreateObjectBean existingCreateObjectBean = new CreateObjectBean();
            existingCreateObjectBean.setTnr(existingItemsTNR);
            existingCreateObjectBean.setSource("pdm");

            String mapsAbsoluteDirectory = CommonUtil.populateMapDirectoryFromObject(existingCreateObjectBean, true);

            if (!ItemMapper.valMap.containsKey(existingItemsType + "Map")) {
                HashMap<String, String> attributeHashMap = new HashMap<>();
                existingCreateObjectBean.setAttributes(attributeHashMap);
                ItemMapper ObjectTypesAndRelations = new ItemMapper(mapsAbsoluteDirectory, ItemImportMapping.class, existingCreateObjectBean, true);
            }

            ItemImportMapping itemImportMapper = ItemMapper.valMap.get(existingItemsType + "Map");
            return itemImportMapper;
        } catch (Exception matExp) {
            throw new RuntimeException(matExp);
        }
    }

    private void addInterfaceListToVALTypeItem(CreateObjectBean createObjectBean, Context context, String newObjectId, BusinessObjectOperations businessObjectOperations) {
        String inventoryUnitInterfaceList = createObjectBean.getAttributes().get("businessInterfaceList");
        createObjectBean.getAttributes().remove("businessInterfaceList");
        ItemImportMapping valItemImportMapper = ItemMapper.valMap.get(createObjectBean.getTnr().getType() + "Map");

        ItemImportXmlMapElementObject itemImportXMLElementObject = valItemImportMapper.getXmlMapElementObjects().getXmlMapElementObject().get(0);

        Optional.ofNullable(itemImportXMLElementObject).ifPresent(itemImportXmlElementObject -> {
            String runTimeInterfaces = itemImportXmlElementObject.getRunTimeInterfaceList();

            Optional<String> interfaceOptionals = Optional.ofNullable(runTimeInterfaces);
            interfaceOptionals.ifPresent(runtimeInterfaces -> {
                if (!NullOrEmptyChecker.isNullOrEmpty(inventoryUnitInterfaceList)) {
                    runtimeInterfaces = runtimeInterfaces.concat(inventoryUnitInterfaceList);
                }
                List<String> interfaceList = Arrays.asList(runtimeInterfaces.split(",")).stream().map(String::trim).collect(Collectors.toList());
                businessObjectOperations.addInterface(context, newObjectId, interfaceList, "", Boolean.TRUE);
            });
        });
    }

    public String processCreateReviseObjectOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean) throws FrameworkException, Exception {
        String clonedObjectId = null;
        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            Boolean objectIsExists = false;

            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                //String objectId = BusinessObjectUtility.searchByTypeName(context, createObjectBean.getTnr().getType(), createObjectBean.getTnr().getName());
                BusinessObject latestRevisedBO = ItemImportUtil.getExistingBOorRevisedBO(context, businessObjectUtil, createObjectBean.getTnr());

                String objectId = "";

                if (!NullOrEmptyChecker.isNull(latestRevisedBO)) {
                    objectIsExists = true;
                    objectId = latestRevisedBO.getObjectId();
                }

                clonedObjectId = objectId;

                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
//                    this.existingObjectId = objectId;
//                    String errorMessage = "Error in creating of object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() +  (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "'. Object is exists";
//                    OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
//                    throw new RuntimeException(errorMessage);

                    HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
                    OBJECT_CREATION_PROCESSOR_LOGGER.info("ObjectCloningMap is : " + objectCloningMap);

                    clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
                    OBJECT_CREATION_PROCESSOR_LOGGER.info("ClonedObjectId is : " + clonedObjectId);
                } else {
                    this.existingObjectId = objectId;
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                final String updatabelNewClonedObject = clonedObjectId;
                this.businessObjectinterfaceList.forEach((String interfaceName) -> {
                    if (!NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {
                        try {
                            OBJECT_CREATION_PROCESSOR_LOGGER.debug("Interface : " + interfaceName);
                            businessObjectOperations.addInterface(context, updatabelNewClonedObject, interfaceName, "");
                        } catch (MatrixException exp) {
                            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                    }
                });
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "");
            }

            updateObject(createObjectBean, context, clonedObjectId, objectIsExists, businessObjectOperations);

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, String interfaceName) throws FrameworkException, Exception {
        try {
            businessObjectinterfaceName = !NullOrEmptyChecker.isNullOrEmpty(interfaceName) ? interfaceName : null;
            String clonedObjectId = processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList) throws FrameworkException, Exception {
        try {

            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String clonedObjectId = processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private void updateObject(CreateObjectBean createObjectBean, Context context, String clonedObjectId, BusinessObjectOperations businessObjectOperations) throws FrameworkException, InterruptedException, Exception {
        HashMap<String, String> attributes = setVersionId(context, clonedObjectId, createObjectBean.getAttributes());

        //------------------ START: manually name is set ------------------
        String name = createObjectBean.getTnr().getName();
        OBJECT_CREATION_PROCESSOR_LOGGER.info("Object Name:" + name);
        attributes.put("name", name);
        //------------------ END: manually name is set ------------------
        businessObjectOperations.updateObject(context, clonedObjectId, attributes);
    }

    private HashMap<String, String> setVersionId(Context context, String objectId, HashMap<String, String> attributes) throws Exception {
        String versionId = PropertyReader.getProperty("val.component.material.version.id");

        if (attributes.containsKey(versionId)) {
            try {
                String query = "print bus " + objectId + " select physicalId dump";
                String mqlCommand = MqlUtil.mqlCommand(context, query);
                OBJECT_CREATION_PROCESSOR_LOGGER.info("Returned Result for Physical Id : " + mqlCommand);

                String physicalId = mqlCommand;

                attributes.put(versionId, physicalId);
            } catch (Exception exp) {
                OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
                throw exp;
            }
        }

        return attributes;
    }

    private void updateObject(CreateObjectBean createObjectBean, Context context, String clonedObjectId, Boolean isExists, BusinessObjectOperations businessObjectOperations) throws FrameworkException, InterruptedException, Exception {
        if (isExists) {
            HashMap<String, String> objectUpdateMap = getObjectUpdateMap(createObjectBean);
            businessObjectOperations.updateObject(context, clonedObjectId, objectUpdateMap);
        } else {
            updateObject(createObjectBean, context, clonedObjectId, businessObjectOperations);
        }

    }

    private CreateObjectBean validateCreateObjectBean(Context context, CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) throws Exception {
        String errorMessage;

        if (NullOrEmptyChecker.isNull(createObjectBean.getIsAutoName())) {
            errorMessage = "Please set 'isAutoName' property correctly";
            OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        resolveSkeletonId(createObjectBean, businessObjectOperations);
        TNR templateObjectTNR = businessObjectOperations.getObjectTNR(context, createObjectBean.getTemplateBusinessObjectId());

        try {
            businessObjectOperations.validateTNR(createObjectBean.getTnr());
            if (!createObjectBean.getTnr().getType().equalsIgnoreCase(templateObjectTNR.getType())) {
                errorMessage = "Template type and Object type is not same";
                OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
                throw new Exception(errorMessage);
            }
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);

            TNR tnr = new TNR();
            tnr.setType(templateObjectTNR.getType());
            createObjectBean.setTnr(tnr);
        }

        return createObjectBean;
    }

    private CreateObjectBean resolveSkeletonId(CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) throws Exception {
        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTemplateBusinessObjectId())) {
            try {
                businessObjectOperations.validateTNR(createObjectBean.getTnr());
                BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

                String skeletonId = businessObjectUtility.getSkeletonId(createObjectBean.getTnr().getType());
                createObjectBean.setTemplateBusinessObjectId(skeletonId);
            } catch (Exception exp) {
                OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
                throw exp;
            }
        }
        return createObjectBean;
    }

    private HashMap createObjectCloningMap(CreateObjectBean createObjectBean) {
        try {
            HashMap objectCloneParametersMap = new HashMap();

            HashMap specificationMap = new HashMap();
            String folderId = NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getFolderId()) ? "" : createObjectBean.getFolderId();

            specificationMap.put("FolderId", folderId);
            objectCloneParametersMap.put("SpecificationMap", specificationMap);

            HashMap<String, String> attributeMap = new HashMap<>();
            String attributeGlobalRead = NullOrEmptyChecker.isNull(createObjectBean.getAttributeGlobalRead()) ? "false" : createObjectBean.getAttributeGlobalRead().toString();

            attributeMap.put("attribute_GlobalRead", attributeGlobalRead);
            objectCloneParametersMap.put("AttributeMap", attributeMap);
            objectCloneParametersMap.put("Type", createObjectBean.getTnr().getType());
            objectCloneParametersMap.put("Name", createObjectBean.getTnr().getName());

            return objectCloneParametersMap;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private HashMap<String, String> getObjectUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        if (updateAttributes.containsKey("PLMEntity.V_nature")) {
            throw new RuntimeException("Please don't update 'PLMEntity.V_nature'");
        }

        if (!updateAttributes.containsKey("current")) {
            updateAttributes.put("current", "IN_WORK");
        }

        return updateAttributes;
    }
}
