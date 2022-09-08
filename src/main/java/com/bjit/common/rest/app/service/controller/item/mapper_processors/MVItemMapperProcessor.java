package com.bjit.common.rest.app.service.controller.item.mapper_processors;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;

import matrix.db.Context;

public class MVItemMapperProcessor extends CommonItemMapperProcessor {

    private final Logger logger = Logger.getLogger(MVItemMapperProcessor.class);

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

        Boolean isUpdatable = searchItemsExistence(createObjectBean, context);

        try {

            ItemImportMapping mapper = getXmlMapper(mapsAbsoluteDirectory, classType);

            HashMap<String, String> destinationSourceXmlMap = new HashMap<>();
            List<String> interfaceListFromXMLMap = new ArrayList<>();
            List<String> insertableList = new ArrayList<>();
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
                });
            });

            HashMap<String, String> createObjectBeansAttributesUpdated = isUpdatable ? attributeBusinessLogic.updatableBusinessLogic(businessObjectUtil, context, mapper, createObjectBean) : attributeBusinessLogic.businessLogic(context, businessObjectUtil, mapper, createObjectBean);

            String runTimeInterfaces = createObjectBeansAttributesUpdated.get("runtimeInterfaceList");
            getSplittedRuntimeIterfaceList(runTimeInterfaces, interfaceListFromXMLMap);
            createObjectBeansAttributesUpdated.remove("runtimeInterfaceList");

            setDestinationSourceMap(destinationSourceXmlMap);
            setRunTimeInterfaceList(interfaceListFromXMLMap);

            commonItemParameters.setInsertableProperties(insertableList);
            logger.info("--- processAttributeXMLMapper ---");

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
