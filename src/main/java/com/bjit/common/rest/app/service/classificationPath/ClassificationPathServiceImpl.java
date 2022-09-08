/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.classificationPath;

import com.bjit.common.rest.app.service.model.modelVersion.MVResponseMessageFormatterBean;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

/**
 * Classification Path Service to add classification path to item object
 *
 * @author Arifur Rahman
 */
public class ClassificationPathServiceImpl implements ClassificationPathService {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ClassificationPathServiceImpl.class);

    /**
     * Multiple Objects Multilevel Classification Path Update Service
     *
     * @param context User Security Context
     * @param successFulItemList Multiple Items
     * @return
     */
    @Override
    public List<MVResponseMessageFormatterBean> addClassificationPath(Context context, List<MVResponseMessageFormatterBean> successFulItemList) {
        /*
         * @Note: send request for add classification path for created/updated objects
         * @Note: if failed to add classification path set message to objects errorMessages property
         */
        ArrayList requestedObjectList = new ArrayList();
        for (MVResponseMessageFormatterBean responseMessageFormaterBean : successFulItemList) {
            String classificationPath = responseMessageFormaterBean.getClassificationPath();
            /*
             * @Note: if no classification path is send to update then stop sending
             * @Note: the object to classification path update service
             */
            if (classificationPath != null) {
                HashMap object = new HashMap();
                object.put("objectId", responseMessageFormaterBean.getObjectId());
                object.put("classificationPath", classificationPath);
                requestedObjectList.add(object);
            }
        }
        if (requestedObjectList.isEmpty()) {
            return successFulItemList;
        }
        HashMap params = new HashMap();
        params.put("data", requestedObjectList);
        String[] constructor = {null};
        HashMap result;
        try {
            result = (HashMap) JPO.invoke(context,
                    "emxClassificationPath",
                    constructor,
                    "updateMassClassificationPath",
                    JPO.packArgs(params),
                    HashMap.class
            );
            for (ResponseMessageFormaterBean responseMessageFormaterBean : successFulItemList) {
                String objectId = responseMessageFormaterBean.getObjectId();
                ArrayList<Map<String, Object>> classificationPathAddingResultList = (ArrayList) result.get("data");
                for (Map<String, Object> classificationPathAddingResult : classificationPathAddingResultList) {
                    if (classificationPathAddingResult.get("objectId").equals(objectId)) {
                        if (classificationPathAddingResult.get("status").equals("FAIL")) {
                            String classificationPathMessage = classificationPathAddingResult.get("message") != null ? classificationPathAddingResult.get("message").toString() : "";
                            responseMessageFormaterBean.setErrorMessage("ClassificationPath=Cannot Update Classification path;ClassificationPathError=" + classificationPathMessage);
                        }
                        break;
                    }
                }
            }
            LOGGER.debug("Classification Path add/update response: " + result);
        } catch (MatrixException e) {
            String errorMessage = e.getLocalizedMessage();
            errorMessage = errorMessage.replaceAll(":", "=");
            for (ResponseMessageFormaterBean responseMessageFormaterBean : successFulItemList) {
                responseMessageFormaterBean.setErrorMessage("ClassificationPath=Cannot Update Classification path;ClassificationPathError=" + errorMessage);
            }
            LOGGER.fatal("Error occured during adding classification path : " + errorMessage);
        }
        return successFulItemList;
    }

    /**
     * Add classification path for item object
     *
     * @param context
     * @param objectId
     * @param classificationPath
     * @return
     */
    @Override
    public String addClassificationPath(Context context, String objectId, String classificationPath) {
        String responseString = "";
        /*
         * @Note: send request for add classification path for created/updated objects
         *
         * @Note: if failed to add classification path set message to objects
         * errorMessages property
         */

        List<Object> requestedObjectList = new ArrayList();
        if (classificationPath != null) {
            HashMap<String, Object> object = new HashMap();
            object.put("objectId", objectId);
            object.put("classificationPath", classificationPath);
            requestedObjectList.add(object);
        }
        if (requestedObjectList.isEmpty()) {
            return responseString;
        }
        HashMap params = new HashMap();
        params.put("data", requestedObjectList);
        String[] constructor = {null};
        HashMap result;
        try {
            result = (HashMap) JPO.invoke(context, "emxClassificationPath", constructor, "updateMassClassificationPath",
                    JPO.packArgs(params), HashMap.class);
            ArrayList<Map<String, Object>> classificationPathAddingResultList = (ArrayList) result.get("data");

            for (Map<String, Object> classificationPathAddingResult : classificationPathAddingResultList) {
                if (classificationPathAddingResult.get("objectId").equals(objectId)) {
                    if (classificationPathAddingResult.get("status").equals("FAIL")) {
                        String classificationPathMessage = classificationPathAddingResult.get("message") != null
                                ? classificationPathAddingResult.get("message").toString()
                                : "";
                        responseString = classificationPathMessage;
                    }
                    break;
                }
            }
            LOGGER.debug("Classification Path add/update response: " + result);
            return responseString;
        } catch (MatrixException e) {
            String errorMessage = e.getLocalizedMessage();
            errorMessage = errorMessage.replaceAll(":", "=");
            responseString = "ClassificationPath=Cannot Update Classification path;ClassificationPathError=" + errorMessage;
            LOGGER.fatal("Error occured during adding classification path : " + errorMessage);
            return responseString;
        }
    }
}
