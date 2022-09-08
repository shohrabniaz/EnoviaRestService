/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.bomExport;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.BomExportResults;
import com.bjit.mapper.mapproject.jsonOutput.JsonOutput;
import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.mapper.mapproject.jsonOutput.BomExportType;
import com.bjit.mapper.mapproject.jsonOutput.SelectedAtrributeFetch;
import com.bjit.mapper.mapproject.util.Constants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Tahmid
 */
public class BomExportUtil {

    private static final org.apache.log4j.Logger EXPORT_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(BomExportUtil.class);
    private static HashMap<String, String> DIRECTORY_MAP;

    public HashMap<String, String> getUrlParamValues(String type, String name, String rev, String objId, String expandLevel, String format, String lang, String primaryLang, String secondaryLang, String requestId, String requester, String treeView) throws IOException {
        HashMap<String, String> urlParams = new HashMap<>();

        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = Constant.DEFAULT_TYPE;;
        }
        urlParams.put("type", type);
        urlParams.put("name", name);
        urlParams.put("rev", rev);
        urlParams.put("objId", objId);

        if (format == null) {
            format = Constants.JSON;
        }
        urlParams.put("format", format);

        if (NullOrEmptyChecker.isNullOrEmpty(expandLevel)) {
            expandLevel = Constant.DEFAULT_LEVEL;;

        }
        urlParams.put("expandLevel", expandLevel);
        if (lang == null) {
            lang = Constants.ENGLISH;
        }
        urlParams.put("lang", lang);
        if (primaryLang == null) {
            primaryLang = Constants.ENGLISH;
        }
        urlParams.put("primaryLang", primaryLang);
        if (secondaryLang == null) {
            secondaryLang = "";
        }
        urlParams.put("secondaryLang", secondaryLang);
        if (requestId == null) {
            requestId = "";
        }
        urlParams.put("requestId", requestId);
        if (requester == null) {
            requester = "";
        }
        urlParams.put("requester", requester);
        if (treeView == null) {
            treeView = Constants.FALSE;
        }
        urlParams.put("treeView", treeView);
        return urlParams;
    }

    public String validateUrlParams(HashMap<String, String> urlParams) {
        if (NullOrEmptyChecker.isNullOrEmpty(urlParams.get("name"))
                && NullOrEmptyChecker.isNullOrEmpty(urlParams.get("objId"))) {
            return "Object ID or Object name, none of them are provided.";
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(urlParams.get("format"))
                && !Constants.ALLOWED_FORMATS.contains(urlParams.get("format").toLowerCase())) {
            return "Format: " + urlParams.get("format") + " is not supported.";
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(urlParams.get("lang"))
                && !Constants.LANG_MAP.containsKey(urlParams.get("lang").toLowerCase())) {
            return "Language code: " + urlParams.get("lang") + " is not supported.";
        }
        try {
            CommonPropertyReader commonProperty = new CommonPropertyReader();
            HashMap<String, String> languageMap = commonProperty.getPropertyValue("gts.table.language", true);
            if (NullOrEmptyChecker.isNullOrEmpty(urlParams.get("primaryLang"))) {
                return "Primary language code cannot be empty.";
            } else if (!languageMap.containsKey(urlParams.get("primaryLang").toLowerCase())) {
                return "Primary language code " + urlParams.get("primaryLang") + " is not supported.";
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(urlParams.get("secondaryLang"))
                    && !languageMap.containsKey(urlParams.get("secondaryLang").toLowerCase())) {
                return "Secondary language code " + urlParams.get("secondaryLang") + " is not supported.";
            }
        } catch (IOException ex) {
            EXPORT_UTIL_LOGGER.debug(ex.getMessage());
        }
        return "";
    }

    public String getAllSelectableAttributes(String type, JsonOutput jsonOutput, String directoryMap) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, Exception {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Resource resource = new ClassPathResource(directoryMap);
        Document doc = dBuilder.parse(resource.getURI().getPath());
        doc.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String attributNames = "";
        String expression = "//attribute/field_label";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        HashMap<String, HashMap> allProperties = new LinkedHashMap<>();
        HashMap<String, String> reportAttributeMap = new LinkedHashMap<>();
        HashMap<String, String> requiredAttributes = new HashMap<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            boolean isVisibleInList = Boolean.parseBoolean(node.getAttributes().getNamedItem("isVisibleInList").getTextContent());
            if (!isVisibleInList) {
                continue;
            }
            String value = node.getTextContent();
            String key = node.getTextContent();
            boolean isPreSelected = Boolean.parseBoolean(node.getAttributes().getNamedItem("isPreSelected").getTextContent());
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
            reportAttributeMap.put(key, value);
            if (isPreSelected) {
                requiredAttributes.put(key, value);
                if (i < nodeList.getLength() - 1) {
                    attributNames += value + ",";
                } else {
                    attributNames += value;
                }

            }
        }

        EXPORT_UTIL_LOGGER.debug(attributNames);
        return attributNames;
    }

    public ArrayList<String> extractRequiredAttributeList(String directoryMap, JsonOutput jsonOutput, String attributeString) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        ArrayList<String> attributeList = new ArrayList<>();
        if (!NullOrEmptyChecker.isNullOrEmpty(attributeString)) {
            LinkedHashSet<String> hashSet = new LinkedHashSet<>();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Resource resource = new ClassPathResource(directoryMap);
            Document doc = dBuilder.parse(resource.getURI().getPath());
            doc.getDocumentElement().normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            String[] attributeArr = attributeString.split(",");
            for (int i = 0; i < attributeArr.length; i++) {
                String expression = "//attribute[field_label='" + attributeArr[i] + "']/field_label";
                NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
                Node fieldLabelNode = nodeList.item(0);
                if (NullOrEmptyChecker.isNull(fieldLabelNode)) {
                    continue;
                }
                hashSet.add(fieldLabelNode.getTextContent());
                String alternatives = fieldLabelNode.getAttributes().getNamedItem("alt-label").getTextContent();
                if (!NullOrEmptyChecker.isNullOrEmpty(alternatives)) {
                    ArrayList<String> alternativeLabels = new ArrayList<>();
                    alternativeLabels.addAll(Arrays.asList(alternatives.split(",")));
                    EXPORT_UTIL_LOGGER.debug("Alternatives for " + fieldLabelNode.getTextContent() + " are :" + alternativeLabels.toString());
                    HashMap<String, ArrayList<String>> updatedAlternativeMap = jsonOutput.getAlternativeAttributeMap();
                    updatedAlternativeMap.put(fieldLabelNode.getTextContent(), alternativeLabels);
                    jsonOutput.setAlternativeAttributeMap(updatedAlternativeMap);
                    hashSet.addAll(alternativeLabels);
                }
            }
            attributeList.addAll(hashSet);
            EXPORT_UTIL_LOGGER.debug("Attribute List: " + attributeList);
        }
        return attributeList;
    }

    public ResponseEntity generateBomExportData(HttpServletRequest httpRequest, HttpServletResponse response, HashMap<String, String> urlParams, String attributeString, Context context, String docType) throws MatrixException, IOException {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        String directoryMap = "";
        String objectId = urlParams.get("objId");
        String type = urlParams.get("type");
        String requester = urlParams.get("requester");
        BomExportUtil bomExportUtil = new BomExportUtil();
        String validationMessage = bomExportUtil.validateUrlParams(urlParams);
        if (!NullOrEmptyChecker.isNullOrEmpty(validationMessage)) {
            buildResponse = responseBuilder.addErrorMessage(validationMessage)
                    .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                    .buildResponse();;
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        BusinessObject businessObject = null;
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(objectId)
                    && !NullOrEmptyChecker.isNullOrEmpty(urlParams.get("name"))) {
                MqlQueries mqlQuery = new MqlQueries();
                objectId = mqlQuery.getObjectId(context, type, urlParams.get("name"), urlParams.get("rev"));
                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                    throw new NullPointerException(Constants.TYPE_NAME_REVISION_BE_NULL_EXCEPTION);
                }
            }

            SelectedAtrributeFetch selectedAtrribute = new SelectedAtrributeFetch();
            if (!NullOrEmptyChecker.isNullOrEmpty(requester) && requester.equals(BomExportType.HIMELLI)) {
                directoryMap = selectedAtrribute.getDirectory(type, requester);
            } else {
                directoryMap = selectedAtrribute.getDirectory(type, BomExportType.BOM_EXPORT);
            }

            JsonOutput jsonOutput = new JsonOutput(directoryMap);
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            jsonOutput.setPositionAttributeNameMap(commonPropertyReader.getPropertyValue("enovia.position.attribute", true));
            if (NullOrEmptyChecker.isNullOrEmpty(attributeString)) {
                try {
                    attributeString = bomExportUtil.getAllSelectableAttributes(type, jsonOutput, directoryMap);
                } catch (Exception exc) {
                    attributeString = "";
                }
            }
            List<String> attributeList = bomExportUtil.extractRequiredAttributeList(directoryMap, jsonOutput, attributeString);
            attributeList.add("bomLines");
            businessObject = new BusinessObject(objectId);
            businessObject.open(context);
            jsonOutput.setRequestLang(urlParams.get("lang"));
            jsonOutput.setRequester(urlParams.get("requester"));
            jsonOutput.setAttributNames(attributeList);
            jsonOutput.setPrimaryLang(urlParams.get("primaryLang"));
            jsonOutput.setSecondaryLang(urlParams.get("secondaryLang"));
            jsonOutput.setRequester(urlParams.get("requester"));
            BomExportResults results = jsonOutput.prepareJsonForBomExport(context, businessObject, urlParams, docType);
            String contentTypeHeader = httpRequest.getHeader("Content-Type");
            if (NullOrEmptyChecker.isNull(contentTypeHeader)) {
                httpRequest.setAttribute("Content-Type", "application/json");
                contentTypeHeader = "application/json";
            }
            responseBuilder.setData(results);
            if (!urlParams.get("format").equalsIgnoreCase(Constants.JSON)) {
                HashMap<String, Object> rootItemParams = jsonOutput.getRootItemParams();
                responseBuilder.addNewProperty("rootItemInfo", rootItemParams);
            }
            buildResponse = responseBuilder.setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK).buildResponse(contentTypeHeader);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception e) {
            buildResponse = responseBuilder.addErrorMessage(e.getMessage() + " Could not generate json or xml String.").setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            businessObject.close(context);
        }
    }

    public String getLatestRevision(Context context, String type, String name) {
        StringBuilder qureyBuilder = new StringBuilder();
        String revList = "";
        qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" * where \" attribute[V_isLastVersion].value == TRUE \" select revision dump |");
        String bundleQuery = qureyBuilder.toString();
        revList = queryParser(context, bundleQuery);
        if (NullOrEmptyChecker.isNullOrEmpty(revList)) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder = queryBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" * select revision dump |");
            bundleQuery = queryBuilder.toString();
            revList = queryParser(context, bundleQuery);
        }
        return revList;
    }

    private String queryParser(Context context, String bundleQuery) {
        String revList = "";
        String queryResult = null;
        try {
            queryResult = MqlUtil.mqlCommand(context, bundleQuery);
        } catch (FrameworkException ex) {
            EXPORT_UTIL_LOGGER.error(ex.getMessage());
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(queryResult)) {
            String[] tempList = queryResult.split("[\\r\\n]+");
            for (int i = 0; i < tempList.length; i++) {
                String[] tempSingleList = tempList[i].split("\\|");
                revList = tempSingleList[3];
            }
        }
        return revList;
    }
}
