/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author Sajjad
 */
public class ItemImportUtil {

    private static final org.apache.log4j.Logger ITEM_IMPORT_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(ItemImportUtil.class);

    public static synchronized BusinessObject getExistingBOorRevisedBO(Context context, BusinessObjectUtil businessObjectUtil, TNR tnr) throws MatrixException, IOException, InterruptedException {
        String type = tnr.getType();
        String name = tnr.getName();
        String rev = tnr.getRevision();
        HashMap<String, String> attributeMap = new HashMap<>();
        
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        String propertyValue = commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision");
        attributeMap.put(propertyValue, rev);
        ArrayList<BusinessObject> existingBOList = businessObjectUtil.findBO(context, type, name, "", attributeMap);
        if (existingBOList.isEmpty()) {
            ITEM_IMPORT_UTIL_LOGGER.debug("Existing Business Object Number : " + existingBOList.size());
            BusinessObject latestBO = businessObjectUtil.getLatestRevBO(context, type, name);

            if (latestBO == null) {
                return null;
            } else {
                ITEM_IMPORT_UTIL_LOGGER.debug("Latest business object id : " + latestBO.getObjectId());
                BusinessObject revisedBO = businessObjectUtil.reviseBO(context, latestBO);
                return revisedBO;
            }
        } else {
            return existingBOList.get(0);
        }
    }

    public static synchronized BusinessObject getExistingBOorRevisedBO(Context context, BusinessObjectUtil businessObjectUtil, TNR tnr, Boolean hasNextVersion) throws MatrixException, IOException, InterruptedException {
        String type = tnr.getType();
        String name = tnr.getName();
        String rev = tnr.getRevision();
        HashMap<String, String> attributeMap = new HashMap<>();

        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        String propertyValue = commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision");
        attributeMap.put(propertyValue, rev);
        ArrayList<BusinessObject> existingBOList = businessObjectUtil.findBO(context, type, name, "", attributeMap);
        if (existingBOList.isEmpty()) {
            ITEM_IMPORT_UTIL_LOGGER.debug("Existing Business Object Number : " + existingBOList.size());
            BusinessObject latestBO = businessObjectUtil.getLatestRevBO(context, type, name);

            if (latestBO == null) {
                return null;
            } else {
                ITEM_IMPORT_UTIL_LOGGER.debug("Latest business object id : " + latestBO.getObjectId());
                BusinessObject revisedBO = businessObjectUtil.reviseBO(context, latestBO, hasNextVersion);
                return revisedBO;
            }
        } else {
            return existingBOList.get(0);
        }
    }

    public static synchronized BusinessObject getExistingorRevisedItem(Context context, BusinessObjectUtil businessObjectUtil, TNR tnr, Boolean hasNextVersion) throws MatrixException, IOException, InterruptedException {
        String type = tnr.getType();
        String name = tnr.getName();
        String rev = tnr.getRevision();
        HashMap<String, String> attributeMap = new HashMap<>();
        
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//        String propertyValue = commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision");
//        attributeMap.put(propertyValue, rev);
        ArrayList<BusinessObject> existingBOList = businessObjectUtil.findBO(context, tnr);
        if (existingBOList.isEmpty()) {
            ITEM_IMPORT_UTIL_LOGGER.debug("Existing Business Object Number : " + existingBOList.size());
            BusinessObject latestBO = businessObjectUtil.getLatestRevBO(context, type, name);

            if (latestBO == null) {
                return null;
            } else {
                ITEM_IMPORT_UTIL_LOGGER.debug("Latest business object id : " + latestBO.getObjectId());
                BusinessObject revisedBO = businessObjectUtil.reviseBO(context, latestBO, hasNextVersion);
                return revisedBO;
            }
        } else {
            return existingBOList.get(0);
        }
    }
}
