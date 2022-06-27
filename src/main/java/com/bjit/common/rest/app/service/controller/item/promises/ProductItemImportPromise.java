/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.promises;

import com.bjit.common.rest.app.service.controller.item.facades.CommonItemImportFacade;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class ProductItemImportPromise implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger COMMON_MAN_ITEM_LOGGER = org.apache.log4j.Logger.getLogger(ProductItemImportPromise.class);
    
    @Override
    public <T, K> K doImport(final Context context, final T objectDataBeanRequest) {
        ObjectDataBean objectDataBean = (ObjectDataBean) objectDataBeanRequest;
        List<DataTree> dataTreeList = objectDataBean.geDataTree();
        List<ResponseMessageFormaterBean> tnrSuccessfullList = new ArrayList<>();
        List<ResponseMessageFormaterBean> tnrErrorList = new ArrayList<>();
        HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = new HashMap<>();

        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("item.import.concurrent.total.thread.count"));
        COMMON_MAN_ITEM_LOGGER.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        COMMON_MAN_ITEM_LOGGER.info("Number of items in the request is : " + dataTreeList.size());

        dataTreeList.forEach((DataTree dataTree) -> {
            CreateObjectBean createObjectBean = dataTree.getItem();
            createObjectBean.setSource(objectDataBean.getSource());

            ResponseMessageFormaterBean responseMessageFormatter = new ResponseMessageFormaterBean();
            try {
//                responseMessageFormatter.setTnr((TNR) createObjectBean.getTnr().clone());
                responseMessageFormatter.setTnr((TNR) createObjectBean.getTnr());
                String itemType = createObjectBean.getTnr().getType();
                
                Callable commonItemImportProcess = new CommonItemImportFacade(context, createObjectBean, responseMessageFormatter, objectDataBean.getSource());
                
                Future<?> future = executorService.submit(commonItemImportProcess);
                futures.add(future);
            } catch (Exception exp) {
                COMMON_MAN_ITEM_LOGGER.error(exp);
            }
        });

        // Await all runnables to be done (blocking)
        futures.forEach((Future<?> future) -> {
            try {
                HashMap<String, ResponseMessageFormaterBean> responseMessage = (HashMap<String, ResponseMessageFormaterBean>) future.get();

                if (responseMessage.containsKey("successful")) {
                    tnrSuccessfullList.add(responseMessage.get("successful"));
                } else {
                    tnrErrorList.add(responseMessage.get("unSuccessful"));
                }

                // get will block until the future is done
            } catch (InterruptedException | ExecutionException exp) {
                COMMON_MAN_ITEM_LOGGER.error(exp);
            }
        });

        executorService.shutdown();

        tnrListMap.put("successFullList", tnrSuccessfullList);
        tnrListMap.put("errorList", tnrErrorList);

        return (K) tnrListMap;
    }
    
}
