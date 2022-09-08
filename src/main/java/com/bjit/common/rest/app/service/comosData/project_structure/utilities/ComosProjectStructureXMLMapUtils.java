/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.utilities;

import com.bjit.common.rest.app.service.comosData.project_structure.mapping.builder.MapperBuilder;
import com.bjit.common.rest.app.service.comosData.project_structure.mapping.mapping_model.*;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author BJIT
 */
public class ComosProjectStructureXMLMapUtils {

    public static HashMap<String, String> typeMap;
    private static Mapping mapper;

    private void initializeMapper() {
        try {
            if (NullOrEmptyChecker.isNull(mapper)) {
                String fileDirectory = PropertyReader.getProperty("comos.project.structure.configuration.file.directory");
                mapper = new MapperBuilder().getMapper(MapperBuilder.XML, fileDirectory);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public HashMap<String, String> getTypeMap() {
        if (NullOrEmptyChecker.isNullOrEmpty(typeMap)) {
            typeMap = new HashMap<>();
//            typeMap.put("Milestone", "Milestone");
            typeMap.put("Project Space", "Project Space");
//            typeMap.put("Deliverable", "Deliverable");
        }
        return typeMap;
    }

    public HashMap<String, String> getCreateOrUpdateProperties(String typeOfObject, String objectOperation, HashMap<String, String> taskAttributeValueMap) {
        try {

            final String objectType = Optional.ofNullable(getTypeMap().get(typeOfObject)).orElse("Task");

            initializeMapper();

            HashMap<String, String> updateProperties = new HashMap();

            mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((MapObject elementObject) -> {
                String type = elementObject.getType();
                String operation = elementObject.getOperation();
                String runtimeInterfaceList = elementObject.getRuntimeInterfaceList();

                if (type.equalsIgnoreCase(objectType) && Optional.ofNullable(operation).orElse("").equalsIgnoreCase(objectOperation)) {
                    elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((MapAttribute elementAttribute) -> {
                        String sourceName = elementAttribute.getSourceName();
                        String destinationName = elementAttribute.getDestinationName();
                        String description = elementAttribute.getDescription();
                        String fixedValue = Optional.ofNullable(elementAttribute.getFixedValue()).orElse("");
                        String dataType = Optional.ofNullable(elementAttribute.getDataType()).orElse("");
                        final Integer dataLength = elementAttribute.getDataLength();

                        if (dataType.equals(Constants.DATE)) {
                            String dateFormat = Optional.ofNullable(elementAttribute.getDateFormat()).orElse("");
                            String mappedDate = Optional.ofNullable(elementAttribute.getDate()).orElse("");

                            if (Constants.CURRENT_DATE.equals(mappedDate)) {
                                Date curDate = new Date();

                                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                                String formatedDate = format.format(curDate);

                                updateProperties.put(destinationName, formatedDate);
                                return;
                            } else {
                                String sourceValue = taskAttributeValueMap.get(sourceName);
                                Optional.ofNullable(sourceValue).filter(value -> !value.isEmpty()).ifPresent(value -> {
                                    try {
                                        String dataDateFormat = Optional.ofNullable(elementAttribute.getDataDateFormat()).orElse("");
                                        String systemDateFormat = Optional.ofNullable(elementAttribute.getDateFormat()).orElse("");

                                        Date dataDate = new SimpleDateFormat(dataDateFormat).parse(value);
                                        String systemDate = new SimpleDateFormat(systemDateFormat).format(dataDate);
                                        updateProperties.put(destinationName, systemDate);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                return;
                            }
                        }

                        if (Optional.ofNullable(taskAttributeValueMap).orElse(new HashMap<>()).containsKey(sourceName)) {
                            if (!NullOrEmptyChecker.isNullOrEmpty(fixedValue)) {
                                updateProperties.put(destinationName, fixedValue.equalsIgnoreCase("null") ? null : fixedValue);
                                return;
                            }

                            String sourceValue = taskAttributeValueMap.get(sourceName);
                            String destinationValue;

                            destinationValue = Optional.ofNullable(Optional.ofNullable(elementAttribute.getRangeValues())
                                            .orElse(new RangeValues()).getValue())
                                    .orElse(new ArrayList<>())
                                    .stream()
                                    .filter(rangeValue -> rangeValue.getSrc().equalsIgnoreCase(sourceValue))
                                    .findFirst()
                                    .orElse(new RangeValue())
                                    .getValue();

                            if (!NullOrEmptyChecker.isNullOrEmpty(destinationValue)) {

                                if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                                    destinationValue = abbreviate(destinationValue, dataLength);
                                }

                                updateProperties.put(destinationName, destinationValue);
                            } else {
                                String updatedSourceValue = sourceValue;
                                if (!NullOrEmptyChecker.isNullOrEmpty(dataLength)) {
                                    updatedSourceValue = abbreviate(sourceValue, dataLength);
                                }
                                updateProperties.put(destinationName, updatedSourceValue);
                            }

                        } else {
                            updateProperties.put(destinationName, Optional.ofNullable(elementAttribute.getFixedValue()).orElse(""));
                        }
                    });
                    updateProperties.put("runtimeInterfaceList", runtimeInterfaceList);
                }

            });

            return updateProperties;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String abbreviate(String str, int maxWidth) {
        if (str.length() <= maxWidth) {
            return str;
        }

        return maxWidth > 20 ? str.substring(0, (maxWidth - 3)) + "..." : str.substring(0, maxWidth);
    }
}
