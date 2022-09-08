/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.utilities;

import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.item.facades.CommonItemImportFacade;
import com.bjit.common.rest.app.service.controller.item.promises.ModelVersionItemImportPromise;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ResponseMessageFormaterBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */
@Component
//@RequestScope
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HardwareProductItemCreateUtilities {
    private static final org.apache.log4j.Logger HARDWARE_PRODUCT_ITEM_CREATE_UTILITIES_LOGGER = org.apache.log4j.Logger.getLogger(HardwareProductItemCreateUtilities.class);

    public String createItem(ChildItem childItem, String source, Context context) throws Exception {
        try {
            CreateObjectBean childItemBean = new CreateObjectBean(childItem.getTnr(), childItem.getAttributes(), source);
            CommonItemImportFacade commonItemImportProcess = new CommonItemImportFacade(context, childItemBean, source);
            return commonItemImportProcess.processItem(context, childItemBean, Boolean.TRUE);
        } catch (Exception exp) {
            HARDWARE_PRODUCT_ITEM_CREATE_UTILITIES_LOGGER.error(exp);
            throw exp;
        }
    }

    public String createHPItem(CreateObjectBean createHardWareItemBean, Context context) throws Exception {
        try {

            DataTree DataTree = new DataTree(createHardWareItemBean);
            List<DataTree> dataTreeList = new ArrayList<>();
            dataTreeList.add(DataTree);

            ObjectDataBean objectDataBean = new ObjectDataBean(dataTreeList, "MassHwImport");

            ItemOrBOMImport hpItemImport = new ModelVersionItemImportPromise();
            HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = hpItemImport.doImport(context, objectDataBean);
            List<ResponseMessageFormaterBean> successFulItemList = tnrListMap.get("successFullList");
            List<ResponseMessageFormaterBean> errorItemList = tnrListMap.get("errorList");

            if (errorItemList.size() > 0) {
                throw new RuntimeException(errorItemList.get(0).getErrorMessage());
            } else {
                return successFulItemList.get(0).getObjectId();
            }

        } catch (RuntimeException exp) {
            HARDWARE_PRODUCT_ITEM_CREATE_UTILITIES_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            HARDWARE_PRODUCT_ITEM_CREATE_UTILITIES_LOGGER.error(exp);
            throw exp;
        }
    }
}
