/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.promises;

import com.bjit.common.rest.app.service.controller.bom.factory.BOMValidatorFactory;
import com.bjit.common.rest.app.service.controller.bom.interfaces.IBomValidator;
import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class BomValidatorPromise {

    private static final org.apache.log4j.Logger BOM_VALIDATOR_PROMISE = org.apache.log4j.Logger.getLogger(BomValidatorPromise.class);

    public Object validatorPromise(String source, List<CreateBOMBean> createBOMBeanList, CommonBOMImportParams commonBomImportVariables, Context context, HashMap<String, Long> bomProcessStatisticalParentMap, List<HashMap<ParentInfo, HashMap<String, ChildInfo>>> parentChildInfoMapList, List<HashMap<String, ArrayList<String>>> existingChildInfoRelMapList) throws InterruptedException, NumberFormatException, ExecutionException {
        IBomValidator commonBomValidation = new BOMValidatorFactory().getValidation(source);

        final int parallelism = Integer.parseInt(PropertyReader.getProperty("bom.validation.import.concurrent.total.thread.count"));
        ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);

        Object validatedObject = forkJoinPool.submit(() -> {

            createBOMBeanList.stream().parallel().forEach((CreateBOMBean commoncreateBOMStructure) -> {

                BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
                BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

                BOMDataCollector dataCollector;
                try {

                    String parent = Optional.ofNullable(commoncreateBOMStructure.getItem()).orElseThrow(() -> new RuntimeException("Invalid request format for parent item")).getName();

                    int child_in_a_line = Optional.ofNullable(commoncreateBOMStructure.getLines()).orElseThrow(() -> new RuntimeException("Invalid request format for child list")).size();
                    commonBomImportVariables.total_Child_from_PDM = commonBomImportVariables.total_Child_from_PDM + child_in_a_line;
                    BOM_VALIDATOR_PROMISE.debug("Parent name | " + parent + " | Total number of requested child " + child_in_a_line);
                    long start_BOM_validationTime = System.currentTimeMillis();

                    dataCollector = commonBomValidation.bomValidationAndDataCollection(context, businessObjectUtil, businessObjectOperations, commoncreateBOMStructure, commonBomImportVariables);

                    long end_BOM_validationTime = System.currentTimeMillis();
                    long total_BOM_validationTime = end_BOM_validationTime - start_BOM_validationTime;
                    bomProcessStatisticalParentMap.put(parent, total_BOM_validationTime);

                    parentChildInfoMapList.add(dataCollector.getRequestParentChildInfoMap());
                    if (!NullOrEmptyChecker.isNullOrEmpty(dataCollector.getExistingChildInfoRelMap())) {
                        existingChildInfoRelMapList.add(dataCollector.getExistingChildInfoRelMap());
                    }
                    if (dataCollector.isBOMcontainError()) {
                        return;
                    }
                } catch (Exception ex) {
                    commonBomImportVariables.isBOMCreatedSuccessfully = false;
                    BOM_VALIDATOR_PROMISE.error(ex.getMessage());
                    BOM_VALIDATOR_PROMISE.error(ex);
                    throw new RuntimeException(ex);
                }
            });
        }).get();

        return validatedObject;
    }
}
