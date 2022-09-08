/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.autoname.controller;

import com.bjit.common.rest.app.service.autoname.service.AutonameGenerateService;
import com.bjit.common.rest.app.service.autoname.validator.AutonameValidator;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * @created 2021-04-06
 * @author Sudeepta
 */
@Controller
@RequestMapping(path = "/autoname")
public class AutonameGenerateController {

    /* Logger variable for this class */
    private static final Logger LOGGER = Logger.getLogger(AutonameGenerateController.class);

    @Autowired
    private IResponse responseBuilder;

    @Autowired
    private AutonameValidator autonameValidator;

    @Autowired
    private AutonameGenerateService autonameGenerateService;

    /**
     * Generate autoname by type depend on user parameter
     *
     * @param httpServletRequest
     * @param type object type required
     * @param prefix object name prefix optional
     * @param affix object name affix optional
     * @param suffix object name suffix optional
     * @param format XML and JSON response format. default value json
     * @param objectCount number of autoname need to generated
     * @param source client application.
     * @return ResponseEntity JSON or XML response
     */
    @ResponseBody
    @RequestMapping(value = "generate/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity generateAutoname(HttpServletRequest httpServletRequest,
            @PathVariable(value = "type") String type,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "affix", required = false) String affix,
            @RequestParam(value = "suffix", required = false) String suffix,
            @RequestParam(value = "format", required = false, defaultValue = "json") String format,
            @RequestParam(value = "objectCount", required = false, defaultValue = "1") int objectCount,
            @RequestParam(value = "source", required = false, defaultValue = "Default") String source
    ) {
        LOGGER.info("======================Start========================");
        LOGGER.info("type: " + type + ", prefix: " + prefix + ", affix: " + affix + ", suffix: " + suffix + ", format: " + format + ", objectCount: " + objectCount + ", source: " + source);

        /* User input validation */
        if (!autonameValidator.isValid(type, objectCount)) {
            String errorMessage = autonameValidator.getErrorMessage(type, objectCount);
            LOGGER.info("validation errorMessage : " + errorMessage);
            return prepareResponse(format, type, null, Status.FAILED, errorMessage);
        }

        Context context = null;
        try {
            context = new CreateContext().getAdminContext();
            autonameGenerateService.setContext(context);
            List<String> autonameList = autonameGenerateService.getAutonameListByType(type, prefix, affix, suffix, objectCount);
            LOGGER.info("type: " + type + ", autoname : " + autonameList.toString());
            return prepareResponse(format, type, autonameList, Status.OK, null);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AutonameGenerateController.class.getName()).log(Level.SEVERE, null, ex);
            return prepareResponse(format, type, null, Status.FAILED, "Error Message : " + ex.getMessage());
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private ResponseEntity prepareResponse(String format, String type, List<String> autonameList, Status status) {
        return prepareResponse(format, type, autonameList, status, null);
    }

    private ResponseEntity prepareResponse(String format, String type, List<String> autonameList, Status status, String message) {
        if(format.toLowerCase().equalsIgnoreCase("xml")) {
                String xmlResponse = prepareXMLResponseData(type, autonameList, status, message);
                return new ResponseEntity<>(xmlResponse, HttpStatus.OK);
        } else {
            //For invalid format response json data
            JSONObject responseData = prepareJSONObjectResponseData(type, autonameList, status, message);
            return new ResponseEntity<>(responseData.toString(), HttpStatus.OK);
        }
    }

    private JSONObject prepareJSONObjectResponseData(String type, List<String> dataList, Status status) {
        return prepareJSONObjectResponseData(type, dataList, status, "");
    }

    private JSONObject prepareJSONObjectResponseData(String type, List<String> dataList, Status status, String message) {
        JSONObject response = new JSONObject();
        response.put("type", type);
        response.put("name", new JSONArray(dataList));
        response.put("status", status);
        if (message != null && message.length() > 0) {
            response.put("message", message);
        }
        return response;
    }

    private String prepareXMLResponseData(String type, List<String> dataList, Status status) {
        return prepareXMLResponseData(type, dataList, status, "");
    }

    private String prepareXMLResponseData(String type, List<String> dataList, Status status, String message) {
        StringWriter resultStringWriter = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);

            //root element
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            //type element
            Element typeElement = doc.createElement("type");
            typeElement.appendChild(doc.createTextNode(type));
            rootElement.appendChild(typeElement);
            
            //names element
            Element nameElement = doc.createElement("names");
            rootElement.appendChild(nameElement);

            //name List element
            if (dataList != null) {
                for(String name: dataList) {
                    Element nameElement1 = doc.createElement("name");
                    nameElement1.appendChild(doc.createTextNode(name));
                    nameElement.appendChild(nameElement1);
                }
            }
            //Status element
            Element statusElement = doc.createElement("status");
            statusElement.appendChild(doc.createTextNode(status.toString()));
            rootElement.appendChild(statusElement);

            //Status element
            if (message != null && message.length() > 0) {
                Element messageElement = doc.createElement("message");
                messageElement.appendChild(doc.createTextNode(message));
                rootElement.appendChild(messageElement);
            }

            //Transformation
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(resultStringWriter));

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
            return "Server error: " + pce.getMessage();
        }
        return resultStringWriter.toString();
    }
}
