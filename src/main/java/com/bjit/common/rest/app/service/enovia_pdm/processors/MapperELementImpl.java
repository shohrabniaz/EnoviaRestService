/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperElementMemento;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportXmlMapElementAttribute;
import java.util.HashMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */

@Component
@Scope(value = "prototype")
public class MapperELementImpl implements IMapperElementMemento {

    private final HashMap<String, ItemImportXmlMapElementAttribute> elementAttributeMap;

    public MapperELementImpl() {
        elementAttributeMap = new HashMap<>();
    }

    @Override
    public void addRelationshipAttribute(String relationshipAttrName, ItemImportXmlMapElementAttribute mapElementAttribute) {
        elementAttributeMap.put(relationshipAttrName, mapElementAttribute);
    }

    @Override
    public void addProperty(String itemPropName, ItemImportXmlMapElementAttribute mapElementAttribute) {
        elementAttributeMap.put(itemPropName, mapElementAttribute);
    }

    @Override
    public void addItemAttribute(String itemAttrName, ItemImportXmlMapElementAttribute mapElementAttribute) {
        elementAttributeMap.put(itemAttrName, mapElementAttribute);
    }

    @Override
    public ItemImportXmlMapElementAttribute getRelationshipAttribute(String relationshipAttrName) {
        return elementAttributeMap.get(relationshipAttrName);
    }

    @Override
    public ItemImportXmlMapElementAttribute getProperty(String itemPropName) {
        return elementAttributeMap.get(itemPropName);
    }

    @Override
    public ItemImportXmlMapElementAttribute getItemAttribute(String itemAttrName) {
        return elementAttributeMap.get(itemAttrName);
    }
}
