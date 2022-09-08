/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.service;

import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.model.ExpandObjectRequestForm;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.EwcUtilities;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.underscore.lodash.U;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import matrix.db.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import static jdk.nashorn.internal.runtime.Debug.id;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Kayum-603
 */
@Service
public class ExpandObjectServiceImpl implements ExpandObjectService {
    private Gson gson = new GsonBuilder().serializeNulls().create();
    @Autowired
    ServletContext servletContext;

    private static final Logger LOGGER = Logger.getLogger(ExpandObjectServiceImpl.class);
    EwcUtilities ewcUtilities = new EwcUtilities();
    ExpandObjectRequestForm expandObjectRequest = new ExpandObjectRequestForm();

    @Override
    public String getXlsOutput(HttpSession httpSession, Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm, List<String> unchangableAttr) throws CustomException {
        try {
            LOGGER.debug("Started generating XLS output");
            String finalJson;
            finalJson = getFinalProccessedJson(expandObjectForm, context, physicalId, objectParamList, objectRelAttrList);
            
            gson = new GsonBuilder().serializeNulls().create();
            JsonElement jelem = gson.fromJson(finalJson, JsonElement.class);
            JsonObject jsonObject = jelem.getAsJsonObject();  
            LOGGER.debug("Json object getXlsOutput method: "+jsonObject);
            
            String xlsFilePath = generateJSONToXLS(jsonObject, expandObjectForm, unchangableAttr);
            expandObjectForm.setOutput(xlsFilePath);
            expandObjectRequest.setExpndObj(expandObjectForm);
            LOGGER.debug("service=ExportStructure;msg=" + "returning to controller.");
            
            return xlsFilePath;
        } catch (MatrixException ex) {
            java.util.logging.Logger.getLogger(ExpandObjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getJsonOutput(HttpSession httpSession, Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException {
        LOGGER.debug("Started generating json output");
        ResponseEntity<String> response;
        String jsonPrettyPrintString = null;
        String finalJson;
        try {
            finalJson = getFinalProccessedJson(expandObjectForm, context, physicalId, objectParamList, objectRelAttrList);
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(finalJson, Object.class);
            jsonPrettyPrintString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            //expandObjectForm.setPassword(expandObjectForm.getPassword());
            expandObjectForm.setOutput(jsonPrettyPrintString);
            expandObjectRequest.setExpndObj(expandObjectForm);
            LOGGER.debug("service=ExportStructure;msg=" + "returning to controller.");
        } catch (IOException ex) {
            LOGGER.error("Exception: " + ex);
            throw new CustomException(ex.getMessage());
        } catch (MatrixException ex) {
            java.util.logging.Logger.getLogger(ExpandObjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jsonPrettyPrintString;
    }

    @Override
    public String getXmlOutput(HttpSession httpSession, Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException {
        String xml;
        String finalOutput = null;
        String root = "root";
        String finalJson;
        try {
            finalJson = getFinalProccessedJson(expandObjectForm, context, physicalId, objectParamList, objectRelAttrList);
            System.out.println("FinalJson with Root: "+finalJson);
//            JSONObject json = new JSONObject(finalJson);
//            JSONObject outputJson = ewcUtilities.fixJsonKey(json);
//            System.out.println("outputJson1: "+outputJson);
          
          //JsonElement jsonElement = new JsonParser().parse(finalJson);
          // JsonObject jsonObject = jsonElement.getAsJsonObject();
         
          // JsonArray properties = jsonObject.getAsJsonArray("results");
          //System.out.println("properties: "+properties);
    
           String  outputJsonStringWithKeyFixed = ewcUtilities.fixJsonKey(finalJson);
           finalOutput = U.jsonToXml(outputJsonStringWithKeyFixed);  
           //System.out.println("XML final: "+xml); 
           // System.out.println("outputJson2: "+outputJson1);
           // xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<" + root + ">"
           //         + XML.toString(outputJson) + "</" + root + ">";
           //System.out.println("XML response: " + xml);
           //finalOutput = ewcUtilities.format(xml);

            expandObjectForm.setOutput(finalOutput);
            expandObjectRequest.setExpndObj(expandObjectForm);
            LOGGER.debug("service=ExportStructure;msg=" + "returning to controller.");
        } catch (CustomException | JSONException ex) {
            LOGGER.error("Exception::" + ex);
            throw new CustomException(ex.getMessage());
        } catch (MatrixException ex) {
            java.util.logging.Logger.getLogger(ExpandObjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return finalOutput;
    }

    @Override
    public ExpandObjectForm populateServiceInfo(ExpandObjectForm expandObjectForm, String fileName) throws CustomException {
        InputStream is = null;
        //logger.debug("Populating Service Info from :: " + "/" + fileName + ".xml");
        File file = new File(getClass().getResource("/" + fileName + ".xml").getFile());
        try {
            is = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception Occured: " + e);
            throw new CustomException(e.getMessage());
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element element = doc.getDocumentElement();

            String relationshipPattern = element.getElementsByTagName("relationshipPattern").item(0).getTextContent();
            //logger.debug("Provided Relationship Pattern: " + relationshipPattern);
            expandObjectForm.setRelationshipPattern(relationshipPattern);

            String typePattern = element.getElementsByTagName("typePattern").item(0).getTextContent();
            LOGGER.debug("Provided Type Pattern: " + typePattern);
            System.out.println("Provided Type Pattern: " + typePattern);
            expandObjectForm.setTypePattern(typePattern);

            Boolean getTo = Boolean.parseBoolean(element.getElementsByTagName("getTo").item(0).getTextContent());
            LOGGER.debug("Provided get to: " + getTo);
            expandObjectForm.setGetTo(getTo);

            Boolean getFrom = Boolean.parseBoolean(element.getElementsByTagName("getFrom").item(0).getTextContent());
            LOGGER.debug("Provided get from: " + getFrom);
            expandObjectForm.setGetFrom(getFrom);

            int limit = Integer.parseInt(element.getElementsByTagName("limit").item(0).getTextContent());
            LOGGER.debug("Provided Limit: " + limit);
            expandObjectForm.setLimit(limit);

            String tableName = element.getElementsByTagName("tableName").item(0).getTextContent();
            LOGGER.debug("Provided Table Name: " + tableName);
            expandObjectForm.setTableName(tableName);

            Boolean objectIdFlag = Boolean.parseBoolean(element.getElementsByTagName("objectIdFlag").item(0).getTextContent());
            LOGGER.debug("Provided Object Id Flag: " + objectIdFlag);
            expandObjectForm.setObjectIdFlag(objectIdFlag);

            Boolean depthFlag = Boolean.parseBoolean(element.getElementsByTagName("depthFlag").item(0).getTextContent());
            LOGGER.debug("Provided Depth Flag: " + depthFlag);
            expandObjectForm.setDepthFlag(depthFlag);

            // LOGGER.debug("List of ExpandObjectForm value : " + expandObjectForm.toString());
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NumberFormatException ex) {
            throw new CustomException(ex.getMessage());
        }
        return expandObjectForm;
    }

    @Override
    public String getSelectedTypePatternListExpression(ExpandObjectForm expandObjectForm) {
        String typePattern = "";
        String typeName;
        List<String> selectedTypeList = expandObjectForm.getSelectedTypeList();
        LOGGER.debug("Selected Type Pattern size() " + selectedTypeList.size());
        for (int i = 0; i < selectedTypeList.size(); i++) {
            typeName = selectedTypeList.get(i);
            if (i != 0) {
                typePattern = typePattern + "," + typeName;
            } else {
                typePattern = typePattern + typeName;
            }
        }
        return typePattern;
    }

    @Override
    public ResponseEntity<String> getRootObjectProperties(String physicalId, String host, HttpHeaders headers, List<String> objectParamList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException {
        try {
            LOGGER.debug("Getting properties for root object");
            System.out.println("<<<<<<<<< Getting properties for root object >>>>>>>>>");
            List<String> rootObjectPropertiesList = new ArrayList<>();
            String ticket = ewcUtilities.getTicket(host, expandObjectForm.getUserID(), expandObjectForm.getPassword());
            String uriRootObject = host + "/resourcesbjit/trm/browsing/attributeListbjit" + ticket;
            rootObjectPropertiesList.addAll(objectParamList);
            rootObjectPropertiesList.addAll(objectRelAttrList);
            LOGGER.debug("Final request param for root object: " + rootObjectPropertiesList.toString());
            StringBuilder requestJsonBuilder = ewcUtilities.getRootObjectResquestJson(physicalId, rootObjectPropertiesList);
            LOGGER.debug("Root object properties: " + requestJsonBuilder);
            ResponseEntity<String> response = ewcUtilities.getPostRestResponse(uriRootObject, requestJsonBuilder, headers);
            LOGGER.debug("Response for root object: " + response.getBody());
            System.out.println(">>>>>>>>>> Response for root object: " + response.getBody());
            return response;
        } catch (CustomException ex) {
            LOGGER.error("Exception occured: " + ex);
            throw new CustomException(ex.getMessage());
        }

    }

    public String getFinalProccessedJson(ExpandObjectForm expandObjectForm, Context context, String physicalId, List<String> objectParamList, List<String> objectRelAttrList) throws CustomException, MatrixException {
        try {
            System.out.println(">>>>>>>>>> PHYSICAL ID : " + physicalId);
            ResponseEntity<String> response;
            String finalJson;
            String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
            System.out.println(">>>>>>>> host name : " + host);
            String ticket = ewcUtilities.getTicket(host, expandObjectForm);
            //String uri = host + "/resources/trm/browsing/model/expand" + ticket;
            String uri = host + "/resourcesbjit/trm/browsing/model/exobejecttest" + ticket;
            StringBuilder requestJsonBuilder = ewcUtilities.getExpandObjectResquestJson(physicalId, objectParamList, expandObjectForm, objectRelAttrList);
            LOGGER.debug("final requestJson: " + requestJsonBuilder.toString());
            System.out.println(">>>>>>>> final requestJson: " + requestJsonBuilder.toString());
            HttpHeaders headers = ewcUtilities.getHttpRequestHeaders(expandObjectForm.getSecurityContext());
            LOGGER.debug("Headers: " + headers);
            System.out.println("Headers: " + headers);
            response = ewcUtilities.getPostRestResponse(uri, requestJsonBuilder, headers);
            LOGGER.debug("Response - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
            LOGGER.debug("Response: " + response.getBody());
            System.out.println("Response - status (" + response.getStatusCode() + ") has body: " + response.hasBody()   );
            System.out.println(" >>>>>> response body : " + response.getBody());
            ResponseEntity<String> rootObjectProperties = getRootObjectProperties(physicalId, host, headers, objectParamList, objectRelAttrList, expandObjectForm);
            System.out.println(">>>>> root object properties : " + rootObjectProperties);
            System.out.println("<<<<<<<< root object properties body : " + rootObjectProperties.getBody() + "\n\n\n");
            String finalRootObject = ewcUtilities.getModifiedRootObjectJson(expandObjectForm, rootObjectProperties.getBody());
            LOGGER.debug("Root object : " + finalRootObject);
            System.out.println("Root object : " + finalRootObject);
            finalJson = ewcUtilities.getFinalJsonWithRootObjectAdded(finalRootObject, response.getBody());
            LOGGER.debug("finalJson : " + finalJson);
            System.out.println("finalJson : " + finalJson);
            if (expandObjectForm.getSelectedItem().contains("Classification Path")) {
                LOGGER.debug("Started getting class path");
                finalJson = ewcUtilities.getFinalJsonWithClassiificationPath(context, finalJson);
                LOGGER.debug("Final json object with class path: " + finalJson);              
            }
            return finalJson;
        } catch (CustomException ex) {
            LOGGER.error("Occured exception while getting final json: " + ex);
            throw new CustomException(ex.getMessage());
        }

    }

    public String generateJSONToXLS(JsonObject jsonObject, ExpandObjectForm expandObjectForm, List<String> unchangableAttr) throws CustomException {
        
        String filePath = null;
        // Creating a Workbook
        try {
            HSSFWorkbook workBook = new HSSFWorkbook();
            HSSFSheet spreadSheet = workBook.createSheet("spreadSheet");

            HSSFRow dataRow = null;
            HSSFCell cell = null;

            dataRow = spreadSheet.createRow(10);

            List<String> selectedHeader = getAttributeList(jsonObject);
            LOGGER.debug("Found headers: "+selectedHeader);
            int typeIndex = selectedHeader.indexOf("type");
            
            LOGGER.debug("XLS Header preperation starting");
            for (int columnNumber = 0; columnNumber < selectedHeader.size(); ++columnNumber) {
                spreadSheet.setColumnWidth((short) columnNumber, (short) (256 * 25));
                cell = spreadSheet.getRow(10).createCell(columnNumber);
                if (unchangableAttr.contains(selectedHeader.get(columnNumber))) {
                    cell.setCellValue(selectedHeader.get(columnNumber));
                } else {
                    cell.setCellValue(selectedHeader.get(columnNumber).replace("_", " "));
                }
            }

            //print data/rows
            LOGGER.debug("XLS Data preperation starting");
            //JSONArray jSONArray = json.getJSONArray("results");
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            for (int rowNumber = 1; rowNumber <= jsonArray.size(); ++rowNumber) {
                LOGGER.debug(jsonArray.get(rowNumber - 1));
                dataRow = spreadSheet.createRow(rowNumber + 10);
               // JSONObject jSONObject = (JSONObject) jSONArray.get(rowNumber - 1);
                JsonObject jsonbject = (JsonObject) jsonArray.get(rowNumber - 1);
                int noOfSpaces = 0;
                //noOfSpaces = jSONObject.getInt("depth");
                noOfSpaces = jsonbject.getAsJsonPrimitive("depth").getAsInt();
                String treeSpace = "";
                for (int spaceNumber = 0; spaceNumber < noOfSpaces; spaceNumber++) {
                    treeSpace = treeSpace.concat("  ");
                }
                for (int columnNumber = 0; columnNumber < selectedHeader.size(); ++columnNumber) {
                    cell = spreadSheet.getRow(rowNumber + 10).createCell(columnNumber);
                    if (columnNumber == typeIndex) {
                        cell.setCellValue(treeSpace + jsonbject.get(selectedHeader.get(columnNumber)).getAsString());
                    } else {
                        //if (jSONObject.isNull(selectedHeader.get(columnNumber))) {
                        JsonElement jsonElement = jsonbject.get(selectedHeader.get(columnNumber));
                        System.out.println("Instance of :"+jsonElement.isJsonNull());
                        System.out.println("Header : "+selectedHeader.get(columnNumber)+" val"+jsonElement);
                        if (jsonElement.isJsonNull()){
                            cell.setCellValue("");
                        } else {
                            //cell.setCellValue((String) jSONObject.get(selectedHeader.get(columnNumber)));
                            cell.setCellValue(jsonElement.getAsString());
                        }
                    }
                }
            }

            //inserting template header
            dataRow = spreadSheet.createRow(0);
            cell = spreadSheet.getRow(0).createCell(2);
            cell.setCellValue("Powered By : Valmet");
            cell = spreadSheet.getRow(0).createCell(3);
            cell.setCellValue("Type : " + expandObjectForm.getType());

            dataRow = spreadSheet.createRow(1);
            cell = spreadSheet.getRow(1).createCell(2);
            cell.setCellValue("Application Name : VSIX");
            cell = spreadSheet.getRow(1).createCell(3);
            cell.setCellValue("Name : " + expandObjectForm.getName());

            dataRow = spreadSheet.createRow(2);
            cell = spreadSheet.getRow(2).createCell(2);
            cell.setCellValue("Date : " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            cell = spreadSheet.getRow(2).createCell(3);
            cell.setCellValue("Revision : " + expandObjectForm.getRevision());

            FileInputStream imageStream = new FileInputStream(getClass().getResource("/img/ValmetLogo.jpg").getFile());
            CreationHelper creationHelper = workBook.getCreationHelper();
            Drawing drawing = spreadSheet.createDrawingPatriarch();

            ClientAnchor anchor = creationHelper.createClientAnchor();
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

            int pictureIndex = workBook.addPicture(IOUtils.toByteArray(imageStream), Workbook.PICTURE_TYPE_PNG);

            anchor.setCol1(0);
            anchor.setRow1(0);
            anchor.setRow2(0);
            anchor.setCol2(1);
            Picture picture = drawing.createPicture(anchor, pictureIndex);
            picture.resize();

            //writing file
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
            String path = servletContext.getRealPath("/resources/download/xls");
            String expandObjectFileName = expandObjectForm.getType() + "_" + expandObjectForm.getName() + "_" + expandObjectForm.getRevision();
            expandObjectFileName = expandObjectFileName + "_" + dateFormat.format(date) + ".xls";
            File file = new File(path + "/" + expandObjectFileName);

            FileOutputStream output = new FileOutputStream(file);
            workBook.write(output);
            output.flush();
            output.close();
            workBook.close();
            filePath = "resources/download/xls/" + expandObjectFileName;
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        }

        LOGGER.info("Generated XLS path : " + filePath);
        return filePath;
    }
    
    public List<String> getAttributeList(JsonObject json) {
        LOGGER.debug("Parsing JSON object attribute");
        JsonObject jsonObject = (JsonObject) json.getAsJsonArray("results").get(0);
        List<String> attributeList = new ArrayList<>();
        Set<String> keys = jsonObject.keySet();
        for(String key: keys)
        {
            attributeList.add(key);
        }
        return attributeList;
    }
    }
