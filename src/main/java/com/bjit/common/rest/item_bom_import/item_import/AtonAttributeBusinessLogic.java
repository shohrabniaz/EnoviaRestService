/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
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
 * @author BJIT
 */
public class AtonAttributeBusinessLogic extends AttributeBusinessLogic {
    private static final Logger ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(AtonAttributeBusinessLogic.class);
    protected String getOwnerGroup(CommonPropertyReader commonPropertyReader,
                                   XmlParse xmlParse, String projectValue) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {
        String ownerGroupCollaborationSpace = PropertyReader.getProperty("item.import.owner.group.collaboration.space");
        String source = PropertyReader.getProperty("item.import.owner.group.collaboration.space.aton.source");
        String ownerGroup = PropertyReader.getProperty("item.import.owner.group");
        String collaborationSpace = PropertyReader.getProperty("item.import.collaboration.space");
        String mapValue = xmlParse.getPredefinedValue(ownerGroupCollaborationSpace, source, ownerGroup, collaborationSpace, projectValue);
        ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug(source + "'s Owner group is : '" + projectValue + "' and Enovia's Collaboration space is : '" + mapValue + "'");
        return mapValue;
    }

    /**
     If the business object is updatable then it checks only the data comes in
     JSON string

     @param context
     @param mapper
     @param createObjectBean
     @return
     @throws IOException
     @throws ParserConfigurationException
     @throws SAXException
     @throws XPathExpressionException
     @throws FrameworkException
     */
    //public static synchronized HashMap<String, String> updatableBusinessLogic(Context context, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {
    public HashMap<String, String> updatableBusinessLogic(
            BusinessObjectUtil businessObjectUtil, Context context,
            ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException
    {

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

    public HashMap<String, String> updateItemForAttributeBusinessLogic(
            CreateObjectBean createObjectBean, ItemImportMapping mapper,
            HashMap<String, String> objectAttributeMap,
            HashMap<String, String> newObjectAttributeMap) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException
    {
        validatObjectAttributeMap(objectAttributeMap);
        final CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((
                ItemImportXmlMapElementObject elementObject) -> {
            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((
                    ItemImportXmlMapElementAttribute elementAttribute) -> {

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

                ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Source Name : " + sourceName);
                ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Destination Name : " + destinationName);

                if (isRequired && NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap.get(sourceName))) {
                    String errorMessage = MessageFormat.format(PropertyReader.getProperty("missing.attribute.exception"), "'" + sourceName + "'");
                    throw new RuntimeException(errorMessage);
                }

                String defaultValue = "";

                if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                    defaultValue = NullOrEmptyChecker.isNullOrEmpty(elementAttribute.getValues().getDefaultValue()) ? "" : elementAttribute.getValues().getDefaultValue();
                    ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Default Value : " + defaultValue);
                }

                //if (!NullOrEmptyChecker.isNullOrEmpty(objectAttributeMap)) {
                /**
                 Checks the attributes are present in the JSON attribute map
                 */
                if (objectAttributeMap.containsKey(sourceName)) {
                    String attributeValue = objectAttributeMap.get(sourceName);

                    if (NullOrEmptyChecker.isNullOrEmpty(attributeValue)) {
                        attributeValue = defaultValue;
                    } else {
                        if (!NullOrEmptyChecker.isNull(elementAttribute.getValues())) {
                            ItemImportValues rangeValues = elementAttribute.getValues();
                            List<ItemImportValue> itemImportValueList = rangeValues.getValue();
                            if (!NullOrEmptyChecker.isNull(itemImportValueList)) {

                                /**
                                 Finds out the range values
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
                                        Optional.ofNullable(runTimeInterfaceList).ifPresent((
                                                runtimeInterfaceList) -> {
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

                                            Optional.ofNullable(runTimeInterfaceList).ifPresent((
                                                    runtimeInterfaceList) -> {
                                                businessInterfaceList += ", " + runtimeInterfaceList;
                                            });
                                            break;
                                        }
                                    }
                                }
                                if (!rangeUpdated) {
                                    if (!NullOrEmptyChecker.isNullOrEmpty(defaultValue)) {
                                        attributeValue = defaultValue;
                                    } else {
                                        String errorMessage = MessageFormat.format(PropertyReader.getProperty("unsupported.value.exception"), "'" + attributeValue + "'", "'" + sourceName + "'");
                                        throw new RuntimeException(errorMessage);
                                    }
                                }
                            }
                        }
                    }

                    if (dataType.equalsIgnoreCase("date")) {

                        if (NullOrEmptyChecker.isNull(elementAttribute.getDataFormat())) {
                            throw new NullPointerException("Data format is missing for the 'Date' type attribute '" + sourceName + "'");
                        }

                        String timeZone = elementAttribute.getDataFormat().getTimezone();

                        ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.debug("Locale is : " + timeZone);

                        String changedFormat = NullOrEmptyChecker.isNullOrEmpty(timeZone)
                                ? changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat())
                                : changeDateType(attributeValue, elementAttribute.getDataFormat().getSourceFormat(), elementAttribute.getDataFormat().getDestinationFormat(), timeZone);

                        attributeValue = NullOrEmptyChecker.isNullOrEmpty(attributeValue) ? attributeValue : changedFormat;
                    }

                    if (sourceName.toLowerCase().contains(commonPropertyReader.getPropertyValue("item.attribute.commodity.source.pdm"))) {
                        try {
                            manipulateCommodityCodeForCreateAssembly(newObjectAttributeMap, attributeValue);

                        }
                        catch (IOException exp) {
                            ATON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.error(exp);
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
}
