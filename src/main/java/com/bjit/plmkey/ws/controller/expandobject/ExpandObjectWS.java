/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.plmkey.ws.controller.expandobject;

import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.utils.EnoviaWebserviceCommon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 *
 * @author Sajjad
 */
@Endpoint
public class ExpandObjectWS {

    @Autowired
    private com.bjit.ewc18x.service.ExpandObjectService expandObjectService;
    @Autowired
    private com.bjit.ewc18x.model.ExpandObjectForm expandObjectForm;

    public static final String NAMESPACE_URI = "http://controller.ws.plmkey.bjit.com/expandObject";
    public static final String REQUEST_LOCAL_NAME = "expandObjectRequest";
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExpandObjectWS.class);

    @PayloadRoot(localPart = REQUEST_LOCAL_NAME, namespace = NAMESPACE_URI)
    @ResponsePayload
    public com.bjit.plmkey.ws.controller.expandobject.ExpandObjectResponse expandObject(@RequestPayload ExpandObjectRequest expandObjectRequest) throws Exception {
        com.bjit.plmkey.ws.controller.expandobject.ExpandObjectResponse expandObjectResponse = new com.bjit.plmkey.ws.controller.expandobject.ExpandObjectResponse();
        mapValues(expandObjectRequest);
        EnoviaWebserviceCommon enoviaWebserviceCommon = new EnoviaWebserviceCommon();
        ExpandObjectUtil epandObjectUtil = new ExpandObjectUtil();
        String servicName = expandObjectRequest.getServiceName();
        servicName = servicName.replace(" ", "");

        AttributesForm attributes = new AttributesForm();

        Context context = enoviaWebserviceCommon.getSecureContext(expandObjectForm.getUserID(), expandObjectForm.getPassword());

        ArrayList<String> finalSelectedAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedRelAttributeList = new ArrayList<>();
        ArrayList<String> finalSelectedObjParamList = new ArrayList<>();

        File file = new File(getClass().getResource("/services/" + servicName + ".xml").getFile());
        expandObjectForm.setDefaultTypeList(epandObjectUtil.getExpandObjectTypeList(file));
        if(expandObjectForm.getSelectedTypeList().isEmpty()){
            expandObjectForm.setSelectedTypeList(epandObjectUtil.getExpandObjectTypeList(file));
        }
        attributes.readValues("Attributes.conf");

        Map<String, String> allItemMap = attributes.getAttributeNameMap();
        logger.debug("Al items map: " + allItemMap);

        ArrayList<String> allRelAttributeList = (ArrayList<String>) attributes.getRelationshipAttrName();
        logger.debug("allRelAttributeList: " + allRelAttributeList);
        ArrayList<String> propertyList = (ArrayList<String>) attributes.getPropertyNamesWS();
        logger.debug("propertyList: " + propertyList);
        ArrayList<String> notPropertyNotAttributeList = (ArrayList<String>) attributes.getNotPropertyNotAttributeNames();
        logger.debug("notPropertyNotAttributeList: " + notPropertyNotAttributeList);
        ArrayList<String> selectedAttrList = expandObjectForm.getSelectedItem();
        logger.debug("Selected item: " + selectedAttrList.toString());
        if(servicName.length()>0){
            expandObjectForm = expandObjectService.populateServiceInfo(expandObjectForm, "services/"+servicName);
        }

        for (String listItem : selectedAttrList) {
            logger.debug("Current item: " + listItem);
            if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                logger.debug("property: " + listItem);
                finalSelectedObjParamList.add("'" + listItem + "'");
            } else if (allRelAttributeList.contains(listItem)) {
                logger.debug("rel atr: " + listItem);
                finalSelectedRelAttributeList.add("'attribute[" + listItem + "]'");
            } else {
                logger.debug("obj atr: " + listItem);
                finalSelectedAttributeList.add("'attribute[" + listItem + "]'");
            }
        }
        finalSelectedObjParamList.addAll(finalSelectedAttributeList);
        String physicalId = epandObjectUtil.getPhycialId(context,expandObjectForm);
        expandObjectResponse.setOutput(expandObjectService.getXmlOutput(null, context, physicalId, finalSelectedObjParamList, finalSelectedAttributeList, finalSelectedRelAttributeList, expandObjectForm));

        return expandObjectResponse;
    }
    
    private void mapValues(ExpandObjectRequest expandObjectRequest) {
        expandObjectForm.setName(expandObjectRequest.getName());
        expandObjectForm.setType(expandObjectRequest.getType());
        expandObjectForm.setRevision(expandObjectRequest.getRevision());
        expandObjectForm.setUserID(expandObjectRequest.getUserID());
        expandObjectForm.setPassword(expandObjectRequest.getPassword());
        expandObjectForm.setRecursionLevel(expandObjectRequest.getRecursionLevel());
        expandObjectForm.setServiceName(expandObjectRequest.getServiceName());
        expandObjectForm.setSecurityContext(expandObjectRequest.getSecurityContext());

        String outputFileFormat = expandObjectRequest.getOutputFileFormat();

        if (outputFileFormat.equals("XML")) {
            expandObjectForm.setOutputFileFormat("0");
        } else {
            expandObjectForm.setOutputFileFormat("1");
        }

        List<String> selectedItem = new ArrayList<>(expandObjectRequest.getAttributeList());
        List<String> removeItem = new ArrayList<String>() {{add(",");}};
        selectedItem.removeAll(removeItem);
        logger.debug("service=expandObjectWS;msg=" + "total no of selected items : " + selectedItem.size());
        List<String> configuredSelectedItem = new ArrayList<>();
        List<String> nonConfiguredPropertyList = new ArrayList<>();
        for(int i = 0;i< selectedItem.size() ; i++){
            
            
            
            if(selectedItem.get(i).equalsIgnoreCase("objectId")){
                nonConfiguredPropertyList.add("id");
            } else if(selectedItem.get(i).equalsIgnoreCase("depth")){
                nonConfiguredPropertyList.add("level");
            } else if(selectedItem.get(i).equalsIgnoreCase("state")){
                nonConfiguredPropertyList.add("current");
            }else{
                selectedItem.set(i, selectedItem.get(i).replace("_", " "));
                configuredSelectedItem.add(selectedItem.get(i).replace("_", " "));
            }
            
            /*else if(selectedItem.get(i).equalsIgnoreCase("Position")){
                nonConfiguredPropertyList.add("level");
            }*/
            
            System.out.println(i + ">> selected item : " + selectedItem.get(i));
        }
        selectedItem.clear();
        selectedItem.addAll(nonConfiguredPropertyList);
        selectedItem.addAll(configuredSelectedItem);
        logger.debug("service=expandObjectWS;msg=" + "selectedItems : " + selectedItem);
        expandObjectForm.setSelectedItem((ArrayList<String>) selectedItem);
        
        List<String> selectedType = new ArrayList<>(expandObjectRequest.getTypeList());
        removeItem = new ArrayList<String>() {{add(",");}};
        selectedType.removeAll(removeItem);
        for (int i = 0; i < selectedType.size(); ++i) {
            selectedType.set(i, selectedType.get(i).replace("_", " "));
        }
        expandObjectForm.getRecursionLevel();
        expandObjectForm.setSelectedTypeList(selectedType);
        expandObjectForm.setGetFrom(Boolean.TRUE);
        expandObjectForm.setGetTo(Boolean.FALSE);
        
        logger.debug("service=expandObjectWS;msg=" + "total no of selected items : " + selectedType.size());
        logger.debug("service=expandObjectWS;msg=" + "selectedItems : " + selectedItem);
    }    
}
