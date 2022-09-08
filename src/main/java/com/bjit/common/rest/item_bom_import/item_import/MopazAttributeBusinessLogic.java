/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author Tahmid
 */
public class MopazAttributeBusinessLogic extends AttributeBusinessLogic {

    private String businessInterfaceList = "";
    private static final Logger MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(MopazAttributeBusinessLogic.class);

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
    public HashMap<String, String> updateItemForAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);

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

                MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
                }

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

                        MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    }

                    if (!NullOrEmptyChecker.isNull(dataLength)) {
                        String paddingCharacters = "";
                        if(!NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getDataLength().getPaddingChar())) {
                            paddingCharacters = elementAttribute.getDataLength().getPaddingChar();
                        }
                        attributeValue = abbreviate(attributeValue, dataLength, paddingCharacters);
                    }

                    newObjectAttributeMap.put(destinationName, attributeValue);
                }
            });
        });

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = businessLogic(mapper, createObjectBean);

        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                objectAttributeMap.put("organization", businessObjectUtil.getOrganization(context, objectAttributeMap.get("project")));
                break;

            case "ProcessContinuousCreateMaterial":
                objectAttributeMap.put("organization", businessObjectUtil.getOrganization(context, objectAttributeMap.get("project")));
                break;

            case "CreateMaterial":
                objectAttributeMap.put("organization", businessObjectUtil.getOrganization(context, objectAttributeMap.get("project")));
                break;

            case "Provide":
                objectAttributeMap.put("organization", businessObjectUtil.getOrganization(context, objectAttributeMap.get("project")));
                break;

            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                objectAttributeMap = mopazAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;

            case "ProcessContinuousCreateMaterial":
                objectAttributeMap = mopazAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);

                if (!NullOrEmptyChecker.isNullOrEmpty(businessInterfaceList)) {
                    objectAttributeMap.put("businessInterfaceList", businessInterfaceList);
                }
                break;

            case "CreateMaterial":
                objectAttributeMap = mopazAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Provide":
                objectAttributeMap = mopazAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    public HashMap<String, String> mopazAttributeBusinessLogic(CreateObjectBean createObjectBean, ItemImportMapping mapper, HashMap<String, String> objectAttributeMap, HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        validatObjectAttributeMap(objectAttributeMap);

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

                MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
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

                        MOPAZ_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
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
            });
        });

        objectAttributeMap.clear();
        objectAttributeMap.putAll(newObjectAttributeMap);
        return objectAttributeMap;
    }

}
