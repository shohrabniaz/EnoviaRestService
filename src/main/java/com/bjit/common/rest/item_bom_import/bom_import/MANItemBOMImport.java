package com.bjit.common.rest.item_bom_import.bom_import;

import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.import_threads.BOMValidatorProcess;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
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
import java.util.concurrent.ForkJoinPool;
import matrix.db.Context;
import matrix.util.MatrixException;

public class MANItemBOMImport implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger IMPORT_BOM_LOGGER = org.apache.log4j.Logger.getLogger(MANItemBOMImport.class);
    public static Relationships bomRelationship = null;

    Relationships relationshipMapping = null;
    int counter = 0;
    Boolean gotErrorMessage = false;

    /**
     *
     * @param <T>
     * @param <K>
     * @param context
     * @param createBOMBeanDataList
     * @return
     */
    @Override
    public <T, K> K doImport(Context context, T createBOMBeanDataList) {
        List<CreateBOMBean> createBOMBeanList = (List<CreateBOMBean>) createBOMBeanDataList;
        try {

            String mappingFilePath = PropertyReader.getProperty("bom.import.mapping.xml.directory");

            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("bom.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(MANItemBOMImport.bomRelationship)) {
                    MANItemBOMImport.bomRelationship = (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath);
                }
                relationshipMapping = MANItemBOMImport.bomRelationship;
            } else {
                relationshipMapping = (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath);
            }

        } catch (Exception exp) {
            IMPORT_BOM_LOGGER.error(exp);
        }
        HashMap<String, List<ParentInfo>> responseMsgMap = new HashMap<>();

        try {
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            HashMap<String, Long> bomValidationAndProcessingTimeForParentMap = new HashMap<>();
            int total_line_from_PDM = createBOMBeanList.size();

            final int parallelism = Integer.parseInt(PropertyReader.getProperty("bom.validation.import.concurrent.total.thread.count"));
            ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);

            forkJoinPool.submit(() -> {

                createBOMBeanList.stream().parallel().forEach((CreateBOMBean createBOMBean) -> {

                    BOMImportVariables bomImportVariables = new BOMImportVariables();

                    bomImportVariables.relName = "";
                    bomImportVariables.interfaceName = "";

                    bomImportVariables.relationshipList = relationshipMapping.getRelationshipList();
                    bomImportVariables.total_Child_from_PDM = 0;

                    try {
                        List<HashMap<ParentInfo, HashMap<String, ChildInfo>>> parentChildInfoMapList = new ArrayList<>();
                        List<HashMap<String, ArrayList<String>>> existingChildInfoRelMapList = new ArrayList<>();

                        BOMValidatorProcess BOMValidation = new BOMValidatorProcess();
                        bomImportVariables.errorInStructure = false;
                        bomImportVariables.isBOMCreatedSuccessfully = true;

                        List<ParentInfo> successfulParentInfoList = new ArrayList<>();
                        List<ParentInfo> errorParentInfoList = new ArrayList<>();
                        System.out.println("\n\n\n\n\n\n---------------------------" + (++counter) + "---------------------------\n\n\n\n\n\n");

                        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
                        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

                        BOMDataCollector dataCollector = new BOMDataCollector();
                        try {
                            String parent = createBOMBean.getItem().getName();

                            int child_in_a_line = createBOMBean.getLines().size();
                            bomImportVariables.total_Child_from_PDM = bomImportVariables.total_Child_from_PDM + child_in_a_line;
                            IMPORT_BOM_LOGGER.debug("Parent name | " + parent + " | Total number of requested child " + child_in_a_line);
                            long start_BOM_validationTime = System.currentTimeMillis();

                            dataCollector = BOMValidation.bomValidationAndDataCollection(businessObjectUtil, businessObjectOperations, createBOMBean, context, bomImportVariables.relationshipList, bomImportVariables.relName, bomImportVariables.interfaceName, responseMsgMap);

                            long end_BOM_validationTime = System.currentTimeMillis();
                            long total_BOM_validationTime = end_BOM_validationTime - start_BOM_validationTime;
                            bomValidationAndProcessingTimeForParentMap.put(parent, total_BOM_validationTime);

                            parentChildInfoMapList.add(dataCollector.getRequestParentChildInfoMap());
                            if (!NullOrEmptyChecker.isNullOrEmpty(dataCollector.getExistingChildInfoRelMap())) {
                                existingChildInfoRelMapList.add(dataCollector.getExistingChildInfoRelMap());
                            }
                            if (dataCollector.isBOMcontainError()) {

                                HashMap<String, ParentInfo> errorCheckerMapper = new HashMap<>();

                                gotErrorMessage = false;
                                parentChildInfoMapList.get(0).keySet().parallelStream().forEach((ParentInfo parentInfo) -> {

                                    if (!gotErrorMessage) {
                                        parentChildInfoMapList.get(0).keySet().forEach((ParentInfo parentInformation) -> {
                                            if (NullOrEmptyChecker.isNullOrEmpty(parentInformation.getErrorMessage())) {
                                                parentInformation.setErrorMessage("");
                                                HashMap<String, ChildInfo> childrenMap = parentChildInfoMapList.get(0).get(parentInfo);
                                                childrenMap.keySet().stream().forEach((String childName) -> {
                                                    String errorMessage = childrenMap.get(childName).getMessage();
                                                    String parentInfoErrors = CommonUtilities.removeExceptions(parentInfo.getErrorMessage());

                                                    addErrorInTheResponseList(parentInformation, errorMessage);
                                                });
                                                parentInformation.setErrorMessage(null);
                                            } else {
                                                String errorMessage = parentInformation.getErrorMessage();
                                                addErrorInTheResponseList(parentInformation, errorMessage);
                                            }

                                            uniqueErrorList(parentInformation, errorCheckerMapper, errorParentInfoList);

                                            gotErrorMessage = true;
                                        });
                                    }

                                    if (!gotErrorMessage) {
                                        parentChildInfoMapList.get(0).get(parentInfo).keySet().stream().forEach((String childName) -> {
                                            ChildInfo childInfo = parentChildInfoMapList.get(0).get(parentInfo).get(childName);
                                            String errorMessage = CommonUtilities.removeExceptions(childInfo.getMessage());
                                            if (!NullOrEmptyChecker.isNullOrEmpty(childInfo.getMessage())) {

                                                addErrorInTheResponseList(parentInfo, errorMessage);
                                                uniqueErrorList(parentInfo, errorCheckerMapper, errorParentInfoList);
                                                
                                                gotErrorMessage = true;
                                            }
                                        });
                                    }
                                });

                                if (responseMsgMap.containsKey("Error")) {
                                    responseMsgMap.get("Error").addAll(errorParentInfoList);
                                } else {
                                    responseMsgMap.put("Error", errorParentInfoList);
                                }

                                return;
                            }
                        } catch (IOException | MatrixException | NullPointerException ex) {
                            bomImportVariables.isBOMCreatedSuccessfully = false;
                            IMPORT_BOM_LOGGER.error(ex);
                            throw new RuntimeException(ex);
                        } catch (Exception ex) {
                            bomImportVariables.isBOMCreatedSuccessfully = false;
                            IMPORT_BOM_LOGGER.error(ex);
                            throw new RuntimeException(ex);
                        }

                        IMPORT_BOM_LOGGER.info(" | Data | Total Requested BOM | " + total_line_from_PDM);
                        IMPORT_BOM_LOGGER.info(" | Data | Total Requested Child | " + bomImportVariables.total_Child_from_PDM);
                        IMPORT_BOM_LOGGER.info(" | Process Time | Total Validation | " + bomValidationAndProcessingTimeForParentMap);

                        CommonUtilities commonUtilities = new CommonUtilities();
                        try {
                            commonUtilities.doStartTransaction(context);
                        } catch (FrameworkException | RuntimeException | InterruptedException ex) {
                            IMPORT_BOM_LOGGER.error(ex);
                            throw new RuntimeException(ex);
                        }

                        bomImportVariables.parentChildInfoMapList = parentChildInfoMapList;
                        bomImportVariables.errorParentInfoList = errorParentInfoList;
                        bomImportVariables.responseMsgMap = responseMsgMap;
                        bomImportVariables.commonPropertyReader = commonPropertyReader;

                        bomImportVariables.successfulParentInfoList = successfulParentInfoList;
                        bomImportVariables.bomValidationAndProcessingTimeForParentMap = bomValidationAndProcessingTimeForParentMap;

                        BOMImportProcess BOMImportProcess = new BOMImportProcess();
                        BOMImportProcess.bomImportProcess(context, bomImportVariables);

                        for (int i = 0; i < existingChildInfoRelMapList.size(); i++) {
                            HashMap<String, ArrayList<String>> existingChildInfoRelMap = existingChildInfoRelMapList.get(i);
                            for (String itemNameRevPos : existingChildInfoRelMap.keySet()) {
                                ArrayList<String> relIDList = existingChildInfoRelMap.get(itemNameRevPos);
                                relIDList.stream().parallel().forEach(relID -> {
                                    try {
                                        Instant BomUpdateStartTime = Instant.now();
                                        businessObjectUtil.disconnectRelationship(context, relID);
                                        Instant BomUpdateEndTime = Instant.now();
                                        long bomUpdateDuration = DateTimeUtils.getDuration(BomUpdateStartTime, BomUpdateEndTime);
                                        bomImportVariables.deleted_connection++;
                                        IMPORT_BOM_LOGGER.info(" | Disconnect | rel_ID=" + relID + " | " + bomUpdateDuration);
                                    } catch (MatrixException ex) {
                                        bomImportVariables.errorInStructure = true;
                                        IMPORT_BOM_LOGGER.error(ex);
                                        return;
                                    }
                                });
                            }
                        }

                        IMPORT_BOM_LOGGER.debug("Total BOM/line requested from PDM | " + total_line_from_PDM);
                        IMPORT_BOM_LOGGER.debug("Total number of child requested from PDM | " + bomImportVariables.total_Child_from_PDM);
                        bomImportVariables.total_connection_processed = bomImportVariables.new_connection + bomImportVariables.modified_connection + bomImportVariables.deleted_connection;
                        IMPORT_BOM_LOGGER.debug("New connection | " + bomImportVariables.new_connection);
                        IMPORT_BOM_LOGGER.debug("Modified connection | " + bomImportVariables.modified_connection);
                        IMPORT_BOM_LOGGER.debug("Deleted connection | " + bomImportVariables.deleted_connection);
                        IMPORT_BOM_LOGGER.debug("Total connection processed | " + bomImportVariables.total_connection_processed);
                        IMPORT_BOM_LOGGER.info(" | Data | Total Child ENOVIA | " + bomImportVariables.total_child_in_ENOVIA);
                        IMPORT_BOM_LOGGER.info(" | Process Time | Total BOM VAlidation and Process | " + bomValidationAndProcessingTimeForParentMap);

                        if (!bomImportVariables.errorInStructure) {
                            IMPORT_BOM_LOGGER.debug("Committing Transaction");
                            try {
                                ContextUtil.commitTransaction(context);
                            } catch (Exception ex) {
                                IMPORT_BOM_LOGGER.error(ex);
                                throw new RuntimeException(ex);
                            }
                        }

                        if (errorParentInfoList.isEmpty()) {
                            if (responseMsgMap.containsKey("Successful")) {
                                responseMsgMap.get("Successful").addAll(successfulParentInfoList);
                            } else {
                                responseMsgMap.put("Successful", successfulParentInfoList);
                            }
                        } else {
                            throw new RuntimeException(CommonUtilities.removeExceptions(errorParentInfoList.get(0).getErrorMessage()));
                        }
                    } catch (Exception exp) {
                        IMPORT_BOM_LOGGER.error("Aborting Transaction");
                        IMPORT_BOM_LOGGER.error(exp);
                        ContextUtil.abortTransaction(context);
                    }
                });
            }).get();
        } catch (Exception exp) {
            IMPORT_BOM_LOGGER.error(exp);
        }
        IMPORT_BOM_LOGGER.debug("responseMsgMap :: " + responseMsgMap);
        return (K) responseMsgMap;
    }

    private List<ParentInfo> uniqueErrorList(ParentInfo parentInfo, HashMap<String, ParentInfo> errorCheckerMapper, List<ParentInfo> errorParentInfoList) {
        String tnr_key = parentInfo.getTnr().getType() + "_" + parentInfo.getTnr().getName() + "_" + parentInfo.getTnr().getRevision();

        if (errorCheckerMapper.containsKey(tnr_key)) {
            ParentInfo previousParentErrors = errorCheckerMapper.get(tnr_key);
            previousParentErrors.getErrorMessages().addAll(parentInfo.getErrorMessages());
            //parentInformation = previousParentErrors;
        } else {
            errorCheckerMapper.put(tnr_key, parentInfo);
            errorParentInfoList.add(parentInfo);
        }
        return errorParentInfoList;
    }

    private ParentInfo addErrorInTheResponseList(ParentInfo parentInformation, String errorMessage) {
        if (NullOrEmptyChecker.isNullOrEmpty(parentInformation.getErrorMessages())) {
            List<String> childErrorList = new ArrayList<>();
            parentInformation.setErrorMessages(childErrorList);
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(errorMessage)) {
            String[] errorSplitByNewLine = errorMessage.split(System.lineSeparator());
            Arrays.stream(errorSplitByNewLine).forEach(childError -> {
                parentInformation.getErrorMessages().add(childError);
            });
        }
        
        return parentInformation;
    }

    /**
     *
     * @param context
     * @param relationshipId
     * @return
     * @throws FrameworkException
     */
    public List<String> getRelationshipInterfaces(Context context, String relationshipId) throws FrameworkException {
        try {
            String mqlRelationshipInterfaceQuery = "print connection " + relationshipId + " select interface dump";
            IMPORT_BOM_LOGGER.debug(mqlRelationshipInterfaceQuery);
            String queryResult = MqlUtil.mqlCommand(context, mqlRelationshipInterfaceQuery);
            return Arrays.asList(queryResult.split(","));
        } catch (Exception exp) {
            IMPORT_BOM_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
}
