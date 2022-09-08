/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.facades;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import matrix.db.Context;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemCreationProcessor;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;

import com.bjit.common.rest.app.service.controller.item.factories.AttributeBusinessLogicFactory;
import com.bjit.common.rest.app.service.controller.item.factories.ItemValidatorFactory;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemValidator;
import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.factories.ItemCreationProcessorFactory;
import com.bjit.common.rest.app.service.controller.item.factories.XMLAttributeMapperFactory;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.controller.item.utility.CommonItemImportUtility;

import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;

/**
 *
 * @author BJIT
 */
public class CommonItemImportFacade implements Callable {

    public static HashMap<String, String> BUSINESS_OBJECT_TYPE_MAP;
    private static final org.apache.log4j.Logger COMMON_IMPORT_PROCESS_LOGGER = org.apache.log4j.Logger.getLogger(CommonItemImportFacade.class);
    protected Context context;
    protected CreateObjectBean createObjectBean;
    protected ResponseMessageFormaterBean responseMessageFormatterBean;
    protected String source;
    protected HashMap<String, String> destinationSourceMap;

    public CommonItemImportFacade() {

    }

    public CommonItemImportFacade(Context context, CreateObjectBean createObjectBean, String source) {
        this.context = context;
        this.createObjectBean = createObjectBean;
        this.source = source;
    }

    public CommonItemImportFacade(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean, String source) {
        this(context, createObjectBean, source);
        this.responseMessageFormatterBean = responseMessageFormatterBean;
    }

    @Override
    public Object call() throws Exception {
        HashMap<String, ResponseMessageFormaterBean> threadResponse = new HashMap<>();
        try {

            processItem(this.context, this.createObjectBean, this.responseMessageFormatterBean);

            threadResponse.put("successful", this.responseMessageFormatterBean);
        } catch (FrameworkException | RuntimeException exp) {
            COMMON_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.responseMessageFormatterBean);
        } catch (Exception exp) {
            COMMON_IMPORT_PROCESS_LOGGER.error(exp);
            threadResponse.put("unSuccessful", this.responseMessageFormatterBean);
        }

        return threadResponse;
    }

    public String processItem(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, RuntimeException, Exception {

        CommonItemImportUtility commonItemImportUtility = new CommonItemImportUtility();

        try {
            CommonUtilities commonUtils = new CommonUtilities();
            commonUtils.doStartTransaction(context);

            /*---------------------------------------- ||| Process for create object ||| ----------------------------------------*/
            String newlyCreatedOrExistingItemsObjectId = process(context, createObjectBean, responseMessageFormatterBean);

            /*---------------------------------------- ||| Commit Transaction ||| ----------------------------------------*/
            COMMON_IMPORT_PROCESS_LOGGER.info("Committing for " + createObjectBean.getTnr().toString());
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

    public String processItem(Context context, CreateObjectBean createObjectBean, Boolean externalTransaction) throws FrameworkException, RuntimeException, Exception {

        ResponseMessageFormaterBean responseMessageFormatter = new ResponseMessageFormaterBean();
        responseMessageFormatter.setTnr(createObjectBean.getTnr());
        return externalTransaction ? process(context, createObjectBean, responseMessageFormatter) : processItem(context, createObjectBean, responseMessageFormatter);
    }

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

    /**
     * Removes exception class names like NullPointerException or
     * RuntimeException or ArrayOutOfBoundException etc
     *
     * @param exp
     * @param createObjectBean
     * @param commonItemImportUtility
     * @param responseMessageFormatterBean
     * @return
     */
    public String modifyExceptionResponse(Exception exp, CreateObjectBean createObjectBean, CommonItemImportUtility commonItemImportUtility, ResponseMessageFormaterBean responseMessageFormatterBean) {
        /*---------------------------------------- ||| Aborting Transaction ||| ----------------------------------------*/
        COMMON_IMPORT_PROCESS_LOGGER.debug(exp);
        COMMON_IMPORT_PROCESS_LOGGER.error("Aborting for " + createObjectBean.getTnr().toString());

        String replaceResponseMessage = commonItemImportUtility.replaceResponseMessage(exp.getMessage(), this.destinationSourceMap);
        String checkErrorCodeInErrorMessage = commonItemImportUtility.checkErrorCodeInErrorMessage(replaceResponseMessage);
        responseMessageFormatterBean.setErrorMessage(checkErrorCodeInErrorMessage);
        return checkErrorCodeInErrorMessage;
    }

    /**
     * Prepares CreateObjectBean validator from the the factory which validates
     * object type for different erp source Checks the xml mapper location
     *
     * @param commonItemParameters
     * @throws Exception
     */
    protected void validatorFactoryForCreateBean(CommonItemParameters commonItemParameters) throws Exception {
        ItemValidatorFactory ItemValidatorFactory = new ItemValidatorFactory();
        IItemValidator itemValidator = ItemValidatorFactory.getValidator(this.source.toLowerCase());
        itemValidator.validateItem(commonItemParameters);
    }

    /**
     * Prepares xmlMapperAttribute from the factory for retrieving maps from a
     * configurable xml file
     *
     * @param commonItemParameters
     * @return
     * @throws Exception
     */
    protected IXmlMapperProcessor XMLAttributeMapperFactoryForCreateBean(CommonItemParameters commonItemParameters) throws Exception {
        XMLAttributeMapperFactory xmlAttributeMapperFactory = new XMLAttributeMapperFactory();
        IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor = xmlAttributeMapperFactory.getTypeRelationMapperProcessor(this.source.toLowerCase());
        commonItemTypesAndRelationXMLProcessor.processAttributeXMLMapper(commonItemParameters);

        return commonItemTypesAndRelationXMLProcessor;
    }

    /**
     * Sets interface list and destination maps
     *
     * @param commonItemTypesAndRelationXMLProcessor
     * @param commonItemParameters
     * @throws Exception
     */
    protected void setXMLAttributeMapperProperties(IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor, CommonItemParameters commonItemParameters) throws Exception {
        this.destinationSourceMap = commonItemTypesAndRelationXMLProcessor.getDestinationSourceMap();

        List<String> runTimeInterfaceList = commonItemTypesAndRelationXMLProcessor.getRunTimeInterfaceList();
        commonItemParameters.setRunTimeInterfaceList(runTimeInterfaceList);
    }

    /**
     * Prepares the item creation processor from factory and creates or updates
     * the item
     *
     * @param commonItemParameters
     * @return
     * @throws Exception
     */
    protected String itemCreationProcessFactoryForCreateBean(CommonItemParameters commonItemParameters) throws Exception {
        ItemCreationProcessorFactory itemCreationProcessorFactory = new ItemCreationProcessorFactory();
        IItemCreationProcessor itemCreationProcessor = itemCreationProcessorFactory.getItemCreationProcessor(this.source.toLowerCase());
        return itemCreationProcessor.processCreateItemOperation(commonItemParameters);
    }
}
