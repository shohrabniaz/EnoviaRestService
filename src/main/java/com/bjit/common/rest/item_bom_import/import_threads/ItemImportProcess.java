/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.import_threads;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Priority;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.HP_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.MD_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.MV_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.SP_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.SW_AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.creators.ItemCreationProcessor;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ItemTypesAndRelationsOfCustomXMLMap;
import com.bjit.mapper.mapproject.util.Constants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.BusinessObject;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public final class ItemImportProcess implements /*Runnable,*/ Callable {

    public static HashMap<String, String> BUSINESS_OBJECT_TYPE_MAP;
    private static final org.apache.log4j.Logger ITEM_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(ItemImportProcess.class);
    private static HashMap<String, String> MAP_DIRECTORY;

    private Context context;
    private CreateObjectBean createObjectBean;
    private ResponseMessageFormaterBean responseMessageFormatterBean;
    private String source;
    private HashMap<String, String> destinationSourceMap;

    public ItemImportProcess(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        BUSINESS_OBJECT_TYPE_MAP = PropertyReader.getProperties(BUSINESS_OBJECT_TYPE_MAP, "import.type.map.Enovia", Boolean.TRUE);
        this.setContext(context);
        this.setCreateObjectBean(createObjectBean);
        this.setResponseMessageFormaterBean(responseMessageFormatterBean);
        this.setSource(source);
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

    private void setDestinationSourceMap(HashMap<String, String> destinationSourceMap) {
        this.destinationSourceMap = destinationSourceMap;
    }

//    @Override
//    public void run() {
//        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");
//        ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Started  |");
//        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");
//        try {
//            processItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());
//
//        } catch (FrameworkException | RuntimeException exp) {
//            ITEM_IMPORT_PROCESS_LOGGER.error(exp);
//            throw new RuntimeException(exp);
//        } catch (Exception ex) {
//            ITEM_IMPORT_PROCESS_LOGGER.error(ex);
//            throw new RuntimeException(ex);
//        } finally {
//            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
//            ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Completed  |");
//            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
//        }
//    }
    @Override
    public Object call() throws Exception {
        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");
        ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Started  |");
        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");

        HashMap<String, ResponseMessageFormaterBean> threadResponse = new HashMap<>();
        try {

            processItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());

            threadResponse.put("successful", this.getResponseMessageFormaterBean());
        } catch (FrameworkException | RuntimeException exp) {
            ITEM_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
        } catch (Exception exp) {
            ITEM_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
        } finally {
            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
            ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Create Assembly Process Has Been Completed  |");
            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
        }

        return threadResponse;
    }

    public String processItem(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, RuntimeException, Exception {
//        Date transactionStartTime = DateTimeUtils.getTime(new Date());
        String typeForTimeChecking = createObjectBean.getTnr().getType();
        String nameForTimeChecking = createObjectBean.getTnr().getName();
        String revisionForTimeChecking = createObjectBean.getTnr().getRevision();

        HashMap<String, String> destinationSourceXMLMap;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();

        String newlyCreatedOrExistingItemsObjectId = "";

        try {
            /*---------------------------------------- ||| Start Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.debug("Starting Transaction");
            new CommonUtilities().doStartTransaction(context);

            checkAutoNameGeneration(context, createObjectBean);

            BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
            AttributeBusinessLogic attributeBusinessLogic = getTypeWiseAttributeBusinessLogicProcessor(createObjectBean);

            try {
                String objectType = createObjectBean.getTnr().getType();

                this.setSource(this.getSource().toLowerCase());

                if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
                    ITEM_IMPORT_PROCESS_LOGGER.error("Attribute list is Null or Empty");
                    throw new NullPointerException("Attribute list is Null or Empty");
                }
//                createObjectBean.getAttributes().put(commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision"), createObjectBean.getTnr().getRevision());

                ITEM_IMPORT_PROCESS_LOGGER.debug("Object Type : " + objectType);
                ITEM_IMPORT_PROCESS_LOGGER.debug("Source Environment : " + this.getSource());

                String envObjectType = this.getSource() + "." + objectType;

                ITEM_IMPORT_PROCESS_LOGGER.debug("Environment Object Type : " + envObjectType);

                createObjectBean.getTnr().setType(BUSINESS_OBJECT_TYPE_MAP.containsKey(envObjectType) ? BUSINESS_OBJECT_TYPE_MAP.get(envObjectType) : objectType);

                String mapsAbsoluteDirectory = validateCreateObjectBean(createObjectBean, businessObjectOperations);

                if (NullOrEmptyChecker.isNullOrEmpty(mapsAbsoluteDirectory)) {
                    //String errorMessage = "Mapping file may be not exist for the item type '" + createObjectBean.getTnr().getType() + "' or '" + objectType + "'";
                    String errorMessage = "System could not recognize the type : '" + createObjectBean.getTnr().getType() + "'";
                    ITEM_IMPORT_PROCESS_LOGGER.error(errorMessage);
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
//                ImportProductConfigurationStructureController importItemStructure = new ImportProductConfigurationStructureController();
//                if (importItemStructure.isIsImportItemStructure()) {
//                    ObjectCreationProcessor itemCreationProcessor = new ItemCreationProcessor();
//                    newlyCreatedOrExistingItemsObjectId = itemCreationProcessor.processCreateItemOperation(context, businessObjectUtil, businessObjectOperations, createObjectBean, new ArrayList<>(), Boolean.TRUE);
//                } else {
                //ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, BomImportMapping.class, createObjectBean);
                ItemTypesAndRelationsOfCustomXMLMap itemTypesAndRelationsFromCustomXMLMap = new ItemTypesAndRelationsOfCustomXMLMap(context, mapsAbsoluteDirectory, businessObjectUtil, businessObjectOperations, ItemImportMapping.class, createObjectBean, attributeBusinessLogic);
                destinationSourceXMLMap = itemTypesAndRelationsFromCustomXMLMap.getDestinationSourceMap();
                setDestinationSourceMap(destinationSourceXMLMap);
                //setDestinationSourceMap(destinationSourceMap);
                List<String> runTimeInterfaceList = itemTypesAndRelationsFromCustomXMLMap.getRunTimeInterfaceList();

                ObjectCreationProcessor itemCreationProcessor = new ItemCreationProcessor();
                newlyCreatedOrExistingItemsObjectId = itemCreationProcessor.processCreateItemOperation(context, businessObjectUtil, businessObjectOperations, createObjectBean, runTimeInterfaceList, Boolean.TRUE);
//                }
                ITEM_IMPORT_PROCESS_LOGGER.info("Newly Created or Existing Items ObjectId : " + newlyCreatedOrExistingItemsObjectId);

                /* ************************** Must Delete ************************** */
                responseMessageFormatterBean.setTnr(businessObjectOperations.getObjectTNR(context, newlyCreatedOrExistingItemsObjectId));
                /* ************************** Must Delete ************************** */

//                responseMessageFormatterBean.setObjectId(newlyCreatedOrExistingItemsObjectId);
            } catch (FrameworkException exp) {
                ITEM_IMPORT_PROCESS_LOGGER.error(exp);
                throw new RuntimeException(exp);
            } catch (Exception exp) {
                ITEM_IMPORT_PROCESS_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }

            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.info("Committing for '" + createObjectBean.getTnr().getType() + "' '" + createObjectBean.getTnr().getName() + "' '" + createObjectBean.getTnr().getRevision() + "'");
            ContextUtil.commitTransaction(context);
            responseMessageFormatterBean.setObjectId(newlyCreatedOrExistingItemsObjectId);
            return newlyCreatedOrExistingItemsObjectId;
        } catch (FrameworkException | RuntimeException exp) {
            ITEM_IMPORT_PROCESS_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.error("Aborting for '" + createObjectBean.getTnr().getType() + "' '" + createObjectBean.getTnr().getName() + "' '" + createObjectBean.getTnr().getRevision() + "'");
            ContextUtil.abortTransaction(context);
            String replaceResponseMessage = replaceResponseMessage(exp.getMessage());
            String checkErrorCodeInErrorMessage = checkErrorCodeInErrorMessage(replaceResponseMessage);
            responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
            throw new RuntimeException(checkErrorCodeInErrorMessage);
        } catch (Exception exp) {
            ITEM_IMPORT_PROCESS_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.error("Aborting for '" + createObjectBean.getTnr().getType() + "' '" + createObjectBean.getTnr().getName() + "' '" + createObjectBean.getTnr().getRevision() + "'");
            ContextUtil.abortTransaction(context);
            String replaceResponseMessage = replaceResponseMessage(exp.getMessage());
            String checkErrorCodeInErrorMessage = checkErrorCodeInErrorMessage(replaceResponseMessage);
            responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
            throw new Exception(checkErrorCodeInErrorMessage);
        } finally {
            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
            ITEM_IMPORT_PROCESS_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + typeForTimeChecking + "' name '" + nameForTimeChecking + "' revision '" + revisionForTimeChecking + "' has taken : '" + duration + "' milli-seconds for completion the full process");
        }
    }

    /**
     * Getting attribute business logic according to item type
     *
     * @param createObjectBean
     * @return
     */
    public AttributeBusinessLogic getTypeWiseAttributeBusinessLogicProcessor(CreateObjectBean createObjectBean) {
        AttributeBusinessLogic attributeBusinessLogic;
        switch (createObjectBean.getTnr().getType()) {
            case Constants.MODEL_VERSION:
                attributeBusinessLogic = new MV_AttributeBusinessLogic();
                break;
            case Constants.HARDWARE_PRODUCT:
                attributeBusinessLogic = new HP_AttributeBusinessLogic();
                break;
            case Constants.SOFTWARE_PRODUCT:
                attributeBusinessLogic = new SW_AttributeBusinessLogic();
                break;
            case Constants.SERVICE_PRODUCT:
                attributeBusinessLogic = new SP_AttributeBusinessLogic();
                break;
            case Constants.MEDICAL_DEVICE_PRODUCT:
                attributeBusinessLogic = new MD_AttributeBusinessLogic();
                break;
            default:
                attributeBusinessLogic = new AttributeBusinessLogic();
                break;
        }
        return attributeBusinessLogic;
    }

    /**
     * Check auto name generation
     *
     * @param createObjectBean
     * @param context
     * @throws Exception
     */
    private void checkAutoNameGeneration(CreateObjectBean createObjectBean, Context context) throws Exception {
        if (createObjectBean.getIsAutoName()) {
            String templateBusinessObjectId = createObjectBean.getTemplateBusinessObjectId();
            templateBusinessObjectId = Optional.ofNullable(templateBusinessObjectId).orElse(PropertyReader.getProperty("template.object.type.Hardware Product"));
            createObjectBean.setTemplateBusinessObjectId(templateBusinessObjectId);
            BusinessObjectOperations businessObjectOperation = new BusinessObjectOperations();
            String autoGenerateHardwareProductName = businessObjectOperation.getAutoGenerateObjectName(context, new BusinessObject(templateBusinessObjectId));
            createObjectBean.getTnr().setName(autoGenerateHardwareProductName);
            createObjectBean.setIsAutoName(Boolean.FALSE);
        }
    }

    private void checkAutoNameGeneration(Context context, CreateObjectBean createObjectBean) throws Exception {
        if (createObjectBean.getIsAutoName()) {
            BusinessObjectOperations businessObjectOperation = new BusinessObjectOperations();
            String autoGenerateHardwareProductName = businessObjectOperation.getAutoName(context, createObjectBean.getTnr().getType());
            createObjectBean.getTnr().setName(autoGenerateHardwareProductName);
            createObjectBean.setIsAutoName(Boolean.FALSE);
        }
    }

    private String replaceResponseMessage(String responseMessage) {
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
            ITEM_IMPORT_PROCESS_LOGGER.debug(exp);
            return responseMessage;
        }
    }

    private String checkErrorCodeInErrorMessage(String errorMessage) {
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

    private String validateCreateObjectBean(CreateObjectBean createObjectBean, BusinessObjectOperations BusinessObjectOperations) {
        String errorMessage;

        if (NullOrEmptyChecker.isNull(createObjectBean)) {
            errorMessage = "No data found in the bean object";
            ITEM_IMPORT_PROCESS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
            MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
            ITEM_IMPORT_PROCESS_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
        }

        //BusinessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName());
        BusinessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), false);

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            errorMessage = "There is no attribute presents in the request";
            ITEM_IMPORT_PROCESS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        } else if (NullOrEmptyChecker.isNullOrEmpty(this.getSource())) {
            return MAP_DIRECTORY.get("common");
        } else {
            return MAP_DIRECTORY.get(this.getSource() + "." + createObjectBean.getTnr().getType());
        }
    }
}
