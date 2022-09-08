/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportXmlMapElementAttribute;

/**
 *
 * @author BJIT
 */
public interface IMapperElementMemento {

    void addRelationshipAttribute(String relationshipAttrName, ItemImportXmlMapElementAttribute mapElementAttribute);

    void addProperty(String itemPropName, ItemImportXmlMapElementAttribute mapElementAttribute);

    void addItemAttribute(String itemAttrName, ItemImportXmlMapElementAttribute mapElementAttribute);

    ItemImportXmlMapElementAttribute getRelationshipAttribute(String relationshipAttrName);

    ItemImportXmlMapElementAttribute getProperty(String itemPropName);

    ItemImportXmlMapElementAttribute getItemAttribute(String itemAttrName);
}
