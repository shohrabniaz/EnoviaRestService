/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.validators;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class CommonItemParameters {

    private Context context;
    private CreateObjectBean createObjectBean;
    private ResponseMessageFormaterBean responseMessageFormaterBean;
    private HashMap<String,String> destinationSourceMap;
    private String source;
    private BusinessObjectUtil businessObjectUtil;
    private BusinessObjectOperations businessObjectOperations;
    private String xmlMapDirectory;
    private TNR tnr;
    private Class<ItemImportMapping> classType;
    private AttributeBusinessLogic attributeBusinessLogic;
    private List<String> runTimeInterfaceList;
    private Boolean itemExists;
    private String objectId;
    private List<String> insertableProperties;

    public CommonItemParameters() {

    }

    public CommonItemParameters(Context context, CreateObjectBean createObjectBean, String source, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations) {
        this.context = context;
        this.createObjectBean = createObjectBean;
        this.source = source;
        this.businessObjectUtil = businessObjectUtil;
        this.businessObjectOperations = businessObjectOperations;
    }

    public CommonItemParameters(Context context, CreateObjectBean createObjectBean, String source, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, Class<ItemImportMapping> classType, AttributeBusinessLogic attributeBusinessLogic) {
        this(context, createObjectBean, source, businessObjectUtil, businessObjectOperations);

        this.classType = classType;
        this.attributeBusinessLogic = attributeBusinessLogic;
    }

    public CommonItemParameters(Context context, CreateObjectBean createObjectBean, ResponseMessageFormaterBean responseMessageFormaterBean, HashMap<String,String> destinationSourceMap, String source, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, Class<ItemImportMapping> classType, AttributeBusinessLogic attributeBusinessLogic) {		
    	this(context, createObjectBean, source, businessObjectUtil, businessObjectOperations, classType, attributeBusinessLogic);

    	this.responseMessageFormaterBean = responseMessageFormaterBean;
    	this.destinationSourceMap = destinationSourceMap;
    }


    public CommonItemParameters(Context context, CreateObjectBean createObjectBean, String source, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, Class<ItemImportMapping> classType, AttributeBusinessLogic attributeBusinessLogic, List<String> runTimeInterfaceList) {
        this(context, createObjectBean, source, businessObjectUtil, businessObjectOperations, classType, attributeBusinessLogic);

        this.runTimeInterfaceList = runTimeInterfaceList;
    }

    public CommonItemParameters(Context context, CreateObjectBean createObjectBean, String source, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, Class<ItemImportMapping> classType, AttributeBusinessLogic attributeBusinessLogic, List<String> runTimeInterfaceList, Boolean itemExists, String objectId) {
        this(context, createObjectBean, source, businessObjectUtil, businessObjectOperations, classType, attributeBusinessLogic, runTimeInterfaceList);

        this.itemExists = itemExists;
        this.objectId = objectId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CreateObjectBean getCreateObjectBean() {
        return createObjectBean;
    }

    public void setCreateObjectBean(CreateObjectBean createObjectBean) {
        this.createObjectBean = createObjectBean;
    }

    public void setDestinationSourceMap(HashMap<String,String> destinationSourceMap) {
        this.destinationSourceMap = destinationSourceMap;
    }

    public HashMap<String,String> getDestinationSourceMap() {
        return this.destinationSourceMap;
    }


    public ResponseMessageFormaterBean getResponseMessageFormaterBean() {
        return this.responseMessageFormaterBean;
    }

    public void setResponseMessageFormaterBean(ResponseMessageFormaterBean responseMessageFormaterBean) {
        this.responseMessageFormaterBean = responseMessageFormaterBean;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BusinessObjectUtil getBusinessObjectUtil() {
        return businessObjectUtil;
    }

    public void setBusinessObjectUtil(BusinessObjectUtil businessObjectUtil) {
        this.businessObjectUtil = businessObjectUtil;
    }

    public BusinessObjectOperations getBusinessObjectOperations() {
        return businessObjectOperations;
    }

    public void setBusinessObjectOperations(BusinessObjectOperations businessObjectOperations) {
        this.businessObjectOperations = businessObjectOperations;
    }

    public String getXmlMapDirectory() {
        return xmlMapDirectory;
    }

    public void setXmlMapDirectory(String xmlMapDirectory) {
        this.xmlMapDirectory = xmlMapDirectory;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public Class<ItemImportMapping> getClassType() {
        return classType;
    }

    public void setClassType(Class<ItemImportMapping> classType) {
        this.classType = classType;
    }

    public AttributeBusinessLogic getAttributeBusinessLogic() {
        return attributeBusinessLogic;
    }

    public void setAttributeBusinessLogic(AttributeBusinessLogic attributeBusinessLogic) {
        this.attributeBusinessLogic = attributeBusinessLogic;
    }

    public List<String> getRunTimeInterfaceList() {
        return runTimeInterfaceList;
    }

    public void setRunTimeInterfaceList(List<String> runTimeInterfaceList) {
        this.runTimeInterfaceList = runTimeInterfaceList;
    }

    public Boolean getItemExists() {
        return itemExists;
    }

    public void setItemExists(Boolean itemExists) {
        this.itemExists = itemExists;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

	public List<String> getInsertableProperties() {
		return this.insertableProperties;
	}

	public void setInsertableProperties(List<String> insertableProperties) {
		this.insertableProperties = insertableProperties;
	}
}
