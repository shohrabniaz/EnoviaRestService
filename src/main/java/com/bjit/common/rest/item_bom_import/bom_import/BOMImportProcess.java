/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.bom_import;

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

/**
 *
 * @author BJIT
 */
public class BOMImportProcess {

    private static final org.apache.log4j.Logger BOM_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(BOMImportProcess.class);

    public <K> void bomImportProcess(Context context, BOMImportVariables bomImportVariables) {
        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("bom.import.concurrent.total.thread.count"));
        BOM_IMPORT_PROCESS_LOGGER.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        bomImportVariables.parentChildInfoMapList.forEach((HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap) -> {
            
            BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
            bomImportVariables.businessObjectUtil = businessObjectUtil;
            
            BOMImportThreadedProcessing BOMImportCallable = new BOMImportThreadedProcessing(context, parentChildInfoMap, bomImportVariables);
            Future<?> future = executorService.submit(BOMImportCallable);
            futures.add(future);
        });

        // Await all runnables to be done (blocking)
        futures.forEach((Future<?> future) -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                BOM_IMPORT_PROCESS_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }
        });
    }
}
