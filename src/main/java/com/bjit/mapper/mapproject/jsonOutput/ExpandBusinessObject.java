/**
 *
 */
package com.bjit.mapper.mapproject.jsonOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bjit.mapper.mapproject.expand.ExpandObjectTest;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelationsTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;

import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectAttributes;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipList;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 * @author TAREQ SEFATI
 *
 */
public class ExpandBusinessObject {

    private ExpandObjectTest expandObject;
    private ObjectTypesAndRelationsTest typesAndRelations;
    private Map<String, String> jsonObjectMap = new HashMap<>();
    private Map<String, String> typeSelectablesWithOutputName = new LinkedHashMap<>();
    private Map<String, String> relationSelectablesWithOutputName = new LinkedHashMap<>();
    private List<String> typeSelectables;
    private List<String> relationSelectables;

    private List<Map<String, String>> resultsMap;
    private Map<String, String> singleResult;

    private Map<String, Object> singleResultCpx;
    //private List<Map<String, Object>> resultsMapCpx;

    private Map<String, Object> singleResultBrochureItem;
    private List<Map<String, Object>> resultsMapBrochureItem;
    private Map<String, Object> singleResultBrochure;
    private List<Map<String, Object>> resultsMapBrochure;
    private static final Map<Object, Object> rootItemParams = new LinkedHashMap<>();

    private boolean foundData = false;
    private static final Logger JSON_OUTPUT_LOGGER = Logger.getLogger(ExpandBusinessObject.class);

    public ExpandBusinessObject() throws FileNotFoundException, Exception {
        try {
            typesAndRelations = new ObjectTypesAndRelationsTest();
            initializeMapsAndLists();

        } catch (FileNotFoundException ex) {
            JSON_OUTPUT_LOGGER.error(ex);
            throw ex;
        } catch (Exception ex) {
            JSON_OUTPUT_LOGGER.error(ex);
            throw ex;
        }
    }

    public ExpandBusinessObject(String absoluteMapperFileDirectory) throws FileNotFoundException, Exception {
        try {
            typesAndRelations = new ObjectTypesAndRelationsTest(absoluteMapperFileDirectory);
            initializeMapsAndLists();
//            typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
//            relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        } catch (FileNotFoundException ex) {
            JSON_OUTPUT_LOGGER.error(ex);
            throw ex;
        } catch (Exception ex) {
            JSON_OUTPUT_LOGGER.error(ex);
            throw ex;
        }
    }

    private void initializeMapsAndLists() {
        singleResult = new LinkedHashMap<>();
        singleResultCpx = new LinkedHashMap<>();
        singleResultBrochureItem = new LinkedHashMap<>();
        singleResultBrochure = new LinkedHashMap<>();
        resultsMap = new ArrayList<>();
        //resultsMapCpx = new ArrayList<>();
        resultsMapBrochureItem = new ArrayList<>();
        resultsMapBrochure = new ArrayList<>();

        typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
    }

    public void showJsonData(Set<String> ids, Context context) throws MatrixException {
        BusinessObject businessObject;
        for (String id : ids) {
            businessObject = new BusinessObject(id);
            businessObject.open(context);

            BusinessObjectAttributes attributes = businessObject.getAttributes(context);
            AttributeList attributeList = attributes.getAttributes();
            AttributeItr attrItr = new AttributeItr(attributeList);
            while (attrItr.next()) {
                Attribute attr = attrItr.obj();
                // System.out.println(attr.getName()+" = "+attr.getValue());
                if (jsonObjectMap.containsKey(attr.getName())) {
                    System.out.println(
                            "\"" + jsonObjectMap.get(attr.getName()) + "\" : " + "\"" + attr.getValue() + "\"");
                }
            }
            RelationshipList rList = businessObject.getAllRelationship(context);
            System.out.println("Relation size: " + rList.size());
//            System.out.println();
        }
    }

    //Normal format json output
    public String prepareJsonFromExpandObject(Context context, BusinessObject businessObject) throws MatrixException {
        expandObject = new ExpandObjectTest();
        ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, typesAndRelations);

        BusinessObjectWithSelect root = expandResult.getRootWithSelect();
        populateResultsMap(root, new RelationshipWithSelect(), typeSelectablesWithOutputName, relationSelectablesWithOutputName, true);

        RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(
                expandResult.getRelationships());
        while (relationshipWithSelectItr.next()) {
            RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
            BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();

            populateResultsMap(businessObjectWithSelect, relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName, false);

        }

        Results results = new Results();
        results.setResults(resultsMap);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(results);
        JSON_OUTPUT_LOGGER.debug(json);
        return json;
    }

    //Normal format json output
    public Results getExportResultFromByExpandingBusinessObject(Context context, BusinessObject businessObject) throws MatrixException {
        expandObject = new ExpandObjectTest();
        ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, typesAndRelations);

        BusinessObjectWithSelect root = expandResult.getRootWithSelect();
        populateResultsMap(root, new RelationshipWithSelect(), typeSelectablesWithOutputName, relationSelectablesWithOutputName, true);

        RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(
                expandResult.getRelationships());
        while (relationshipWithSelectItr.next()) {
            RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
            BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();

            populateResultsMap(businessObjectWithSelect, relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName, false);

        }

        Results results = new Results();
        results.setResults(resultsMap);
        return results;
    }

    //Complex format json output
//    public void prepareJsonFromExpandObjectComplexFormat(Context context, BusinessObject businessObject) throws MatrixException {
//        expandObject = new ExpandObject();
//        ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, typesAndRelations);
//
//        BusinessObjectWithSelect root = expandResult.getRootWithSelect();
//
//        populateResultsMapComplexFormat(root, new RelationshipWithSelect(), typeSelectablesWithOutputName, relationSelectablesWithOutputName);
//
//        RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(
//                expandResult.getRelationships());
//        while (relationshipWithSelectItr.next()) {
//            RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
//            BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();
//
//            populateResultsMapComplexFormat(businessObjectWithSelect, relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName);
//
//        }
//
//        ResultsComplexFormat resultComplex = new ResultsComplexFormat();
//        resultComplex.setResults(resultsMapCpx);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(resultComplex);
//        JSON_OUTPUT_LOGGER.debug(json);
//
//    }

    private void populateResultsMap(BusinessObjectWithSelect businessObjectWithSelect,
            RelationshipWithSelect relationshipWithSelect, Map<String, String> typeSelectablesWithOutputName,
            Map<String, String> relationSelectablesWithOutputName, boolean isRoot) {

        singleResult = new LinkedHashMap<>();
        typeSelectablesWithOutputName.forEach((key, value) -> {
            String str = businessObjectWithSelect.getSelectData(key);
            if (str.isEmpty()) {
                singleResult.put(value, "");
            } else {
                singleResult.put(value, str);
            }
        });

        if (isRoot) {
            singleResult.put("Rel Name", "");
            singleResult.put("Position", "");
            singleResult.put("Qty", "");
        } else {
            relationSelectablesWithOutputName.forEach((key, value) -> {
                String str = relationshipWithSelect.getSelectData(key);
                if (str.isEmpty()) {
                    singleResult.put(value, "");
                } else {
                    if (value.equals("Name")) {
                        singleResult.put("Rel " + value, str);
                    } else {
                        singleResult.put(value, str);
                    }
                }
            });
        }

/*

//        if (isRoot) {
//            rootItemParams.clear();
//            rootItemParams.put("rootObjectName", businessObjectWithSelect.getSelectData("name"));
//            rootItemParams.put("rootObjectRev", businessObjectWithSelect.getSelectData("revision"));
//            rootItemParams.put("rootObjectDrNum", businessObjectWithSelect.getSelectData("attribute[MBOM_MBOMReference.MBOM_Drawing_Number]"));
//            rootItemParams.put("rootObjectDes", businessObjectWithSelect.getSelectData("attribute[PLMEntity.V_description]"));
//            rootItemParams.put("rootObjectState", businessObjectWithSelect.getSelectData("current"));
//            //logger.debug("Current State: " + businessObjectWithSelect.getSelectData("revision"));
//        }

*/
        resultsMap.add(singleResult);
    }

    public Map<Object, Object> getRootItemParams() {
        JSON_OUTPUT_LOGGER.debug("Root Object parameters: " + rootItemParams.toString());
        return rootItemParams;
    }

//    private void populateResultsMapComplexFormat(BusinessObjectWithSelect businessObjectWithSelect,
//            RelationshipWithSelect relationshipWithSelect, Map<String, String> typeSelectablesWithOutputName, Map<String, String> relationSelectablesWithOutputName) {
//
//        singleResultCpx = new LinkedHashMap<>();
//        typeSelectablesWithOutputName.forEach((key, value) -> {
//            String str = businessObjectWithSelect.getSelectData(key);
//            if (str.isEmpty()) {
//                singleResultCpx.put(value, "");
//            } else {
//                singleResultCpx.put(value, str);
//            }
//
//        });
//
//        Map<String, String> relMap = new LinkedHashMap<>();
//        relationSelectablesWithOutputName.forEach((key, value) -> {
//            String str = relationshipWithSelect.getSelectData(key);
//            if (str.isEmpty()) {
//                relMap.put(value, "");
//            } else {
//                relMap.put(value, str);
//                foundData = true;
//            }
//
//        });
//        if (foundData) {
//            singleResultCpx.put("rel", relMap);
//        } else {
//            singleResultCpx.put("rel", new LinkedHashMap<>());
//        }
//        resultsMapCpx.add(singleResultCpx);
//    }
}
