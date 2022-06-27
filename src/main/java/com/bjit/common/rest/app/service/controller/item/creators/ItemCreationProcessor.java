/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.creators;

import com.bjit.common.rest.app.service.constants.VaultAndPolicyConstants;
import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.ItemImportUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.Context;
import org.apache.log4j.Priority;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
public class ItemCreationProcessor extends ObjectCreationProcessor {

    private static final org.apache.log4j.Logger ITEM_CREATION_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(ItemCreationProcessor.class);
    private String existingObjectId;
    private String businessObjectinterfaceName;
    private List<String> businessObjectinterfaceList;
    private Boolean checkInterfaceExistence = Boolean.FALSE;

    @Override
    public String processCreateItemOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList, Boolean checkInterfacesExistence) throws FrameworkException, Exception {
        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String newlyCreatedItemsObjectId = processCreateItemOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return newlyCreatedItemsObjectId;
        } catch (FrameworkException exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    @Override
    public String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean) throws FrameworkException, Exception {
        String newlyCreateItemsObjectId = null;
        Boolean hasNextVersion = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();

        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

        try {
            CreateObjectBean createObjectBean = createBean;
            //CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            Boolean objectIsExists = false;
            String nextVersion = createObjectBean.getNextVersion();

            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                ITEM_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                hasNextVersion = !NullOrEmptyChecker.isNullOrEmpty(nextVersion);
                BusinessObject latestRevisedBO = ItemImportUtil.getExistingorRevisedItem(context, businessObjectUtil, createObjectBean.getTnr(), hasNextVersion);

                String objectId = "";

                if (!NullOrEmptyChecker.isNull(latestRevisedBO)) {
                    objectIsExists = true;
                    objectId = latestRevisedBO.getObjectId();
                }

                newlyCreateItemsObjectId = objectId;

                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {

                    HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
                    ITEM_CREATION_PROCESSOR_LOGGER.debug("ObjectCloningMap is : " + objectCloningMap);

//                    newlyCreateItemsObjectId = businessObjectOperations.createObject(context, createObjectBean.getTnr(), PropertyReader.getProperty(createObjectBean.getTnr().getType().toLowerCase() + ".vault"), PropertyReader.getProperty(createObjectBean.getTnr().getType().toLowerCase() + ".policy"));//.cloneObject(context, createObjectBean, objectCloningMap);
                    newlyCreateItemsObjectId = businessObjectOperations.createObject(context, createObjectBean.getTnr(), VaultAndPolicyConstants.getObjectVault(createObjectBean.getTnr().getType()), VaultAndPolicyConstants.getObjectPolicy(createObjectBean.getTnr().getType()));
                    ITEM_CREATION_PROCESSOR_LOGGER.debug("ClonedObjectId is : " + newlyCreateItemsObjectId);
                } else {
                    this.existingObjectId = objectId;
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                //final String updatabelNewClonedObject = clonedObjectId;
                businessObjectOperations.addInterface(context, newlyCreateItemsObjectId, this.businessObjectinterfaceList, "", this.checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                //BusinessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "");
                businessObjectOperations.addInterface(context, newlyCreateItemsObjectId, this.businessObjectinterfaceName, "", this.checkInterfaceExistence);
            }

            Instant itemUpdateStartTime = Instant.now();
            long createDuration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);
            ITEM_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + createDuration + "' milli-seconds for creating in the DB");

            updateItem(createObjectBean, context, businessObjectOperations, newlyCreateItemsObjectId, objectIsExists);

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);
            ITEM_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + updateDuration + "' milli-seconds for updating in the DB");

            return newlyCreateItemsObjectId;
        } catch (FrameworkException exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

}
