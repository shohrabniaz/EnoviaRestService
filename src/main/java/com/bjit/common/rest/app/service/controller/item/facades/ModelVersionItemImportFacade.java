/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.facades;

import matrix.db.Context;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;

import java.util.HashMap;

import com.bjit.common.rest.app.service.controller.item.factories.AttributeBusinessLogicFactory;
import com.bjit.common.rest.app.service.controller.item.factories.ItemCreationProcessorFactory;
import com.bjit.common.rest.app.service.controller.item.factories.XMLAttributeMapperFactory;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemCreationProcessor;
import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.utility.CommonItemImportUtility;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;

/**
 * This Facade to model version item import
 *
 * @author Arifur
 */
public class ModelVersionItemImportFacade extends CommonItemImportFacade {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ModelVersionItemImportFacade.class);

    public ModelVersionItemImportFacade() {
        super();
    }

    public ModelVersionItemImportFacade(Context context, CreateObjectBean createObjectBean, String source) {
        super(context, createObjectBean, source);
    }

    public ModelVersionItemImportFacade(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        super(context, createObjectBean, responseMessageFormatterBean, source);
    }

    /**
     * This is the item import process method to import different 'Model
     * Version' First Product is created , after this model is created and
     * finally Company is assigned to created product. During product creation
     * 'Classification path' is connected/disconnected with created/updated
     * product After above instructions finalizing whole transaction is
     * committed
     *
     * @param context User Security Context
     * @param createObjectBean Product Object bean which holds item objects data
     * @param responseMessageFormatterBean After successfully product
     * information import this response message bean is populated
     * @return
     * @throws FrameworkException
     * @throws RuntimeException
     * @throws Exception
     */
    @Override
    public String processItem(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, RuntimeException, Exception {

        CommonItemImportUtility commonItemImportUtility = new CommonItemImportUtility();
        try {
            CommonUtilities commonUtils = new CommonUtilities();
            // commonUtils.escapeOperationOn(context); // ignored will be implemented later
            commonUtils.doStartTransaction(context);

            /*---------------------------------------- ||| Process for create object ||| ----------------------------------------*/
            CreateObjectBean modelBean = getPreparedtModel((CreateObjectBean) createObjectBean.clone());

            String newlyCreatedOrExistingItemsObjectId = process(context, createObjectBean, responseMessageFormatterBean);

            modelBean.getTnr().setName(createObjectBean.getTnr().getName());
            process(context, modelBean, new ResponseMessageFormaterBean());
            BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
            businessObjectUtil.updateObjectCompany(context, createObjectBean.getAttributes().get("owner"), createObjectBean.getTnr().getType(), createObjectBean.getTnr().getName(), createObjectBean.getTnr().getRevision());
            /*---------------------------------------- ||| Commit Transaction ||| ----------------------------------------*/
            logger.info("Committing for " + createObjectBean.getTnr().toString());
            // commonUtils.escapeOperationOff(context); // ignored will be implemented later
            ContextUtil.commitTransaction(context);
            return newlyCreatedOrExistingItemsObjectId;
        } catch (FrameworkException | RuntimeException exp) {
            /*---------------------------------------- ||| Aborting Transaction ||| ----------------------------------------*/
            String checkErrorCodeInErrorMessage = modifyExceptionResponse(exp, createObjectBean, commonItemImportUtility, responseMessageFormatterBean);
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(checkErrorCodeInErrorMessage);
        } catch (Exception exp) {
            String checkErrorCodeInErrorMessage = modifyExceptionResponse(exp, createObjectBean, commonItemImportUtility, responseMessageFormatterBean);
            ContextUtil.abortTransaction(context);
            throw new Exception(checkErrorCodeInErrorMessage);
        }
    }

    /**
     * Process method for product and Model creation
     *
     * @param context User Security Context
     * @param createObjectBean This bean holds item object information
     * @param responseMessageFormatterBean This bean holds response bean
     * @return
     * @throws Exception
     */
    @Override
    public String process(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws Exception {
        /*---------------------------------------- ||| Process for create object ||| ----------------------------------------*/
        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        AttributeBusinessLogicFactory businessLogicFactory = new AttributeBusinessLogicFactory();
        AttributeBusinessLogic commonAttributeBusinessLogic = businessLogicFactory.getAttributeBusinessLogic(source.toLowerCase());
        HashMap<String, String> destinationSourceMap = null;
        CommonItemParameters commonItemParemeters = new CommonItemParameters(context, createObjectBean, responseMessageFormatterBean, destinationSourceMap, this.source, businessObjectUtil, businessObjectOperations, ItemImportMapping.class, commonAttributeBusinessLogic);
        validatorFactoryForCreateBean(commonItemParemeters);
        IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor = XMLAttributeMapperFactoryForMVCreateBean(commonItemParemeters);
        destinationSourceMap = commonItemTypesAndRelationXMLProcessor.getDestinationSourceMap();
        commonItemParemeters.setDestinationSourceMap(destinationSourceMap);

        setXMLAttributeMapperProperties(commonItemTypesAndRelationXMLProcessor, commonItemParemeters);

        String newlyCreatedOrExistingItemsObjectId = itemCreationProcessFactoryForMVCreateBean(commonItemParemeters);
        logger.info("Processed ObjectId : " + newlyCreatedOrExistingItemsObjectId);
        responseMessageFormatterBean.setObjectId(newlyCreatedOrExistingItemsObjectId);
        return newlyCreatedOrExistingItemsObjectId;
    }

    /**
     * Prepares xmlMapperAttribute from the factory for retrieving maps from a
     * configurable xml file
     *
     * @param commonItemParameters Required parameters are attached
     * @return
     * @throws Exception
     */
    private IXmlMapperProcessor XMLAttributeMapperFactoryForMVCreateBean(CommonItemParameters commonItemParameters) throws Exception {
        XMLAttributeMapperFactory xmlAttributeMapperFactory = new XMLAttributeMapperFactory();
        IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor = xmlAttributeMapperFactory.getTypeRelationMapperProcessor(this.source.toLowerCase());
        commonItemTypesAndRelationXMLProcessor.processAttributeXMLMapper(commonItemParameters);

        return commonItemTypesAndRelationXMLProcessor;
    }

    /**
     * Prepares the item creation processor from factory and creates or updates
     * the item
     *
     * @param commonItemParameters Required parameters are attached
     * @return
     * @throws Exception
     */
    private String itemCreationProcessFactoryForMVCreateBean(CommonItemParameters commonItemParameters) throws Exception {
        ItemCreationProcessorFactory itemCreationProcessorFactory = new ItemCreationProcessorFactory();
        IItemCreationProcessor itemCreationProcessor = itemCreationProcessorFactory.getItemCreationProcessor(this.source.toLowerCase());
        return itemCreationProcessor.processCreateItemOperation(commonItemParameters);
    }

    /**
     * Prepare Model Object Bean
     *
     * @param productModelBean Product model bean to create model
     * @return
     * @throws CloneNotSupportedException
     */
    private CreateObjectBean getPreparedtModel(CreateObjectBean productModelBean) throws CloneNotSupportedException {

        productModelBean.getTnr().setType("Model");
        productModelBean.getTnr().setRevision("");
        productModelBean.setIsAutoName(Boolean.FALSE);
        return productModelBean;
    }
}
