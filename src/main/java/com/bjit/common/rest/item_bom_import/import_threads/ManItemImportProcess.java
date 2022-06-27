/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.import_threads;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Priority;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import static com.bjit.common.rest.item_bom_import.import_threads.ODIItemImportProcess.BUSINESS_OBJECT_TYPE_MAP;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.item_import.MopazAttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import matrix.db.BusinessObject;

import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public final class ManItemImportProcess extends ODIItemImportProcess {

    private static final org.apache.log4j.Logger ITEM_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(ManItemImportProcess.class);

    public ManItemImportProcess(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        super(context, createObjectBean, responseMessageFormatterBean, source);
        this.setSource(this.getSource().toLowerCase());
    }

    @Override
    public Object call() throws Exception {
        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");
        ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Mopaz Create Assembly Process Has Been Started  |");
        ITEM_IMPORT_PROCESS_LOGGER.debug("----------------------------- -----------------------------");

        HashMap<String, ResponseMessageFormaterBean> threadResponse = new HashMap<>();
        try {

            processManItem(this.getContext(), this.getCreateObjectBean(), this.getResponseMessageFormaterBean());

            threadResponse.put("successful", this.getResponseMessageFormaterBean());
        } catch (FrameworkException | RuntimeException exp) {
            exp.printStackTrace();
            ITEM_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
        } catch (Exception exp) {
            ITEM_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.getResponseMessageFormaterBean());
        } finally {
            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
            ITEM_IMPORT_PROCESS_LOGGER.debug("|  Asynchronous Mopaz Create Assembly Process Has Been Completed  |");
            ITEM_IMPORT_PROCESS_LOGGER.debug("------------------------------ ------------------------------");
        }

        return threadResponse;
    }

    public void processManItem(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, RuntimeException, Exception {
        String typeForTimeChecking = createObjectBean.getTnr().getType();
        String nameForTimeChecking = createObjectBean.getTnr().getName();
        String revisionForTimeChecking = createObjectBean.getTnr().getRevision();

        HashMap<String, String> destinationSourceXMLMap;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        CommonUtilities commonUtils = new CommonUtilities();
        Instant itemImportStartTime = Instant.now();

        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        AttributeBusinessLogic attributeBusinessLogic = new MopazAttributeBusinessLogic();
        try {
            commonUtils.doStartTransaction(context);
            try {
                String objectType = createObjectBean.getTnr().getType();
                String enoviaType = commonUtils.getEnoviaType(this.getSource(), objectType);

                if (NullOrEmptyChecker.isNullOrEmpty(enoviaType)) {
                    throw new RuntimeException("Given object type: '" + objectType + "' is not allowed for ERP: '" + this.getSource());
                }

                BusinessObject searchedBusinessObject = null;
                ArrayList<BusinessObject> listOfMatchedItems = businessObjectUtil.findBO(context, enoviaType, createObjectBean.getTnr().getName(), createObjectBean.getTnr().getRevision(), new HashMap<String, String>());
                boolean doesItemExist = false;
                String searchedObjectsTypeName = "";
                if (listOfMatchedItems.size()> 0) {
                    searchedBusinessObject = listOfMatchedItems.get(0);
                    searchedBusinessObject.open(context);
                    searchedObjectsTypeName = searchedBusinessObject.getTypeName();
                    doesItemExist = true;
                    searchedBusinessObject.close(context);
                }

                String inventoryUnit = Optional.ofNullable(createObjectBean.getAttributes().get("Inventory unit")).orElse(PropertyReader.getProperty("pdm.default.inventory.unit"));

                List<String> createAssemblyUnits = Arrays.asList(PropertyReader.getProperty("pattern.create.assembly.inventory.unit").split("\\|"));
                List<String> processContinuousCreateMaterialUnits = Arrays.asList(PropertyReader.getProperty("pattern.process.continuous.create.material.inventory.unit").split("\\|"));

                boolean inventoryUnitError = true;
                if (createAssemblyUnits.stream().anyMatch(createAssemblyInventory -> createAssemblyInventory.equalsIgnoreCase(inventoryUnit))
                        && (objectType.equals("CreateAssembly") || objectType.equals("CreateMaterial"))) {
                    inventoryUnitError = false;
                } else if (processContinuousCreateMaterialUnits.stream().anyMatch(materialUnit -> materialUnit.equalsIgnoreCase(inventoryUnit))
                        && objectType.equals("ProcessContinuousCreateMaterial")) {
                    inventoryUnitError = false;
                }

                if (inventoryUnitError) {
                    throw new RuntimeException("Inventory Unit '" + inventoryUnit + "' is not allowed for Type: '" + objectType);
                }

                if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
                    ITEM_IMPORT_PROCESS_LOGGER.error("Attribute list is Null or Empty");
                    throw new NullPointerException("Attribute list is Null or Empty");
                }

                ITEM_IMPORT_PROCESS_LOGGER.debug("Object Type : " + objectType);
                ITEM_IMPORT_PROCESS_LOGGER.debug("Source Environment : " + this.getSource());

                String envObjectType = this.getSource() + "." + objectType;

                ITEM_IMPORT_PROCESS_LOGGER.debug("Environment Object Type : " + envObjectType);

                String itemType = BUSINESS_OBJECT_TYPE_MAP.containsKey(envObjectType) ? BUSINESS_OBJECT_TYPE_MAP.get(envObjectType) : objectType;

                /**
                 * Here business process will change for changing type of the
                 * object. But for now we will shutdown the further processing
                 * of updating or creating item
                 */
                if (!NullOrEmptyChecker.isNullOrEmpty(searchedObjectsTypeName)) {
                    if (!itemType.equalsIgnoreCase(searchedObjectsTypeName)) {
                        throw new RuntimeException("Type change of an object is not supported by the system (different inventory unit found)");
                    }
                }

                createObjectBean.getTnr().setType(itemType);

                String mapsAbsoluteDirectory = validateCreateObjectBean(createObjectBean, businessObjectOperations);

                if (NullOrEmptyChecker.isNullOrEmpty(mapsAbsoluteDirectory)) {
                    //String errorMessage = "Mapping file may be not exist for the item type '" + createObjectBean.getTnr().getType() + "' or '" + objectType + "'";
                    String errorMessage = "System could not recognize the type : '" + createObjectBean.getTnr().getType() + "'";
                    ITEM_IMPORT_PROCESS_LOGGER.error(errorMessage);
                    throw new NullPointerException(errorMessage);
                }

                /*---------------------------------------- ||| Process for cloning object ||| ----------------------------------------*/
                /**
                 * This process modifies the attribute map of CreateObjectBean
                 * class. As the attribute map is a reference type so when the
                 * map is manipulated on other places then the real object face
                 * the changes
                 */
                ObjectTypesAndRelations objectTypesAndRelations = new ObjectTypesAndRelations(context, businessObjectUtil, doesItemExist, mapsAbsoluteDirectory, ItemImportMapping.class, createObjectBean, attributeBusinessLogic);
                destinationSourceXMLMap = objectTypesAndRelations.getDestinationSourceMap();
                setDestinationSourceMap(destinationSourceXMLMap);
                List<String> runTimeInterfaceList = objectTypesAndRelations.getRunTimeInterfaceList();

                ObjectCreationProcessor objectCreationProcessor = new ObjectCreationProcessor();
                String clonedObjectId = objectCreationProcessor.processCreateReviseEnoviaObject(context, businessObjectUtil, businessObjectOperations, createObjectBean, runTimeInterfaceList, Boolean.TRUE);

                ITEM_IMPORT_PROCESS_LOGGER.info("Cloned ObjectId : " + clonedObjectId);

                responseMessageFormatterBean.setObjectId(clonedObjectId);

            } catch (FrameworkException exp) {
                ITEM_IMPORT_PROCESS_LOGGER.error(exp);
                throw new RuntimeException(exp);
            } catch (Exception exp) {
                exp.printStackTrace();
                ITEM_IMPORT_PROCESS_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }

            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.info("Committing for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");
            ContextUtil.commitTransaction(context);
        } catch (FrameworkException | RuntimeException exp) {
            exp.printStackTrace();
            ITEM_IMPORT_PROCESS_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.error("Aborting for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");
            ContextUtil.abortTransaction(context);
            String replaceResponseMessage = replaceResponseMessage(exp.getMessage());
            String checkErrorCodeInErrorMessage = checkErrorCodeInErrorMessage(replaceResponseMessage);
            responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
            throw new RuntimeException(checkErrorCodeInErrorMessage);
        } catch (Exception exp) {
            ITEM_IMPORT_PROCESS_LOGGER.debug(exp);
            /*---------------------------------------- ||| Aborting Transaction Clone Business Object ||| ----------------------------------------*/
            ITEM_IMPORT_PROCESS_LOGGER.error("Aborting for '" + typeForTimeChecking + "' '" + nameForTimeChecking + "' '" + revisionForTimeChecking + "'");
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
}
