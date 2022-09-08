/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.plmkey.ws.controller.expandobject;

import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.EnoviaWebserviceCommon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sajjad
 */
@RestController
public class ExpandObjectRestService {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExpandObjectRestService.class);

    @Autowired
    private com.bjit.ewc18x.model.ExpandObjectForm expandObjectForm;
    @Autowired
    private com.bjit.ewc18x.service.ExpandObjectService expandObjectService;
    
    @SuppressWarnings("static-access")
    @RequestMapping(value = "/ExpandObjectrs")
    public String ExpandObject(@RequestParam(value="userID", defaultValue="") String userID, 
            @RequestParam(value="pass", defaultValue="") String pass, 
            @RequestParam(value="type", defaultValue="") String type, 
            @RequestParam(value="name", defaultValue="") String name,
            @RequestParam(value="rev", defaultValue="") String rev,
            @RequestParam(value="recurseLevel", defaultValue="") String recurseLevel,
            @RequestParam(value="serviceName", defaultValue="") String serviceName,
            @RequestParam(value="attributeList", defaultValue="") String attributes) throws IOException{
        StringBuffer errorMsg = new StringBuffer();
        serviceName = serviceName.replace(" ", "");
        
        File file = new File(getClass().getResource("/services/" + serviceName + ".xml").getFile());
        logger.debug("service=expandObjectWS;msg=" + serviceName + ".xml" + " File exists !! " + file.exists());
        
        ArrayList<String> attributeList = new ArrayList<>();
        String[] attributeArr = attributes.split(",");
        for (String attribute : attributeArr) {
            attributeList.add(attribute.trim());
        }
        expandObjectForm.setName(name);
        expandObjectForm.setType(type);
        expandObjectForm.setRevision(rev);
        expandObjectForm.setUserID(userID);
        expandObjectForm.setPassword(pass);
        expandObjectForm.setRecursionLevel(Integer.parseInt(recurseLevel));
        expandObjectForm.setServiceName(serviceName);
        ExpandObjectUtil epandObjectUtil = new ExpandObjectUtil();

        expandObjectForm.setSelectedItem(attributeList);
        expandObjectForm.setGetFrom(Boolean.TRUE);
        expandObjectForm.setGetTo(Boolean.FALSE);
        try {
            expandObjectForm.setDefaultTypeList(epandObjectUtil.getExpandObjectTypeList(file));
            expandObjectForm.setSelectedTypeList(epandObjectUtil.getExpandObjectTypeList(file));

        } catch (CustomException ex) {
            Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        EnoviaWebserviceCommon enoviaWebserviceCommon = new EnoviaWebserviceCommon();
        Context context = null;
        try {
            context = enoviaWebserviceCommon.getSecureContext(expandObjectForm.getUserID(), expandObjectForm.getPassword());
        } catch (CustomException ex) {
            Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg.append("Unable to create context !!\n");
        }
        AttributesForm attributeFrom = new AttributesForm();
        try {
            attributeFrom.readValues("Attributes.conf");
        } catch (CustomException ex) {
            Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg.append("Could not read attribute configuration.\n");
        }
        
        ArrayList<String> finalSelectedAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedRelAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedObjParamList = new ArrayList<>();
        
        Map<String, String> allItemMap = attributeFrom.getAttributeNameMap();
        logger.debug("Al items map: " + allItemMap);

        ArrayList<String> allRelAttributeList = (ArrayList<String>) attributeFrom.getRelationshipAttrName();
        logger.debug("allRelAttributeList: " + allRelAttributeList);
        ArrayList<String> propertyList = (ArrayList<String>) attributeFrom.getPropertyNames();
        logger.debug("propertyList: " + propertyList);
        ArrayList<String> notPropertyNotAttributeList = (ArrayList<String>) attributeFrom.getNotPropertyNotAttributeNames();
        logger.debug("notPropertyNotAttributeList: " + notPropertyNotAttributeList);
        ArrayList<String> selectedAttrList = expandObjectForm.getSelectedItem();
        logger.debug("Selected item: " + selectedAttrList.toString());
        
        for (String listItem : selectedAttrList) {
            logger.debug("Current item: " + listItem);
            if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                System.out.println("property: " + listItem);
                logger.debug("property: " + listItem);
                finalSelectedObjParamList.add("'" + epandObjectUtil.getKeyFromValue(allItemMap, listItem) + "'");
            } else if (allRelAttributeList.contains(listItem)) {
                logger.debug("rel atr: " + listItem);
                System.out.println("rel atr : " + listItem);
                String attrName = attributeFrom.getAttributeNameMap().get("listItem");
                finalSelectedAttributeList.add("'attribute[" + attrName + "]'");
            } else {
                logger.debug("obj atr: " + listItem);
                System.out.println("obj atr : " + listItem);
                String attrName = attributeFrom.getAttributeNameMap().get("listItem");
                finalSelectedAttributeList.add("'attribute[" + attrName + "]'");
            }
        }

        finalSelectedObjParamList.addAll(finalSelectedAttributeList);
         if(serviceName.length()>0){
            try {
                expandObjectForm = expandObjectService.populateServiceInfo(expandObjectForm, "services/"+serviceName);
            } catch (CustomException ex) {
                Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String physicalId ="";
        try {
            physicalId = epandObjectUtil.getPhycialId(context,expandObjectForm);
        } catch (CustomException ex) {
            Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg.append("No Physical ID found !!\n");
        }
        String data = "";
        try {
            data = expandObjectService.getJsonOutput(null, context, physicalId, finalSelectedObjParamList, finalSelectedAttributeList, finalSelectedRelAttributeList, expandObjectForm);
        } catch (CustomException ex) {
            Logger.getLogger(ExpandObjectRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(errorMsg.length()>0){
            return errorMsg.toString();
        }
        return data;
    }
}
