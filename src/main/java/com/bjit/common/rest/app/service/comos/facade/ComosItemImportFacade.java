/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.facade;

import com.bjit.common.rest.app.service.controller.item.facades.CommonItemImportFacade;
import com.bjit.common.rest.app.service.controller.item.factories.AttributeBusinessLogicFactory;
import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class ComosItemImportFacade extends CommonItemImportFacade {

    private static final org.apache.log4j.Logger COMMON_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(ComosItemImportFacade.class);

    public ComosItemImportFacade() {
        super();
    }

    public ComosItemImportFacade(Context context, CreateObjectBean createObjectBean, String source) {
        super(context, createObjectBean, source);
//        this.context = context;
//        this.createObjectBean = createObjectBean;
//        this.source = source;
    }

    public ComosItemImportFacade(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        super(context, createObjectBean, responseMessageFormatterBean, source);
//        this(context, createObjectBean, source);
//        this.responseMessageFormatterBean = responseMessageFormatterBean;
    }

    @Override
    public String process(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws Exception {
        /*---------------------------------------- ||| Process for create object ||| ----------------------------------------*/
        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        AttributeBusinessLogicFactory businessLogicFactory = new AttributeBusinessLogicFactory();
        AttributeBusinessLogic commonAttributeBusinessLogic = businessLogicFactory.getAttributeBusinessLogic(this.source.toLowerCase());
        CommonItemParameters commonItemParemeters = new CommonItemParameters(context, createObjectBean, this.source, businessObjectUtil, businessObjectOperations, ItemImportMapping.class, commonAttributeBusinessLogic);
        validatorFactoryForCreateBean(commonItemParemeters);
        IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor = XMLAttributeMapperFactoryForCreateBean(commonItemParemeters);
        setXMLAttributeMapperProperties(commonItemTypesAndRelationXMLProcessor, commonItemParemeters);
        String newlyCreatedOrExistingItemsObjectId = itemCreationProcessFactoryForCreateBean(commonItemParemeters);
        COMMON_IMPORT_PROCESS_LOGGER.info("Processed ObjectId : " + newlyCreatedOrExistingItemsObjectId);
        responseMessageFormatterBean.setObjectId(newlyCreatedOrExistingItemsObjectId);
        return newlyCreatedOrExistingItemsObjectId;
    }
}
