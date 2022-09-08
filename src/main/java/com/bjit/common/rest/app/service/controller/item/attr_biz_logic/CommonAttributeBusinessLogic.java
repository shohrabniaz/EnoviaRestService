/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.attr_biz_logic;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.controller.item.interfaces.IAttributeBusinessLogic;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValues;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.LazyInterface;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.LazyInterfaceIsValue;
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
public class CommonAttributeBusinessLogic extends AttributeBusinessLogic implements IAttributeBusinessLogic {

    protected String businessInterfaceList = "";
    private static final Logger COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(CommonAttributeBusinessLogic.class);

    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "CreateMaterial":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "ProcessContinuousCreateMaterial":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Provide":
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

        String organization = "";
        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", organization);
                break;
            case "Provide":
                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", organization);
                break;
            case "ProcessContinuousCreateMaterial":
                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", organization);
                break;
            case "CreateMaterial":
                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
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

    @Override
    public HashMap<String, String> itemAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        processXMLMap(objectAttributeMap, mapper, newObjectAttributeMap, Boolean.TRUE);
        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> updateItemForAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        processXMLMap(objectAttributeMap, mapper, newObjectAttributeMap, Boolean.FALSE);
        return objectAttributeMap;
    }

    private void processXMLMap(HashMap<String, String> objectAttributeMap, ItemImportMapping mapper, HashMap<String, String> newObjectAttributeMap, Boolean isNewItem) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);

        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                validateXMLMapData(elementAttribute);

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();
                String dataType = elementAttribute.getDataType();

                requiredDataValidation(elementAttribute, sourceName, destinationName, objectAttributeMap);

                String defaultValue = getDefaultValue(elementAttribute);

                if (objectAttributeMap.containsKey(sourceName)) {
                    String attributeValue = objectAttributeMap.get(sourceName);

                    if (NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                        attributeValue = defaultValue;
                    } else if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                        attributeValue = processRangeValues(elementAttribute, attributeValue, sourceName, newObjectAttributeMap);
                    }

                    attributeValue = dateFormatProcess(dataType, elementAttribute, sourceName, attributeValue);
                    attributeValue = dataLengthProcess(elementAttribute, attributeValue);

                    try {
                        attributeValue = getLazyInterfaces(elementAttribute, attributeValue, newObjectAttributeMap);

                    } catch (Exception exp) {
                        COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                        return;
                    }
                    newObjectAttributeMap.put(destinationName, attributeValue);
                } else {
                    if (isNewItem) {
                        try {
                            getLazyInterfaces(elementAttribute, "", newObjectAttributeMap);
                        } catch (Exception exp) {
                            COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
                            return;
                        }

                        newObjectAttributeMap.put(destinationName, defaultValue);
                    }
                }
            });
        });

        if (isNewItem) {
            itemOwnerGroup(newObjectAttributeMap);
        }

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
    }

    private String getLazyInterfaces(ItemImportXmlMapElementAttribute elementAttribute, String attributeValue, HashMap<String, String> newObjectAttributeMap) throws Exception {
        LazyInterface lazyInterface = elementAttribute.getLazyInterface();

        if (!NullOrEmptyChecker.isNull(lazyInterface)) {
            Boolean hasValue = lazyInterface.getRequestHasValue();
//            String hasValueString = lazyInterface.getHasValue();
//            Boolean hasValue = Boolean.parseBoolean(hasValueString);

            if (!NullOrEmptyChecker.isNull(hasValue)) {
                if (hasValue) {
                    if (NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                        throw new RuntimeException(elementAttribute.getSourceName() + " attribute doesn't have the value");
                    }

                    String interfaceToBeAdded = lazyInterface.getInterfaces();
                    if (!NullOrEmptyChecker.isNullOrEmpty(interfaceToBeAdded)) {
                        setRuntimeInterfaces(newObjectAttributeMap, interfaceToBeAdded);
                    }

                    LazyInterfaceIsValue isValue = lazyInterface.getIsValue();

                    if (!NullOrEmptyChecker.isNull(isValue)) {
                        String condition = isValue.getCondition();
                        Boolean reverse = isValue.getReverse();

                        if (!NullOrEmptyChecker.isNullOrEmpty(condition)) {
                            if (condition.equalsIgnoreCase("positive")) {
                                Double parsedDouble = Double.parseDouble(attributeValue);

                                if (!NullOrEmptyChecker.isNull(reverse) && reverse && parsedDouble > 0) {
                                    attributeValue = numberredDataProcess(lazyInterface, attributeValue, reverse, parsedDouble);
                                }

                            } else if (condition.equalsIgnoreCase("negative")) {
                                Double parsedDouble = Double.parseDouble(attributeValue);

                                if (!NullOrEmptyChecker.isNull(reverse) && reverse && parsedDouble < 0) {
                                    attributeValue = numberredDataProcess(lazyInterface, attributeValue, reverse, parsedDouble);
                                }

                            } else if (condition.equalsIgnoreCase("true") || condition.equalsIgnoreCase("false")) {
                                Boolean data = Boolean.parseBoolean(attributeValue);
                                attributeValue = booleanDataProcess(lazyInterface, attributeValue, reverse, data);
                            } else if (condition.equalsIgnoreCase("upper")) {
                                attributeValue = stringDataProcessToLower(lazyInterface, attributeValue, reverse);
                            }
                        }
                    }

                } else {
                    throw new RuntimeException(elementAttribute.getSourceName() + " attribute doesn't have the value");
                }
            }
        }

        return attributeValue;
    }

    private String numberredDataProcess(LazyInterface lazyInterface, String attributeValue, Boolean reverse, Double data) {
        String setAttirbuteValue = lazyInterface.getSetValue();
        if (!NullOrEmptyChecker.isNullOrEmpty(setAttirbuteValue)) {
            attributeValue = setAttirbuteValue;
        } else {
            data = (-1) * data;
            attributeValue = data.toString();
        }

        return attributeValue;
    }

    private String booleanDataProcess(LazyInterface lazyInterface, String attributeValue, Boolean reverse, Boolean data) {
        String setAttirbuteValue = lazyInterface.getSetValue();
        if (!NullOrEmptyChecker.isNullOrEmpty(setAttirbuteValue)) {
            attributeValue = setAttirbuteValue;
        } else {
            if (!NullOrEmptyChecker.isNull(reverse) && reverse) {
                data = !data;
                attributeValue = data.toString();
            }
        }

        return attributeValue;
    }

    private String stringDataProcessToLower(LazyInterface lazyInterface, String attributeValue, Boolean reverse) {
        String setAttirbuteValue = lazyInterface.getSetValue();
        if (!NullOrEmptyChecker.isNullOrEmpty(setAttirbuteValue)) {
            attributeValue = setAttirbuteValue;
        } else {
            if (reverse) {
                attributeValue = attributeValue.toLowerCase();
            }
        }

        return attributeValue;
    }

    private String processRangeValues(ItemImportXmlMapElementAttribute elementAttribute, String attributeValue, String sourceName, HashMap<String, String> newObjectAttributeMap) throws RuntimeException {
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
                    rangeUpdated = true;
                    attributeValue = destinationValue;

                    getRangeInterfaces(rangeValue, newObjectAttributeMap);

                    break;
                }
            }
            if (!rangeUpdated) {
                String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                throw new RuntimeException(errorMessage);
            }
        }
        return attributeValue;
    }

    private void getRangeInterfaces(ItemImportValue rangeValue, HashMap<String, String> newObjectAttributeMap) {
        String runTimeRangeInterfaceList = rangeValue.getRunTimeInterfaceList();
        if (!NullOrEmptyChecker.isNullOrEmpty(runTimeRangeInterfaceList)) {
            setRuntimeInterfaces(newObjectAttributeMap, runTimeRangeInterfaceList);
        }
    }

    private void requiredDataValidation(ItemImportXmlMapElementAttribute elementAttribute, String sourceName, String destinationName, HashMap<String, String> objectAttributeMap) throws RuntimeException {
        Boolean isRequired = elementAttribute.getIsRequired();

        COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
        COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

        if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
            String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
            throw new RuntimeException(errorMessage);
        }
    }

    private String getDefaultValue(ItemImportXmlMapElementAttribute elementAttribute) {
        String defaultValue = "";
        if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
            defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
            COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
        }
        return defaultValue;
    }

    private String dateFormatProcess(String dataType, ItemImportXmlMapElementAttribute elementAttribute, String sourceName, String attributeValue) throws NullPointerException {
        if (dataType.equalsIgnoreCase("date")) {

            if (NullOrEmptyChecker.isNull(elementAttribute.getDataFormat())) {
                throw new NullPointerException("Data format is missing for the 'Date' type attribute '" + sourceName + "'");
            }

            String timeZone = elementAttribute.getDataFormat().getTimezone();

            COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

            String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                    ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                    : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

            attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
        }
        return attributeValue;
    }

    private String dataLengthProcess(ItemImportXmlMapElementAttribute elementAttribute, String attributeValue) {
        Integer dataLength = null;
        if (!NullOrEmptyChecker.isNull(elementAttribute.getDataLength())) {
            if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getValue())) {
                dataLength = elementAttribute.getDataLength().getValue();
            }
        }

        if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
            String paddingCharacters = "";
            if (!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
            }
            attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
        }
        return attributeValue;
    }

    private void setRuntimeInterfaces(HashMap<String, String> newObjectAttributeMap, String runTimeRangeInterfaceList) {
        if (newObjectAttributeMap.containsKey("runtimeInterfaceList")) {
            String runtimeInterfaceList = newObjectAttributeMap.get("runtimeInterfaceList");
            if (NullOrEmptyChecker.isNullOrEmpty(runtimeInterfaceList)) {
                newObjectAttributeMap.put("runtimeInterfaceList", runTimeRangeInterfaceList);
            } else {
                runtimeInterfaceList = runtimeInterfaceList + ", " + runTimeRangeInterfaceList;
                newObjectAttributeMap.put("runtimeInterfaceList", runtimeInterfaceList);
            }
        } else {
            newObjectAttributeMap.put("runtimeInterfaceList", runTimeRangeInterfaceList);
        }
    }
}
