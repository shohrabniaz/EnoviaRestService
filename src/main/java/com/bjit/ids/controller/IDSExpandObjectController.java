/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ids.controller;

import com.bjit.ewc18x.message.EWMessages;
import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.model.ExpandObjectRequestForm;
import com.bjit.ewc18x.model.UiStatusMessageForm;
import com.bjit.ewc18x.service.CustomAuthenticationService;
import com.bjit.ewc18x.service.ExpandObjectService;
import com.bjit.ewc18x.utils.ApplicationMessage;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ewc18x.validator.CustomValidator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Mamun
 */
@Controller
public class IDSExpandObjectController {

    private ArrayList<String> columnList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(IDSExpandObjectController.class);
    @Autowired
    private ExpandObjectService expandObjectService;
    @Autowired
    private CustomAuthenticationService customAuthenticationService;
    ExpandObjectRequestForm expandObjectRequest = new ExpandObjectRequestForm();
    private MqlQueries mqlQueries = new MqlQueries();

    /**
     * Used to get object type list
     *
     * @return type Names like Product Line, Products, LOGICAL STRUCTURES,
     * Model, Document, CONFIGURATION FEATURES, Route
     * @throws com.bjit.ewc.utils.CustomException
     */
    public List<String> getExpandObjectTypeList() throws CustomException {
        System.out.println("method called ---------->> get expand object type list");
        try {
            File file = new File(getClass().getResource("/ExpandObjectTypePatternList.txt").getFile());
            Scanner input = new Scanner(file);
            ArrayList<String> typeNameList = new ArrayList<String>();
            while (input.hasNextLine()) {
                String line = input.nextLine();
                System.out.println("property : " + line);
                typeNameList.add(line);
            }
            input.close();
            return typeNameList;
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
            throw new CustomException("Could not read type configuration!");
        }
    }

    /**
     * Used to get expandObject initial page
     *
     * @param userId indicates username
     * @param type indicates object type
     * @param name indicates object name
     * @param revision indicates object revision
     * @param serviceName
     * @param httpSession is a http session object, holds session attributes
     * @param expandObjectForm is a model object for ExpandObjectForm class
     * @param model
     * @return expandObject page
     */
    @RequestMapping(value = "exportStructure", method = RequestMethod.GET)
    public String expandObjectForm(@RequestParam(required = false) String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String revision,
            HttpSession httpSession, @ModelAttribute("expandObjectForm") ExpandObjectForm expandObjectForm, Model model) {

        logger.info(ApplicationMessage.MS001 + " " + ApplicationMessage.PG006);
        try {
            InputStream is = null;
            String fileName = "input.xml";
            is = PropertyReader.class.getClassLoader().getResourceAsStream(fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element element = doc.getDocumentElement();
            String recursionLev = element.getElementsByTagName("recursionLevel").item(0).getTextContent();
            logger.debug("Provided recursionLevel: " + recursionLev);
            int recursionLevel = Integer.parseInt(recursionLev);
            expandObjectForm.setRecursionLevel(recursionLevel);
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException ex) {
            logger.debug(ex.getMessage());
        }
        if (userId != null && type != null && name != null && revision != null) {
            if (!userId.equals((String) httpSession.getAttribute("username"))) {
                httpSession.setAttribute("username", null);
                httpSession.setAttribute("plmKey_export", null);
                httpSession.setAttribute("userSecurityContext", null);
                httpSession.setAttribute("context", null);
            }
            expandObjectForm.setUserID(userId);
            expandObjectForm.setType(type);
            expandObjectForm.setName(name);
            expandObjectForm.setRevision(revision);

        } else {
            UiStatusMessageForm uiStatusMessageForm = new UiStatusMessageForm();
            EWMessages messages = EWMessages.error().add("e.ew.vm.4.0004");
            uiStatusMessageForm.setEwMessages(messages);
            model.addAttribute("uiStatusMessageForm", uiStatusMessageForm);
            logger.debug("User is not requesting from 3DSpace");
            return "error";
        }
        AttributesForm attributes = new AttributesForm();
        try {
            attributes.readValues();
        } catch (CustomException ex) {
            EWMessages messages = EWMessages.error().add("e.ew.fm.4.0005").addMsgText(ex.getMessage());
            expandObjectForm.setEwMessages(messages);
            model.addAttribute("expandObjectForm", expandObjectForm);
            return "expandObject";
        }

        logger.debug("attributeList : " + attributes.getAttributeNameMap());

        expandObjectForm.setDefaultItem(attributes.getAttributeNameMap());

        ArrayList<String> defaultSelectedItem = new ArrayList<>(attributes.getDefaultSelectedItem());
        try {
            expandObjectForm.setDefaultTypeList(getExpandObjectTypeList());
            expandObjectForm.setSelectedTypeList(getExpandObjectTypeList());
        } catch (CustomException ex) {
            EWMessages messages = EWMessages.error().addMsgText(ex.getMessage());
            expandObjectForm.setEwMessages(messages);
        }
        expandObjectForm.setSelectedItem(defaultSelectedItem);
        logger.debug("Default selected item: " + defaultSelectedItem.toString());
        model.addAttribute("expandObjectForm", expandObjectForm);
        return "ids/exportStructure";
    }

    /**
     * Used to get structure in XML, Json format also export object details in a
     * xls file
     *
     * @param httpSession is a http session object, holds session attributes
     * @param expandObjectForm is a model object for ExpandObjectForm class
     * @param result is for checking error
     * @param model
     * @param request
     * @return expandObject page
     */
    @RequestMapping(value = "exportStructure", method = RequestMethod.POST)
    public String expandObject(HttpSession httpSession, @ModelAttribute("expandObjectForm") ExpandObjectForm expandObjectForm, BindingResult result, Model model, HttpServletRequest request) throws IOException {
        logger.info("service=ExportStructure;com=request;method=post;user=" + expandObjectForm.getUserID() + ";msg=Received Export Request;type=" + expandObjectForm.getType() + ";name=" + expandObjectForm.getName() + ";revision=" + expandObjectForm.getRevision() + ";typelist=" + expandObjectForm.getSelectedTypeList()+ ";itemlist=" + expandObjectForm.getSelectedItem());
       if(expandObjectForm.getSelectedItem()==null) {
       logger.debug("getSelectedItem is null");
       }
        CustomValidator userValidator = new CustomValidator();
        userValidator.validateData(expandObjectForm, result, httpSession);
        AttributesForm attributes = new AttributesForm();

        ArrayList<String> finalSelectedAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedPropertyList = new ArrayList<>();
        ArrayList<String> finalSelectedRelAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedObjParamList = new ArrayList<>();

        try {

            expandObjectForm.setDefaultTypeList(getExpandObjectTypeList());

            attributes.readValues("Attributes.conf");
            //expandObjectForm.setDefaultItem(attributeList.getAttributeName());
            if (result.hasErrors()) {
                expandObjectForm.setDefaultItem(attributes.getAttributeNameMap());
                expandObjectForm.setDefaultTypeList(getExpandObjectTypeList());
                String error = result.toString();
                expandObjectForm.setOutput("");
                expandObjectForm.setSendResult("");
                logger.error("service=ExportStructure;msg=" + error);
                return "ids/exportStructure";
            }

//            expandObjectForm.setDefaultTypeList(getExpandObjectTypeList());
//            expandObjectForm.setDefaultItem(attributes.getAttributeNameMap());
            //attributes.readValues("Attributes.conf");
            Map<String, String> allItemMap = attributes.getAttributeNameMap();
            logger.debug("Al items map: " + allItemMap);

            ArrayList<String> allRelAttributeList = (ArrayList<String>) attributes.getRelationshipAttrName();
            logger.debug("allRelAttributeList: " + allRelAttributeList);
            ArrayList<String> propertyList = (ArrayList<String>) attributes.getPropertyNames();
            logger.debug("propertyList: " + propertyList);
            ArrayList<String> notPropertyNotAttributeList = (ArrayList<String>) attributes.getNotPropertyNotAttributeNames();
            logger.debug("notPropertyNotAttributeList: " + notPropertyNotAttributeList);
            ArrayList<String> selectedAttrList = expandObjectForm.getSelectedItem();
            logger.debug("Selected item: " + selectedAttrList.toString());
            for (String listItem : selectedAttrList) {
                logger.debug("Current item: " + listItem);
                if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                    logger.debug("property: " + listItem);
                    finalSelectedObjParamList.add("'" + getKeyFromValue(allItemMap, listItem) + "'");
                    //finalSelectedPropertyList.add(listItem);

                } else if (allRelAttributeList.contains(listItem)) {
                    logger.debug("rel atr: " + listItem);
                    finalSelectedRelAttributeList.add("'attribute[" + listItem + "]'");
                } else {
                    logger.debug("obj atr: " + listItem);
                    finalSelectedAttributeList.add("'attribute[" + listItem + "]'");
                }

            }

            logger.debug("finalSelectedAttributeList: " + finalSelectedAttributeList.toString());
            logger.debug("finalSelectedObjParamList: " + finalSelectedObjParamList.toString());
            logger.debug("finalSelectedRelAttributeList: " + finalSelectedRelAttributeList.toString());

            finalSelectedObjParamList.addAll(finalSelectedAttributeList);
            //finalSelectedObjParamList.addAll(finalSelectedRelAttributeList);

            logger.debug("final : " + finalSelectedObjParamList.toString());

            //expandObjectForm.setSelectedItem(finalSelectedAttributeList);
            // getting physical id
            Context context = (Context) httpSession.getAttribute("context");
            String query = "print bus '" + expandObjectForm.getType() + "' '" + expandObjectForm.getName() + "' '" + expandObjectForm.getRevision() + "' " + " select physicalid";
            String queryResult = mqlQueries.getQueryResults((Context) context, query);
            String physicalId = queryResult.substring(queryResult.lastIndexOf("=") + 1, queryResult.length()).trim();

            String typePattern = expandObjectService.getSelectedTypePatternListExpression(expandObjectForm);
            logger.debug("Type Pattern with comma separated: " + typePattern);
            expandObjectForm.setTypePattern(typePattern);
            expandObjectForm = expandObjectService.populateServiceInfo(expandObjectForm, "input");
            logger.debug("Physical Id: " + physicalId);
            logger.debug("output file format: " + expandObjectForm.getOutputFileFormat());
            switch (expandObjectForm.getOutputFileFormat()) {
                case "0":
                    logger.debug("Generating output as xml format");
                    expandObjectService.getXmlOutput(httpSession, context, physicalId, finalSelectedObjParamList, finalSelectedAttributeList, finalSelectedRelAttributeList, expandObjectForm);
                    break;
                case "1":
                    logger.debug("Generating output as json format");
                    String response = expandObjectService.getJsonOutput(httpSession, context, physicalId, finalSelectedObjParamList, finalSelectedAttributeList, finalSelectedRelAttributeList, expandObjectForm);
                    break;
                case "2":
                    logger.debug("Generating output as xls format");
                    expandObjectService.getXlsOutput(httpSession, context, physicalId, finalSelectedObjParamList, finalSelectedAttributeList, finalSelectedRelAttributeList, expandObjectForm, attributes.getUnchangeableItems());
                    break;
                default:
                    break;
            }
            
            logger.debug("service=ExportStructure;msg=After calling service class: " + expandObjectForm);
            expandObjectRequest.setExpndObj(expandObjectForm);
            model.addAttribute("expandObjectForm", expandObjectForm);
            logger.debug("service=ExportStructure;msg=Set to expandObjectForm attribute: " + expandObjectForm);
            logger.debug("service=ExportStructure;msg=" + ApplicationMessage.MS013);
            logger.debug("service=ExportStructure;msg=setting default items again");
            expandObjectForm.setDefaultItem(attributes.getAttributeNameMap());
            logger.info("service=ExportStructure;com=response;method=post;user=" + expandObjectForm.getUserID() + ";status=success;msg=Sending Export Response;type=" + expandObjectForm.getType() + ";name=" + expandObjectForm.getName() + ";revision=" + expandObjectForm.getRevision());

        } catch (CustomException ex) {
            logger.error("Exception occured, may be table not found at MQL server: " + ex.getMessage());
            EWMessages messages = EWMessages.error().add("e.ew.fm.4.0005").addMsgText(ex.getMessage());
            expandObjectForm.setEwMessages(messages);

            expandObjectForm.setOutput(null);
            model.addAttribute("expandObjectForm", expandObjectForm);
            logger.info("Set to expandObjectForm attribute: " + expandObjectForm);
            logger.info(ApplicationMessage.MS013);
            expandObjectForm.setDefaultItem(attributes.getAttributeNameMap());
            //return "expandObject";
        }

        logger.debug("ExpandObject controller method quiting : " + expandObjectForm.getDefaultItem());
        return "ids/exportStructure";
    }

    public static Object getKeyFromValue(Map map, Object value) {
        for (Object o : map.keySet()) {
            if (map.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    private void setSessionAttributes(HttpSession session, String username, String securityContext, Context context) {
        session.setAttribute("username", username);
        session.setAttribute("securityContext", securityContext);
        session.setAttribute("context", context);
    }
}
