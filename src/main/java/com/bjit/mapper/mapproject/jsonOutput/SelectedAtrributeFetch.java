/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.jsonOutput;

import com.bjit.common.rest.app.service.background.rnp.RnPBomData;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public class SelectedAtrributeFetch {

    private static final Logger SELECTED_ATTRIBUTE_LOGGER = Logger.getLogger(SelectedAtrributeFetch.class);
    private static HashMap<String, String> DIRECTORY_MAP;

    public ResponseEntity getAllSelectableAttributes(HttpServletRequest httpRequest, HttpServletResponse response,
            @RequestParam(value = "type", required = false) String type, @RequestParam(value = "requester", required = false) String requester) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        String directoryMap = "";
        if (!NullOrEmptyChecker.isNullOrEmpty(requester)) {
            directoryMap = getDirectory(type, requester);
        } else {
            directoryMap = getDirectory(type, "");
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Resource resource = new ClassPathResource(directoryMap);
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

    public String getDirectory(String type, String requester) {

        if (requester.equalsIgnoreCase(BomExportType.HIMELLI)) {
            DIRECTORY_MAP = PropertyReader.getProperties("himelli.export.type.map.directory", true);
        } else if (requester.equalsIgnoreCase(BomExportType.BOM_EXPORT)) {
            DIRECTORY_MAP = PropertyReader.getProperties("bom.export.type.map.directory", true);
        } else {
            DIRECTORY_MAP = PropertyReader.getProperties("reporting.printing.map.directory", true);
        }

        SELECTED_ATTRIBUTE_LOGGER.debug("Directory Map " + DIRECTORY_MAP);
        String directoryMap = DIRECTORY_MAP.get(type);
        return directoryMap;
    }

}
