package com.bjit.common.rest.app.service.controller.export.report.single_level.provider;

import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
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
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides Report Attributes
 * @author BJIT
 * @version 1.0
 * @since 1.0
 */
public class ReportAttributeProvider {
    private static final Logger REPORT_ATTR_PROVIDER_LOGGER = Logger.getLogger(ReportAttributeProvider.class);
    
    /**
     * Extracts alternative attribute names that are not present in UI, however
     * required in order to fetch value for same field from different object.
     * @param mapAbsoluteDirectory
     * @param attributeString
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws Exception 
     */
    public Map<String, Object> gatherReportAttributeList(String mapAbsoluteDirectory, String attributeString) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, Exception {
        ArrayList<String> attributeList = new ArrayList<>();
        HashMap<String, ArrayList<String>> alternativeAttributeMap = new HashMap<>();
        Map<String, String> requiredAttrsMap = new HashMap<>();
        
        ResponseEntity<String> allSelectableAttributeResponse = getAllSelectableAttributes(mapAbsoluteDirectory);
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
                    Map<String, String> reportAttrsMap = new HashMap<>();
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
                REPORT_ATTR_PROVIDER_LOGGER.error("Exception while fetching all attribute list: " + exc.getMessage());
            }
        } else {
            LinkedHashSet<String> hashSet = new LinkedHashSet<>();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Resource resource = new ClassPathResource(mapAbsoluteDirectory);
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
                    HashMap<String, ArrayList<String>> updatedAlternativeMap = alternativeAttributeMap;
                    updatedAlternativeMap.put(fieldLabelNode.getTextContent(), alternativeLabels);
                    alternativeAttributeMap = updatedAlternativeMap;
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
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attributeList", attributeList);
        responseMap.put("alternativeAttributeMap", alternativeAttributeMap);
        return responseMap;
    }
    
    /**
     * Wrapper function for Report Business Model class parameter
     * @param businessModel
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws Exception 
     */
    public Map<String, Object> gatherReportAttributeList(ReportBusinessModel businessModel) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, Exception {
        return gatherReportAttributeList(businessModel.getMapAbsoluteDirectory(), 
                businessModel.getParameter().getAttributeListString());
    }
    
    /**
     * Provides Required or Default Attributes for report Generation
     * @param mapAbsoluteDirectory
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws Exception 
     */
    private ResponseEntity getAllSelectableAttributes(String mapAbsoluteDirectory) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Resource resource = new ClassPathResource(mapAbsoluteDirectory);
        Document doc = dBuilder.parse(resource.getURI().getPath());
        doc.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
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
            }
        }
        allProperties.put("report_attributes", reportAttributeMap);
        allProperties.put("required_attributes", requiredAttributes);
        buildResponse = responseBuilder
                .setData(allProperties)
                .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK)
                .buildResponse();
        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
    }
    
    /**
     * Add extra Attributes in List for Business Implementation purpose
     * @param reportBusinessModel 
     */
    private void addExtraAttributes(ReportBusinessModel reportBusinessModel) {
        ArrayList<String> attributeList = reportBusinessModel.getAttributeList();
        reportBusinessModel.setIsTitleRequired(attributeList.isEmpty() || attributeList.contains("Title"));
        if (reportBusinessModel.isIsTitleRequired()) {
            attributeList.add("Term_ID");
        }
        attributeList.add("Technical Designation");
        attributeList.add("Revision Comment");
        attributeList.add("Reference");
        attributeList.add("item common text");
        attributeList.add("bom common text");
        attributeList.add("PCS");
    }
    
    /**
     * Provides Attribute List for Specific Report Type that will be used to fetch Report Data
     * @param businessModel
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws Exception 
     */
    public void provideAttributeData(ReportBusinessModel businessModel) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, Exception {
        Map<String, Object> attributeResponseMap = gatherReportAttributeList(businessModel);
        businessModel.setAttributeList((ArrayList<String>) attributeResponseMap.get("attributeList"));
        addExtraAttributes(businessModel);
        businessModel.setAlternativeAttributeMap((HashMap<String, ArrayList<String>>) attributeResponseMap.get("alternativeAttributeMap"));
    }
}
