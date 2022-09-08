/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.creator;

import com.bjit.common.rest.app.service.controller.item.creators.CommonItemCreator;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ComosItemCreator extends CommonItemCreator {

    private static final Logger COMOS_ITEM_CREATOR_LOGGER = Logger.getLogger(ComosItemCreator.class);

    @Override
    protected String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createObjectBean) throws FrameworkException, Exception {
        String comosType = createObjectBean.getTnr().getType();
        try {
            String v6Type = getV6TypeFromComosType(comosType);
            createObjectBean.getTnr().setType(v6Type);
            businessObjectOperations.checkRevision = Boolean.FALSE;
            String objectId = super.processCreateItemOperation(context, businessObjectUtil, businessObjectOperations, createObjectBean);
            return objectId;
        } catch (Exception exp) {
            COMOS_ITEM_CREATOR_LOGGER.error(exp);
            throw exp;
        } finally {
            createObjectBean.getTnr().setType(comosType);
        }
    }

    protected String getV6TypeFromComosType(String comosType) {
        String v6Type = PropertyReader.getProperty("import.type.map.Enovia.comos." + comosType);
        return v6Type;
    }
}
