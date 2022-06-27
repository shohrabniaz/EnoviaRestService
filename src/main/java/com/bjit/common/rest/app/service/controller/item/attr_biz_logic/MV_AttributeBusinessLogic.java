/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.attr_biz_logic;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValues;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ewc18x.validator.ProductAPIValidator;
import com.bjit.mapper.mapproject.util.Constants;
import com.matrixone.apps.domain.util.FrameworkException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public class MV_AttributeBusinessLogic extends AttributeBusinessLogic {

    private static final Logger logger = Logger.getLogger(MV_AttributeBusinessLogic.class);
    private ProductAPIValidator validator = new ProductAPIValidator();

    /**
     * Basic Business logic is implemented here
     *
     * @param mapper xml item object mapper
     * @param createObjectBean item data is kept here
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        logger.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case Constants.MODEL_VERSION:
                objectAttributeMap = this.itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    /**
     * During Item create some business logic are implemented here
     *
     * @param context
     * @param businessObjectUtil
     * @param mapper
     * @param createObjectBean
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     */
    @Override
    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = this.businessLogic(mapper, createObjectBean);

        switch (createObjectBean.getTnr().getType()) {
            case Constants.MODEL_VERSION:
                String organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", organization);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    /**
     * If the business object is updateable then it checks only the data comes
     * in JSON string
     *
     * @param businessObjectUtil
     * @param context
     * @param mapper
     * @param createObjectBean
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     */
    @Override
    public HashMap<String, String> updatableBusinessLogic(BusinessObjectUtil businessObjectUtil, Context context, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();

        objectAttributeMap = this.updateItemForAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get("project"))) {
            String personOrganization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
            objectAttributeMap.put("organization", personOrganization);
        }
        return objectAttributeMap;
    }

    /**
     * During Item creation 'itemAttributeBusinessLogic' will be called This
     * method will update newly created 'Product' with basic attributes with
     * default values
     *
     * @param createObjectBean Item Object Bean
     * @param mapper XML Attribute mapper
     * @param objectAttributeMap Object Attribute Map
     * @param newObjectAttributeMap Final Attribute Map
     * @return HashMap<String, String>
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws IOException, ParserConfigurationException, SAXException,
     * XPathExpressionException
     */
    @Override
    public HashMap<String, String> itemAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> requesedObjectAttributeMap = (HashMap<String, String>) objectAttributeMap.clone();
        super.validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        logger.info("+++ itemAttributeBusinessLogic +++");
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                super.validateXMLMapData(elementAttribute);

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();
                String dataType = elementAttribute.getDataType();
                Integer dataLength = null;
                if (!NullOrEmptyChecker.isNull(elementAttribute.getDataLength())) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getValue())) {
                        dataLength = elementAttribute.getDataLength().getValue();
                    }
                }
                Boolean isRequired = elementAttribute.getIsRequired();

                logger.debug("Source Name : " + sourceName);
                logger.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                if (!validator.isValidMarketingName(createObjectBean.getAttributes().get("Marketing Name"))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("Unvalid.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    logger.debug("Default Value : " + defaultValue);
                }

                //if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap)) {
                /**
                 * Checks the attributes are present in the JSON attribute map
                 */
                if (objectAttributeMap.containsKey(sourceName)) {
                    String attributeValue = objectAttributeMap.get(sourceName);

                    if (NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                        attributeValue = defaultValue;
                    } else if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                        ItemImportValues rangeValues = elementAttribute.getValues();
                        List<ItemImportValue> bomImportValueList = rangeValues.getValue();
                        if (!NullOrEmptyChecker.isNull(bomImportValueList)) {

                            /**
                             * Finds out the range values
                             */
                            boolean rangeUpdated = false;
                            for (int iterator = 0; iterator < bomImportValueList.size(); iterator++) {
                                ItemImportValue rangeValue = bomImportValueList.get(iterator);
                                String sourceValue = rangeValue.getSrc();
                                String destinationValue = rangeValue.getValue();

                                if (attributeValue.equalsIgnoreCase(sourceValue)) {

                                    if (destinationName.equalsIgnoreCase(commonPropertyReader.getPropertyValue("item.attribute.current.destination.pdm"))
                                            && !NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getNextVersion())
                                            && sourceValue.equalsIgnoreCase("forbidden")) {
                                        destinationValue = "RELEASED";
                                    }
                                    rangeUpdated = true;
                                    attributeValue = destinationValue;
                                    break;
                                }
                            }
                            if (!rangeUpdated) {
                                String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                                throw new RuntimeException(errorMessage);
                            }
                        }
                    }

                    attributeValue = validator.getValidatedAttributeValue(this, elementAttribute, attributeValue, dataType, sourceName);

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            super.manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            logger.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                        String paddingCharacters = "";
                        if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = super.abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                } else {
                    newObjectAttributeMap.put(destinationName, defaultValue);
                }
                // remove valid attribute , later
                requesedObjectAttributeMap.remove(sourceName);
            });
        });

        super.itemOwnerGroup(newObjectAttributeMap);

        // keep all requested attributes, replace valid attributes key with original one
        requesedObjectAttributeMap.putAll(newObjectAttributeMap);
        objectAttributeMap.clear();
        objectAttributeMap.putAll(requesedObjectAttributeMap);
        logger.info("--- itemAttributeBusinessLogic ---");
        return objectAttributeMap;
    }

    /**
     * This method is called during item object update action
     *
     * @param createObjectBean
     * @param mapper
     * @param objectAttributeMap
     * @param newObjectAttributeMap
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Override
    public HashMap<String, String> updateItemForAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> requesedObjectAttributeMap = (HashMap<String, String>) objectAttributeMap.clone();
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        logger.info("+++ updateItemForAttributeBusinessLogic +++");
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                super.validateXMLMapData(elementAttribute);

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();
                String dataType = elementAttribute.getDataType();
                Integer dataLength = null;
                if (!NullOrEmptyChecker.isNull(elementAttribute.getDataLength())) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getValue())) {
                        dataLength = elementAttribute.getDataLength().getValue();
                    }
                }
                Boolean isRequired = elementAttribute.getIsRequired();

                logger.debug("Source Name : " + sourceName);
                logger.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }
                if (!validator.isValidMarketingName(createObjectBean.getAttributes().get("Marketing Name"))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("Unvalid.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    logger.debug("Default Value : " + defaultValue);
                }

                //if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap)) {
                /**
                 * Checks the attributes are present in the JSON attribute map
                 */
                if (objectAttributeMap.containsKey(sourceName)) {
                    String attributeValue = objectAttributeMap.get(sourceName);
                    if (NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                        attributeValue = defaultValue;
                    } else if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                        ItemImportValues rangeValues = elementAttribute.getValues();
                        List<ItemImportValue> bomImportValueList = rangeValues.getValue();
                        if (!NullOrEmptyChecker.isNull(bomImportValueList)) {

                            /**
                             * Finds out the range values
                             */
                            boolean rangeUpdated = false;
                            for (int iterator = 0; iterator < bomImportValueList.size(); iterator++) {
                                ItemImportValue rangeValue = bomImportValueList.get(iterator);
                                String sourceValue = rangeValue.getSrc();
                                String destinationValue = rangeValue.getValue();

                                String transferredToErp = commonPropertyReader.getPropertyValue("item.attribute.transferred.to.erp.source.pdm");

                                if (sourceName.equalsIgnoreCase(transferredToErp)) {
                                    destinationValue = attributeValue.contains(sourceValue) ? destinationValue : "false";
                                    rangeUpdated = true;
                                    attributeValue = destinationValue;
                                    break;
                                }

                                if (attributeValue.equalsIgnoreCase(sourceValue)) {

                                    if (destinationName.equalsIgnoreCase(commonPropertyReader.getPropertyValue("item.attribute.current.destination.pdm"))
                                            && !NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getNextVersion())
                                            && sourceValue.equalsIgnoreCase("forbidden")) {
                                        destinationValue = "RELEASED";
                                    }
                                    rangeUpdated = true;
                                    attributeValue = destinationValue;
                                    break;
                                }
                            }
                            if (!rangeUpdated) {
                                String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                                throw new RuntimeException(errorMessage);
                            }
                        }
                    }
                    attributeValue = validator.getValidatedAttributeValue(this, elementAttribute, attributeValue, dataType, sourceName);

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            super.manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            logger.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNull(dataLength)) {
                        String paddingCharacters = "";
                        if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = super.abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                    // remove valid attribute , later
                    requesedObjectAttributeMap.remove(sourceName);
                }
            });
        });

        // keep all requested attributes, replace valid attributes key with original one
        requesedObjectAttributeMap.putAll(newObjectAttributeMap);
        objectAttributeMap.clear();
        objectAttributeMap.putAll(requesedObjectAttributeMap);
        logger.info("--- updateItemForAttributeBusinessLogic ---");
        return objectAttributeMap;
    }

}
