package com.bjit.common.rest.app.service.controller.item.creators;

import java.util.List;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.Context;

public class MDItemCreator extends MVItemCreator {

    /**
     * Process Create Item operation
     *
     * @param commonItemParemeters
     * @return HashMap<String, String>
     * @throws com.matrixone.apps.domain.util.FrameworkException
     * @throws Exception
     */
    @Override
    public String processCreateItemOperation(CommonItemParameters commonItemParemeters) throws Exception {
        logger.info("\n+++  processMVCreateItemOperation +++");
        this.commonItemParemeters = commonItemParemeters;
        this.errorMessages = new StringBuilder();
        return this.processCreateItemOperation(commonItemParemeters.getContext(),
                commonItemParemeters.getBusinessObjectUtil(), commonItemParemeters.getBusinessObjectOperations(),
                commonItemParemeters.getCreateObjectBean(), commonItemParemeters.getResponseMessageFormaterBean(),
                commonItemParemeters.getRunTimeInterfaceList(), Boolean.TRUE);
    }

    /**
     * Process Create Item Operation
     *
     * @param context
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createBean
     * @param responseMessageFormatterBean
     * @param interfaceList
     * @param checkInterfacesExistence
     * @return HashMap<String, String>
     * @throws com.matrixone.apps.domain.util.FrameworkException
     * @throws Exception
     */
    @Override
    public String processCreateItemOperation(Context context, BusinessObjectUtil businessObjectUtil,
            BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean,
            ResponseMessageFormaterBean responseMessageFormatterBean, List<String> interfaceList,
            Boolean checkInterfacesExistence) throws FrameworkException, Exception {

        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String newlyCreatedItemsObjectId = super.processCreateItemOperation(context, businessObjectUtil,
                    businessObjectOperations, createBean, responseMessageFormatterBean);
            return newlyCreatedItemsObjectId;
        } catch (FrameworkException exp) {
            logger.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            logger.error(exp.getMessage());
            throw exp;
        }
    }

}
