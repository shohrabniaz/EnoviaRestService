/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.promises;

import com.bjit.common.rest.app.service.controller.bom.factory.BOMCreationProcessorFactory;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import matrix.db.Context;
import com.bjit.common.rest.app.service.controller.bom.interfaces.IBOMCreationProcessor;
import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;

/**
 *
 * @author BJIT
 */
public class CommonBOMImportPromise {

    private static final org.apache.log4j.Logger COMMON_BOM_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMImportPromise.class);

    public void bomImportProcess(Context context, CommonBOMImportParams commonBomImportVariables) {
        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("bom.import.concurrent.total.thread.count"));
        COMMON_BOM_IMPORT_PROCESS_LOGGER.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        BOMCreationProcessorFactory bomCreationProcessorFactory = new BOMCreationProcessorFactory();
        commonBomImportVariables.parentChildInfoMapList.forEach((HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap) -> {

            BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
            commonBomImportVariables.businessObjectUtil = businessObjectUtil;

            IBOMCreationProcessor bomCreationProcess = bomCreationProcessorFactory.getBomCreationProcess(commonBomImportVariables.source);
            bomCreationProcess.initilize(context, parentChildInfoMap, commonBomImportVariables);

            Future<?> future = executorService.submit(bomCreationProcess);
            futures.add(future);
        });

        // Await all runnables to be done (blocking)
        futures.forEach((Future<?> future) -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                COMMON_BOM_IMPORT_PROCESS_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }
        });
    }
}
