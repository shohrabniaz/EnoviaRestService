/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.creator;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.HashMap;
import java.util.Optional;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ComosItemCloner extends ComosItemCreator {

    private static final Logger COMOS_ITEM_CLONE_LOGGER = Logger.getLogger(ComosItemCloner.class);

    @Override
    protected String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createObjectBean) throws FrameworkException, Exception {
        String comosType = createObjectBean.getTnr().getType();
        try {

            String v6Type = getV6TypeFromComosType(comosType);
            createObjectBean.getTnr().setType(v6Type);

            createObjectBean.getTnr().setName(createObjectBean.getIsAutoName()
                    ? businessObjectOperations.getAutoName(context, createObjectBean.getTnr().getType())
                    : createObjectBean.getTnr().getName());

            Boolean objectIsExists = Optional.ofNullable(this.commonItemParemeters.getItemExists()).orElse(Boolean.FALSE);
            String objectId = Optional.ofNullable(this.commonItemParemeters.getObjectId()).orElse("");

            CloneItem cloneItem = new CloneItem();
            HashMap createObjectCloningMap = cloneItem.createObjectCloningMap(createObjectBean);
            String templateBusinessObjectId = PropertyReader.getProperty("template.object.type." + createObjectBean.getTnr().getType());
            createObjectBean.setTemplateBusinessObjectId(templateBusinessObjectId);

            objectId = !objectIsExists
                    ? cloneItem.cloneObject(context, createObjectBean, createObjectCloningMap, new BusinessObjectUtility())
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
            COMOS_ITEM_CLONE_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            COMOS_ITEM_CLONE_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            createObjectBean.getTnr().setType(comosType);
        }
    }
}
