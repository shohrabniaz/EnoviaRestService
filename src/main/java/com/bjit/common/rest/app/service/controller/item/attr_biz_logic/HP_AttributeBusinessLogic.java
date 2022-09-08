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
public class HP_AttributeBusinessLogic extends AttributeBusinessLogic {

    private String businessInterfaceList = "";
    private static final Logger HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(HP_AttributeBusinessLogic.class);

    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case "Hardware Product":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = businessLogic(mapper, createObjectBean);

        switch (createObjectBean.getTnr().getType()) {
            case "Hardware Product":
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
    //public static synchronized HashMap<String, String> updatableBusinessLogic(Context context, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {
    @Override
    public HashMap<String, String> updatableBusinessLogic(BusinessObjectUtil businessObjectUtil, Context context, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();

        objectAttributeMap = updateItemForAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get("project"))) {
            String personOrganization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
            objectAttributeMap.put("organization", personOrganization);
        }
        return objectAttributeMap;
    }

    /**
     * During Item creation 'itemAttributeBusinessLogic' will be called
     * This method will update newly created 'Product' with basic attributes with default values
     *
     * @param createObjectBean
     * @param mapper
     * @param objectAttributeMap
     * @param newObjectAttributeMap
     * @return HashMap<String, String>
     * @throws IOException, ParserConfigurationException, SAXException, XPathExpressionException
     */
    @Override
    public HashMap<String, String> itemAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String>  requesedObjectAttributeMap = (HashMap<String, String>) objectAttributeMap.clone();
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("+++ itemAttributeBusinessLogic +++");
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();
                String dataType = elementAttribute.getDataType();
                Integer dataLength = null;
                if(!NullOrEmptyChecker.isNull(elementAttribute.getDataLength())) {
                    if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getValue())) {
                        dataLength = elementAttribute.getDataLength().getValue();
                    }
                }
                Boolean isRequired = elementAttribute.getIsRequired();

                HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                if (!isValid(createObjectBean.getAttributes().get("Marketing Name"))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("Unvalid.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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

//                                String transferredToErp = commonPropertyReader.getPropertyValue("item.attribute.transferred.to.erp.source.pdm");
//
//                                if (sourceName.equalsIgnoreCase(transferredToErp)) {
//                                    destinationValue = attributeValue.contains(sourceValue) ? destinationValue : "false";
//                                    rangeUpdated = true;
//                                    attributeValue = destinationValue;
//                                    break;
//                                }

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

                    if (dataType.equalsIgnoreCase("date")) {

                        if (NullOrEmptyChecker.isNull(elementAttribute.getDataFormat())) {
                            throw new NullPointerException("Data format is missing for the 'Date' type attribute '" + sourceName + "'");
                        }

                        String timeZone = elementAttribute.getDataFormat().getTimezone();

                        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    } else if (dataType.equalsIgnoreCase("float") && !NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                    	HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("\n\n#####   Data type: float ###\n\n");
						try {
							Float.parseFloat(attributeValue);
						} catch (Exception e) {
							String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                            throw new RuntimeException(errorMessage);
						}
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                        String paddingCharacters = "";
                        if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                } else {
                        String insertable = elementAttribute.getInsertable();
                        if (insertable != null && insertable.equals("true")) {
                            HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info(" Adding extra attribute: " + destinationName + ", defaultValue: " + defaultValue);
                            newObjectAttributeMap.put(destinationName, defaultValue);
                        }
                }
                // remove valid attribute , later
                requesedObjectAttributeMap.remove(sourceName);
            });
        });

        itemOwnerGroup(newObjectAttributeMap);

        // keep all requested attributes, replace valid attributes key with original one
        requesedObjectAttributeMap.putAll(newObjectAttributeMap);
        objectAttributeMap.clear();
        objectAttributeMap.putAll(requesedObjectAttributeMap);
        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("--- itemAttributeBusinessLogic ---");
        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> updateItemForAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    	HashMap<String, String>  requesedObjectAttributeMap = (HashMap<String, String>) objectAttributeMap.clone();
    	validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("+++ updateItemForAttributeBusinessLogic +++");
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();
                String dataType = elementAttribute.getDataType();
                Integer dataLength = null;
                if(!NullOrEmptyChecker.isNull(elementAttribute.getDataLength())) {
                    if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getValue())) {
                        dataLength = elementAttribute.getDataLength().getValue();
                    }
                }
                Boolean isRequired = elementAttribute.getIsRequired();

                HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }
                if (!isValid(createObjectBean.getAttributes().get("Marketing Name"))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("Unvalid.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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

                    if (dataType.equalsIgnoreCase("date")) {

                        if (NullOrEmptyChecker.isNull(elementAttribute.getDataFormat())) {
                            throw new NullPointerException("Data format is missing for the 'Date' type attribute '" + sourceName + "'");
                        }

                        String timeZone = elementAttribute.getDataFormat().getTimezone();

                        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    } else if (dataType.equalsIgnoreCase("float") && !NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                    	HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("\n\n#####   Data type: float ###\n\n");
						try {
							float attributeValueFloat = Float.parseFloat(attributeValue);
							HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("attributeValueFloat: " + attributeValueFloat);
						} catch (Exception e) {
							String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                            throw new RuntimeException(errorMessage);
						}
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNull(dataLength)) {
                        String paddingCharacters = "";
                        if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
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
        HP_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("--- updateItemForAttributeBusinessLogic ---");
        return objectAttributeMap;
    }
     public boolean isValid(String value) {
        boolean isValidValue = true;
        value = value.trim();
        if (value.isEmpty()||value.equalsIgnoreCase("null")) {
            isValidValue = false;
        }
        return isValidValue;
    }
}
