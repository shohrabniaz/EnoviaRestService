/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.mapproject.builder.MapperBuilder;
import com.bjit.mapproject.xml_mapping_model.Mapping;
import com.bjit.mapproject.xml_mapping_model.XmlMapElementAttribute;
import com.bjit.mapproject.xml_mapping_model.XmlMapElementAttributes;
import com.bjit.mapproject.xml_mapping_model.XmlMapElementBOMRelationship;
import com.bjit.mapproject.xml_mapping_model.XmlMapElementBOMRelationships;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author 88017
 */
public class XmlMapUtils {
     public List<XmlMapElementAttribute> getXMLMapElementAttributeList(Logger LOGGER,String attributeName) throws Exception {
        List<XmlMapElementAttribute> xmlMapElementAttributeList = null;
        try { //readXmlMapperFromFile
            final Mapping mapper = new MapperBuilder().getMapper(MapperBuilder.XML);
            XmlMapElementBOMRelationships xmlMapElementBOMRelationships = mapper.getXmlMapElementBOMRelationships();
            List<XmlMapElementBOMRelationship> xmlMapElementBOMRelationship = xmlMapElementBOMRelationships.getXmlMapElementBOMRelationship();
            XmlMapElementBOMRelationship bomRelationship = getXmlMappingOfSpecificBOM(attributeName, xmlMapElementBOMRelationship);
            
            if (bomRelationship == null) {
                LOGGER.error("Mapping not present for this relationship: ");
                throw new Exception("Map Attribute List Cannot Be Empty!");
            }
            //List<XmlMapElementBOMRelationship> xmlMapElementBOMRelationshipList = xmlMapElementBOMRelationships.getXmlMapElementBOMRelationship();
            //XmlMapElementBOMRelationship bomRelationship = xmlMapElementBOMRelationshipList.get(0);
            XmlMapElementAttributes xmlMapElementAttributes = bomRelationship.getXmlMapElementAttributes();
            xmlMapElementAttributeList = xmlMapElementAttributes.getXmlMapElementAttribute();
        } catch (Exception e) {
            throw e;
        }
        return xmlMapElementAttributeList;
    }
     public XmlMapElementBOMRelationship getXmlMappingOfSpecificBOM(String relName, List<XmlMapElementBOMRelationship> xmlMapElementBOMRelationship) {
        for (int i = 0; i < xmlMapElementBOMRelationship.size(); i++) {
            XmlMapElementBOMRelationship relElement = xmlMapElementBOMRelationship.get(i);
            if (relElement.getName().equals(relName)) {
                return relElement;
            }
        }
        return null;
    }
}
