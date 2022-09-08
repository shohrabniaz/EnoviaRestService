/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.creators;

import com.bjit.common.rest.app.service.constants.VaultAndPolicyConstants;
import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemCreationProcessor;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class CommonItemCreator implements IItemCreationProcessor {

    private static final org.apache.log4j.Logger ITEM_CREATION_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(CommonItemCreator.class);
    protected String businessObjectinterfaceName;
    protected List<String> businessObjectinterfaceList;
    protected Boolean checkInterfaceExistence = Boolean.FALSE;
    protected CommonItemParameters commonItemParemeters;

    @Override
    public String processCreateItemOperation(CommonItemParameters commonItemParemeters) throws Exception {
        this.commonItemParemeters = commonItemParemeters;
        return processCreateItemOperation(commonItemParemeters.getContext(), commonItemParemeters.getBusinessObjectUtil(), commonItemParemeters.getBusinessObjectOperations(), commonItemParemeters.getCreateObjectBean(), commonItemParemeters.getRunTimeInterfaceList(), Boolean.TRUE);
    }

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

    protected String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createObjectBean) throws FrameworkException, Exception {
        try {

            createObjectBean.getTnr().setName(createObjectBean.getIsAutoName()
                    ? businessObjectOperations.getAutoName(context, createObjectBean.getTnr().getType())
                    : createObjectBean.getTnr().getName());

            Boolean objectIsExists = Optional.ofNullable(this.commonItemParemeters.getItemExists()).orElse(Boolean.FALSE);
            String objectId = Optional.ofNullable(this.commonItemParemeters.getObjectId()).orElse("");

            objectId = !objectIsExists
                    ? businessObjectOperations.createObject(context, createObjectBean.getTnr(), VaultAndPolicyConstants.getObjectVault(createObjectBean.getTnr().getType()), VaultAndPolicyConstants.getObjectPolicy(createObjectBean.getTnr().getType()))
                    : objectId;

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                businessObjectOperations.addInterface(context, objectId, this.businessObjectinterfaceList, "", this.checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                businessObjectOperations.addInterface(context, objectId, this.businessObjectinterfaceName, "", this.checkInterfaceExistence);
            }

            HashMap<String, String> updatedAttributeMap = objectIsExists ? validateUpdateAttributeMap(createObjectBean) : addDefaultAttributeValues(createObjectBean);
            businessObjectOperations.updateObject(context, objectId, updatedAttributeMap);

            return objectId;
        } catch (FrameworkException exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            ITEM_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    protected HashMap<String, String> validateUpdateAttributeMap(CreateObjectBean createObjectBean) {
        if (createObjectBean.getAttributes().containsKey("PLMEntity.V_nature")) {
            throw new RuntimeException("Please don't update 'PLMEntity.V_nature'");
        }

        return createObjectBean.getAttributes();
    }

    protected HashMap<String, String> addDefaultAttributeValues(CreateObjectBean createObjectBean) throws FrameworkException, InterruptedException {
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();
        HashMap<String, String> objectUpdateMap = objectDefaultValues.getObjectUpdateMap(createObjectBean);
        createObjectBean.setAttributes(objectUpdateMap);
        objectUpdateMap = validateUpdateAttributeMap(createObjectBean);
        return objectUpdateMap;
    }
}
