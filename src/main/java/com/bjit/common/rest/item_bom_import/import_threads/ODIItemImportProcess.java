/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.import_threads;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import matrix.db.BusinessObject;
import matrix.db.Context;
import org.apache.log4j.Priority;

/**
 *
 * @author BJIT
 */
public class ODIItemImportProcess implements Runnable, Callable {

    public static HashMap<String, String> BUSINESS_OBJECT_TYPE_MAP;
    private static final org.apache.log4j.Logger ODI_ITEM_IMPORT_THREAD_LOGGER = org.apache.log4j.Logger.getLogger(ODIItemImportProcess.class);
    private static HashMap<String, String> MAP_DIRECTORY;

    private Context context;
    private CreateObjectBean createObjectBean;
    private ResponseMessageFormaterBean responseMessageFormatterBean;
    private String source;
    private HashMap<String, String> destinationSourceMap;
//    private List<String> interfaceList;

    public ODIItemImportProcess(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        BUSINESS_OBJECT_TYPE_MAP = PropertyReader.getProperties(BUSINESS_OBJECT_TYPE_MAP, "import.type.map.Enovia", Boolean.TRUE);
        this.setContext(context);
        this.setCreateObjectBean(createObjectBean);
        this.setResponseMessageFormaterBean(responseMessageFormatterBean);
        this.setSource(source);
//        this.setInterfaceList();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CreateObjectBean getCreateObjectBean() {
        return createObjectBean;
    }

    public void setCreateObjectBean(CreateObjectBean createObjectBean) {
        this.createObjectBean = createObjectBean;
    }

    public ResponseMessageFormaterBean getResponseMessageFormaterBean() {
        return responseMessageFormatterBean;
    }

    public void setResponseMessageFormaterBean(ResponseMessageFormaterBean responseMessageFormatterBean) {
        this.responseMessageFormatterBean = responseMessageFormatterBean;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public HashMap<String, String> getDestinationSourceMap() {
        return destinationSourceMap;
    }

    public void setDestinationSourceMap(HashMap<String, String> destinationSourceMap) {
        this.destinationSourceMap = destinationSourceMap;
    }

//    private void setInterfaceList() {
//        try {
//            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//            HashMap<String, String> interfaceMap = commonPropertyReader.getPropertyValue("item.createAssembly.interface", Boolean.TRUE);
//
//            this.interfaceList = new ArrayList<>(interfaceMap.values());
//            ODI_ITEM_IMPORT_THREAD_LOGGER.info("Interface List : " + this.interfaceList);
//        } catch (IOException exp) {
//            ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
//        }
//    }
    @Override
    public void run() {
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("----------------------------- -----------------------------");
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Started  |");
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("----------------------------- -----------------------------");
        try {
            processODIItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());

        } catch (FrameworkException | RuntimeException exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
            throw new RuntimeException(exp);
        } catch (Exception ex) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(ex);
            throw new RuntimeException(ex);
        } finally {
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("------------------------------ ------------------------------");
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Completed  |");
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("------------------------------ ------------------------------");
        }
    }

    @Override
    public Object call() throws Exception {
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("----------------------------- -----------------------------");
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Started  |");
        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("----------------------------- -----------------------------");

        HashMap<String, ResponseMessageFormaterBean> threadResponse = new HashMap<>();
        try {

            processODIItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());

            threadResponse.put("successful", this.getResponseMessageFormaterBean());
        } catch (FrameworkException | RuntimeException exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
            retryCreatingOrUpdatingObject(exp, threadResponse);
        } catch (Exception exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
            retryCreatingOrUpdatingObject(exp, threadResponse);
        } finally {
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("------------------------------ ------------------------------");
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Completed  |");
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("------------------------------ ------------------------------");
        }

        return threadResponse;
    }

    private void processODIItem(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, RuntimeException, Exception {
//        Date transactionStartTime = DateTimeUtils.getTime(new Date());
        String typeForTimeChecking = createObjectBean.getTnr().getType();
        String nameForTimeChecking = createObjectBean.getTnr().getName();
        String revisionForTimeChecking = createObjectBean.getTnr().getRevision();

        HashMap<String, String> destinationSourceXMLMap;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();

        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        AttributeBusinessLogic attributeBusinessLogic = new AttributeBusinessLogic();

        CommonUtilities commonUtilities = new CommonUtilities();
        try {
            
            commonUtilities.doStartTransaction(context);
//            commonUtilities.escapeOperationOn(context);

            try {
                String objectType = createObjectBean.getTnr().getType();

                XmlParse xmlParse = new XmlParse();
                String enoviaType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                        commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        objectType);

                BusinessObject searchedBusinessObject = businessObjectUtil.findPDMItem(context, enoviaType, createObjectBean.getTnr().getName(), createObjectBean.getTnr().getRevision());
                String searchedObjectsTypeName = "";
                if (!NullOrEmptyChecker.isNull(searchedBusinessObject)) {
                    searchedBusinessObject.open(context);
                    searchedObjectsTypeName = searchedBusinessObject.getTypeName();
                    searchedBusinessObject.close(context);
                }

                String inventoryUnit = Optional.ofNullable(createObjectBean.getAttributes().get("Inventory unit")).orElse(PropertyReader.getProperty("pdm.default.inventory.unit"));

                String createAssemblyInventoryUnitPattern = PropertyReader.getProperty("pattern.create.assembly.inventory.unit");
                String processContinuousCreateMaterialInventoryUnitPattern = PropertyReader.getProperty("pattern.process.continuous.create.material.inventory.unit");

                if (Arrays.asList(createAssemblyInventoryUnitPattern.split("\\|")).stream().anyMatch(createAssemblyInventory -> createAssemblyInventory.equalsIgnoreCase(inventoryUnit))) {
                    objectType = objectType;
                } else {
                    objectType = "ProcessContinuousCreateMaterial";
                }
                
//                else if (Arrays.asList(processContinuousCreateMaterialInventoryUnitPattern.split("\\|")).stream().anyMatch(createMaterialInventory -> createMaterialInventory.equalsIgnoreCase(inventoryUnit))) {
//                    objectType = "ProcessContinuousCreateMaterial";
//                } else {
//                    throw new RuntimeException("Inventory Unit '" + inventoryUnit + "' is not allowed for '" + objectType + "' name '" + createObjectBean.getTnr().getName() + "' revision '" + createObjectBean.getTnr().getRevision() + "'");
//                }

                this.setSource(this.getSource().toLowerCase());

                if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
                    ODI_ITEM_IMPORT_THREAD_LOGGER.error("Attribute list is Null or Empty");
                    throw new NullPointerException("Attribute list is Null or Empty");
                }
                createObjectBean.getAttributes().put(commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision"), createObjectBean.getTnr().getRevision());

                ODI_ITEM_IMPORT_THREAD_LOGGER.debug("Object Type : " + objectType);
                ODI_ITEM_IMPORT_THREAD_LOGGER.debug("Source Environment : " + this.getSource());

                String envObjectType = this.getSource() + "." + objectType;

                ODI_ITEM_IMPORT_THREAD_LOGGER.debug("Environment Object Type : " + envObjectType);

                String itemType = BUSINESS_OBJECT_TYPE_MAP.containsKey(envObjectType) ? BUSINESS_OBJECT_TYPE_MAP.get(envObjectType) : objectType;

                /**
                 * Here business process will change for changing type of the
                 * object. But for now we will shutdown the further processing
                 * of updating or creating item
                 */
                if (!NullOrEmptyChecker.isNullOrEmpty(searchedObjectsTypeName)) {
                    if (!itemType.equalsIgnoreCase(searchedObjectsTypeName)) {
                        throw new RuntimeException("Type change of an object doesn't supported by the system (different inventory unit found)");
                    }
                }

                createObjectBean.getTnr().setType(itemType);

                String mapsAbsoluteDirectory = validateCreateObjectBean(createObjectBean, businessObjectOperations);

                if (NullOrEmptyChecker.isNullOrEmpty(mapsAbsoluteDirectory)) {
                    //String errorMessage = "Mapping file may be not exist for the item type '" + createObjectBean.getTnr().getType() + "' or '" + objectType + "'";
                    String errorMessage = "System could not recognize the type : '" + createObjectBean.getTnr().getType() + "'";
                    ODI_ITEM_IMPORT_THREAD_LOGGER.error(errorMessage);
                    throw new NullPointerException(errorMessage);
                }

                /*---------------------------------------- ||| Process for cloning object ||| ----------------------------------------*/
                //ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, BomImportMapping.class);
                /**
                 * This process modifies the attribute map of CreateObjectBean
                 * class. As the attribute map is a reference type so when the
                 * map is manipulated on other places then the real object face
                 * the changes
                 */
                //ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, BomImportMapping.class, createObjectBean);
                ObjectTypesAndRelations objectTypesAndRelations = new ObjectTypesAndRelations(context, businessObjectUtil, businessObjectOperations, mapsAbsoluteDirectory, ItemImportMapping.class, createObjectBean, attributeBusinessLogic);
                destinationSourceXMLMap = objectTypesAndRelations.getDestinationSourceMap();
                setDestinationSourceMap(destinationSourceXMLMap);
                //setDestinationSourceMap(destinationSourceMap);
                List<String> runTimeInterfaceList = objectTypesAndRelations.getRunTimeInterfaceList();

                ObjectCreationProcessor objectCreationProcessor = new ObjectCreationProcessor();
                String clonedObjectId;
                if (createObjectBean.getSource().equalsIgnoreCase("PDM")) {
                    //clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, createObjectBean, "MBOM_MBOMPDM");
                    //clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, createObjectBean, runTimeInterfaceList);
                    clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createObjectBean, runTimeInterfaceList, Boolean.TRUE);
                } else {
                    clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createObjectBean);
                }
                //String clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, createObjectBean, "MBOM_MBOMPDM");
                //String clonedObjectId = objectCreationProcessor.processCreateReviseObjectOperation(context, createObjectBean, this.interfaceList);

                ODI_ITEM_IMPORT_THREAD_LOGGER.info("Cloned ObjectId : " + clonedObjectId);

                responseMessageFormatterBean.setObjectId(clonedObjectId);

            } catch (FrameworkException exp) {
                ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
                throw new RuntimeException(exp);
            } catch (Exception exp) {
                ODI_ITEM_IMPORT_THREAD_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }

            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
            ODI_ITEM_IMPORT_THREAD_LOGGER.info("Committing for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");           
//            commonUtilities.escapeOperationOff(context);
            ContextUtil.commitTransaction(context);
        } catch (FrameworkException | RuntimeException exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ODI_ITEM_IMPORT_THREAD_LOGGER.error("Aborting for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");
//            commonUtilities.escapeOperationOff(context);
            ContextUtil.abortTransaction(context);
            String replaceResponseMessage = replaceResponseMessage(exp.getMessage());
            String checkErrorCodeInErrorMessage = checkErrorCodeInErrorMessage(replaceResponseMessage);
            responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
            throw new RuntimeException(checkErrorCodeInErrorMessage);
        } catch (Exception exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ODI_ITEM_IMPORT_THREAD_LOGGER.error("Aborting for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");
//            commonUtilities.escapeOperationOff(context);
            ContextUtil.abortTransaction(context);
            String replaceResponseMessage = replaceResponseMessage(exp.getMessage());
            String checkErrorCodeInErrorMessage = checkErrorCodeInErrorMessage(replaceResponseMessage);
            responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
            throw new Exception(checkErrorCodeInErrorMessage);
        } finally {
            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
            ODI_ITEM_IMPORT_THREAD_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + typeForTimeChecking + "' name '" + nameForTimeChecking + "' revision '" + revisionForTimeChecking + "' has taken : '" + duration + "' milli-seconds for completion the full process");
        }

//        Date transactionEndTime = DateTimeUtils.getTime(new Date());
//        String elapsedTime = DateTimeUtils.elapsedTime(transactionStartTime, transactionEndTime, null, null);
//        ODI_ITEM_IMPORT_THREAD_LOGGER.debug("Time elapsed for the object type : '" + typeForTimeChecking + "' name : '" + nameForTimeChecking + "' revision : '" + revisionForTimeChecking + "' is " + elapsedTime);
    }

    public void retryCreatingOrUpdatingObject(Exception exp, HashMap<String, ResponseMessageFormaterBean> threadResponse) throws Exception {
        Boolean uniqueObjectErrorMessage = false;

        String deadLockErrorPattern = PropertyReader.getProperty("pattern.transaction.deadlock");

        Pattern pattern = Pattern.compile(deadLockErrorPattern);
        Matcher matcher = pattern.matcher(exp.getMessage());

        uniqueObjectErrorMessage = matcher.find();

        if (uniqueObjectErrorMessage) {
            Instant retryStartTime = Instant.now();
            do {
                try {
                    processODIItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());
                    threadResponse.put("successful", this.getResponseMessageFormaterBean());
                    this.getResponseMessageFormaterBean().setErrorMessage("");
                    uniqueObjectErrorMessage = false;
                } catch (Exception retryException) {
                    uniqueObjectErrorMessage = pattern.matcher(retryException.getMessage()).find();
                    if (uniqueObjectErrorMessage) {
                        Thread.sleep(1000);
                        Instant retryAgainTime = Instant.now();
                        long duration = DateTimeUtils.getDuration(retryStartTime, retryAgainTime);
                        if ((duration / 60000) == 1) {
                            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
                            uniqueObjectErrorMessage = false;
                        }
                    } else {
                        threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
                        uniqueObjectErrorMessage = false;
                    }
                }
            } while (uniqueObjectErrorMessage);

        } else {
            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
        }
    }

    public String replaceResponseMessage(String responseMessage) {
        try {
            Set<String> destinationMapKeySet = this.getDestinationSourceMap().keySet();
            Iterator<String> destinationKeyIterator = destinationMapKeySet.iterator();

            while (destinationKeyIterator.hasNext()) {
                String sourceKey = destinationKeyIterator.next();
                if (responseMessage.contains(sourceKey)) {
                    return responseMessage.replace(sourceKey, this.getDestinationSourceMap().get(sourceKey));
                }
            }

            return responseMessage;
        } catch (Exception exp) {
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug(exp);
            return responseMessage;
        }
    }

    public String checkErrorCodeInErrorMessage(String errorMessage) {
        String errorCode = "Error: #1900068: ";
        String errorCode2 = "Error: #1900016: ";
        String errorCode3 = "modify business object failed.";
        String errorPattern = errorCode + "|" + errorCode2 + "|" + errorCode3;

        Pattern pattern = Pattern.compile(errorPattern);
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            errorMessage = errorMessage.replace(errorCode, "");
            errorMessage = errorMessage.replace(errorCode2, "");
            errorMessage = errorMessage.replace("\n", ".");
            errorMessage = errorMessage.replace(errorCode3, "");
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1);
            return errorMessage;
        }

        return errorMessage;
    }

    public String validateCreateObjectBean(CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) {
        String errorMessage;
        if (NullOrEmptyChecker.isNull(createObjectBean)) {
            errorMessage = "No data found in the bean object";
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
            MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
            ODI_ITEM_IMPORT_THREAD_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
        }

        //BusinessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName());
        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), !createObjectBean.getIsAutoName());

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            errorMessage = "There is no attribute presents in the request";
            ODI_ITEM_IMPORT_THREAD_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        } else if (NullOrEmptyChecker.isNullOrEmpty(this.getSource())) {
            return MAP_DIRECTORY.get("common");
        } else {
            return MAP_DIRECTORY.get(this.getSource() + "." + createObjectBean.getTnr().getType());
        }
    }
}
