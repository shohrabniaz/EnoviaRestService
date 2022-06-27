/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.import_threads.ItemImportProcess;
import com.bjit.common.rest.item_bom_import.import_threads.ODIItemImportProcess;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class ItemImport implements ItemOrBOMImport {

  private static final org.apache.log4j.Logger CREATE_MAN_ITEM_LOGGER = org.apache.log4j.Logger.getLogger(ItemImport.class);

    public <T, K> K doImport(final Context context, final T objectDataBeanRequest) {

        ObjectDataBean objectDataBean = (ObjectDataBean) objectDataBeanRequest;
        List<DataTree> dataTreeList = objectDataBean.geDataTree();
        List<ResponseMessageFormaterBean> tnrSuccessfullList = new ArrayList<>();
        List<ResponseMessageFormaterBean> tnrErrorList = new ArrayList<>();
        HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = new HashMap<>();

        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("item.import.concurrent.total.thread.count"));
        CREATE_MAN_ITEM_LOGGER.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        CREATE_MAN_ITEM_LOGGER.info("Number of items in the request is : " + dataTreeList.size());

        dataTreeList.forEach((DataTree dataTree) -> {
            CreateObjectBean createObjectBean = dataTree.getItem();
            createObjectBean.setSource(objectDataBean.getSource());

            ResponseMessageFormaterBean responseMessageFormatterBean = new ResponseMessageFormaterBean();
            try {
                responseMessageFormatterBean.setTnr((TNR) createObjectBean.getTnr());
                responseMessageFormatterBean.setRowSequenceIdentifier(Optional.ofNullable(dataTree.getRowSequenceIdentifier()).orElse(null));

                //Runnable odiImportProcess = new ODIItemImportProcess(context, createObjectBean, tnrId);
                Callable importProcess = new ItemImportProcess(context, createObjectBean, responseMessageFormatterBean, objectDataBean.getSource());
                //executor.execute(odiImportProcess);
                Future<?> future = executorService.submit(importProcess);
                futures.add(future);
            } /*catch (CloneNotSupportedException exp) {
                CREATE_MAN_ITEM_LOGGER.error(exp);
            }*/ catch (Exception exp) {
                CREATE_MAN_ITEM_LOGGER.error(exp);
            }
        });

        // Await all runnables to be done (blocking)
        futures.forEach((Future<?> future) -> {
            try {
                HashMap<String, ResponseMessageFormaterBean> name = (HashMap<String, ResponseMessageFormaterBean>) future.get();

                if (name.containsKey("successful")) {
                    tnrSuccessfullList.add(name.get("successful"));
                } else {
                    tnrErrorList.add(name.get("unSuccessful"));
                }

                // get will block until the future is done
            } catch (InterruptedException | ExecutionException exp) {
                CREATE_MAN_ITEM_LOGGER.error(exp);
            }
        });

//        // Check if all runnables are done (non-blocking)
//        boolean allDone = true;
//        for (Future<?> future : futures) {
//            allDone &= future.isDone(); // check if future is done
//        }
        executorService.shutdown();

        tnrListMap.put("successFullList", tnrSuccessfullList);
        tnrListMap.put("errorList", tnrErrorList);

        return (K) tnrListMap;
    }
    
}
