/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.promise;

import com.bjit.common.rest.app.service.comos.facade.ComosItemImportFacade;
import com.bjit.common.rest.app.service.controller.item.facades.CommonItemImportFacade;
import com.bjit.common.rest.app.service.controller.item.promises.CommonItemImportPromise;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.concurrent.Callable;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ComosItemImportPromise extends CommonItemImportPromise {

    private static final Logger COMOS_ITEM_IMPORT_PROMISE_LOGGER = Logger.getLogger(ComosItemImportPromise.class);

    @Override
    protected Callable getItemImportFacade(final Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, ObjectDataBean objectDataBean) {
        Callable commonItemImportProcess = new ComosItemImportFacade(context, createObjectBean, responseMessageFormatterBean, objectDataBean.getSource());
        return commonItemImportProcess;

    }
}
