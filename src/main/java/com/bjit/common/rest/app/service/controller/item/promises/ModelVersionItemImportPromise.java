/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.promises;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bjit.common.rest.app.service.controller.item.facades.ModelVersionItemImportFacade;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateObjectBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVObjectDataBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVResponseMessageFormatterBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.ewc18x.utils.PropertyReader;

import matrix.db.Context;

/**
 *
 * @author Arifur
 */
public class ModelVersionItemImportPromise implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ModelVersionItemImportPromise.class);

    /**
     * Imports Item , in this context item should be 'Product' ; After product
     * and model is created company is assigned to created product and
     * classification path is also assigned
     *
     * @param <T> This key of map is Generic type allows, In this context successfullItemList or errorItemList 
     * @param <K> This value of map is Generic type, In this context 'ResponseFormatterBean' is returned.
     * @param context User Security Context
     * @param objectDataBeanRequest API Object data bean, which holds multiple object data
     * @return
     */
    public <T, K> K doImport(final Context context, final T objectDataBeanRequest) {

        MVObjectDataBean objectDataBean = (MVObjectDataBean) objectDataBeanRequest;
        String source = objectDataBean.getSource();
        List<MVDataTree> dataTreeList = objectDataBean.geDataTree();
        List<MVResponseMessageFormatterBean> tnrSuccessfullList = new ArrayList<>();
        List<MVResponseMessageFormatterBean> tnrErrorList = new ArrayList<>();
        HashMap<String, List<MVResponseMessageFormatterBean>> tnrListMap = new HashMap<>();

        int numberOfThreads = Integer.parseInt(PropertyReader.getProperty("item.import.concurrent.total.thread.count"));
        logger.debug("Number of workers (Thread) is : " + numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        logger.info("Number of items in the request is : " + dataTreeList.size());

        dataTreeList.forEach((MVDataTree dataTree) -> {
            MVCreateObjectBean hpCreateObjectBean = dataTree.getItem();
            hpCreateObjectBean.setSource(source);

            MVResponseMessageFormatterBean responseMessageFormatterBean = new MVResponseMessageFormatterBean();
            try {
                responseMessageFormatterBean.setClassificationPath(hpCreateObjectBean.getClassificationPath());
                responseMessageFormatterBean.setTnr((TNR) hpCreateObjectBean.getTnr());
                responseMessageFormatterBean.setRowSequenceIdentifier(Optional.ofNullable(dataTree.getRowSequenceIdentifier()).orElse(null));

                String uiType = hpCreateObjectBean.getTnr().getType();
                String propertyKey = "import.type.map.Enovia." + source + "." + uiType;
                String actualType = PropertyReader.getProperty(propertyKey);
                actualType = (actualType == null || actualType.isEmpty()) ? uiType : actualType;
                logger.info("\nProperty Key: " + propertyKey + "\nRequested Type: " + uiType + " \n Changing Type: " + actualType);
                hpCreateObjectBean.getTnr().setType(actualType);
                Callable importProcess = new ModelVersionItemImportFacade(context, hpCreateObjectBean, responseMessageFormatterBean, objectDataBean.getSource());

                Future<?> future = executorService.submit(importProcess);
                futures.add(future);
            } catch (Exception exp) {
                logger.error(exp);
            }
        });

        // Await all runnables to be done (blocking)
        futures.forEach((Future<?> future) -> {
            try {
                HashMap<String, MVResponseMessageFormatterBean> responseMap = (HashMap<String, MVResponseMessageFormatterBean>) future.get();

                if (responseMap.containsKey("successful")) {
                    MVResponseMessageFormatterBean responseBean = responseMap.get("successful");
                    String type = responseBean.getTnr().getType();
                    String propertyKey = "import.type.map.Enovia." + source + "." + type;
                    String uiType = PropertyReader.getProperty(propertyKey);
                    uiType = (uiType == null || uiType.isEmpty()) ? type : uiType;
                    logger.info("\nProperty Key: " + propertyKey + "\nRequested Type: " + uiType + " \n Changing Type: " + type);
                    responseBean.getTnr().setType(uiType);
                    tnrSuccessfullList.add(responseBean);
                } else {
                    MVResponseMessageFormatterBean responseBean = responseMap.get("unSuccessful");
                    String type = responseBean.getTnr().getType();
                    String propertyKey = "import.type.map.Enovia." + source + "." + type;
                    String uiType = PropertyReader.getProperty(propertyKey);
                    uiType = (uiType == null || uiType.isEmpty()) ? type : uiType;
                    logger.info("\nRequested Type: " + type + " \n Changing Type: " + uiType);
                    responseBean.getTnr().setType(uiType);
                    tnrErrorList.add(responseBean);
                }

                // get will block until the future is done
            } catch (InterruptedException | ExecutionException exp) {
                logger.error(exp);
            }
        });

        executorService.shutdown();

        tnrListMap.put("successFullList", tnrSuccessfullList);
        tnrListMap.put("errorList", tnrErrorList);

        return (K) tnrListMap;
    }

}
