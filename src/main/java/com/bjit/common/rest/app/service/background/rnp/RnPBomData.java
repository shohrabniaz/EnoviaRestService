/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.background.rnp;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.JsonOutput;
import com.bjit.mapper.mapproject.jsonOutput.ReportResults;
import com.bjit.mapper.mapproject.jsonOutput.SelectedAtrributeFetch;
import com.bjit.mapper.mapproject.util.Constants;
import com.bjit.plmkey.ws.controller.expandobject.ExpandObjectUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public class RnPBomData {

    private static final Logger RnP_BOM_DATA_LOGGER = Logger.getLogger(RnPBomData.class);
    private static HashMap<String, String> DIRECTORY_MAP;

    public ResponseEntity generateBomData(HttpServletRequest httpRequest, HttpServletResponse response,
            String type, String name, String rev, String objectId, String expandLevel, String isDrawingInfoRequired, String isSummaryRequired, String attributeString, String printDelivery, String mainProjTitle, String psk, String subTitle, String product, String lang, String primaryLang, String secondaryLang, String format, String requestId, Context context, String docType
    ) throws MatrixException, IOException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = Constant.DEFAULT_TYPE;
        }
        if (NullOrEmptyChecker.isNullOrEmpty(expandLevel)) {
            expandLevel = Constant.DEFAULT_LEVEL;
        }
        if (NullOrEmptyChecker.isNullOrEmpty(name) && NullOrEmptyChecker.isNullOrEmpty(objectId)) {
            buildResponse = responseBuilder.addErrorMessage("Object ID or Object name, none of them are provided.").setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(format)
                && !Constants.ALLOWED_FORMATS.contains(format.toLowerCase())) {
            buildResponse = responseBuilder.addErrorMessage("Format: " + format + " is not supported.").setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(lang)
                && !Constants.LANG_MAP.containsKey(lang.toLowerCase())) {
            RnP_BOM_DATA_LOGGER.error("Language code " + lang + " is not supported.");
            buildResponse = responseBuilder.addErrorMessage("Language code: " + lang + " is not supported.").setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        BusinessObject businessObject = null;
        try {
            ExpandObjectUtil.addGraphicsSupport();

            if (NullOrEmptyChecker.isNullOrEmpty(objectId) && !NullOrEmptyChecker.isNullOrEmpty(name)) {
                MqlQueries mqlQuery = new MqlQueries();
                objectId = mqlQuery.getObjectId(context, type, name, rev);
                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                    RnP_BOM_DATA_LOGGER.error("Object of type = '" + type + "' and name = '" + name + "' is not present in the system");
                    throw new NullPointerException(Constants.TYPE_NAME_BE_NULL_EXCEPTION);
                }
            }
            SelectedAtrributeFetch selectedAtrribute = new SelectedAtrributeFetch();
            String directoryMap = selectedAtrribute.getDirectory(type, "");
            JsonOutput jsonOutput = new JsonOutput(directoryMap);
            jsonOutput.setRequestId(requestId);
            List<String> attributeList = extractRequiredAttributeList(httpRequest, response, type, directoryMap, attributeString, jsonOutput);
            jsonOutput.setAttributNames(attributeList);
            businessObject = new BusinessObject(objectId);
            businessObject.open(context);
            jsonOutput.setRequestLang(lang);
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            jsonOutput.setPositionAttributeNameMap(commonPropertyReader.getPropertyValue("enovia.position.attribute", true));
            if (Boolean.parseBoolean(isDrawingInfoRequired)) {
                jsonOutput.setDrawingDataRequired(true);
            }
            if (Boolean.parseBoolean(isSummaryRequired)) {
                jsonOutput.setIsSummaryRequired(true);
            }
            jsonOutput.setPrimaryLang(primaryLang);
            jsonOutput.setSecondaryLang(secondaryLang);
            ReportResults results = jsonOutput.prepareJsonFromExpandObject(context, businessObject, expandLevel, docType);
            HashMap<String, Object> rootItemParams = jsonOutput.getRootItemParams();

            String contentTypeHeader = httpRequest.getHeader("Content-Type");

            if (NullOrEmptyChecker.isNull(contentTypeHeader)) {
                httpRequest.setAttribute("Content-Type", "application/json");
                contentTypeHeader = "application/json";
            }
            responseBuilder.setData(results).addNewProperty("rootItemInfo", rootItemParams);
            HashMap<String, Object> deliveryParams = new LinkedHashMap<String, Object>();
            if (!NullOrEmptyChecker.isNullOrEmpty(printDelivery)) {
                if (Boolean.parseBoolean(printDelivery)) {
                    deliveryParams.put(PropertyReader.getProperty("delivery.main.project.tile"), mainProjTitle);
                    deliveryParams.put(PropertyReader.getProperty("delivery.project.search.key"), psk);
                    deliveryParams.put(PropertyReader.getProperty("delivery.subtitle"), subTitle);
                    deliveryParams.put(PropertyReader.getProperty("delivery.product"), product);
                    responseBuilder.addNewProperty(PropertyReader.getProperty("delivery.project.info"), deliveryParams);

                }
            }
            if (jsonOutput.getIsSummaryRequired()) {
                List<HashMap<String, String>> summaryResultList = jsonOutput.getSummaryResultList();
                responseBuilder.addNewSummaryProperty("summaryReportData", summaryResultList);
            }
            buildResponse = responseBuilder.setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK).buildResponse(contentTypeHeader);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception e) {
            RnP_BOM_DATA_LOGGER.error(e.getMessage());
            buildResponse = responseBuilder.addErrorMessage(e.getMessage() + " Could not generate json or xml String.").setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            businessObject.close(context);
        }
    }

    private ArrayList<String> extractRequiredAttributeList(HttpServletRequest httpRequest, HttpServletResponse response, String type, String directoryMap, String attributeString, JsonOutput jsonOutput) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, Exception {
        ArrayList<String> attributeList = new ArrayList<>();
        SelectedAtrributeFetch selectedAtrribute = new SelectedAtrributeFetch();
        ResponseEntity<String> allSelectableAttributeResponse = selectedAtrribute.getAllSelectableAttributes(httpRequest, response, type, "");
        Map<String, String> requiredAttrsMap = new HashMap<String, String>();
        Gson gson = new Gson();
        JsonElement jelement = new JsonParser().parse(allSelectableAttributeResponse.getBody());
        JsonObject jObject = jelement.getAsJsonObject();
        jObject = jObject.getAsJsonObject("data");
        JsonObject requiredAttrs = jObject.getAsJsonObject("required_attributes");
        if (NullOrEmptyChecker.isNullOrEmpty(attributeString)) {
            try {

                if (allSelectableAttributeResponse.getStatusCode() == HttpStatus.OK) {
                    requiredAttrsMap = (Map<String, String>) gson.fromJson(requiredAttrs.toString(), requiredAttrsMap.getClass());
                    JsonObject reportAttrs = jObject.getAsJsonObject("report_attributes");
                    Map<String, String> reportAttrsMap = new HashMap<String, String>();
                    reportAttrsMap = (Map<String, String>) gson.fromJson(reportAttrs.toString(), reportAttrsMap.getClass());
                    reportAttrsMap.putAll(requiredAttrsMap);
                    attributeString = "";
                    for (Map.Entry<String, String> entry : requiredAttrsMap.entrySet()) {
                        attributeString += entry.getValue() + ",";
                        attributeList.add(entry.getValue());
                    }

                } else {
                    throw new Exception("No attributes found.");
                }
            } catch (Exception exc) {
                attributeString = "";
                RnP_BOM_DATA_LOGGER.error("Exception while fetching all attribute list: " + exc.getMessage());
            }
        } else {
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
                    HashMap<String, ArrayList<String>> updatedAlternativeMap = jsonOutput.getAlternativeAttributeMap();
                    updatedAlternativeMap.put(fieldLabelNode.getTextContent(), alternativeLabels);
                    jsonOutput.setAlternativeAttributeMap(updatedAlternativeMap);
                    hashSet.addAll(alternativeLabels);
                }
            }
            attributeList.addAll(hashSet);
            requiredAttrsMap = (Map<String, String>) gson.fromJson(requiredAttrs.toString(), requiredAttrsMap.getClass());
            for (Map.Entry<String, String> entry : requiredAttrsMap.entrySet()) {
                attributeString += entry.getValue() + ",";
                attributeList.add(entry.getValue());
            }
        }
        return attributeList;
    }

}
