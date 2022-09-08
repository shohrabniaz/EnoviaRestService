/**
 *
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import java.util.regex.*;

import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperElementMemento;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperProcessor;
import com.bjit.common.rest.app.service.cache_manager.interfaces.IBOMCacheManager;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.XmlMapElementBOMRelationship;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.XmlMapElementProperty;
import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * @author BJIT
 *
 */
@Component
//@Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public class ItemMapperProcessor implements IMapperProcessor {

    @Autowired
    IBOMCacheManager bomCacheManager;

    @Autowired
    IMapperElementMemento mapperElementMemento;

    private final Logger ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER = Logger.getLogger(ItemMapperProcessor.class);

    @Override
    public HashMap<String, HashMap<String, String>> processAttributeXMLMapper(CommonItemParameters commonItemParameters, String typeName) throws Exception {

        try {
            IBOMCacheManager bomCacheManager = new BOMMapperCacheMgr();
            ItemImportMapping mapper = bomCacheManager.getMapper(typeName);

            HashMap<String, HashMap<String, String>> itemAndRelMap = new HashMap<>();
            itemAndRelMap.put(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.map"), getItemAttributeMap(mapper));
            itemAndRelMap.put(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.property.map"), getItemPropertyMap(mapper));
            itemAndRelMap.put(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.rel.map"), getRelationshipAttributeMap(mapper));

            return itemAndRelMap;
        } catch (JAXBException exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (FileNotFoundException | RuntimeException exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private HashMap<String, String> getRelationshipAttributeMap(ItemImportMapping mapper) {
        HashMap<String, String> relDestinationSourceXmlMap = new HashMap<>();
        mapper.getXmlMapElementBOMRelationships().getXmlMapElementBOMRelationship().forEach((XmlMapElementBOMRelationship bomRelationShipData) -> {

            bomRelationShipData.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute relationshipAttribute) -> {
                String sourceName = relationshipAttribute.getSourceName();
                String destinationName = relationshipAttribute.getDestinationName();

                relDestinationSourceXmlMap.put(destinationName, sourceName);
                mapperElementMemento.addRelationshipAttribute(destinationName, relationshipAttribute);
            });
        });
        return relDestinationSourceXmlMap;
    }

    private HashMap<String, String> getItemAttributeMap(ItemImportMapping mapper) {
        HashMap<String, String> attributeDestinationSourceXmlMap = new HashMap<>();
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {

            elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {
                
                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();

                        attributeDestinationSourceXmlMap.put(destinationName, sourceName);
     
                mapperElementMemento.addRelationshipAttribute(destinationName, elementAttribute);
                
                
            });
        });
        return attributeDestinationSourceXmlMap;
    }

    private HashMap<String, String> getItemPropertyMap(ItemImportMapping mapper) {
        HashMap<String, String> attributeDestinationSourceXmlMap = new HashMap<>();
        mapper.getXmlMapElementProperties().getXmlMapElementProperty().forEach((XmlMapElementProperty elementProperty) -> {

            elementProperty.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                String sourceName = elementAttribute.getSourceName();
                String destinationName = elementAttribute.getDestinationName();

                attributeDestinationSourceXmlMap.put(destinationName, sourceName);

                mapperElementMemento.addProperty(destinationName, elementAttribute);
            });
        });
        return attributeDestinationSourceXmlMap;
    }

    @Override
    public IMapperElementMemento getMapperElementMemento() {
        return mapperElementMemento;
    }
}
