package com.bjit.common.rest.pdm_enovia.importer;

import com.bjit.common.rest.app.service.dsservice.consumers.ConsumerContainers;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.common.rest.pdm_enovia.utility.CustomException;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.HashMap;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class ItemImporter {

    JSON json;
    public static HashMap<String, String> BUSINESS_OBJECT_TYPE_MAP;
    private static final Logger ITEM_IMPORTER_LOGGER = Logger.getLogger(ItemImporter.class);

    public ItemImporter() {
        json = new JSON();
    }

    /**
     * The <code>importObject</code> method clones and updates the object if it
     * doesn't exist; otherwise only updates the existing object.
     *
     * @param context
     * @param itemObject
     * @param businessObjectOperations
     * @param resultUtil
     * @return the id of imported object
     * @throws CustomException
     * @throws Exception
     */
    public String doItemImport(Context context, CreateObjectBean itemObject, BusinessObjectOperations businessObjectOperations, ResultUtil resultUtil, ConsumerContainers consumerContainers) throws CustomException, Exception {
        ITEM_IMPORTER_LOGGER.debug("------------- ||| Item Import Process Started ||| -------------");
        ITEM_IMPORTER_LOGGER.info(">>>>> Item Type=" + itemObject.getTnr().getType() + " Name=" + itemObject.getTnr().getName());
        ITEM_IMPORTER_LOGGER.debug("START IMPORTING : Time: " + CommonUtil.getSystemDate());

        BUSINESS_OBJECT_TYPE_MAP = PropertyReader.getProperties(BUSINESS_OBJECT_TYPE_MAP, "import.type.map.Enovia", Boolean.TRUE);
        TNR itemTNR = itemObject.getTnr();
        try {
//            ITEM_IMPORTER_LOGGER.debug("Starting Transaction");
//            ContextUtil.startTransaction(context, true);

            CommonUtilities commonUtilities = new CommonUtilities();
            commonUtilities.doStartTransaction(context);
            try {
                String itemType = itemTNR.getType();

                itemObject.setSource(itemObject.getSource().toLowerCase());

                ITEM_IMPORTER_LOGGER.debug("Object Type : " + itemType);
                ITEM_IMPORTER_LOGGER.debug("Source Environment : " + itemObject.getSource());

                String envObjectType = itemObject.getSource() + "." + itemType;

                ITEM_IMPORTER_LOGGER.debug("Environment Object Type : " + envObjectType);

                itemObject.getTnr().setType(BUSINESS_OBJECT_TYPE_MAP.containsKey(envObjectType) ? BUSINESS_OBJECT_TYPE_MAP.get(envObjectType) : itemType);

                ITEM_IMPORTER_LOGGER.debug(">>>>> ITEM IMPORT REQUEST : " + json.serialize(itemObject) + "\n\n");
                ItemImportProcessor itemImportProcessor = new ItemImportProcessor();
                String importedObjectId = itemImportProcessor.processImportItemOperation(context, itemObject, Boolean.TRUE, businessObjectOperations, resultUtil, consumerContainers);

                ITEM_IMPORTER_LOGGER.info("Commiting Transaction");
                ContextUtil.commitTransaction(context);

                ITEM_IMPORTER_LOGGER.info(">>>>> Imported Ojbect Id : " + importedObjectId);
                ITEM_IMPORTER_LOGGER.debug("------------- ||| Item Import Process Completed ||| -------------\n\n\n");
                resultUtil.addSuccessResult(resultUtil.getItemTNR(itemTNR.getName()), importedObjectId);
                return importedObjectId;
            } catch (FrameworkException e) {
                ITEM_IMPORTER_LOGGER.error(e);
                String errorMessage = CommonUtil.formatErrorMessage(e.getMessage());
                resultUtil.addErrorResult(itemTNR.getName(), resultUtil.getItemTNR(itemTNR.getName()), errorMessage);
                ITEM_IMPORTER_LOGGER.error("Aborting Transaction");
                ContextUtil.abortTransaction(context);
            } catch (Exception exp) {
                ITEM_IMPORTER_LOGGER.error(exp);
                String errorMessage = CommonUtil.formatErrorMessage(exp.getMessage());
                resultUtil.addErrorResult(itemTNR.getName(), resultUtil.getItemTNR(itemTNR.getName()), errorMessage);
                ITEM_IMPORTER_LOGGER.error("Aborting Transaction");
                ContextUtil.abortTransaction(context);
            }
        } catch (FrameworkException e) {
            ITEM_IMPORTER_LOGGER.error(e);
            ITEM_IMPORTER_LOGGER.error("Aborting Transaction");
            ContextUtil.abortTransaction(context);
        }

        return null;
    }
}
