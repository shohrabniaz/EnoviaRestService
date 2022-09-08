/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.mapper;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportValues;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
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
import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class AttributeMapper {

    private static final Logger ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(AttributeMapper.class);
    public static String businessInterfaceList = "";

    public static synchronized HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();

        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {

            case "VAL_VALComponent":
                objectAttributeMap = valItemBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                ItemPredefinedValues.getObjectUpdateMap(createObjectBean);
                break;

            case "VAL_VALComponentMaterial":
                objectAttributeMap = valItemBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                if (!NullOrEmptyChecker.isNullOrEmpty(businessInterfaceList)) {
                    objectAttributeMap.put("businessInterfaceList", businessInterfaceList);
                }

                ItemPredefinedValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Document":
                objectAttributeMap = valItemBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                ItemPredefinedValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    private static synchronized void validatObjectAttributeMap(HashMap<String, String> objectAttributeMap) {
        if (NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap)) {
            objectAttributeMap = new HashMap<>();
        }
    }

    public static synchronized HashMap<String, String> valItemBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException {
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        CommonUtil.attributeNameMap = new HashMap<>();
        businessInterfaceList = "";

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
                String outOfScope = elementAttribute.getOutOfScope();
                
                if(!NullOrEmptyChecker.isNullOrEmpty(outOfScope) && outOfScope.equals("true")) {
                    ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName + " is Out of Scope");
                }
                else {
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

                        if (NullOrEmptyChecker.isNullOrEmpty(attributeValue) && NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
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
                                    String runTimeInterfaceList = rangeValue.getRunTimeInterfaceList();

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
                            if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                                paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                            }
                            attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                        }

                        newObjectAttributeMap.put(destinationName, attributeValue);
                    } else {
                        newObjectAttributeMap.put(destinationName, defaultValue);
                    }
                    CommonUtil.attributeNameMap.put(destinationName, sourceName);
                }
            });
        });

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

    private synchronized static void validateXMLMapData(ItemImportXmlMapElementAttribute elementAttribute) throws NullPointerException {
        if (NullOrEmptyChecker.isNull(elementAttribute.getSourceName())) {
            throw new NullPointerException("'Source Name' not found in the xml map");
        }
        if (NullOrEmptyChecker.isNull(elementAttribute.getDestinationName())) {
            throw new NullPointerException("'Destination Name' not found in the xml map");
        }
        if (NullOrEmptyChecker.isNull(elementAttribute.getIsRequired())) {
            elementAttribute.setIsRequired(Boolean.FALSE);
        }
    }
    
    public static synchronized String abbreviate(String str, int maxWidth, String paddingChar) {
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        return businessObjectOperations.abbreviate(str, maxWidth, paddingChar);
    }

    public static synchronized String changeDateType(String date, String sourceFormat, String destinationFormat) {
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Date : " + date);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Format : " + sourceFormat);
        ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Format : " + destinationFormat);

        SimpleDateFormat destinationDateFormat = new SimpleDateFormat(destinationFormat);
        return dateFormating(sourceFormat, date, destinationDateFormat);
    }

    public static synchronized String changeDateType(String date, String sourceFormat, String destinationFormat, String localeString) {
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

    private static String dateFormating(String sourceFormat, String date, SimpleDateFormat destinationDateFormat) {
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

    public static synchronized HashMap<String, String> commodityCodeSplitter(String value) {
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

    public static synchronized HashMap<String, String> manipulateCommodityCodeForCreateAssembly(HashMap<String, String> attributeMap, String commodityCode) throws IOException {
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
}
