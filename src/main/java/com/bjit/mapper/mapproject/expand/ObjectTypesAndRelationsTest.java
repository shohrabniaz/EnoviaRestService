/**
 *
 */
package com.bjit.mapper.mapproject.expand;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.bjit.mapper.mapproject.builder.MapperBuilder;
import com.bjit.mapper.mapproject.xml_mapping_model.Mapping;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementAttribute;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementAttributes;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationship;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationships;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObject;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObjects;

/**
 * @author TAREQ SEFATI
 *
 */
public class ObjectTypesAndRelationsTest {

    private List<String> typeNames;
    private List<String> relationNames;
    private Map<String, String> typeSelectablesWithOutputName;
    private Map<String, String> relationSelectablesWithOutputName;
    private boolean expandUp;
    private boolean expandDown;
    private int expandLevel;
    private int dataFetchLimit;
    private String busWhereClause = "";
    private String relWhereClause = "";
    private final Logger logger = Logger.getLogger(ObjectTypesAndRelationsTest.class);

    public ObjectTypesAndRelationsTest() throws JAXBException, FileNotFoundException, Exception {
        typeNames = new ArrayList<>();
        relationNames = new ArrayList<>();
        typeSelectablesWithOutputName = new LinkedHashMap<>();
        relationSelectablesWithOutputName = new LinkedHashMap<>();
        try {
            Mapping mapper = new MapperBuilder().getMapper(MapperBuilder.XML);
            XmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
            setExpandQueryData(elementObjects);
            List<XmlMapElementObject> elementObjectList = elementObjects.getXmlMapElementObject();

            populateObjectTypeNameAndVariable(elementObjectList);

            populateSelectables(elementObjectList);

            XmlMapElementBOMRelationships elementRelationships = mapper.getXmlMapElementBOMRelationships();
            List<XmlMapElementBOMRelationship> relationshipList = elementRelationships.getXmlMapElementBOMRelationship();

            populateRelationshipNameAndVariables(relationshipList);

            populateSelectablesFromRelationshipAttribute(relationshipList);

        } catch (JAXBException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public ObjectTypesAndRelationsTest(String absoluteMapperFileDirectory) throws JAXBException, FileNotFoundException, Exception {
        typeNames = new ArrayList<>();
        relationNames = new ArrayList<>();
        typeSelectablesWithOutputName = new LinkedHashMap<>();
        relationSelectablesWithOutputName = new LinkedHashMap<>();
        try {
            Mapping mapper = new MapperBuilder().getMapper(MapperBuilder.XML, absoluteMapperFileDirectory);
            XmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
            setExpandQueryData(elementObjects);
            List<XmlMapElementObject> elementObjectList = elementObjects.getXmlMapElementObject();

            populateObjectTypeNameAndVariable(elementObjectList);

            populateSelectables(elementObjectList);

            XmlMapElementBOMRelationships elementRelationships = mapper.getXmlMapElementBOMRelationships();
            List<XmlMapElementBOMRelationship> relationshipList = elementRelationships.getXmlMapElementBOMRelationship();

            populateRelationshipNameAndVariables(relationshipList);

            populateSelectablesFromRelationshipAttribute(relationshipList);

        } catch (JAXBException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    private void populateObjectTypeNameAndVariable(List<XmlMapElementObject> elementObjectList) {
        //Populating each object type in typeNames variable
        elementObjectList.forEach((object) -> {
            //System.out.println(object.getType());
            typeNames.add(object.getType());
        });
    }

    private void populateRelationshipNameAndVariables(List<XmlMapElementBOMRelationship> relationshipList) {
        //Populating each relationship name in relationNames variable
        relationshipList.forEach((relationship) -> {
            //System.out.println(relationship.getName());
            relationNames.add(relationship.getRelName());
        });
    }

    private void populateSelectablesFromRelationshipAttribute(List<XmlMapElementBOMRelationship> relationshipList) {
        //Populating all selectables from a relationship's all attribute
        if (!relationshipList.isEmpty()) {
            XmlMapElementAttributes elementAttributes = relationshipList.get(0).getXmlMapElementAttributes();
            List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
            elementAttributeList.forEach((elementAttribute) -> {
                relationSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
                //System.out.println(elementAttribute.getSelectable()+" "+elementAttribute.getOutputName());
            });
        }
    }

    private void populateSelectables(List<XmlMapElementObject> elementObjectList) {
        //Populating all selectables from an object's all attribute
        if (!elementObjectList.isEmpty()) {
            XmlMapElementAttributes elementAttributes = elementObjectList.get(0).getXmlMapElementAttributes();
            List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
            elementAttributeList.forEach((elementAttribute) -> {
                typeSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
            });
        }
    }

    private void setExpandQueryData(XmlMapElementObjects elementObjects) {
        expandUp = elementObjects.getExpandUp();
        expandDown = elementObjects.getExpandDown();
        expandLevel = elementObjects.getExpandLevel();
        dataFetchLimit = elementObjects.getDataFetchLimit();
        busWhereClause = elementObjects.getExpandBusWhereClause();
        relWhereClause = elementObjects.getExpandRelWhereClause();
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public Map<String, String> getTypeSelectablesWithOutputName() {
        return typeSelectablesWithOutputName;
    }

    public List<String> getRelationshipNames() {
        return relationNames;
    }

    public Map<String, String> getRelationSelectablesWithOutputName() {
        return relationSelectablesWithOutputName;
    }

    public boolean getExpandUp() {
        return expandUp;
    }

    public boolean getExpandDown() {
        return expandDown;
    }

    public String getBusWhereClause() {
        return busWhereClause != null ? busWhereClause : "";
    }

    public String getRelWhereClause() {
        return relWhereClause != null ? relWhereClause : "";
    }

    public int getExpandLevel() {
        return expandLevel;
    }

    public int getDataFetchLimit() {
        return dataFetchLimit;
    }
}
