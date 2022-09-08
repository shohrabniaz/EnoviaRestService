package com.bjit.common.rest.app.service.export.product;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.EwcUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.List;
import matrix.db.Context;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.ServletContext;
import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Suvonkar Kudnu
 */
@Service
public class ProductExportServiceImpl implements ProductExportService {

    private Gson gson = new GsonBuilder().serializeNulls().create();
    @Autowired
    ServletContext servletContext;
    private static final Logger LOGGER = Logger.getLogger(ProductExportServiceImpl.class);
    EwcUtilities ewcUtilities = new EwcUtilities();
    private final String TYPE = "type";
    private final String NAME = "Name";
    private final String REVISION = "Revision";
    private final String PHYSICALID = "PhysicalId";
    private final String OWENR = "Owner";
    private final String Marketing_Text = "Marketing Text";

    /**
     * This function get product information by calling JPO method passing
     * context,name and get classification path and email and prepare json
     * response.
     *
     *
     * @param type
     * @param limit
     * @param context
     * @return jsonPrettyPrintString
     * @throws CustomException
     */
    @Override
    public String getJsonOutput(String type, int limit, String startDate, String endDate, Context context) throws CustomException {
        LOGGER.debug("Started generating json output");
        String finalJson;
        String jsonPrettyPrintString = null;
        JSONObject finalObject = new JSONObject();
        List<String> ownerList = new ArrayList<String>();
        List<String> emailList = new ArrayList<String>();
        List<String> objectIdList = new ArrayList<String>();
        List<String> classificationPathList = new ArrayList<String>();
        JSONArray mainJsonArray = new JSONArray();
        List<String> expandObjectByType;
        String initargs[] = {};
        HashMap<String, String> mapValue = new HashMap<String, String>();
        mapValue.put("Type", type);
        mapValue.put("Limit", String.valueOf(limit));
        try {
//            expandObjectByType = JPO.invoke(context,
//                    "UserInformation", initargs, "getObjectInformation",
//                    JPO.packArgs(mapValue), List.class);
            expandObjectByType = getProductByType(context, mapValue, startDate, endDate);
            for (int i = 0; i < expandObjectByType.size(); i++) {
                String expandObjectValue = expandObjectByType.get(i);
                JSONObject jsonobject = new JSONObject();
                expandObjectValue = expandObjectValue.substring(1, expandObjectValue.length() - 1);
                String[] keyValuePairs = expandObjectValue.split(",");
                Map<String, Object> map = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    if (entry.length == 2) {
                        if (entry[1].trim().equalsIgnoreCase("true") || entry[1].trim().equalsIgnoreCase("false")) {
                            map.put(entry[0].trim(), Boolean.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isDouble(entry[1].trim())) {
                            map.put(entry[0].trim(), Float.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isIntreger(entry[1].trim())) {
                            map.put(entry[0].trim(), Integer.valueOf(entry[1].trim()));
                        } else {
                            map.put(entry[0].trim(), entry[1].trim());
                        }
                    } else {
                        map.put(entry[0].trim(), "");
                    }
                }
                String owner = (String) map.get("Owner");
                String physicalId = (String) map.get("PhysicalId");
                ownerList.add(owner);
                objectIdList.add(physicalId);
                if (map.containsKey("Name")) {
                    jsonobject.put("name", map.get("Name"));
                } else {
                    LOGGER.debug("Product Name is not exists of this product type : " + " Product Type: " + type);
                    jsonobject.put("name", "");
                }
                if (map.containsKey("Marketing Text")) {
                    jsonobject.put("Marketing Text", map.get("Marketing Text"));
                } else {
                    LOGGER.debug("Marketing Text is not exits of product name and product type: " + " Product Name: " + map.get("name") + "Product Type: " + type);
                    jsonobject.put("Marketing Text", "");
                }
                if (map.containsKey("Owner")) {
                    jsonobject.put("owner", map.get("Owner"));
                } else {
                    jsonobject.put("owner", "");
                    LOGGER.debug("Owner is not exits of product name and product type: " + " Product Name: " + map.get("name") + "Product Type: " + type);
                }
                mainJsonArray.put(i, jsonobject);
            }
            if (expandObjectByType.size() == 0) {
                mainJsonArray.put("Object Not Found");
            }
            emailList = ewcUtilities.getUserEmail(context, ownerList);
            classificationPathList = ewcUtilities.getClassificationPath(context, objectIdList);
            String userDetails = null;
            String classificationDetails = null;
            for (int i = 0; i < emailList.size(); i++) {
                userDetails = emailList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userEmail = userDetails.split(",");
                String[] email = userEmail[2].split("=");
                if (email[1].equals("null")) {
                    jSONObject.put("email", "");
                    LOGGER.debug("Email is not exits of " + ownerList.get(i) + " this owner");
                } else {
                    jSONObject.put("email", email[1]);
                    LOGGER.debug("Email has found " + email[1] + " of this owner " + ownerList.get(i));
                }
                mainJsonArray.put(i, jSONObject);
            }
            for (int i = 0; i < classificationPathList.size(); i++) {
                classificationDetails = classificationPathList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userClassificationPath = classificationDetails.split(",");
                String[] classificationPath = userClassificationPath[0].split("=");
                if (classificationPath[1].equals("null")) {
                    jSONObject.put("classification path", "");
                    LOGGER.debug("classification path is not exits of " + objectIdList.get(i) + " this object Id");
                } else {
                    jSONObject.put("classification path", classificationPath[1]);
                    LOGGER.debug("classification path has found " + classificationPath[1] + " of this object id " + objectIdList.get(i));
                }
                mainJsonArray.put(i, jSONObject);
            }
        } catch (MatrixException ex) {
            LOGGER.error("Exception: " + ex + "Product Type: " + type);
//            throw new CustomException(ex.getMessage() + "Product Type: " + type);
        } catch (Exception ex) {
            LOGGER.error("Exception: " + ex + "Product Type: " + type);
//            throw new CustomException(ex.getMessage() + "Product Type: " + type);
        }
        finalObject.put("Results", mainJsonArray);
        return finalObject.toString();
    }

    /**
     * This function get product information by calling getObjectInformation
     * method passing context,name and get classification path and email and
     * prepare json response.
     *
     * @param name
     * @param context
     * @return jsonPrettyPrintString
     * @throws CustomException
     */
    @Override
    public String getJsonOutput(String type, String name, String revision, String startDate, String endDate, Context context) throws CustomException {
        LOGGER.debug("Started generating json output");
        String finalJson;
        String jsonPrettyPrintString = null;
        JSONObject finalObject = new JSONObject();
        List<String> ownerList = new ArrayList<String>();
        List<String> objectIdList = new ArrayList<String>();
        List<String> classificationPathList = new ArrayList<String>();
        List<String> emailList = new ArrayList<String>();
        JSONArray mainJsonArray = new JSONArray();
        List<String> expandProductByName;
        try {
            expandProductByName = getObjectInformation(context, type, name, revision, startDate, endDate);
            for (int i = 0; i < expandProductByName.size(); i++) {
                String expandObjectValue = expandProductByName.get(i);
                JSONObject jsonobject = new JSONObject();
                expandObjectValue = expandObjectValue.substring(1, expandObjectValue.length() - 1);
                String[] keyValuePairs = expandObjectValue.split(",");
                Map<String, Object> map = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    if (entry.length == 2) {
                        if (entry[1].trim().equalsIgnoreCase("true") || entry[1].trim().equalsIgnoreCase("false")) {
                            map.put(entry[0].trim(), Boolean.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isDouble(entry[1].trim())) {
                            map.put(entry[0].trim(), Float.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isIntreger(entry[1].trim())) {
                            map.put(entry[0].trim(), Integer.valueOf(entry[1].trim()));
                        } else {
                            map.put(entry[0].trim(), entry[1].trim());
                        }
                    } else {
                        map.put(entry[0].trim(), "");
                    }
                }
                String owner = (String) map.get("Owner");
                String physicalId = (String) map.get("PhysicalId");
                ownerList.add(owner);
                objectIdList.add(physicalId);
                if (map.containsKey("Name")) {
                    jsonobject.put("name", map.get("Name"));
                } else {
                    LOGGER.debug("Product Name is not exists of this product type : " + " Product name: " + name);
                    jsonobject.put("name", "");
                }
                if (map.containsKey("Marketing Text")) {
                    jsonobject.put("Marketing Text", map.get("Marketing Text"));
                } else {
                    LOGGER.debug("Marketing Text is not exits of product name : " + " Product Name: " + map.get("name"));
                    jsonobject.put("Marketing Text", "");
                }
                if (map.containsKey("Owner")) {
                    LOGGER.debug("Owner is not exits of product name : " + " Product Name: " + map.get("name"));
                    jsonobject.put("owner", map.get("Owner"));
                } else {
                    jsonobject.put("owner", "");
                }
                mainJsonArray.put(i, jsonobject);
            }
            if (expandProductByName.size() == 0) {
                mainJsonArray.put("Object Not Found");
            }
            emailList = ewcUtilities.getUserEmail(context, ownerList);
            classificationPathList = ewcUtilities.getClassificationPath(context, objectIdList);
            String userDetails = null;
            String classificationDetails = null;
            for (int i = 0; i < emailList.size(); i++) {
                userDetails = emailList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userEmail = userDetails.split(",");
                String[] email = userEmail[2].split("=");
                if (email[1].equals("null")) {
                    jSONObject.put("email", "");
                    LOGGER.debug("Email is not exits of " + ownerList.get(i) + " this owner");
                } else {
                    jSONObject.put("email", email[1]);
                    LOGGER.debug("Email has found " + email[1] + " of this owner " + ownerList.get(i));
                }
                mainJsonArray.put(i, jSONObject);
            }
            for (int i = 0; i < classificationPathList.size(); i++) {
                classificationDetails = classificationPathList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userClassificationPath = classificationDetails.split(",");
                String[] classificationPath = userClassificationPath[0].split("=");
                if (classificationPath[1].equals("null")) {
                    jSONObject.put("classification path", "");
                    LOGGER.debug("classification path is not exits of " + objectIdList.get(i) + " this object Id");
                } else {
                    jSONObject.put("classification path", classificationPath[1]);
                    LOGGER.debug("classification path has found " + classificationPath[1] + " of this object id " + objectIdList.get(i));
                }
                mainJsonArray.put(i, jSONObject);
            }
        } catch (MatrixException ex) {
            LOGGER.error("Exception: " + ex + "Product name: " + name);
            //throw new CustomException(ex.getMessage() + "Product name: " + name);
        } catch (Exception ex) {
            LOGGER.error("Exception: " + ex + "Product name: " + name);
            //throw new CustomException(ex.getMessage() + "Product name: " + name);
        }
        finalObject.put("Results", mainJsonArray);
        return finalObject.toString();
    }

    /**
     * This function get product information by passing context,name
     *
     * @param context
     * @param name
     * @return List<String> response
     * @throws Exception
     */
    public List<String> getObjectInformation(Context context, String type, String name, String revision, String startDate, String endDate, List<String> attributeList, List<String> propertyList, List<String> requestAttributeList) throws Exception {
        List<String> response = new ArrayList<String>();
        try {
            HashMap<String, String> object = new HashMap<String, String>();
            String objectInformation = getAttributeValues(context, type, name, revision, startDate, endDate, attributeList, propertyList);
            Scanner scLine = new Scanner(objectInformation);
            String lineobject;
            while (scLine.hasNext()) {
                lineobject = scLine.nextLine();
                String[] objectValue = lineobject.split("<>", -1);
                int count = 0;
                for (String value : objectValue) {
                    switch (count) {
                        case 0:
                            object.put(TYPE, value);
                            break;
                        case 1:
                            object.put(NAME, value);
                            break;
                        case 2:
                            object.put(REVISION, value);
                            break;
                        case 3:
                            object.put(PHYSICALID, value);
                            break;
                        case 4:
                            object.put(OWENR, value);
                            break;

                    }
                    count++;
                    if (count == 5) {
                        break;
                    }
                }

                for (String property : propertyList) {
                    if (count < objectValue.length) {
                        if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                            String target = objectValue[count];
                            if (target.contains(",")) {
                                target = target.replace(",", "^$");
                                objectValue[count] = target;
                            }
                            object.put(property, objectValue[count]);
                            count++;
                        }
                    }
                }

                for (String attribute : attributeList) {
                    if (count < objectValue.length) {
                        if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                            String target = objectValue[count];
                            if (target.contains(",")) {
                                target = target.replace(",", "^$");
                                objectValue[count] = target;
                            }
                            object.put(attribute, objectValue[count]);
                            count++;
                        }
                    }
                }

                response.add(object.toString());
            }

        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
        return response;
    }

    public List<String> getObjectInformation(Context context, String type, String name, String revision, String startDate, String endDate) throws Exception {
        List<String> response = new ArrayList<String>();
        try {
//            MQLCommand objMQL = new MQLCommand();
            StringBuilder queryBuilder = buildQueryForObject(type, name, revision, 0, startDate, endDate);
            queryBuilder.append(" select physicalId owner attribute[Marketing Text]  dump ");
            HashMap<String, String> object = new HashMap<String, String>();
            String objectInformation = executeMqlQuerys(queryBuilder.toString(), context);
            Scanner scLine = new Scanner(objectInformation);
            String lineobject;
            while (scLine.hasNext()) {
                lineobject = scLine.nextLine();
                String[] objectValue = lineobject.split(",", -1);
                int count = 1;
                for (String value : objectValue) {
                    switch (count) {
                        case 1:
                            object.put(TYPE, value);
                            break;
                        case 2:
                            object.put(NAME, value);
                            break;
                        case 3:
                            object.put(REVISION, value);
                            break;
                        case 4:
                            object.put(PHYSICALID, value);
                            break;
                        case 5:
                            object.put(OWENR, value);
                            break;
                        default:
                            object.put(Marketing_Text, value);
                            break;

                    }
                    count++;
                }
                response.add(object.toString());
            }

        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
        return response;
    }

    public String executeMqlQuerys(String mqlStatement, Context context) throws MatrixException {
        MQLCommand objMQL = new MQLCommand();
        objMQL.open(context);
        String objectInformation = MqlUtil.mqlCommand(context, objMQL, mqlStatement);
        return objectInformation;
    }

    public List<String> getProductByType(Context context, HashMap<String, String> mapvalue, String startDate, String endDate) throws Exception {

        HashMap<String, String> object = new HashMap<String, String>();
        List<String> response = new ArrayList<String>();
        StringBuilder mqlStatement = buildQueryForObject(mapvalue.get("Type"), null, null, Integer.parseInt(mapvalue.get("Limit")), startDate, endDate);
        mqlStatement.append(" select physicalId owner attribute[Marketing Text]  dump ");
        String objectInformation = executeMqlQuerys(mqlStatement.toString(), context);
        Scanner scLine = new Scanner(objectInformation);
        String lineobject;
        while (scLine.hasNext()) {
            lineobject = scLine.nextLine();
            String[] objectValue = lineobject.split(",");
            int count = 1;
            for (String value : objectValue) {
                switch (count) {
                    case 1:
                        object.put(TYPE, value);
                        break;
                    case 2:
                        object.put(NAME, value);
                        break;
                    case 3:
                        object.put(REVISION, value);
                        break;
                    case 4:
                        object.put(PHYSICALID, value);
                        break;
                    case 5:
                        object.put(OWENR, value);
                        break;
                    default:
                        object.put(Marketing_Text, value);
                        break;

                }
                count++;
            }
            response.add(object.toString());
        }
        return response;
    }

    /**
     * This function append '' with string
     *
     * @param name
     * @return string
     *
     */
    public static String quote(String name) {
        return new StringBuilder()
                .append('\'')
                .append(name)
                .append('\'')
                .toString();
    }

    @Override
    public String getJsonOutput(String type, String name, String revision, String startDate, String endDate, Context context, List<String> attributeList, List<String> propertyList, List<String> requestAttributeList) throws CustomException {
        LOGGER.debug("Started generating json output");
        String finalJson;
        String jsonPrettyPrintString = null;
        JSONObject finalObject = new JSONObject();
        List<String> ownerList = new ArrayList<String>();
        List<String> objectIdList = new ArrayList<String>();
        List<String> classificationPathList = new ArrayList<String>();
        List<String> emailList = new ArrayList<String>();
        JSONArray mainJsonArray = new JSONArray();
        List<String> expandProductByName;
        try {
            expandProductByName = getObjectInformation(context, type, name, revision, startDate, endDate, attributeList, propertyList, requestAttributeList);
            for (int i = 0; i < expandProductByName.size(); i++) {
                String expandObjectValue = expandProductByName.get(i);
                JSONObject jsonobject = new JSONObject();
                expandObjectValue = expandObjectValue.substring(1, expandObjectValue.length() - 1);
                String[] keyValuePairs = expandObjectValue.split(",");
                Map<String, Object> map = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    if (entry.length == 2) {
                        if ((entry[1].trim().equalsIgnoreCase("true") || entry[1].trim().equalsIgnoreCase("false"))&&!entry[0].trim().contains(NAME)&&!entry[0].trim().contains(REVISION)&&!entry[0].trim().contains(TYPE)) {
                            map.put(entry[0].trim(), Boolean.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isDouble(entry[1].trim())&&!entry[0].trim().contains(NAME)&&!entry[0].trim().contains(REVISION)&&!entry[0].trim().contains(TYPE)) {
                            map.put(entry[0].trim(), Float.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isIntreger(entry[1].trim())&&!entry[0].trim().contains(NAME)&&!entry[0].trim().contains(REVISION)&&!entry[0].trim().contains(TYPE)) {
                            map.put(entry[0].trim(), Integer.valueOf(entry[1].trim()));
                        } else {
                            map.put(entry[0].trim(), entry[1].trim());
                        }
                    } else {
                        map.put(entry[0].trim(), "");
                    }
                }
                String owner = (String) map.get("Owner");
                String physicalId = (String) map.get("PhysicalId");
                ownerList.add(owner);
                objectIdList.add(physicalId);
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equals("name")) {
                        if (map.containsKey("Name")) {
                            jsonobject.put(requestAttribute, map.get("Name"));
                        } else {
                            LOGGER.debug("Product Name is not exists");
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equals("type")) {
                        if (map.containsKey("type")) {
                            jsonobject.put(requestAttribute, map.get("type"));
                        } else {
                            LOGGER.debug("Product Type is not exists of this product name : " + " Product Name: " + name);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equals("revision")) {
                        if (map.containsKey("Revision")) {
                            jsonobject.put(requestAttribute, map.get("Revision"));
                        } else {
                            LOGGER.debug(" Revision is not exists of this product name : " + " Product Name: " + name);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equalsIgnoreCase("owner")) {
                        if (map.containsKey("Owner")) {
                            jsonobject.put(requestAttribute, map.get("Owner"));
                        } else {
                            LOGGER.debug("Owner is not exists of this product Name : " + " Product Name: " + name);
                            jsonobject.put(requestAttribute, "");
                        }
                    }

                    if (requestAttribute.equalsIgnoreCase("physicalid")) {
                        if (map.containsKey("PhysicalId")) {
                            jsonobject.put(requestAttribute, map.get("PhysicalId"));
                        } else {
                            LOGGER.debug("Physical Id is not exists of this product name : " + " Product Name: " + name);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equalsIgnoreCase("owner")) {
                        if (map.containsKey("Owner")) {
                            jsonobject.put(requestAttribute, map.get("Owner"));
                        } else {
                            jsonobject.put(requestAttribute, "");
                            LOGGER.debug("Owner is not exits of product name : " + " Product Name: " + map.get("name"));
                        }
                    }

                    for (String property : propertyList) {
                        if (quote(requestAttribute).equalsIgnoreCase(property)) {
                            if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                                if (map.get(property).toString().contains("^$")) {
                                    map.put(property, map.get(property).toString().replace("^$", ","));
                                }
                                if(requestAttribute.contains("description")){
                                    jsonobject.put("Description", map.get(property).toString().replace("^@^", "\n"));
                                }else {
                                jsonobject.put(requestAttribute, map.get(property));
                                }
                                LOGGER.debug("Request attribute is " + requestAttribute + "and property is " + map.get(property));
                                break;
                            }
                        }
                    }
                    for (String attribute : attributeList) {
                        if (quote("attribute[" + requestAttribute + "]").equals(attribute)) {
                            if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                                Attribute attr = null;
                                if (map.get("type").equals("Products")) {
                                    String query = "print bus " + (String) map.get("type") + " " + (String) map.get("Name") + " " + (String) map.get("Revision") + " select id dump";
                                    String objectId = MqlUtil.mqlCommand(context, query);
                                    BusinessObject businessObject = new BusinessObject(objectId);
                                    attr = businessObject.getAttributeValues(context, requestAttribute);
                                    if (attr.isMultiVal()) {
                                        List<String> multiValueList = attr.getValueList();
                                        String value = "";
                                        for (int iter = 0; iter < multiValueList.size(); iter++) {
                                            if (iter == multiValueList.size() - 1) {
                                                value = value + multiValueList.get(iter);
                                            } else {
                                                value = value + multiValueList.get(iter) + ",";
                                            }
                                        }
                                        map.replace(attribute, value);
//                            map.remove(attribute);
                                    }
                                }
                                if (map.get(attribute).toString().contains("^$")) {
                                    map.put(attribute, map.get(attribute).toString().replace("^$", ","));
                                }
                                if(attribute.contains("Quantity Unit of Measure")&& map.get(attribute).toString().length()!=0){
                                    map.put(attribute,map.get(attribute).toString().split(" ")[0]);
                                }
                                if(attribute.contains("Aton Version")){
                                    map.put(attribute,map.get(attribute).toString());
                                }
                                if(attribute.contains("Sales statistical group SSG") && map.get(attribute).toString().length()!=0){
                                    map.put(attribute,map.get(attribute).toString().substring(0, 6));
                                }
                                if(requestAttribute.contains("Level AUT")){
                                    jsonobject.put("Level", map.get(attribute).toString());
                                }
                                else if(requestAttribute.contains("Language AUT")){
                                  jsonobject.put("Language", map.get(attribute));
                                }
                                else if(requestAttribute.contains("License Pricing Type")){
                                  jsonobject.put("Pricing Type", map.get(attribute));
                                }else if(requestAttribute.contains("Cost editable")){
                                  jsonobject.put("Cost Editable", map.get(attribute));
                                }else{
                                  jsonobject.put(requestAttribute, map.get(attribute).toString().replace("^@^", "\n"));
                                }
                                LOGGER.debug("Request attribute is " + requestAttribute + "and attribute is " + map.get(attribute));
                                break;
                            }
                        }
                    }

                    mainJsonArray.put(i, jsonobject);
                }
            }
            if (expandProductByName.size() == 0) {
                mainJsonArray.put("Object Not Found");
            }
            emailList = ewcUtilities.getUserEmail(context, ownerList);
            classificationPathList = ewcUtilities.getClassificationPath(context, objectIdList);
            String userDetails = null;
            String classificationDetails = null;
            for (int i = 0; i < emailList.size(); i++) {
                userDetails = emailList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userEmail = userDetails.split(",");
                String[] email = userEmail[2].split("=");
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equalsIgnoreCase("email")) {
                        if (email[1].equals("null")) {
                            jSONObject.put("email", "");
                            LOGGER.debug("Email is not exits of " + ownerList.get(i) + " this owner");
                        } else {
                            jSONObject.put("email", email[1]);
                            LOGGER.debug("Email has found " + email[1] + " of this owner " + ownerList.get(i));
                        }
                        mainJsonArray.put(i, jSONObject);
                    }
                }
            }
            for (int i = 0; i < classificationPathList.size(); i++) {
                classificationDetails = classificationPathList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userClassificationPath = classificationDetails.split(",");
                String[] classificationPath = userClassificationPath[0].split("=");
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equalsIgnoreCase("classification Path")) {
                        if (classificationPath[1].equals("null")) {
                            jSONObject.put("classification path", "");
                            LOGGER.debug("classification path is not exits of " + objectIdList.get(i) + " this object Id");
                        } else {
                            jSONObject.put("classification path", classificationPath[1]);
                            LOGGER.debug("classification path has found " + classificationPath[1] + " of this object id " + objectIdList.get(i));
                        }
                    }
                }
                mainJsonArray.put(i, jSONObject);
            }
        } catch (MatrixException ex) {
            LOGGER.error("Exception: " + ex + "Product name: " + name);
            //throw new CustomException(ex.getMessage() + "Product name: " + name);
        } catch (Exception ex) {
            LOGGER.error("Exception: " + ex + "Product name: " + name);
            //throw new CustomException(ex.getMessage() + "Product name: " + name);
        }
        finalObject.put("Results", mainJsonArray);
        return finalObject.toString();
    }

    /**
     * This method is using to get CasSecurity Context
     *
     * @param Map<String, String> map
     * @param String value
     * @return String first security context of list
     */
    @Override
    public boolean hasValueInMap(Map<String, String> map, String value) {
        for (String o : map.keySet()) {
            if (map.get(o).equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public String getAttributeValues(Context context, String type, String name, String revision, String startDate, String endDate, List<String> attributeList, List<String> propertyList) throws Exception {

        boolean hasObjProperties = false;
        boolean hasObjAttributes = false;

        String objProperties = "";
        String objAttributes = "";

        for (String property : propertyList) {
            if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                objProperties += property + " ";
            }
            hasObjProperties = true;
        }

        for (String attribute : attributeList) {
            if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                objAttributes += attribute.replaceAll("\'", "") + " ";
                hasObjAttributes = true;
            }
        }
        if (!hasObjAttributes && !hasObjProperties) {
            return "";
        }
        StringBuilder queryBuilder = buildQueryForObject(type, name, revision, 0, startDate, endDate);
        queryBuilder.append(" select ").append("physicalid ").append("owner ");
        if (hasObjProperties) {
            queryBuilder.append(objProperties).append(" ");
        }
        if (hasObjAttributes) {
            queryBuilder.append(objAttributes);
        }

        queryBuilder.append(" dump <> recordseparator '~'");

        LOGGER.info("MQL Query: " + queryBuilder.toString());
        String queryResult = MqlUtil.mqlCommand(context, queryBuilder.toString());
        queryResult = fixNewLine(queryResult);
        LOGGER.info("Query Result : " + queryResult);
        return queryResult;

    }

    @Override
    public String getJsonOutput(String type, int limit, String startDate, String endDate, Context context, List<String> attributeList, List<String> propertyList, List<String> requestAttributeList) throws CustomException {
        LOGGER.debug("Started generating json output");
        String finalJson;
        String jsonPrettyPrintString = null;
        JSONObject finalObject = new JSONObject();
        List<String> ownerList = new ArrayList<String>();
        List<String> objectIdList = new ArrayList<String>();
        List<String> classificationPathList = new ArrayList<String>();
        List<String> emailList = new ArrayList<String>();
        JSONArray mainJsonArray = new JSONArray();
        List<String> expandProductByName;
        try {
            expandProductByName = getObjectInformationByType(type, limit, startDate, endDate, context, attributeList, propertyList);
            for (int i = 0; i < expandProductByName.size(); i++) {
                String expandObjectValue = expandProductByName.get(i);
                JSONObject jsonobject = new JSONObject();
                expandObjectValue = expandObjectValue.substring(1, expandObjectValue.length() - 1);
                String[] keyValuePairs = expandObjectValue.split(",");
                Map<String, Object> map = new HashMap<>();
                for (String pair : keyValuePairs) {
                    String[] entry = pair.split("=");
                    if (entry.length == 2) {
                        if (entry[1].trim().equalsIgnoreCase("true") || entry[1].trim().equalsIgnoreCase("false")) {
                            map.put(entry[0].trim(), Boolean.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isDouble(entry[1].trim())) {
                            map.put(entry[0].trim(), Float.valueOf(entry[1].trim()));
                        } else if (ewcUtilities.isIntreger(entry[1].trim())) {
                            map.put(entry[0].trim(), Integer.valueOf(entry[1].trim()));
                        } else {
                            map.put(entry[0].trim(), entry[1].trim());
                        }
                    } else {
                        map.put(entry[0].trim(), "");
                    }
                }
                String owner = (String) map.get("Owner");
                String physicalId = (String) map.get("PhysicalId");
                objectIdList.add(physicalId);
                ownerList.add(owner);
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equals("name")) {
                        if (map.containsKey("Name")) {
                            jsonobject.put(requestAttribute, map.get("Name"));
                        } else {
                            LOGGER.debug("Product Name is not exit of this product type : " + " Product Type: " + type);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equals("type")) {
                        if (map.containsKey("type")) {
                            jsonobject.put(requestAttribute, map.get("type"));
                        } else {
                            LOGGER.debug("Product Type is not exit ");
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equals("revision")) {
                        if (map.containsKey("Revision")) {
                            jsonobject.put(requestAttribute, map.get("Revision"));
                        } else {
                            LOGGER.debug("Revision is not exit of this product type: " + " Product Type: " + type);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equalsIgnoreCase("owner")) {
                        if (map.containsKey("Owner")) {
                            jsonobject.put(requestAttribute, map.get("Owner"));
                        } else {
                            LOGGER.debug("Owner is not exit of this product type : " + " Product Type: " + type);
                            jsonobject.put(requestAttribute, "");
                        }
                    }

                    if (requestAttribute.equalsIgnoreCase("physicalid")) {
                        if (map.containsKey("PhysicalId")) {
                            jsonobject.put(requestAttribute, map.get("PhysicalId"));
                        } else {
                            LOGGER.debug("Physical Id is not exit of this product type : " + " Product Type: " + type);
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    if (requestAttribute.equalsIgnoreCase("owner")) {
                        if (map.containsKey("Owner")) {
                            LOGGER.debug("Owner is not exits of product type : " + " Product Type: " + type);
                            jsonobject.put(requestAttribute, map.get("Owner"));
                        } else {
                            jsonobject.put(requestAttribute, "");
                        }
                    }
                    for (String property : propertyList) {
                        if (quote(requestAttribute).equalsIgnoreCase(property)) {
                            if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                                jsonobject.put(requestAttribute, map.get(property));
                                LOGGER.debug("Request attribute is " + requestAttribute + "and property is " + map.get(property));
                                break;
                            }
                        }
                    }
                    for (String attribute : attributeList) {
                        if (quote("attribute[" + requestAttribute + "]").equalsIgnoreCase(attribute)) {
                            if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                                jsonobject.put(requestAttribute, map.get(attribute));
                                LOGGER.debug("Request attribute is " + requestAttribute + "and attribute is " + map.get(attribute));
                                break;
                            }
                        }
                    }

                    mainJsonArray.put(i, jsonobject);
                }
            }
            if (expandProductByName.size() == 0) {
                mainJsonArray.put("Object Not Found");
            }
            emailList = ewcUtilities.getUserEmail(context, ownerList);
            classificationPathList = ewcUtilities.getClassificationPath(context, objectIdList);
            String userDetails = null;
            String classificationDetails = null;
            for (int i = 0; i < emailList.size(); i++) {
                userDetails = emailList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userEmail = userDetails.split(",");
                String[] email = userEmail[2].split("=");
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equalsIgnoreCase("email")) {
                        if (email[1].equals("null")) {
                            jSONObject.put("email", "");
                            LOGGER.debug("Email is not exits of " + ownerList.get(i) + " this owner");
                        } else {
                            jSONObject.put("email", email[1]);
                            LOGGER.debug("Email has found " + email[1] + " of this owner " + ownerList.get(i));
                        }
                        mainJsonArray.put(i, jSONObject);
                    }
                }
            }
            for (int i = 0; i < classificationPathList.size(); i++) {
                classificationDetails = classificationPathList.get(i);
                JSONObject jSONObject = mainJsonArray.getJSONObject(i);
                String[] userClassificationPath = classificationDetails.split(",");
                String[] classificationPath = userClassificationPath[0].split("=");
                for (String requestAttribute : requestAttributeList) {
                    if (requestAttribute.equalsIgnoreCase("classification Path")) {
                        if (classificationPath[1].equals("null")) {
                            jSONObject.put("classification path", "");
                            LOGGER.debug("classification path is not exits of " + objectIdList.get(i) + " this object Id");
                        } else {
                            jSONObject.put("classification path", classificationPath[1]);
                            LOGGER.debug("classification path has found " + classificationPath[1] + " of this object id " + objectIdList.get(i));
                        }
                    }
                }
                mainJsonArray.put(i, jSONObject);
            }
        } catch (MatrixException ex) {
            LOGGER.error("Exception: " + ex + "Product Type: " + type);
            //throw new CustomException(ex.getMessage() + "Product Type: " + type);
        } catch (Exception ex) {
            LOGGER.error("Exception: " + ex + "Product Type: " + type);
            //throw new CustomException(ex.getMessage() + "Product Type: " + type);
        }
        finalObject.put("Results",mainJsonArray);
        return finalObject.toString();
    }

    public List<String> getObjectInformationByType(String type, int limit, String startDate, String endDate, Context context, List<String> attributeList, List<String> propertyList) throws Exception {
        List<String> response = new ArrayList<String>();
        try {
            MQLCommand objMQL = new MQLCommand();
            HashMap<String, String> object = new HashMap<String, String>();
            String objectInformation = getAttributeValueByType(context, type, limit, startDate, endDate, attributeList, propertyList);
            Scanner scLine = new Scanner(objectInformation);
            String lineobject;
            while (scLine.hasNext()) {
                lineobject = scLine.nextLine();
                String[] objectValue = lineobject.split(",", -1);
                int count = 0;
                for (String value : objectValue) {
                    switch (count) {
                        case 0:
                            object.put(TYPE, value);
                            break;
                        case 1:
                            object.put(NAME, value);
                            break;
                        case 2:
                            object.put(REVISION, value);
                            break;
                        case 3:
                            object.put(PHYSICALID, value);
                            break;
                        case 4:
                            object.put(OWENR, value);
                            break;

                    }
                    count++;
                    if (count == 5) {
                        break;
                    }
                }

                for (String property : propertyList) {
                    if (count < objectValue.length) {
                        if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                            object.put(property, objectValue[count]);
                            count++;
                        }
                    }
                }

                for (String attribute : attributeList) {
                    if (count < objectValue.length) {
                        if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                            object.put(attribute, objectValue[count]);
                            count++;
                        }
                    }
                }

                response.add(object.toString());
            }

        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
        return response;
    }

    public String getAttributeValueByType(Context context, String type, int limit, String startDate, String endDate, List<String> attributeList, List<String> propertyList) throws Exception {

        boolean hasObjProperties = false;
        boolean hasObjAttributes = false;

        String objProperties = "";
        String objAttributes = "";

        for (String property : propertyList) {
            if (!property.equals("'name'") && !property.equals("'type'") && !property.equals("'revision'") && !property.equals("'owner'")) {
                objProperties += property + " ";
            }
            hasObjProperties = true;
        }

        for (String attribute : attributeList) {
            if (!attribute.equals("'attribute[email]'") && !attribute.equals("'attribute[classification path]'")) {
                objAttributes += attribute.replaceAll("\'", "") + " ";
                hasObjAttributes = true;
            }
        }

        if (!hasObjAttributes && !hasObjProperties) {
            return "";
        }
        StringBuilder queryBuilder = buildQueryForObject(type, null, null, limit, startDate, endDate);
        queryBuilder.append(" select ").append("physicalid ").append("owner ");
        if (hasObjProperties) {
            queryBuilder.append(objProperties).append(" ");
        }
        if (hasObjAttributes) {
            queryBuilder.append(objAttributes);
        }

        queryBuilder.append(" dump ");

        LOGGER.info("MQL Query: " + queryBuilder.toString());
        return executeMqlQuerys(queryBuilder.toString(), context);

    }

    /**
     * This method build query for product object that takes type, name,
     * revision,limit,startDate,endDate parameters
     *
     * @param type this is product object type
     * @param name this is product object name
     * @param revision this is product object revision
     * @param limit it indicate how much product object get. when limit value is
     * zero that contains all product object data.
     * @param startDate Those product create or update date after start date
     * that product object export.
     * @param endDate Those product create or update date before end date that
     * product object export.
     * @return queryBuilder It is StringBuilder type that return build query for
     * product object
     */
    public StringBuilder buildQueryForObject(String type, String name, String revision, int limit, String startDate, String endDate) {
        StringBuilder queryBuilder = new StringBuilder();
        if (type == null || type == "") {
            type = "*";
        }
        if (name == null || name == "") {
            name = "*";
        }
        if (revision == null || revision == "") {
            revision = "*";
        }
        if (startDate == null || startDate == "") {
            startDate = "11/10/1900 11:11:11 AM";
        }
        if (endDate == null || endDate == "") {
            endDate = "11/11/2100 11:11:11 AM";
        }
        queryBuilder.append("temp query bus ").append(quote(type)).append(quote(name)).append(quote(revision)).append("limit ").append(String.valueOf(limit));
        queryBuilder.append(" where ");
        queryBuilder.append(quote("(originated >= \"" + startDate + "\" && " + " originated <= \"" + endDate + "\")" + " || " + "(modified >= \"" + startDate + "\" && " + " modified <= \"" + endDate + "\")"));
        return queryBuilder;
    }
    public String fixNewLine(String query) {
        String newString=query.replaceAll("[\r\n]+", "^@^");
        String[] arr = newString.split("~");
        String finalQuery = "";
        for(String s: arr){
            finalQuery = finalQuery + s + "\n";
        }       
        finalQuery = finalQuery.replaceAll("^[\n\r]", "").replaceAll("[\n\r]$", "");
        return finalQuery ;
    }
    }
