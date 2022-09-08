/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportDataLength;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValues;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public class AttributeBusinessLogic {

    public String businessInterfaceList = "";
    private static final Logger ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(AttributeBusinessLogic.class);

    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                objectAttributeMap = createAssemblyAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;

            case "ProcessContinuousCreateMaterial":
                objectAttributeMap = createAssemblyAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);

                if (!NullOrEmptyChecker.isNullOrEmpty(businessInterfaceList)) {
                    objectAttributeMap.put("businessInterfaceList", businessInterfaceList);
                }
                break;

            case "VAL_VALComponent":
                objectAttributeMap = createAssemblyAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;

            case "VAL_VALComponentMaterial":
                objectAttributeMap = createAssemblyAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Document":
                objectAttributeMap = createAssemblyAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Product Configuration":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Configuration Feature":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Configuration Option":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = businessLogic(mapper, createObjectBean);

        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                String personOrganization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", personOrganization);
                break;

            case "ProcessContinuousCreateMaterial":
                String personOrg = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", personOrg);
                break;

            case "VAL_VALComponent":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;

            case "VAL_VALComponentMaterial":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;
            case "Document":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;
            case "Product Configuration":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;
            case "Hardware Product":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;
            case "Configuration Feature":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                break;
            case "Configuration Option":
                //BusinessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
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
    public HashMap<String, String> updatableBusinessLogic(BusinessObjectUtil businessObjectUtil, Context context, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();

        objectAttributeMap = updateItemForAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get("project"))) {
            String personOrganization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
            objectAttributeMap.put("organization", personOrganization);
        }

        if (!NullOrEmptyChecker.isNullOrEmpty(businessInterfaceList)) {
            objectAttributeMap.put("businessInterfaceList", businessInterfaceList);
        }
        return objectAttributeMap;
    }

    public void validatObjectAttributeMap(HashMap<String, String> objectAttributeMap) {
        if (NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap)) {
            objectAttributeMap = new HashMap<>();
        }
    }

    public HashMap<String, String> createAssemblyAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

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

                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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
                        List<ItemImportValue> itemImportValueList = rangeValues.getValue();
                        if (!NullOrEmptyChecker.isNull(itemImportValueList)) {

                            /**
                             * Finds out the range values
                             */
                            boolean rangeUpdated = false;
                            for (int iterator = 0; iterator < itemImportValueList.size(); iterator++) {
                                ItemImportValue rangeValue = itemImportValueList.get(iterator);
                                String sourceValue = rangeValue.getSrc();
                                String destinationValue = rangeValue.getValue();
                                String runTimeInterfaceList = rangeValue.getRunTimeInterfaceList();

                                String transferredToErp = commonPropertyReader.getPropertyValue("item.attribute.transferred.to.erp.source.pdm");

                                if (sourceName.equalsIgnoreCase(transferredToErp)) {
                                    destinationValue = attributeValue.contains(sourceValue) ? destinationValue : "false";
                                    rangeUpdated = true;
                                    attributeValue = destinationValue;
                                    break;
                                }

                                if (!NullOrEmptyChecker.isNullOrEmpty(rangeValue.getHasAny())) {
                                    Pattern pattern = Pattern.compile(rangeValue.getHasAny());
                                    Matcher matcher = pattern.matcher(attributeValue);

                                    attributeValue = matcher.find() ? destinationValue : defaultValue;
                                    rangeUpdated = true;
                                    Optional.ofNullable(runTimeInterfaceList).ifPresent((runtimeInterfaceList) -> {
                                        businessInterfaceList += ", " + runtimeInterfaceList;
                                    });

                                    break;
                                } else {
                                    if (attributeValue.equalsIgnoreCase(sourceValue)) {

                                        if (destinationName.equalsIgnoreCase(commonPropertyReader.getPropertyValue("item.attribute.current.destination.pdm"))
                                                && !NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getNextVersion())
                                                && sourceValue.equalsIgnoreCase("forbidden")) {
                                            destinationValue = "RELEASED";
                                        }

                                        attributeValue = destinationValue;
                                        rangeUpdated = true;

                                        Optional.ofNullable(runTimeInterfaceList).ifPresent((runtimeInterfaceList) -> {
                                            businessInterfaceList += ", " + runtimeInterfaceList;
                                        });
                                        break;
                                    }
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

                        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                        String paddingCharacters = "";
                        if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                } else {
                    newObjectAttributeMap.put(destinationName, defaultValue);
                }
            });
        });

        processOwnerGroup(newObjectAttributeMap);

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

    public HashMap<String, String> itemAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

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

                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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

                        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                        String paddingCharacters = "";
                        if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                } else {
                    newObjectAttributeMap.put(destinationName, defaultValue);
                }
            });
        });

        itemOwnerGroup(newObjectAttributeMap);

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

    public String delimittedValues(String delimiters, String jsonAttributeValue, String xmlMapSourceValue, String xmlMapDestinationValue) throws IOException {
        String[] delimiterList = delimiters.split(" ");
        String destinationValueForJsonValue = "";

        for (int delimiterIterator = 0; delimiterIterator < delimiterList.length; delimiterIterator++) {
            String delimiter = delimiterList[delimiterIterator];

            if (delimiterList.length == 1 && NullOrEmptyChecker.isNullOrEmpty(delimiterIterator)) {
                destinationValueForJsonValue = jsonAttributeValue.contains(xmlMapSourceValue) ? xmlMapDestinationValue : "";
                break;
            }

            String[] delimittedValueList = jsonAttributeValue.split(delimiter);
            for (int delimittedValueIterator = 0; delimittedValueIterator < delimittedValueList.length; delimittedValueIterator++) {
                String delimittedValue = delimittedValueList[delimittedValueIterator];
                if (delimittedValue.equalsIgnoreCase(xmlMapSourceValue)) {
                    destinationValueForJsonValue = xmlMapDestinationValue;
                    break;
                }
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(destinationValueForJsonValue)) {
                break;
            }
        }

        return destinationValueForJsonValue;
    }

    public HashMap<String, String> updateItemForAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

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

                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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
                        List<ItemImportValue> itemImportValueList = rangeValues.getValue();
                        if (!NullOrEmptyChecker.isNull(itemImportValueList)) {

                            /**
                             * Finds out the range values
                             */
                            boolean rangeUpdated = false;
                            for (int iterator = 0; iterator < itemImportValueList.size(); iterator++) {
                                ItemImportValue rangeValue = itemImportValueList.get(iterator);
                                String sourceValue = rangeValue.getSrc();
                                String destinationValue = rangeValue.getValue();
                                String runTimeInterfaceList = rangeValue.getRunTimeInterfaceList();

                                String transferredToErp = commonPropertyReader.getPropertyValue("item.attribute.transferred.to.erp.source.pdm");

                                if (sourceName.equalsIgnoreCase(transferredToErp)) {
                                    destinationValue = attributeValue.contains(sourceValue) ? destinationValue : "false";
                                    rangeUpdated = true;
                                    attributeValue = destinationValue;
                                    break;
                                }

                                if (!NullOrEmptyChecker.isNullOrEmpty(rangeValue.getHasAny())) {
                                    Pattern pattern = Pattern.compile(rangeValue.getHasAny());
                                    Matcher matcher = pattern.matcher(attributeValue);

                                    attributeValue = matcher.find() ? destinationValue : defaultValue;
                                    rangeUpdated = true;
                                    Optional.ofNullable(runTimeInterfaceList).ifPresent((runtimeInterfaceList) -> {
                                        businessInterfaceList += ", " + runtimeInterfaceList;
                                    });

                                    break;
                                } else {
                                    if (attributeValue.equalsIgnoreCase(sourceValue)) {

                                        if (destinationName.equalsIgnoreCase(commonPropertyReader.getPropertyValue("item.attribute.current.destination.pdm"))
                                                && !NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getNextVersion())
                                                && sourceValue.equalsIgnoreCase("forbidden")) {
                                            destinationValue = "RELEASED";
                                        }

                                        attributeValue = destinationValue;
                                        rangeUpdated = true;

                                        Optional.ofNullable(runTimeInterfaceList).ifPresent((runtimeInterfaceList) -> {
                                            businessInterfaceList += ", " + runtimeInterfaceList;
                                        });
                                        break;
                                    }
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

                        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        } catch (IOException exp) {
                            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            throw new RuntimeException(exp);
                        }
                        return;
                    }

                    if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                        String paddingCharacters = "";
                        if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                }
            });
        });

        updateOwnerGroup(newObjectAttributeMap);

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

    public void validateXMLMapData(ItemImportXmlMapElementAttribute elementAttribute) throws NullPointerException {
        if (NullOrEmptyChecker.isNull(elementAttribute.getSourceName())) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.fatal("'Source Name' not found in the xml map");
            throw new NullPointerException("'Source Name' not found in the xml map");
        }
        if (NullOrEmptyChecker.isNull(elementAttribute.getDestinationName())) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.fatal("'Destination Name' not found in the xml map");
            throw new NullPointerException("'Destination Name' not found in the xml map");
        }
        if (NullOrEmptyChecker.isNull(elementAttribute.getIsRequired())) {
            elementAttribute.setIsRequired(Boolean.FALSE);
        }
    }

    public HashMap<String, String> itemOwnerGroup(HashMap<String, String> newObjectAttributeMap) throws ParserConfigurationException, SAXException, XPathExpressionException {
//        newObjectAttributeMap.put("project", "GLOBAL_COMPONENTS_INTERNAL");
//        newObjectAttributeMap.put("organization", "VALMET_INTERNAL");

//        newObjectAttributeMap.put("project", "TESTING");
//        newObjectAttributeMap.put("organization", "PM_VAL_INTERNAL");
        return newObjectAttributeMap;
    }

    public HashMap<String, String> processOwnerGroup(HashMap<String, String> newObjectAttributeMap) throws ParserConfigurationException, SAXException, XPathExpressionException {
        try {
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            String defaultCollaborationSpaceElementPath = commonPropertyReader.getPropertyValue("item.import.collaboration.space.default");

            XmlParse xmlParse = new XmlParse();
            String defaultCollaborationSpace = xmlParse.getAttributeValue(defaultCollaborationSpaceElementPath, "defaultCollaborationSpace");

            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Collaboration Space is : " + defaultCollaborationSpace);
            String project = commonPropertyReader.getPropertyValue("item.attribute.project.destination.pdm");
            String projectValue = newObjectAttributeMap.get(project);
            if (!NullOrEmptyChecker.isNullOrEmpty(projectValue)) {
                try {
                    String ownerGroupCollaborationSpace = commonPropertyReader.getPropertyValue("item.import.owner.group.collaboration.space");
                    String source = commonPropertyReader.getPropertyValue("item.import.owner.group.collaboration.space.source");
                    String ownerGroup = commonPropertyReader.getPropertyValue("item.import.owner.group");
                    String collaborationSpace = commonPropertyReader.getPropertyValue("item.import.collaboration.space");
                    String mapValue = xmlParse.getPredefinedValue(ownerGroupCollaborationSpace, source, ownerGroup, collaborationSpace, projectValue);
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug(source + "'s Owner group is : '" + projectValue + "' and Enovia's Collaboration space is : '" + mapValue + "'");

                    if (NullOrEmptyChecker.isNullOrEmpty(mapValue)) {
                        if (NullOrEmptyChecker.isNullOrEmpty(defaultCollaborationSpace)) {
                            String errorValue = MessageFormat.format(PropertyReader.getProperty("item.import.unsupported.owner.group.exception"), projectValue);
                            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(errorValue);
                            throw new NullPointerException(errorValue);
                        }
                        mapValue = defaultCollaborationSpace;
                    }

                    newObjectAttributeMap.put(project, NullOrEmptyChecker.isNullOrEmpty(mapValue) ? defaultCollaborationSpace : mapValue);
                } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
                    newObjectAttributeMap.put(project, defaultCollaborationSpace);
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
                }
            } else {
                newObjectAttributeMap.put(project, defaultCollaborationSpace);
            }

        } catch (IOException ex) {
            //newObjectAttributeMap.put("project", "PM_DRYER_INTERNAL");
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
        }
        return newObjectAttributeMap;
    }

    public HashMap<String, String> updateOwnerGroup(HashMap<String, String> newObjectAttributeMap) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        try {
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

            String project = commonPropertyReader.getPropertyValue("item.attribute.project.destination.pdm");
            String projectValue = newObjectAttributeMap.get(project);
            if (!NullOrEmptyChecker.isNullOrEmpty(projectValue)) {
                try {
                    String ownerGroupCollaborationSpace = commonPropertyReader.getPropertyValue("item.import.owner.group.collaboration.space");
                    String source = commonPropertyReader.getPropertyValue("item.import.owner.group.collaboration.space.source");
                    String ownerGroup = commonPropertyReader.getPropertyValue("item.import.owner.group");
                    String collaborationSpace = commonPropertyReader.getPropertyValue("item.import.collaboration.space");

                    XmlParse xmlParse = new XmlParse();
                    String mapValue = xmlParse.getPredefinedValue(ownerGroupCollaborationSpace, source, ownerGroup, collaborationSpace, projectValue);
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug(source + "'s Owner group is : '" + projectValue + "' and Enovia's Collaboration space is : '" + mapValue + "'");

                    if (!NullOrEmptyChecker.isNullOrEmpty(mapValue)) {
                        newObjectAttributeMap.put(project, mapValue);
                    } else {
                        String errorValue = MessageFormat.format(PropertyReader.getProperty("item.import.unsupported.owner.group.exception"), projectValue);
                        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(errorValue);
                        throw new NullPointerException(errorValue);
                    }

                } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
                    throw ex;
                }
            }

        } catch (IOException ex) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
            throw ex;
        } catch (NullPointerException | ParserConfigurationException | XPathExpressionException | SAXException ex) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
            throw ex;
        } catch (Exception ex) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(ex);
            throw ex;
        }
        return newObjectAttributeMap;
    }

//    private HashMap<String, String> processInventoryUnit(HashMap<String, String> newObjectAttributeMap, String sourceName, String destinationName, String value){
//        String defaultInventoryUnit = PropertyReader.getProperty("default.inventory.unit");
//        if(sourceName.equalsIgnoreCase("Inventory unit")){
//            if(NullOrEmptyChecker.isNullOrEmpty(value)){
//                newObjectAttributeMap.put(destinationName, defaultInventoryUnit);
//            }
//        }
//        return newObjectAttributeMap;
//    }
    public String abbreviate(String str, int maxWidth, String paddingChar) {
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        return businessObjectOperations.abbreviate(str, maxWidth, paddingChar);
    }

    public String changeDateType(String date, String sourceFormat, String destinationFormat) {
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Date : " + date);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Format : " + sourceFormat);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Format : " + destinationFormat);

        SimpleDateFormat destinationDateFormat = new SimpleDateFormat(destinationFormat);
        return dateFormating(sourceFormat, date, destinationDateFormat);
    }

    public String changeDateType(String date, String sourceFormat, String destinationFormat, String localeString) {
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Date : " + date);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Format : " + sourceFormat);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Format : " + destinationFormat);

        SimpleDateFormat destinationDateFormat;
        if (!NullOrEmptyChecker.isNullOrEmpty(localeString)) {
            Locale toLocale = LocaleUtils.toLocale(localeString);
            destinationDateFormat = new SimpleDateFormat(destinationFormat, toLocale);
            return dateFormating(sourceFormat, date, destinationDateFormat);
        }
        return changeDateType(date, sourceFormat, destinationFormat);
    }

    public String dateFormating(String sourceFormat, String date, SimpleDateFormat destinationDateFormat) {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
        try {
            Date sourceDate = sourceDateFormat.parse(date);
            String destinedDate = destinationDateFormat.format(sourceDate);
            return destinedDate;
        } catch (ParseException exp) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp.getMessage());
        }
        return null;
    }

    public HashMap<String, String> commodityCodeSplitter(String value) {
        HashMap<String, String> commodityCodeMap = new HashMap();
        String[] commodityCodeValues = value.split("\\n");
        for (String temp : commodityCodeValues) {
            String[] temp2 = temp.split("\\|");
            String key = temp2[0];
            String v = "";
            if (temp2.length >= 2) {
                v = temp2[1];
            }
            commodityCodeMap.put(key, v);
        }
        return commodityCodeMap;
    }

    public HashMap<String, String> manipulateCommodityCodeForCreateAssembly(HashMap<String, String> attributeMap, String commodityCode) throws IOException {
        HashMap<String, String> commodityCodeMap = commodityCodeSplitter(commodityCode);
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        HashMap<String, String> commodityCodesPropertyMap = commonPropertyReader.getPropertyValue("item.attribute.commodity.code", Boolean.TRUE);

        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Commodity Codes Property Map : " + commodityCodesPropertyMap);
        commodityCodesPropertyMap.forEach((String key, String value) -> {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Key : " + key + " Commodity Code : " + commodityCodeMap.get(key));
            attributeMap.put(commodityCodesPropertyMap.get(key), commodityCodeMap.containsKey(key) ? commodityCodeMap.get(key) : "");
        });

        commodityCodeMap.clear();
        commonPropertyReader = null;

        return attributeMap;
    }

    public void addInterfaceToValComponentMaterial(Context context, String busId, String inventoryUnit) throws MatrixException {
        try {
            String[] constructor = {null};
            HashMap mainParams = new HashMap();
            String interfaceName = "";
            Boolean addedInerface = false;
            if (inventoryUnit.equalsIgnoreCase("m") || inventoryUnit.equalsIgnoreCase("in") || inventoryUnit.equalsIgnoreCase("ft")) {
                interfaceName = ImportItemConstants.CM_LENGTH;
                addedInerface = true;
            } else if (inventoryUnit.equalsIgnoreCase("m2") || inventoryUnit.equalsIgnoreCase("in2") || inventoryUnit.equalsIgnoreCase("ft2")) {
                interfaceName = ImportItemConstants.CM_AREA;
                addedInerface = true;
            } else if (inventoryUnit.equalsIgnoreCase("m3") || inventoryUnit.equalsIgnoreCase("gal") || inventoryUnit.equalsIgnoreCase("l") || inventoryUnit.equalsIgnoreCase("in3") || inventoryUnit.equalsIgnoreCase("ft3")) {
                interfaceName = ImportItemConstants.CM_VOLUME;
                addedInerface = true;
            } else if (inventoryUnit.equalsIgnoreCase("Kg") || inventoryUnit.equalsIgnoreCase("lb") || inventoryUnit.equalsIgnoreCase("g")) {
                interfaceName = ImportItemConstants.CM_MASS;
                addedInerface = true;
            }

            if (addedInerface) {
                mainParams.put("objectId", busId);
                mainParams.put("interfaceName", interfaceName);
                JPO.invoke(context, "CloneObjectUtil", constructor, "addInterfaceToObject", JPO.packArgs(mainParams), null);
            }

        } catch (MatrixException exp) {
            ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
            throw exp;
        }
    }
}
