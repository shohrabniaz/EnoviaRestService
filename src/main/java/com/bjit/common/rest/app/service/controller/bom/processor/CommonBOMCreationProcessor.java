/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.processor;

import com.bjit.common.rest.app.service.controller.bom.utilities.CommonBOMUtilities;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import matrix.db.Context;
import com.bjit.common.rest.app.service.controller.bom.interfaces.IBOMCreationProcessor;

/**
 *
 * @author BJIT
 */
public class CommonBOMCreationProcessor implements IBOMCreationProcessor {

    private static final org.apache.log4j.Logger COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMCreationProcessor.class);

    private Context context;
    private HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap;
    private CommonBOMImportParams commonBomImportVariables;
    private List<ParentInfo> errorParentInfoList;
    private HashMap<String, List<ParentInfo>> responseMsgMap;
    private BusinessObjectUtil businessObjectUtil;
    private List<ParentInfo> successfulParentInfoList;
    private HashMap<String, Long> bomValidationAndProcessingTimeForParentMap;
    private CommonBOMUtilities commonBomUtilities;

    public CommonBOMCreationProcessor() {
    }

    public CommonBOMCreationProcessor(Context context, HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap, CommonBOMImportParams commonBomImportVariables) {
        this.initilize(context, parentChildInfoMap, commonBomImportVariables);
    }

    @Override
    public void initilize(Context context, HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap, CommonBOMImportParams commonBomImportVariables) {

        this.context = context;
        this.parentChildInfoMap = parentChildInfoMap;
        this.commonBomImportVariables = commonBomImportVariables;

        this.errorParentInfoList = commonBomImportVariables.errorParentInfoList;
        this.responseMsgMap = commonBomImportVariables.responseMsgMap;
        this.businessObjectUtil = commonBomImportVariables.businessObjectUtil;
        this.successfulParentInfoList = commonBomImportVariables.successfulParentInfoList;
        this.bomValidationAndProcessingTimeForParentMap = commonBomImportVariables.bomStatisticalTimeForParentMap;

        commonBomUtilities = new CommonBOMUtilities(this.context, this.businessObjectUtil, this.commonBomImportVariables);
    }

    @Override
    public Object call() throws Exception {
        for (ParentInfo parentInfoKey : parentChildInfoMap.keySet()) {
            StringBuilder errorMsgBuilder = new StringBuilder();
            ParentInfo responseParentInfo = new ParentInfo();
            if (!NullOrEmptyChecker.isNullOrEmpty(parentInfoKey.getErrorMessage())) {
                commonBomImportVariables.errorInStructure = true;
                errorMsgBuilder.append(parentInfoKey.getErrorMessage());
                responseParentInfo.setTnr(parentInfoKey.getTnr());
                responseParentInfo.setErrorMessage(errorMsgBuilder.toString());
                errorParentInfoList.add(responseParentInfo);
                responseMsgMap.put("Error", errorParentInfoList);
                return responseMsgMap;
            }

            String parentName = parentInfoKey.getTnr().getName();
            responseParentInfo.setTnr(parentInfoKey.getTnr());
            HashMap<String, ChildInfo> childInfoMap = parentChildInfoMap.get(parentInfoKey);

            int numberOfChildInTheBOM = 0;
            int unprocessed_connection = 0;
            long start_BOM_process_time = System.currentTimeMillis();

            for (String childNamePositionKey : childInfoMap.keySet()) {
                ChildInfo childInfo = childInfoMap.get(childNamePositionKey);
                if (!childInfo.getMessage().isEmpty() && childInfo.getMessage() != null) {
                    commonBomImportVariables.errorInStructure = true;
                    errorMsgBuilder.append(childInfo.getMessage());
                    responseParentInfo.setErrorMessage(errorMsgBuilder.toString());
                    errorParentInfoList.add(responseParentInfo);
                    responseMsgMap.put("Error", errorParentInfoList);
                    return responseMsgMap;
                }

                HashMap<String, String> relationshipAttributes = commonBomUtilities.relationShipsDefaultAttributes(childInfo.getAttributeNameValueMap(), childInfo.getChildTNR());

                childInfo.setAttributeNameValueMap(relationshipAttributes);

                int childQuantityFromRequest = 0;

                childQuantityFromRequest = childInfo.getChildQuantity();

                numberOfChildInTheBOM = numberOfChildInTheBOM + childQuantityFromRequest;

                if (!NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelIDList())) {

                    COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER.debug("Modifying relationship from parent :  " + parentName + " to child : " + childInfo.getChildTNR().toString());

                    int numOfExisitingChild = childInfo.getRelIDList().size(); //num of child with same name and position

                    if (childQuantityFromRequest > numOfExisitingChild) {
                        childInfo.getRelIDList().stream().parallel().forEach((String relID) -> {
                            commonBomUtilities.updateExistingConnectionAttributes(childInfo, relID, relationshipAttributes, parentName);
                        });

                        int noOfNewConnection = childQuantityFromRequest - numOfExisitingChild;
                        COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER.debug("Requested new duplicate connection : No of duplicate connection " + noOfNewConnection);

                        IntStream.range(0, noOfNewConnection).parallel().forEach((int iterator) -> {
                            commonBomUtilities.createNewConnection(childInfo, relationshipAttributes, parentName);
                        });

                        unprocessed_connection = numOfExisitingChild;
                    } else {
                        final int numOfChildToDisconnect = numOfExisitingChild - childQuantityFromRequest;

                        ArrayList<String> childRelIdList = childInfo.getRelIDList();
                        List<String> disconnectedRelIds = new ArrayList<>();
                        IntStream.range(0, numOfChildToDisconnect).parallel().forEach(iterator -> {
                            commonBomUtilities.disconnectChildFromParent(iterator, childRelIdList, disconnectedRelIds, childInfo, parentName);
                        });

                        childRelIdList.removeAll(disconnectedRelIds);
                        childRelIdList.stream().parallel().forEach(relId -> {
                            commonBomUtilities.updateExistingConnectionAttributes(childInfo, relId, relationshipAttributes, parentName);
                        });
                    }
                }
                if (!commonBomImportVariables.errorInStructure && NullOrEmptyChecker.isNullOrEmpty(childInfo.getRelIDList())) {
                    COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER.debug("Creating relationship from parent ::  " + parentName + " to child :: " + childInfo.getChildTNR().getName());

                    IntStream.range(0, childQuantityFromRequest).parallel().forEach(iterator -> {
                        commonBomUtilities.createNewConnection(childInfo, relationshipAttributes, parentName);
                    });
                }
            }
            long end_BOM_process_time = System.currentTimeMillis();
            long total_BOM_process_time = end_BOM_process_time - start_BOM_process_time;

            COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER.info(" | Data | Parent | " + parentName + " | Total child=" + numberOfChildInTheBOM);
            COMMON_BOM_CREATION_AND_SPLITTING_PROCESSOR_LOGGER.info(" | Process Time | Single BOM | " + parentName + " | " + total_BOM_process_time);
            commonBomImportVariables.total_child_in_ENOVIA = commonBomImportVariables.total_child_in_ENOVIA + numberOfChildInTheBOM;

            if (bomValidationAndProcessingTimeForParentMap.containsKey(parentName)) {
                bomValidationAndProcessingTimeForParentMap.put(parentName, bomValidationAndProcessingTimeForParentMap.get(parentName) + total_BOM_process_time);
            }
            successfulParentInfoList.add(responseParentInfo);
        }

        return true;
    }
}
