/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.service;

import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.model.ObjectIdForm;
import com.bjit.ewc18x.model.UpdateObjectForm;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ewc18x.utils.EnoviaWebserviceClient;
import com.bjit.ewc18x.utils.EwcUtilities;
import com.bjit.ewc18x.utils.MqlQueries;
import com.matrixone.json.JSONArray;
import com.matrixone.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Kayum-603
 */
@Service
public class UpdateObjectServiceImpl implements UpdateObjectService{
private static final Logger LOGGER = Logger.getLogger(UpdateObjectServiceImpl.class);
private MqlQueries mqlQueries = new MqlQueries();
EwcUtilities ewcUtilities = new EwcUtilities();
    @Autowired
    ServletContext servletContext;
@Override
    public UpdateObjectForm populateEnvironment(UpdateObjectForm updateObjectForm) throws CustomException {
        InputStream is = null;
        String fileName = "input.xml";
        is = PropertyReader.class.getClassLoader().getResourceAsStream(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        LOGGER.debug("service=ImportStructure;msg=" + "Populating Environment::");
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element element = doc.getDocumentElement();

            String iContextURI = element.getElementsByTagName("iContextURI").item(0).getTextContent();
            LOGGER.debug("service=ImportStructure;msg=" + "Provided IContext URI: " + iContextURI);
            updateObjectForm.setIContextURI(iContextURI);

            String iValidityURI = element.getElementsByTagName("iValidityURI").item(0).getTextContent();
            LOGGER.debug("service=ImportStructure;msg=" + "Provided IValidity URI: " + iValidityURI);
            updateObjectForm.setIValidityURI(iValidityURI);

            String port = element.getElementsByTagName("port").item(0).getTextContent();
            LOGGER.debug("service=ImportStructure;msg=" + "Provided Port: " + port);
            updateObjectForm.setPort(port);

            String SecurityContext = element.getElementsByTagName("SecurityContext").item(0).getTextContent();
            LOGGER.debug("service=ImportStructure;msg=" + "Provided Security Context: " + SecurityContext);
            //updateObjectForm.setSecurityContext(element.getElementsByTagName("SecurityContext").item(0).getTextContent());

            //updateObjectForm.setCbpKey( new EnoviaWebserviceCommon().getCbpKey(updateObjectForm.getUserID()));
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException ex) {
            ex.printStackTrace();
            throw new CustomException(ex.getMessage());
        }
        return updateObjectForm;
    }


     @Override
   // public void setAttributeValues(HttpSession httpSession, File file, UpdateObjectForm updateObjectForm, List<String> singleRequestAttr, List<String> notUpdatableAttr, List<String> notUpdatableProperties, HashMap<String, String> allRelationshipAttrNameList, Context context) throws CustomException {
        public void setAttributeValues(File file, UpdateObjectForm updateObjectForm,  AttributesForm attributesForm, Context context) throws CustomException, FileNotFoundException {  
        LOGGER.debug("service=ImportStructure;msg=" + "In setAttributeValues method");

        List<String> notUpdatableProperties = attributesForm.getNotUpdatableProperties();

        List<HashMap<String, String>> attributeListOfMaps = new EnoviaWebserviceClient().getMappedValuesFromXls(file, notUpdatableProperties);
        // if xls contains only not-updatable properties, set error
        if (attributeListOfMaps.isEmpty() == true) {
            LOGGER.debug("service=ImportStructure;msg=" + "Nothing to update!");
            throw new CustomException("Nothing to update!");
            //return;
        }
        // not-updatable properties --end
        List<String> statusList = new ArrayList();
        HashMap<String, String> isClassPath = attributeListOfMaps.get(attributeListOfMaps.size() - 1);
        attributeListOfMaps.remove(attributeListOfMaps.size() - 1);
        String isClassificationPath = isClassPath.get("isClassificationPath");
        String attributeInvalid = getInvalidAttributes();
//        if (attributeInvalid != null) {
//            attributeListOfMaps.remove(attributeListOfMaps.size() - 1);
//        }
        if (attributeListOfMaps.isEmpty()) {
            LOGGER.debug("service=ImportStructure;msg=" + "Could not read values from input file.");
            throw new CustomException("Could not read values from input file.");
            //return;
        }
        if (checkId(attributeListOfMaps)) {
            if (checkTypeNameRevision(attributeListOfMaps)) {
                LOGGER.debug("service=ImportStructure;msg=" + "GETTING ID FROM SOAP");
                attributeListOfMaps = addObjectIdInMap(attributeListOfMaps, context, updateObjectForm);
            } else {
                LOGGER.debug("service=ImportStructure;msg=" + "Type, Name, Revision or Id not found!");
                throw new CustomException("Type, Name, Revision or Id not found!");
                //return;
            }
        }
        LOGGER.debug("service=ImportStructure;msg=" + " NEW MAP : " + attributeListOfMaps.toString());
        //HashMap<String, String> errorInUpdate = new HashMap<>();
        Map<String, List> outputStatus = sendUpdateRequest(updateObjectForm, attributeListOfMaps, isClassificationPath, attributesForm, context);
        LOGGER.debug("service=ImportStructure;msg=" + "Found output status");
        statusList = outputStatus.get("statusList");
        LOGGER.debug("service=ImportStructure;msg=" + "Found Status List");
        List<String> unavailableList = outputStatus.get("unavailableList");
        LOGGER.debug("service=ImportStructure;msg=" + "Found unavailableList List" + unavailableList);
        if (statusList.size() > 0) {
            updateObjectForm.setOutput(generateXLS(file, outputStatus, updateObjectForm));
            LOGGER.debug("service=ImportStructure;msg=" + "File: " + updateObjectForm.getOutput());
        }
    }
        
    @Override
    public boolean checkId(List <HashMap <String,String>>Listofmap) {
        HashMap <String,String> map = Listofmap.get(0);
        return (map.get("id") == null);
    }
        @Override
    public boolean checkTypeNameRevision (List <HashMap <String,String>>Listofmap) {
        HashMap <String,String> map=Listofmap.get(0);
        boolean type=false, name=false, revision=false;
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase("type")) {
                type=true;
            } else if (key.equalsIgnoreCase("name")) {
                name=true;
            } else if (key.equalsIgnoreCase("revision")) {
                revision=true;
            }
        }
        return (type && name && revision);
    }
    
    @Override
    public String getInvalidAttributes() {
        List<String> keyToSkip = new ArrayList<String>() {{add("state"); add("owner"); add("Description"); add("revision"); }};
        return keyToSkip.toString();
    }
    
    
    @Override
    public List <HashMap <String,String>> addObjectIdInMap (List <HashMap <String,String>>Listofmap, Context context, UpdateObjectForm updateObjectForm) throws CustomException {
        ObjectIdForm objectIdForm = new ObjectIdForm();
        //objectIdForm.setCbpKey(cbpKey);
        HashMap <String,String> errorMessages = new HashMap<>();
        for (int i=0;i<Listofmap.size();i++) {
            HashMap <String,String> map = Listofmap.get(i);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key=entry.getKey();
                String value=entry.getValue();
                if (key.equalsIgnoreCase("type")) {
                    objectIdForm.setType(value);
                } else if (key.equalsIgnoreCase("name")){
                    objectIdForm.setName(value);
                } else if (key.equalsIgnoreCase("revision")) {
                    objectIdForm.setRevision(value);
                }
            }
            String id = null;
            try {
               //id = getObjectId(objectIdForm);
               //id = objectIDService.getObjectId(objectIdForm);
            String query = "print bus '" + objectIdForm.getType() + "' '" + objectIdForm.getName() + "' '" + objectIdForm.getRevision() + "' " + " select physicalid";
            String queryResult = mqlQueries.getQueryResults((Context) context, query);
            id = queryResult.substring(queryResult.lastIndexOf("=") + 1, queryResult.length()).trim();

            } catch (CustomException ex) {
                LOGGER.debug("service=ImportStructure;msg=" + "Demo Exceptions :"+ex.getMessage());
                errorMessages.put(ex.getMessage(),"#");
            }
            if (id == null) {
                //Listofmap.remove(i);
                //i--;
                Listofmap.get(i).put("id", "DoesNotExist");
                continue;
            }
            Listofmap.get(i).put("id", id);
        }
        if (!errorMessages.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (Map.Entry<String, String> entry : errorMessages.entrySet()) {
                String key = entry.getKey();
                message.append("<br>");
                message.append("# ");
                message.append(key);
            }
            //updateObjectForm.setDemoexception(message.toString());
        }
        return Listofmap;
    }
    
        @Override
    // public Map<String, List> sendUpdateRequest(UpdateObjectForm updateObjectForm, List<HashMap<String, String>> attributeListOfMaps, String cbpKey,  List<String> notUpdatableAttr, String isClassificationPath,  List<String> attrForSingleReq, List<String> notUpdatableProperties, Context context) throws CustomException {
    public Map<String, List> sendUpdateRequest(UpdateObjectForm updateObjectForm, List<HashMap<String, String>> attributeListOfMaps, String isClassificationPath, AttributesForm attributesForm, Context context) throws CustomException {
        LOGGER.debug("service=ImportStructure;msg=" + "In sendUpdateRequest method");
        //this list contains the allowed types for import
        List<String> attrForSingleReq = attributesForm.getSingleRequestAttr();        
        List<String> notUpdatableAttr = attributesForm.getNotUpdatableAttr();
        LOGGER.debug("service=ImportStructure;msg=" + "notUpdatableAttr Attribute names: "+notUpdatableAttr.toString());
        //notUpdatableAttr  = ewcUtilities.getPropertyListWithApropriateNames(notUpdatableAttr);
        //LOGGER.debug("service=ImportStructure;msg=" + "notUpdatableAttr Attribute names after replace: "+notUpdatableAttr.toString());
        List<String> notUpdatableProperties = attributesForm.getNotUpdatableProperties();
        notUpdatableProperties  = ewcUtilities.getPropertyListWithApropriateNames(notUpdatableProperties);
        LOGGER.debug("service=ImportStructure;msg=" + "notUpdatableProperties names: "+notUpdatableProperties.toString());
        List<String> allRelationshipAttrNameList = attributesForm.getRelationshipAttrName();
        LOGGER.debug("service=ImportStructure;msg=" + "Relationship Attribute names: "+allRelationshipAttrNameList.toString());
        HashMap<String, String> allRelationshipAttrRelNameMap = attributesForm.getRelationshipAttrRelName();
        LOGGER.debug("service=ImportStructure;msg=" + "Attribute relationship name map: "+allRelationshipAttrRelNameMap.toString());

        List<String> allowedTypes = new EnoviaWebserviceClient().getAllowedTypesForImport();

        //this map contains the output status and error messages
        Map<String, List> outputStatus = new HashMap<String, List>();
        List<String> statusList = new ArrayList();
        List<String> unavailableList = new ArrayList();
        List<String> messageList = new ArrayList();

        //PLMBusinessObjectService plmbos = new PLMBusinessObjectService();
        //IPLMBusinessObjectService iplmbos = plmbos.getPLMBusinessObjectServicePort();

        LOGGER.debug("service=ImportStructure;msg=" + "is context created: "+context.checkContext());

        /*
         * Check if type is available in object map.
         * if not, fetch the types from JPO using object id
        */
        Map<String, String> objectTypes = null;
        String objectType = attributeListOfMaps.get(0).get("type");
            if(objectType == null || objectType.compareTo("") == 0) {
                objectTypes = getObjectTypes(attributeListOfMaps, context);
                LOGGER.debug("service=ImportStructure;msg=" + "objectTypes" + objectTypes);
            }
        /*
        * The following code loops through the list containing objects with corresponding attributes
        */
        for (int i = 0; i < attributeListOfMaps.size(); i++) {
            String error = "";
            Map<String, String> objectMap = new HashMap<>(attributeListOfMaps.get(i));
            String objectId = attributeListOfMaps.get(i).get("id");
            LOGGER.debug("service=ImportStructure;msg=" + "Got Request for object ID  : " + i + " ->" + objectId + " :: " + objectMap);

            if(objectId == "" || objectId == null) {
                continue;
            }

            Map<String, String> attributeMap = null;
            List<String> relAttributeList = new ArrayList<>();
            Map<String, List<String>> relAttributeMap = new HashMap<>();
            /*
             * Check if all the attributes in object map exist
             * If present, they will be added in update request
             * if not, they will be add in list.
             * Later, in generateXLS() method, corresponding cell in output xls will be colored red.
            */
            try {
                attributeMap = checkAvailableAttributes(context, objectMap, notUpdatableProperties, notUpdatableAttr, allRelationshipAttrNameList);
                relAttributeMap = findRelationshipAttrMap(attributeListOfMaps, attributeListOfMaps.get(i), allRelationshipAttrNameList, i, context);
                relAttributeList = relAttributeMap.get("allRelAttributeList");
                LOGGER.debug("service=ImportStructure;msg=" + "Found available relationship attribute for update: " + relAttributeList);                
            } catch (CustomException  e) {
                //errorInUpdate.put("Possible Error: Object " + objectId + " does not exist.", "#");
                String errorMsg = e.getMessage();
                if(errorMsg.trim().compareTo("System exception occurred: Transaction aborted") == 0) {
                statusList.add("KO");
                messageList.add("Object does not exist. " + e.getMessage());
                LOGGER.debug("service=ImportStructure;msg=" + "Object does not exist.\n Exception: " + e.getMessage());
                } else {
                    if (errorMsg.trim().compareTo("Invalid CBPKey") == 0) {
                        //updateObjectForm.setDemoexception("Incorrect UserID or Password !");
                        LOGGER.debug("service=ImportStructure;msg=" + "Invalid CBPKey!");
                        throw new CustomException("Invalid CBPKey!");
                        //return outputStatus;
                    } else {
                        statusList.add("KO");
                        messageList.add("Error: " + e.getMessage());
                        LOGGER.debug("service=ImportStructure;msg=" + "\n Exception: " + e.getMessage());
                    }
                }
                continue;
            } catch (Exception ex) {
                LOGGER.debug("service=ImportStructure;msg=" + ex.getMessage());
            }
            String unAvailableAttrs = null;
            unAvailableAttrs = attributeMap.get("unavailable");
            attributeMap.remove("unavailable");
            LOGGER.debug("service=ImportStructure;msg=" + "For " + objectId + ": " + unAvailableAttrs);
            // errorInUpdate.put("Possible Error: Object " + objectId + " Object does not have these attributes: " + unAvailableAttrs, "#");
            String attributeInvalid = attributeMap.get("notUpdatableAttr");
            LOGGER.debug("service=ImportStructure;msg=" + "attributeInvalid : " + attributeInvalid + ".");
            attributeMap.remove("notUpdatableAttr");
            String properties = attributeMap.get("properties");
            attributeMap.remove("properties");
            String unAvailableRelAttrs = null;
            if (unAvailableAttrs == null) {
                if (relAttributeMap.get("unavailRelAttributeList")!= null) { 
                    unAvailableAttrs = relAttributeMap.get("unavailRelAttributeList").toString();
                    LOGGER.debug("service=ImportStructure;msg=" + "Found unavailable relationship attributes : " + unAvailableRelAttrs);
                }
            } else if (relAttributeMap.get("unavailRelAttributeList")!= null) {
                unAvailableRelAttrs = relAttributeMap.get("unavailRelAttributeList").toString();
                LOGGER.debug("service=ImportStructure;msg=" + "Found unavailable relationship attributes : " + unAvailableRelAttrs);
                unAvailableAttrs = unAvailableAttrs.substring(0, unAvailableAttrs.length() - 1);
                unAvailableRelAttrs = unAvailableRelAttrs.substring(1, unAvailableRelAttrs.length());
                unAvailableAttrs = unAvailableAttrs +","+ unAvailableRelAttrs;
            }
            unavailableList.add(unAvailableAttrs);
            LOGGER.debug("service=ImportStructure;msg=" + "Total unavailable  attributes : " + unavailableList.toString());
            if (unAvailableAttrs != null) {
//                error += "Object does not have these attributes: " + unAvailableAttrs + ". ";
//                //errorInUpdate.put("Possible Error: For Object " + objectId + ": " + "<br> Object does not have these attributes: " + unAvailableAttrs, "#");
//                logger.debug("service=ImportStructure;msg=" + "Object does not have these attributes: " + unAvailableAttrs);
            }
            if (attributeInvalid != null) {
                error += "Following attribute(s) can not be updated: " + attributeInvalid + ".";
                LOGGER.debug("service=ImportStructure;msg=" + "Following attribute(s) can not be updated: " + attributeInvalid + ".");
            }
            LOGGER.debug("service=ImportStructure;msg=" + "Reading Request for object ID  : " + i + " ->" + objectId + " :: " + objectMap);
            String classification_path = "";
            classification_path = attributeMap.get("Classification Path");
            attributeMap.remove("Classification Path");
//            ListOfNameValue listOfNameValue = new ListOfNameValue();
//            /* The following portion of code sends request for individual attributes*/
//            for (Entry<String, String> entrySet : attributeMap.entrySet()) {
//                LOGGER.debug("service=ImportStructure;msg=" + "debugging single attribute: Name-" + entrySet.getKey() + "Value" + entrySet.getValue());
//                String currentAttr = entrySet.getKey().trim();
//                NameValue nameValue = new NameValue();
//                nameValue.setName(currentAttr);
//                nameValue.setValue(entrySet.getValue().trim());
//                listOfNameValue.getNameValue().add(nameValue);
////                if (attrForSingleReq.contains(currentAttr)) {
////                    try {
////                        LOGGER.debug("service=ImportStructure;msg=" + "Sending request for single attribute: Name-" + entrySet.getKey() + ", Value : " + entrySet.getValue());
////                        ListOfNameValue singleReqNameValue = new ListOfNameValue();
////                        singleReqNameValue.getNameValue().add(nameValue);
////                        iplmbos.setAttributeValues(cbpKey, objectId, singleReqNameValue);
////                        logger.debug("service=ImportStructure;msg=" + "Successfully  set value for single attribute: Name-" + entrySet.getKey() + ", Value : " + entrySet.getValue());
////                    } catch (SetAttributeValuesErrorType ex) {
////                        error += ex.getMessage();
////                        logger.debug("service=ImportStructure;msg=" + "Error Updating Attribute: " + ex.getMessage());
////                        ex.printStackTrace();
////                    }
////                }
//            }
//
//            LOGGER.debug("service=ImportStructure;msg=" + "list of nameValue length : " + listOfNameValue.getNameValue().size());
//            LOGGER.debug("service=ImportStructure;msg=" + "Following list is being sent for request:");
//            for(int length = 0; length < listOfNameValue.getNameValue().size(); ++length) {
//            LOGGER.debug("service=ImportStructure;msg=" + "listofNameValue " + length + " key " + listOfNameValue.getNameValue().get(length).getName() + ", value " + listOfNameValue.getNameValue().get(length).getValue());
//            }

            /*
             * Check if the object map contains 'type'
             * If present, check if this type is allowed for import
             * if type is not present, throw exception
            */
            objectType = attributeListOfMaps.get(i).get("type");
            if(objectType == null || objectType.compareTo("") == 0) {
                objectType = objectTypes.get(objectId);
            }
            if (!allowedTypes.contains(objectType)) {
                statusList.add("KO");
                messageList.add("Update is not allowed for this type.");
                LOGGER.debug("service=ImportStructure;msg=" + "Update is not allowed for this type.");
                continue;
            }

            String classPathUpdate = "";
            String updateResult = "";
            /* Send request to web service for updating attribute
             * call a method to update classification path
             * add status and message to list
             */
            try {
//                iplmbos.setAttributeValues(cbpKey, objectId, listOfNameValue);
               String updateAttributeStatus = "";
               String updateArrtibuteStatus = getUpdateAttributeStatus(updateObjectForm,context, objectId, attributeMap);
               JSONObject messageJson = new JSONObject(updateArrtibuteStatus); 
               JSONArray messageJsonArray = messageJson.getJSONObject("results").getJSONObject("result").getJSONArray("message");
               if(messageJsonArray != null && messageJsonArray.length() > 0 ){
               updateAttributeStatus = messageJsonArray.getString(0);    
               LOGGER.debug("service=ImportStructure;msg=" + "Object attribute update result: "+updateResult);
               //error = error+updateAttributeStatus;
               }
               if (isClassificationPath.compareTo("true") == 0) {
                    LOGGER.debug("service=ImportStructure;msg=" + "Updating classification Path for object: " + objectId);
                    classPathUpdate = " " + updateClassificationPath(objectId, classification_path, context);
                }
                if(relAttributeList!=null) {
                LOGGER.debug("service=ImportStructure;msg=" + "Updating relationship attribute for the object: " + objectId);
                LOGGER.debug("attributeListOfMaps: "+attributeListOfMaps+"\n"+"allRelationshipAttrRelNameMap: "+allRelationshipAttrRelNameMap+"\n"+"relAttributeList: "+relAttributeList+"\n i:"+i);
                updateResult = updateRelatioshipAttributes(attributeListOfMaps, allRelationshipAttrRelNameMap, relAttributeList, i, context );
                LOGGER.debug("service=ImportStructure;msg=" + "Relationship Update result: "+updateResult);
                }
                String updateStatus = "";
                updateStatus = error.concat(updateAttributeStatus).concat(classPathUpdate).concat(updateResult).trim();
                LOGGER.debug("service=ImportStructure;msg=" + "updateStatus :" + updateStatus);
                if (updateStatus.length() <= 0 && attributeMap.size() >= 0) {
                    statusList.add("OK.");
                    messageList.add(updateStatus);
                } else {
                    statusList.add("Partially Updated.");
                    LOGGER.debug("service=ImportStructure;msg=" + "Partially Updated");
                    messageList.add(updateStatus);
                }
            } catch (CustomException ex ) {
                ex.printStackTrace();
                error += ex.getMessage();
                statusList.add("KO.");
                messageList.add(error + classPathUpdate);
                LOGGER.debug("service=ImportStructure;msg=" + messageList);
                //errorInUpdate.put(ex.getMessage(), "#");
                continue;
            } catch (Exception ex) {
            }
        }
        LOGGER.debug("service=ImportStructure;msg=" + "Returning statuslist from sendRequest method");
        outputStatus.put("statusList", statusList);
        outputStatus.put("messageList", messageList);
        outputStatus.put("unavailableList", unavailableList);
        return outputStatus;
    }
    
    
    private Map<String, String> getObjectTypes(List<HashMap<String, String>> attributeListOfMaps, Context context) throws CustomException {
        List<String> objectIds = new ArrayList<>();
        for (int i = 0; i < attributeListOfMaps.size(); i++) {
            Map<String, String> objectMap = new HashMap<>(attributeListOfMaps.get(i));
            String objectId = attributeListOfMaps.get(i).get("id");
            objectIds.add(objectId);
        }
        try {
            HashMap params = new HashMap();
            params.put("objectId", objectIds);
            String[] constructor = {null};
            String[] initArgs = new String[0];
            Map<String, String> objectTypes = (Map) JPO.invoke(context, "emxBusObject", constructor, "getObjectTypes", JPO.packArgs(params), Map.class);
            return objectTypes;

        } catch (MatrixException ex) {
            LOGGER.debug("service=ImportStructure;msg=" + "Exception : + " + ex.getMessage());
            throw new CustomException("Error getting object types.");
        } catch (Exception ex) {
            LOGGER.debug("service=ImportStructure;msg=" + "Exception : + " + ex.getMessage());
            throw new CustomException("Error getting object types.");
        }
    }
   
    @Override
    public Map checkAvailableAttributes(Context context, Map<String, String> objectMap, List<String> notUpdatableProperties,  List<String> notUpdatableAttr, List<String> allRelationshipAttrNameList) throws CustomException {
//        GetAttributeValuesForm getAttributeValuesForm = new GetAttributeValuesForm();
//        getAttributeValuesForm.setCbpKey(cbpKey);
//        getAttributeValuesForm.setObjectId(objectMap.get("id"));
//        //obtain the list of attributes for object
//        //List<NameValue> nameValue = getAttributeValues(getAttributeValuesForm);
//        List<NameValue> nameValue = getAttributeValuesService.getAttributeValues(getAttributeValuesForm);        
          List<String> availableAttributes = new ArrayList();
          List<String> unAvailableAttributes = new ArrayList();
          List<String> notUpdatableProperty = new ArrayList();
          List<String> properties = new ArrayList();

          List<String> attributesList = ewcUtilities.getObjectAttributes(context, objectMap.get("id"));
         LOGGER.debug("--------------------------attributesList : " + attributesList.toString());
         for(String attribute : attributesList) {
            LOGGER.debug("--------------------------attribute : " + attribute);
            availableAttributes.add(attribute);
         }

        String class_Path = objectMap.get("Classification Path");
        objectMap.remove("Classification Path");
        for (Iterator<Map.Entry<String, String>> it = objectMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (!availableAttributes.contains(entry.getKey())) {
                if(notUpdatableProperties.contains(entry.getKey())) {
                   properties.add(entry.getKey());
                }
                else {
                    if(notUpdatableAttr.contains(entry.getKey())) {
                        notUpdatableProperty.add(entry.getKey());
                    }
                    else {
                        LOGGER.debug("service=ImportStructure;msg=" + "Found attribute: "+entry.getKey());
                        if(entry.getValue().compareTo("") != 0 && !allRelationshipAttrNameList.contains(entry.getKey())) {
                            unAvailableAttributes.add(entry.getKey());
                        }
                    }
                }
                it.remove();
            }
        }
        /*
        if start effectivity/end effectivity is present in user input(xls)
        then check validity
        */
        if(objectMap.get("Start Effectivity") != null || objectMap.get("End Effectivity") != null) {
            LOGGER.debug("service=ImportStructure;msg=" + "Going to check validity");
            checkValidity(objectMap, attributesList);
        }
        /*
        end of validation check
        */

        if (unAvailableAttributes.size() > 0) {
            objectMap.put("unavailable", unAvailableAttributes.toString());
        }
        if (properties.size() > 0) {
            objectMap.put("properties", properties.toString());
        }
        if (notUpdatableProperty.size() > 0) {
            objectMap.put("notUpdatableAttr", notUpdatableProperty.toString());
        }
        objectMap.put("Classification Path", class_Path);
        return objectMap;

    }
    
    
        /**
     * check if input Start Effectivity is after End Effectivity
     *
     * @param objectMap
     * @throws com.bjit.ewc.utils.CustomException
     */
    private void checkValidity(Map<String, String> objectMap, List<String> currentValue) throws CustomException {
        /*
         * At first the value of startEffectivity and endEffectivity is obtained from objectMap(xls values)
         */
        String startEffectivity = objectMap.get("Start Effectivity"), endEffectivity = objectMap.get("End Effectivity");
        Date startDate = null, endDate = null;
        /*
         * if the values are not in objectMap(means xls does not have this column)
         * then fetch the value from database values list- currentValue
         */
        for (int i = 0; i < currentValue.size(); i++) {
            if (currentValue.get(i).compareTo("Start Effectivity") == 0 && startEffectivity == null) {
                startEffectivity = currentValue.get(i);
            }
            if (currentValue.get(i).compareTo("End Effectivity") == 0 && endEffectivity == null) {
                endEffectivity = currentValue.get(i);
            }
        }

        /*
         * parse the obtained strings with dateFormatter
         */
        List<String> formatStrings = Arrays.asList("MM/dd/yyyy hh:mm:ss a");
        for (String formatString : formatStrings) {
            try { 
                startDate = new SimpleDateFormat(formatString).parse(startEffectivity);
            } catch (ParseException e) {
            }
            try {
                endDate = new SimpleDateFormat(formatString).parse(endEffectivity);
            } catch (ParseException e) {
            }
        }
        LOGGER.debug("service=ImportStructure;msg=" + "date : " + startDate + " " + endDate);
        /*
        if both the fields are present in xls but empty, throw exception - cannot be empty
         */
        if ((startEffectivity.length() <=0 && startDate == null) && (endEffectivity.length() <=0 && endDate == null)) {
            throw new CustomException("Start Effectivity and End Effectivity cannot be empty.");
        }
        /*
        * if start effectivity is empty in database and xls does not have this column,
        * or if start effectivity column of xls is present but empty
        * and end effectivity field is present in user input(objectMap)
        * throw exception
         */
        if ((startEffectivity.length() <= 0 && startDate == null) && endDate != null) {
            throw new CustomException("End Effectivity is not Empty. Please enter value for Start Effectivity.");
        }
        /*
        * if end effectivity is empty in database and xls does not have this column,
        * or if end effectivity column of xls is present but empty
        * and end effectivity field is present in user input(objectMap)
        * throw exception
         */
        if (startDate != null && (endEffectivity.length() <= 0 && endDate == null)) {
            throw new CustomException("Start Effectivity is not Empty. Please enter value for End Effectivity.");
        }
        /*
        if either/both of the fields contain wrong time format, throw exception
         */
        if ((startEffectivity.length() > 0 && startDate == null) || (endEffectivity.length() > 0 && endDate == null)) {
            throw new CustomException("Invalid Date Format. Allowed Format is : " + formatStrings.toString());
        }
        /*
        if start effectivity is greater then or equal to end effectivity, throw exception
         */
        if (startDate.after(endDate)) {
            LOGGER.debug("service=ImportStructure;msg=" + "Invalid date.");
            throw new CustomException("Invalid Date. Start Effectivity should be less than or equal to End Effectivity.");
        } else {
            LOGGER.debug("service=ImportStructure;msg=" + "Valid Dates. " + startDate + " " + endDate);
        }
       /*
        * if no exception is thrown, continue
        */

    }

  
    public Map<String, List<String>> findRelationshipAttrMap(List<HashMap<String, String>> attributeListOfMaps, Map<String, String> updatingObjectMap, List<String> allRelAttributeList, int objectNo, Context context) throws Exception {
        LOGGER.debug("service=ImportStructure;msg=" + "In find relationship attribute method");
        Map<String, List<String>> relAttributeMap = new HashMap<>();
        LOGGER.debug("service=ImportStructure;msg=" + "Object map: " + updatingObjectMap.toString());
        LOGGER.debug("service=ImportStructure;msg=" + "Relationship attribute list: " + allRelAttributeList.toString());
        String updatingObjectId = updatingObjectMap.get("id");
        LOGGER.debug("service=ImportStructure;msg=" + "Updating Object Id: " + updatingObjectId);
        List<String> availRelAttributeList = new ArrayList<>();
        List<String> unavailRelAttributeList = new ArrayList<>();
        if (objectNo == 0) {
            LOGGER.debug("service=ImportStructure;msg=" + "Root Object Id found: " + updatingObjectId);
            for (Map.Entry<String, String> entry : updatingObjectMap.entrySet()) {
                if (allRelAttributeList.contains(entry.getKey())) {
                    LOGGER.debug("service=ImportStructure;msg=" + "Relationship attibute: " + entry.getKey());
                    unavailRelAttributeList.add(entry.getKey());
                }
            }
        } else {
            String parentId = findParentObjectId(attributeListOfMaps, objectNo);
            LOGGER.debug("service=ImportStructure;msg=" + "Updating Parent Object Id: " + parentId);
            Set<String> allRelAttributesFroUpdatingObject = findRelAllAttributesForUpdatingObject(updatingObjectId, parentId, context);
            for (Iterator<Map.Entry<String, String>> it = updatingObjectMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                LOGGER.debug("service=ImportStructure;msg=" + "Attibute: " + entry.getKey());
                if (allRelAttributeList.contains(entry.getKey())) {
                    LOGGER.debug("service=ImportStructure;msg=" + "Relationship attibute: " + entry.getKey());
                    if (allRelAttributesFroUpdatingObject.contains(entry.getKey())) {
                        LOGGER.debug("service=ImportStructure;msg=" + "Available Relationship attibute: " + entry.getKey());
                        availRelAttributeList.add(entry.getKey());
                    } else {
                        LOGGER.debug("service=ImportStructure;msg=" + "Unavailable Relationship attibute: " + entry.getKey());
                        unavailRelAttributeList.add(entry.getKey());
                    }

                }
            }
        }

        if (availRelAttributeList.size() > 0) {
            relAttributeMap.put("allRelAttributeList", availRelAttributeList);
        }
        if (unavailRelAttributeList.size() > 0) {
            relAttributeMap.put("unavailRelAttributeList", unavailRelAttributeList);
        }
        return relAttributeMap;
    }
    
        public String findParentObjectId(List<HashMap<String, String>> attributeListOfMaps, int objectNumber) {
        String parentId = null;
        for (int k = objectNumber; k > 0; k--) {
            System.out.println("Depth: " + Integer.parseInt(attributeListOfMaps.get(k).get("depth")));
            if (Integer.parseInt(attributeListOfMaps.get(objectNumber).get("depth")) > Integer.parseInt(attributeListOfMaps.get(k - 1).get("depth"))) {
                parentId = attributeListOfMaps.get(k - 1).get("id");
                break;
            }
        }

        return parentId;
    }
    
    public Set<String> findRelAllAttributesForUpdatingObject(String updatingObjectId, String parentId, Context context) throws Exception {
        java.util.Set relationshipAttributes = new HashSet<>();
        String[] constructor = {null};
        HashMap paramsMap = new HashMap();
        paramsMap.put("objId", updatingObjectId);
        paramsMap.put("parentId", parentId);
        java.util.Vector relNamesBetweenTwoObjects = (Vector) JPO.invoke(context,
                "UpdateRelationshipAttribute",
                constructor,
                "getRelationIdsBetweenObjects",
                JPO.packArgs(paramsMap),
                java.util.Vector.class
        );
        if (relNamesBetweenTwoObjects != null) {
            LOGGER.debug("service=ImportStructure;msg=" + "Relationship Names: " + relNamesBetweenTwoObjects.toString());
            HashMap paramsMap1 = new HashMap();
            paramsMap1.put("relatioshipList", relNamesBetweenTwoObjects);
            relationshipAttributes = (java.util.Set) JPO.invoke(context,
                    "UpdateRelationshipAttribute",
                    constructor,
                    "getAllRelationshipAttributes",
                    JPO.packArgs(paramsMap1),
                    java.util.Set.class
            );
            if (relationshipAttributes != null) {
               LOGGER.debug("service=ImportStructure;msg=" + "Relationship Atrr found for updating object from JPO:" + relationshipAttributes.toString());
            } else {
                LOGGER.debug("service=ImportStructure;msg=" + "No Relationship Atrr ");
            }
        } else {
            LOGGER.debug("service=ImportStructure;msg=" + "No Relationship found");
        }
        return relationshipAttributes;
    }
    
    private String getUpdateAttributeStatus(UpdateObjectForm updateObjectForm, Context context, String objectId, Map<String, String> attributeMap) throws CustomException {
        try {
            ResponseEntity<String> response;
            String finalJson;
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
            String ticket = ewcUtilities.getTicket(host, updateObjectForm.getUserID(), updateObjectForm.getPassword());
            //String uri = host + "/resources/trm/browsing/model/expand" + ticket;
            String uri = host + "/resourcesbjit/trm/browsing/updateSingleObjectAttributeBjit" + ticket;
            StringBuilder requestJsonBuilder = ewcUtilities.getUpdateObjectResquestJson(objectId, attributeMap);
            LOGGER.debug("final requestJson: " + requestJsonBuilder.toString());
            HttpHeaders headers = ewcUtilities.getHttpRequestHeaders(updateObjectForm.getSecurityContext());
            LOGGER.debug("Headers: " + headers);
            response = ewcUtilities.getPostRestResponse(uri, requestJsonBuilder, headers);
            LOGGER.debug("Response - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
            LOGGER.debug("Response: " + response.getBody());
            String responseString = response.getBody();
            if(responseString!=null && !responseString.isEmpty()) {
            return responseString;
            }
        } catch (CustomException ex) {
            LOGGER.error("Occured exception while getting getUpdateAttributeStatus json: " + ex);
            throw new CustomException(ex.getMessage());
        }
        return null;
    }
    
     /**
     * updates classification path by sending request to JPO
     *
     * @param objectID
     * @param classificationPath
     * @param context
     *
     * @return classification Path Update Status 
     */
    @Override
    public String updateClassificationPath(String objectID, String classificationPath, Context context) {
        String classPathUpdate = "";
        LOGGER.debug("service=ImportStructure;msg=" + "In updateClassificationPath Method. \n" + objectID + ":" + classificationPath);
        if(classificationPath.compareTo("") == 0) {
         return classPathUpdate;
        } else {
            if(classificationPath.compareTo("\"\"") == 0 || classificationPath.compareTo("“”") == 0) {
                classificationPath = "";
            }
        }
        LOGGER.debug("service=ImportStructure;msg=" + "Sending request for classpath : ->" + classificationPath + "<-");
        try {
            HashMap params = new HashMap();
            params.put("objectId", objectID);
            params.put("classNames", classificationPath);
            String[] constructor = {null};
            String[] initArgs = new String[0];
            String updateResult = (String) JPO.invoke(context, "emxClassificationPath", constructor, "updateClassificationPath", JPO.packArgs(params), String.class);
            LOGGER.debug("service=ImportStructure;msg=" + "updatedClassificationPath : " + updateResult);

            String temp = updateResult.trim().substring(0, 5);
            if(temp.trim().compareTo("Error") == 0) {
                classPathUpdate += updateResult;
            }
        } catch (MatrixException ex) {
            classPathUpdate += "Could not be updated.";
            LOGGER.debug("service=ImportStructure;msg=" + "Exception : + " + ex.getMessage());
        } catch (Exception ex) {
            classPathUpdate += "Could not be updated.";
            LOGGER.debug("service=ImportStructure;msg=" + "Exception : + " + ex.getMessage());
        }
        return classPathUpdate;
    }
    
    
     /**
     * 
     * @param attributeListOfMaps
     * @param allRelAttrRelNameMap
     * @param relationshipAttrNameList
     * @param objectNumber
     * @param context
     * @return
     * @throws Exception 
     */
    public String updateRelatioshipAttributes(List<HashMap<String, String>> attributeListOfMaps, HashMap<String, String> allRelAttrRelNameMap, List<String> relationshipAttrNameList, int objectNumber, Context context) throws Exception {
        LOGGER.debug("service=ImportStructure;msg=" + "Update relationship attribute method");
        String parentId = null;
        String updateStatus = "";
        String updateMessage = "";
        if (objectNumber == 0) {
            updateStatus = "Root object's relationship attribute values cannot be updated";
            return updateStatus;
        } else {            
        String[] constructor = {null};
        HashMap paramsMap = new HashMap();
        paramsMap.put("attributeListOfMaps", attributeListOfMaps);
        paramsMap.put("allRelAttrRelNameMap", allRelAttrRelNameMap);
        paramsMap.put("relationshipAttrNameList", relationshipAttrNameList);
        paramsMap.put("objectNumber", objectNumber);        
        updateStatus = (String) JPO.invoke(context,
                "UpdateRelationshipAttribute",
                constructor,
                "updateRelatioshipAttributes",
                JPO.packArgs(paramsMap),
                String.class
        );
        }
        return updateStatus;
    }
    
     /**
     * generates result xls for update object
     *
     * @param file(input file)
     * @param outputStatus
     * @param updateObjectForm
     * @return (generated) filepath
     * @throws com.bjit.ewc.utils.CustomException
     */
    @Override
    public String generateXLS(File file, Map<String, List> outputStatus, UpdateObjectForm updateObjectForm) throws CustomException {
        LOGGER.debug("service=ImportStructure;msg=" + "In generateXLS method");
        List<String> statusList = outputStatus.get("statusList");
        LOGGER.debug("service=ImportStructure;msg=" + "statusList: "+statusList);
        List<String> unavailableList = outputStatus.get("unavailableList");
        LOGGER.debug("service=ImportStructure;msg=" + "Unavailable list: "+unavailableList);
        List<String> messageList = outputStatus.get("messageList");
        LOGGER.debug("service=ImportStructure;msg=" + "messageList: "+messageList);
         try {
            FileInputStream inputStream = new FileInputStream(file);
            HSSFWorkbook inputBook = new HSSFWorkbook(inputStream);
            HSSFSheet firstSheet = inputBook.getSheetAt(0);

            Iterator<Row> iterator = firstSheet.iterator();
            //skipping xls header template(if any)
            Row nextRow = new EnoviaWebserviceClient().skipHeader(iterator);

            //add template sheet
            addTemplateSheet(inputBook, nextRow.getRowNum() - 1);

            //add status column
            HSSFCell cell = null;
            //add status header
            int lastColumn = nextRow.getLastCellNum();
            int headerRow = nextRow.getRowNum();
            cell = firstSheet.getRow(headerRow).getCell(lastColumn - 2);
            if(cell.getStringCellValue().trim().equalsIgnoreCase("Status")) {
                LOGGER.debug("service=ImportStructure;msg=" + "got an import result as input." + cell.getStringCellValue());
                lastColumn = lastColumn - 2;
            }
            cell = firstSheet.getRow(headerRow).createCell(lastColumn);
            cell.setCellValue("Status");

            cell = firstSheet.getRow(headerRow).createCell(lastColumn + 1);
            cell.setCellValue("Message");
            HSSFCellStyle style = createStyle(inputBook);//inputBook.createCellStyle();
//            style.setFillForegroundColor(IndexedColors.RED1.getIndex());
//            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            Font font = inputBook.createFont();
//            font.setColor(IndexedColors.WHITE.getIndex());
//            style.setFont(font);
//            logger.debug("service=ImportStructure;msg=" + "Style generated for xls.");

            //get headers
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            List<String> tableHeaders = new EnoviaWebserviceClient().getHeaders(cellIterator);
            LOGGER.debug("service=ImportStructure;msg=" + "got headers : " + tableHeaders);
            //add values
            Iterator<String> listItr = statusList.iterator();
            Iterator<String> attrListItr = unavailableList.iterator();
            Iterator<String> messageListItr = messageList.iterator();
            LOGGER.debug("service=ImportStructure;msg=" + "Adding values and styles to xls.");
		while (listItr.hasNext()) {
                    nextRow = iterator.next();
                    //set colour to cells
                    if(attrListItr.hasNext()) {
                        String attrList = attrListItr.next();
                        LOGGER.debug("service=ImportStructure;msg=" + "got unavailable attributes string." + attrList);
                        if (attrList != null && attrList != "") {
                            attrList = attrList.replaceAll("(\\[|\\])", "");
                            List<String> items = Arrays.asList(attrList.split("\\s*,\\s*"));
                            //while (itemsItr.hasNext())
                            for (int i = 0; i < items.size(); ++i) {
                                try {
                                    LOGGER.debug("service=ImportStructure;msg=" + "Item: "+items.get(i).trim());
                                    int index = tableHeaders.indexOf(items.get(i).trim());
                                    LOGGER.debug("service=ImportStructure;msg=" + "cell id: "+index);
                                    LOGGER.debug("service=ImportStructure;msg=" + "row id: "+nextRow.getRowNum());
                                    cell = firstSheet.getRow(nextRow.getRowNum()).getCell(index);
                                    LOGGER.debug("service=ImportStructure;msg=" + "cell: "+cell);
                                    cell.setCellStyle(style);
                                } catch (Exception e) {
                                    LOGGER.debug("service=ImportStructure;msg=" + "could not get cell" + e.getMessage());
                                }
                            }
                        }
                    }
                    firstSheet.setColumnWidth(lastColumn, (short) (256 * 25));
                    int currentRow = nextRow.getRowNum();
                    cell = firstSheet.getRow(currentRow).createCell(lastColumn);
                    cell.setCellValue(listItr.next());
                    firstSheet.setColumnWidth(lastColumn + 1, (short) (356 * 75));
                    cell = firstSheet.getRow(currentRow).createCell(lastColumn + 1);
                    cell.setCellValue(messageListItr.next());
		}

            //writing file
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
            String path = servletContext.getRealPath("/resources/download/xls");
            String expandObjectFileName = "Result";
            expandObjectFileName = expandObjectFileName + "_" + dateFormat.format(date) + ".xls";
            File outputfile = new File(path + "/" + expandObjectFileName);
            LOGGER.debug("service=ImportStructure;msg=" + "Result File: " + outputfile.getAbsolutePath());
            FileOutputStream output = new FileOutputStream(outputfile);
            inputBook.write(output);
            output.flush();
            output.close();
            inputBook.close();
            inputStream.close();
            return "resources/download/xls/" + expandObjectFileName;
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.debug("service=ImportStructure;msg=" + e.getMessage());
            throw new CustomException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.debug("service=ImportStructure;msg=" + e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }
    
    /**
     * adds a second sheet to template
     *
     * @param inputBook
     * @param rowIndex ([0 - rowIndex] rows to be copied from first sheet)
     * @param cbpKey

     */
    @Override
    public void addTemplateSheet(HSSFWorkbook inputBook, int rowIndex) {
        LOGGER.debug("service=ImportStructure;msg=" + "in addTemplateSheet method");
        HSSFSheet firstSheet = inputBook.getSheetAt(0);
        HSSFSheet templateSheet = inputBook.getSheet("templateSheet");

        if(templateSheet != null) {
            inputBook.removeSheetAt(inputBook.getSheetIndex("templateSheet"));
        }
        templateSheet = inputBook.createSheet("templateSheet");
        rowIndex = 0;

        //adding header
        HSSFRow dataRow = null;
        HSSFCell cell = null;
        cell = templateSheet.createRow(rowIndex).createCell(0);
        templateSheet.setColumnWidth((short) rowIndex, (short) (256 * 25));
        cell.setCellValue("Color Code");

        cell = templateSheet.getRow(rowIndex).createCell(1);
        cell.setCellValue("Description");

        //adding color meaning
         HSSFCellStyle style = createStyle(inputBook);
         cell = templateSheet.createRow(rowIndex + 1).createCell(0);
         templateSheet.setColumnWidth((short) rowIndex + 1, (short) (256 * 25));
         cell.setCellStyle(style);

         cell = templateSheet.getRow(rowIndex + 1).createCell(1);
         cell.setCellValue("Update Failed! Attribute does not exist.");
    }
    
    
    /**
     * creates style for inputBook
     *
     * @param inputBook
     * @return HSSFCellStyle
     */
    @Override
    public HSSFCellStyle createStyle(HSSFWorkbook inputBook) {

        HSSFCellStyle style = inputBook.createCellStyle();

        HSSFPalette palette = inputBook.getCustomPalette();
        short colorIndex = 45;
        palette.setColorAtIndex(colorIndex, (byte) 200, (byte) 0, (byte) 0);
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = inputBook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        LOGGER.debug("service=ImportStructure;msg=" + "Style generated for xls.");

        return style;
    }
}
