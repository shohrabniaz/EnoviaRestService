/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import com.bjit.ewc18x.model.ExpandObjectForm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kayum-603
 */
public class EwcUtilities {

    private static final Logger LOGGER = Logger.getLogger(EwcUtilities.class);

    public List<String> getListWithInvertedComma(List<String> list) {
        list.stream().collect(Collectors.joining("','", "'", "'"));
        return list;
    }

    public String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject fixJsonKey(Object json) {
        JSONObject jsonObject = null;
        if (json instanceof JSONObject) {
            //System.out.println("Object");
            jsonObject = (JSONObject) json;
            List<String> keyList = new LinkedList<>(jsonObject.keySet());
            for (String key : keyList) {
                if (!key.matches(".*[\\s\t\n]+.*")) { // key without any space
                    Object value = jsonObject.get(key);
                    fixJsonKey(value);
                    continue;
                }

                Object value = jsonObject.remove(key);
                String newKey = key.replaceAll("[\\s\t\n]", "_");

                fixJsonKey(value);

                jsonObject.accumulate(newKey, value);

            }
        } else if (json instanceof JSONArray) {
            System.out.println("Array");
            for (Object aJsonArray : (JSONArray) json) {
                fixJsonKey(aJsonArray);
            }
        }
        return jsonObject;
    }

    public String fixJsonKey(String json) {
        LinkedTreeMap<String, Object> finalLinkedHashMap;
        LinkedTreeMap<String, Object> linkedHashMap = new Gson().fromJson(json, LinkedTreeMap.class);
        finalLinkedHashMap = getFinalMapWithKeyFixed(linkedHashMap);
        System.out.println("Final map: " + finalLinkedHashMap);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String jsonObjectString = gson.toJson(finalLinkedHashMap);
        return jsonObjectString;
    }

    public LinkedTreeMap<String, Object> getFinalMapWithKeyFixed(Object object) {
        LinkedTreeMap<String, Object> linkedHashMap = null;
        if (object instanceof Map) {
            System.out.println("Map");
            linkedHashMap = (LinkedTreeMap<String, Object>) object;
            System.out.println("Linked map: " + linkedHashMap);
            List<String> keyList = new LinkedList<>(linkedHashMap.keySet());
            for (String key : keyList) {
                System.out.println("Key: " + key);
                if (!key.matches(".*[\\s\t\n]+.*")) { // key without any space                   
                    Object value = linkedHashMap.get(key);
                    System.out.println("value: " + value);
                    getFinalMapWithKeyFixed(value); // check for array, object inside object
                    continue;
                }
                Object value = linkedHashMap.remove(key);
                String newKey = key.replaceAll("[\\s\t\n]", "_");
                System.out.println("New key: " + newKey);
                System.out.println("value: " + value);
                getFinalMapWithKeyFixed(value); // check for array, object inside a key
                linkedHashMap.put(newKey, value);
            }
        } else if (object instanceof List) {
            System.out.println("Array");
            for (Object list : (List) object) {
                getFinalMapWithKeyFixed(list); // check for object inside a array
            }
        }
        return linkedHashMap;
    }

    public String getTicket(String host, ExpandObjectForm expandObjectForm) throws CustomException {
        LOGGER.debug("Started to getting ticket");
        try {
            String ticket = getTicket(host, expandObjectForm.getUserID(), expandObjectForm.getPassword());
            if (ticket != null) {
                return ticket;
            }
        } catch (CustomException ex) {
            LOGGER.error("Exception occured while getting ticket: " + ex);
            throw new CustomException(ex.getMessage());
        }
        return null;
    }

    public String getTicket(String host, String userName, String userPass) throws CustomException {
        LOGGER.debug("Started to getting ticket");
        try {
            LOGGER.debug("User name: " + userName);
            LOGGER.debug("User pass: " + userPass);
            String[] passport = Passport.getTicket(host, userName, userPass).split(";");
            String ticket = passport[0];
            String jsessionId = passport[1];
            if (ticket != null) {
                return ticket;
            }
        } catch (Exception ex) {
            LOGGER.error("Exception occured while getting ticket: " + ex);
            throw new CustomException(ex.getMessage());
        }
        return null;
    }

    public ResponseEntity<String> getPostRestResponse(String uri, StringBuilder requestJsonBuilder, HttpHeaders headers) throws CustomException {
        LOGGER.debug("Started to getting rest service response");
        ResponseEntity<String> response;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(requestJsonBuilder.toString(), headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                    .queryParam("tenant", "OnPremise")
                    .queryParam("xrequestedwith", "xmlhttprequest");

            System.out.println("URL: " + builder.toUriString());
            response = restTemplate.postForEntity(builder.toUriString(), entity, String.class);
            return response;
        } catch (RestClientException ex) {
            LOGGER.error("Exception occured while getting rest response for: " + ex);
            throw new CustomException(ex.getMessage());
        }
    }

    public StringBuilder getExpandObjectResquestJson(String physicalId, List<String> objectParamList, ExpandObjectForm expandObjectForm, List<String> objectRelAttrList) {
        StringBuilder requestJsonBuilder = new StringBuilder();
        requestJsonBuilder.append("{");
        requestJsonBuilder.append("root_physical_id:").append(physicalId).append(",");
        requestJsonBuilder.append("type_filter:").append(expandObjectForm.getSelectedTypeList()).append(",");
        requestJsonBuilder.append("rel_filter:[").append(expandObjectForm.getRelationshipPattern()).append("],");
        //requestJsonBuilder.append("select_level:").append("10").append(",");
        requestJsonBuilder.append("object_selects:").append(objectParamList).append(",");
        requestJsonBuilder.append("relationship_selects:").append(objectRelAttrList).append(",");
        requestJsonBuilder.append("navigate_from_rel:").append("\"").append(expandObjectForm.getGetFrom()).append("\"").append(",");
        requestJsonBuilder.append("navigate_to_rel:").append("\"").append(expandObjectForm.getGetTo()).append("\"").append(",");
        requestJsonBuilder.append("level:").append("\"").append(expandObjectForm.getRecursionLevel()).append("\"").append(",");
        requestJsonBuilder.append("}");
        return requestJsonBuilder;

    }

    public StringBuilder getRootObjectResquestJson(String physicalId, List<String> objectParamList) {
        StringBuilder requestJsonBuilder = new StringBuilder();
        requestJsonBuilder.append("{");
        requestJsonBuilder.append("\"").append("data").append("\"").append(":");
        requestJsonBuilder.append("[{").append("\"").append("physicalid").append("\"").append(":").append("\"").append(physicalId).append("\"").append("}]").append(",");
        requestJsonBuilder.append("\"").append("attributes").append("\"").append(":").append(objectParamList);
        requestJsonBuilder.append("}");
        return requestJsonBuilder;

    }

    public StringBuilder getUpdateObjectResquestJson(String physicalId, Map<String, String> attributeMap) {
        StringBuilder requestJsonBuilder = new StringBuilder();
        requestJsonBuilder.append("{");
        requestJsonBuilder.append("\"").append("obj_physicalid").append("\"").append(":").append("\"").append(physicalId).append("\"").append(",");
        requestJsonBuilder.append("\"").append("object_selects").append("\"").append(":");
        requestJsonBuilder.append("{");
        for (Map.Entry<String, String> entrySet : attributeMap.entrySet()) {
            LOGGER.debug("service=ImportStructure;msg=" + "debugging single attribute: Name-" + entrySet.getKey() + "Value" + entrySet.getValue());
            String currentAttr = entrySet.getKey().trim();
            requestJsonBuilder.append("\"").append(currentAttr).append("\"").append(":").append("\"").append(entrySet.getValue().trim()).append("\"").append(",");

        }
        requestJsonBuilder.append("}");
        requestJsonBuilder.append("}");
        return requestJsonBuilder;

    }

    public HttpHeaders getHttpRequestHeaders(String securityContext) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.6,en;q=0.4");
        headers.add("Accept-Encoding", "gzip, deflate, sdch");
        headers.add("SecurityContext", "ctx::VPLMProjectLeader.Company Name.Common Space");
        return headers;

    }

    public String getModifiedRootObjectJson(ExpandObjectForm expandObjectForm, String json) throws MatrixException {
        LOGGER.debug("Customizing root object: " + json);
//        JSONObject modifiedObject;
//        JSONObject object = new JSONObject(json);
//        JSONArray jsonArray = object.getJSONArray("results");
//        Object rootObject = jsonArray.get(0);
//        LOGGER.debug("Item is: " + rootObject);
//        modifiedObject = new JSONObject(rootObject.toString());
        //String rootObjectString = json.substring(json.indexOf("[")+1, json.indexOf("]"));
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String rootObjectString = jsonObject.getAsJsonArray("results").get(0).toString();
        LOGGER.debug("rootObjectString is : " + rootObjectString);
        LinkedHashMap<String, Object> linkedHashMap = new Gson().fromJson(rootObjectString, LinkedHashMap.class);
        LOGGER.debug("Linked item is: " + linkedHashMap);
        com.matrixone.json.JSONObject responseJsonObject = new com.matrixone.json.JSONObject(linkedHashMap);
        if (expandObjectForm.getSelectedItem().contains("Depth")) {
            responseJsonObject.put("depth", "0");
        }
        LOGGER.debug("Customized Json: " + responseJsonObject.toString());
        return responseJsonObject.toString();
    }

    public String getFinalJsonWithRootObjectAdded(String rootObject, String responseString) {
//        JSONObject mainJsonObject = new JSONObject(responseString);
//        JSONObject rootJsonObject = new JSONObject(rootObject);      
//        JSONObject finalObject = new JSONObject();
//        JSONArray mainJsonArray = mainJsonObject.getJSONArray("results");       
//        mainJsonArray = insert(0,rootJsonObject,mainJsonArray);        
//        finalObject.put("results", mainJsonArray);
//        return finalObject.toString();
        Map<String, Object> finalJsonMap = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonElement jsonElement = gson.fromJson(responseString, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String mainResponseString = jsonObject.getAsJsonArray("results").toString();
        // String mainResponseString = responseString.substring(responseString.indexOf("["), responseString.indexOf("]") + 1);
        LinkedList<Object> linkedList = new Gson().fromJson(mainResponseString, LinkedList.class);
        LinkedHashMap<String, Object> linkedHashMap = new Gson().fromJson(rootObject, LinkedHashMap.class);
        linkedList.add(0, linkedHashMap);
        finalJsonMap.put("results", linkedList);
        String json = gson.toJson(finalJsonMap);
        LOGGER.debug("response Json Object string: " + json);
        return json;
    }

    public static JSONArray insert(int index, JSONObject val, JSONArray currentArray) {
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < index; i++) {
            newArray.put(currentArray.get(i));
        }
        newArray.put(val);

        for (int i = index; i < currentArray.length(); i++) {
            newArray.put(currentArray.get(i));
        }
        return newArray;
    }

    public String getFinalJsonWithClassiificationPath(Context context, String jsonString) throws CustomException {
        JSONObject mainJsonObject = new JSONObject(jsonString);
        JSONObject finalObject = new JSONObject();
        try {
            JSONArray mainJsonArray = mainJsonObject.getJSONArray("results");
            for (int i = 0; i < mainJsonArray.length(); i++) {
                JSONObject jsonObject = mainJsonArray.getJSONObject(i);
                String physicalid = (String) jsonObject.get("physicalid");
                String classPath = getClassificationPath(context, physicalid);
                jsonObject.remove("physicalid");
                jsonObject.put("Classification Path", classPath);
                mainJsonArray.put(i, jsonObject);
            }
            finalObject.put("results", mainJsonArray);
        } catch (Exception ex) {
            LOGGER.error("Exception occured while getting classification path: " + ex);
            throw new CustomException(ex.getMessage());
        }
        return finalObject.toString();
    }

    public String getClassificationPath(Context context, String objectId) throws Exception {
        try {
            LOGGER.info("Started getting classification path");
            //Call the JPO method to get the owner portfolio list
            LOGGER.debug("Call the JPO(emxClassificationPath) method(getClassificationPathsHTML) to get the Classification Path");
            LOGGER.debug("Provided object Id: " + objectId);
            String[] constructor = {null};
            //String objectId = "44480.35242.31744.60621";
            HashMap params = new HashMap();
            params.put("objectId", objectId);
            String emxClassificationPath = (String) JPO.invoke(context,
                    "emxClassificationPath",
                    constructor,
                    "getClassificationPathsHTML",
                    JPO.packArgs(params),
                    String.class
            );

            if (emxClassificationPath != null) {
                LOGGER.debug("Found Classification path:" + emxClassificationPath);
                LOGGER.info("Done getting classification path");
                return emxClassificationPath;
            }

            return "";
        } catch (Exception ex) {
            LOGGER.debug("Exception class is: " + ex.getClass());
            LOGGER.error("Error occured: " + ex);
            // return null;
            throw new Exception("Error while getting classification path." + ex);
        }

    }

    public List<String> getClassificationPath(Context context, List<String> objectId) throws Exception {
        try {
            LOGGER.info("Started getting classification path");
            //Call the JPO method to get the owner portfolio list
            LOGGER.debug("Call the JPO(emxClassificationPath) method(getClassificationPathsHTML) to get the Classification Path");
            LOGGER.debug("Provided object Id: " + objectId);
            String[] constructor = {null};
//            //String objectId = "44480.35242.31744.60621";
//            HashMap params = new HashMap();
//            params.put("objectId", objectId);
            List<String> emxClassificationPath = JPO.invoke(context,
                    "emxClassificationPath",
                    constructor,
                    "getClassificationPath",
                    JPO.packArgs(objectId),
                    List.class
            );

            if (emxClassificationPath.size() != 0) {
                LOGGER.debug("Found Classification path:" + emxClassificationPath);
                LOGGER.info("Done getting classification path");
                return emxClassificationPath;
            }

        } catch (Exception ex) {
            LOGGER.debug("Exception class is: " + ex.getClass());
            LOGGER.error("Error occured: " + ex);
            // return null;
            throw new Exception("Error while getting classification path." + ex);
        }
        return null;

    }

    public List<String> getObjectAttributes(Context context, String objectId) {
        try {
            List<String> attributesList = new ArrayList<>();
            LOGGER.debug("Getting Attributes for object: " + objectId);
            BusinessObject domainObject = new BusinessObject(objectId);
            //BusinessObjectAttributes attributes = domainObject.getAttributes(context);
            AttributeList attributeList = domainObject.getAttributeValues(context);
            if (attributeList != null && !attributeList.isEmpty()) {
                for (int i = 0; i < attributeList.size(); i++) {
                    Attribute attribute = attributeList.getElement(i);
                    attributesList.add(attribute.getName());
                }

            }

            if (attributesList != null && !attributesList.isEmpty()) {
                LOGGER.debug("Attributes: " + attributesList.toString());
                return attributesList;
            }
        } catch (MatrixException ex) {
            LOGGER.error("Exception occured while getting attribute list: " + ex.getMessage());
            return null;
        }
        return null;
    }

    public Map<String, String> getAttributeMappedToMatrix() {
        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("level", "depth");
        //attributeMap.put("current", "state");
        return attributeMap;
    }

    public List<String> getPropertyListWithApropriateNames(List<String> itemList) {
        Map<String, String> attributeMappedToMatrix = getAttributeMappedToMatrix();
        for (String notUpdatableItem : itemList) {
            if (attributeMappedToMatrix.containsKey(notUpdatableItem)) {
                itemList.set(itemList.indexOf(notUpdatableItem), attributeMappedToMatrix.get(notUpdatableItem));
            }
        }
        return itemList;
    }

    /**
     * This method get user email calling JPO
     *
     * @param context
     * @param user
     * @return string email
     */
    public String getUserEmail(Context context, String user) throws Exception {
        try {
            LOGGER.info("Started getting user email");
            LOGGER.debug("Call the JPO(UserInformation) method(getUserEmail) to get the user email");
            LOGGER.debug("Provided User: " + user);
            List<String> users = new ArrayList<>();
            String initargs[] = {};
            List<String> resposne;
            users.add(user);
            resposne = JPO.invoke(context,
                    "UserInformation", initargs, "getUserEmail",
                    JPO.packArgs(users), List.class);
            String userDetails = null;
            for (int i = 0; i < resposne.size(); i++) {
                userDetails = resposne.get(i);

            }
            String[] userEmail = userDetails.split(",");
            String[] email = userEmail[2].split("=");

            if (email.length != 0) {
                LOGGER.debug("Found User Email:" + email[1]);
                LOGGER.info("Done getting User Email");
                return email[1];
            }

            return "";
        } catch (Exception ex) {
            LOGGER.debug("Exception class is: " + ex.getClass());
            LOGGER.error("Error occured: " + ex);
            // return null;
            throw new Exception("Error while get user Email." + ex);
        }

    }

    /**
     * This method get user email calling JPO
     *
     * @param context
     * @param user
     * @return string email
     */
    public List<String> getUserEmail(Context context, List<String> ownerList) throws Exception {
        try {
            LOGGER.info("Started getting user email");
            LOGGER.debug("Call the JPO(UserInformation) method(getUserEmail) to get the user email");
//            LOGGER.debug("Provided User: " + user);
//            List<String> users = new ArrayList<>();
            String initargs[] = {};
            List<String> resposne;
//            users.add(user);
            resposne = JPO.invoke(context,
                    "UserInformation", initargs, "getUserEmail",
                    JPO.packArgs(ownerList), List.class);
            return resposne;
        } catch (Exception ex) {
            LOGGER.debug("Exception class is: " + ex.getClass());
            LOGGER.error("Error occured: " + ex);
            // return null;
            throw new Exception("Error while get user Email." + ex);
        }

    }

    public String jsonArrayToString(JSONArray JSONArray) {
        return JSONArray.toString().replace("\\/", "/");
    }

    /**
     * This function is used to remove scape character before Double
     * quotation(") and new line(\n) which have been added after converting a
     * json string to org.json.JSONObject
     *
     * @param jSONObject
     * @return String
     */
    public String jsonObjectToString(org.json.JSONObject jSONObject) {
        return jSONObject.toString().replace("\\\"", "\"").replace("\\\\n", "\\n");
    }

    /**
     * Replace the last occurrence of any string with provided string
     *
     * @param string the original string that needed to be modified
     * @param substring the last occurrence of the string needed to be replaced
     * @param replacement the new string that will be used in replacement
     * @return
     */
    public String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + replacement
                + string.substring(index + substring.length());
    }
    
    public  boolean isDouble(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            float floatNum = Float.parseFloat(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public  boolean isIntreger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int intNum = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
