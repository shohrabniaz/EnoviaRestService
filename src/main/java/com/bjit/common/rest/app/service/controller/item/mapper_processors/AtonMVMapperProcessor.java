package com.bjit.common.rest.app.service.controller.item.mapper_processors;

import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import matrix.db.Context;
import org.apache.log4j.Logger;

public class AtonMVMapperProcessor extends CommonItemMapperProcessor {

    private final Logger logger = Logger.getLogger(AtonMVMapperProcessor.class);

    /**
     * Fetching properties and attributes and Iterates over item attributes
     * @param commonItemParameters
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, String> processAttributeXMLMapper(CommonItemParameters commonItemParameters) throws Exception {
        this.commonItemParameters = commonItemParameters;
        logger.info("++++ processAttributeXMLMapper ++++");
        Context context = commonItemParameters.getContext();
        CreateObjectBean createObjectBean = commonItemParameters.getCreateObjectBean();
        String mapsAbsoluteDirectory = commonItemParameters.getXmlMapDirectory();
        Class<ItemImportMapping> classType = commonItemParameters.getClassType();
        AttributeBusinessLogic attributeBusinessLogic = commonItemParameters.getAttributeBusinessLogic();
        BusinessObjectUtil businessObjectUtil = commonItemParameters.getBusinessObjectUtil();

        initializeMapsAndLists();

        try {

            ItemImportMapping mapper = getXmlMapper(mapsAbsoluteDirectory, classType);

            HashMap<String, String> destinationSourceXmlMap = new HashMap<>();
            List<String> interfaceListFromXMLMap = new ArrayList<>();
            List<String> insertableList = new ArrayList<>();
            List<String> classAttributesList = new ArrayList<>();
            List<String> interfaceAttributesList = new ArrayList<>();
            Set<String> interfaceSet = new HashSet();
            mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {

                String runTimeInterfaces = elementObject.getRunTimeInterfaceList();
                getSplittedRuntimeIterfaceList(runTimeInterfaces, interfaceListFromXMLMap);

                elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                    String sourceName = elementAttribute.getSourceName();
                    String destinationName = elementAttribute.getDestinationName();

                    destinationSourceXmlMap.put(destinationName, sourceName);

                    String insertable = elementAttribute.getInsertable();
                    if (insertable != null && insertable.equalsIgnoreCase("true")) {
                        insertableList.add(destinationName);
                    }
                    if (!NullOrEmptyChecker.isNull(elementAttribute.getIsClassAttribute()) && elementAttribute.getIsClassAttribute()) {
                        classAttributesList.add(destinationName);
                    }
                    if (!NullOrEmptyChecker.isNull(elementAttribute.getLazyInterface()) && !NullOrEmptyChecker.isNull(elementAttribute.getLazyInterface().getInterfaces())) {
                        interfaceAttributesList.add(destinationName);
                        interfaceSet.add(elementAttribute.getLazyInterface().getInterfaces());
                    }
                });
            });

            HashMap<String, String> createObjectBeansAttributesUpdated = attributeBusinessLogic.updatableBusinessLogic(businessObjectUtil, context, mapper, createObjectBean);

            String runTimeInterfaces = createObjectBeansAttributesUpdated.get("runtimeInterfaceList");
            getSplittedRuntimeIterfaceList(runTimeInterfaces, interfaceListFromXMLMap);
            createObjectBeansAttributesUpdated.remove("runtimeInterfaceList");

            setDestinationSourceMap(destinationSourceXmlMap);
            setRunTimeInterfaceList(interfaceListFromXMLMap);

            commonItemParameters.setInsertableProperties(insertableList);
            logger.info("--- processAttributeXMLMapper ---");

            String classAttributes = classAttributesList.stream()
                    .collect(Collectors.joining(","));
            createObjectBeansAttributesUpdated.put("classAttributes", classAttributes);
            String interfaces = interfaceSet.stream()
                    .collect(Collectors.joining(","));
            createObjectBeansAttributesUpdated.put("interfaces", interfaces);
            String interfaceAttributes = interfaceAttributesList.stream()
                    .collect(Collectors.joining(","));
            createObjectBeansAttributesUpdated.put("interfaceAttributes", interfaceAttributes);

            return createObjectBeansAttributesUpdated;
        } catch (JAXBException exp) {
            logger.error(exp.getMessage());
            throw exp;
        } catch (FileNotFoundException | RuntimeException exp) {
            logger.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            logger.error(exp.getMessage());
            throw exp;
        }
    }
}
