/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.controller;

import com.bjit.common.rest.app.service.controller.createobject.CreateAndUpdateObjectController;
import com.bjit.common.rest.app.service.controller.itemCreate.ItemCreateController;
import com.bjit.common.rest.app.service.model.bom.BomImportBean;
import com.bjit.common.rest.app.service.model.bom.BomItem;
import com.bjit.common.rest.app.service.model.create.item.ItemCreateBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CommonResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.XmlMapUtils;
import com.bjit.mapproject.xml_mapping_model.XmlMapElementAttribute;
import com.matrixone.apps.domain.util.ContextUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import matrix.db.AttributeList;
import matrix.db.Attribute;
import matrix.db.AttributeType;
import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Relationship;
import matrix.db.RelationshipList;
import matrix.db.RelationshipType;
import matrix.db.Vault;
import matrix.util.MatrixException;
import org.apache.log4j.Level;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Tomal
 */
//@Controller
//@RequestMapping(path = "/BomImporter")
public class BomImportController {
//
//    private static final org.apache.log4j.Logger IMPORT_BOM_LOGGER = org.apache.log4j.Logger.getLogger(BomImportController.class);
//    RelationshipList allRelationship;
//
//    /**
//     *
//     * @param httpRequest
//     * @param bomData
//     * @param bomImportBean
//     * @return
//     */
//    @RequestMapping(value = "/importBoms", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
//    @ResponseBody
//    public CommonResponse importBoms(HttpServletRequest httpRequest, @RequestBody String bomData) {
//        CommonResponse response = new CommonResponse();
//        BomImportBean bomImportBean = new JSON().deserialize(bomData, BomImportBean.class);
//        String relationshipName = "DELFmiFunctionIdentifiedInstance";
//        
//        Context context = null;
//        context = (Context) httpRequest.getAttribute("context");
//
//        try {
//            ContextUtil.startTransaction(context, true);
//            XmlMapUtils xmlMapUtils = new XmlMapUtils();
//            List<XmlMapElementAttribute> xmlMapElementAttributeList = xmlMapUtils.getXMLMapElementAttributeList(IMPORT_BOM_LOGGER, relationshipName);
//
//            IMPORT_BOM_LOGGER.log(Level.INFO, "XML Mapper Attribute List Loaded. ");
//            List<BomItem> listOfBomItems = bomImportBean.getBillOfMaterial_ODI();
//            validate(listOfBomItems);
//            List<Object> bomItemResultMapList = new ArrayList<Object>();
//
//            for (BomItem bomItem : listOfBomItems) {
//                Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
//                String parentName = bomItem.getItemID().get("ID");
//                String parentType = bomItem.getItemID().get("type");
//                if (parentType==null || parentType.isEmpty()) {
//                    IMPORT_BOM_LOGGER.debug("Type Must be provided,Not Present for item: " + parentName);
//                    throw new Exception("Type Must be provided,Not Present for item: " + parentName);
//                }
//                List<String> childItems = new ArrayList<String>();
//                try {
//                    String parentId = getObjectIdFromObjectName(context, parentName,parentType);
//
//                    BusinessObject parentBus = new BusinessObject(parentId);
//                    allRelationship = parentBus.getAllRelationship(context);
//                    List<Map<String, String>> listOfLines = bomItem.getLines();
//                    resultMap.put("parentItem", parentName);
//
//                    IMPORT_BOM_LOGGER.debug("Parent Id: " + parentId);
//                    IMPORT_BOM_LOGGER.log(Level.INFO, "Importing BOM For " + parentName + ". ");
//                    String childName = "";
//                    String childType = "";
//                    for (Map<String, String> line : listOfLines) {
//                        try {
//
//                            childName = line.get("component");
//                            childType = line.get("type");
//                            if (childType==null || childType.isEmpty()) {
//                                IMPORT_BOM_LOGGER.debug("Type Must be provided,Not Present for item: " + childName);
//                                throw new Exception("Type Must be provided,Not Present for item: " + childName);
//                            }
//                            childItems.add(childName);
//
//                            String childId = getObjectIdFromObjectName(context, childName,childType);
//
//                            BusinessObject childBus = new BusinessObject(childId);
//
//                            RelationshipType relationshipType = new RelationshipType();
//                            relationshipType.setName(relationshipName);
//
//                            AttributeList attributeList = getAttributeListFromLineInfo(xmlMapElementAttributeList, line);
//
//                            Relationship relationship = getRelationshipByRelationshipType(context, relationshipType, parentBus, childBus);
//                            relationship.deleteAllPaths(context);
//                            
//                            relationship.setAttributeValues(context, attributeList);
//                            relationship.close(context);
//
//                            IMPORT_BOM_LOGGER.debug("Child Id: " + childId);
//                            IMPORT_BOM_LOGGER.debug("Relationship Name: " + relationship.getName());
//                            IMPORT_BOM_LOGGER.log(Level.INFO, childName + " Successfully Imported.");
//                        } catch (Exception e) {
//                            resultMap = new LinkedHashMap<String, Object>();
//                            bomItemResultMapList = new ArrayList<Object>();
//                            resultMap.put(childName, e.getMessage());
//                            bomItemResultMapList.add(resultMap);
//
//                            response.setStatus(Status.FAILED);
//                            response.setMessages(bomItemResultMapList);
//                            response.setData(new ArrayList<Object>());
//                            IMPORT_BOM_LOGGER.log(Level.ERROR, "Error Occured During " + childName + " Import. Cause:" + e.getMessage());
//
//                            ContextUtil.abortTransaction(context);
//                            return response;
//                        }
//
//                    }
//                } catch (Exception e) {
//                    resultMap = new LinkedHashMap<String, Object>();
//                    bomItemResultMapList = new ArrayList<Object>();
//                    resultMap.put(parentName, e.getMessage());
//                    bomItemResultMapList.add(resultMap);
//                    
//                    response.setStatus(Status.FAILED);
//                    response.setMessages(bomItemResultMapList);
//                    response.setData(new ArrayList<Object>());
//                    IMPORT_BOM_LOGGER.log(Level.ERROR, "Error Occured During " + parentName + " Import. Cause:" + e.getMessage());
//
//                    ContextUtil.abortTransaction(context);
//                    return response;
//                }
//                resultMap.put("childItem", childItems);
//
//                bomItemResultMapList.add(resultMap);
//            }
//            response.setStatus(Status.OK);
//            response.setMessages(new ArrayList<Object>());
//            //response.setData(listOfBomItems.size() + " Items And " + bomItemResultMapList.size() + " BOM Items Are Imported.");
//            response.setData(bomItemResultMapList);
//
//            IMPORT_BOM_LOGGER.log(Level.INFO, "Successfully " + listOfBomItems.size() + " Items And " + bomItemResultMapList.size() + " BOM Items Are Imported.");
//
//            ContextUtil.commitTransaction(context);
//        } catch (Exception e) {
//            response.setStatus(Status.FAILED);
//            List<Object> messages = new ArrayList<Object>();
//            messages.add(e.getMessage());
//            response.setData(new ArrayList<Object>());
//            response.setMessages(messages);
//            ContextUtil.abortTransaction(context);
//            return response;
//        }
//        return response;
//    }
//
//    private Relationship checkRelExists(Context context, BusinessObject child) throws MatrixException {
//        for (int i = 0; i < allRelationship.size(); i++) {
//            Relationship relationship = allRelationship.get(i);
//            relationship.open(context);
//            BusinessObject temppBus = relationship.getTo();
//            temppBus.open(context);
//            String name = temppBus.getObjectId();
//            child.open(context);
//            IMPORT_BOM_LOGGER.debug("CHILD ID:" + name);
//            if (name.equals(child.getObjectId())) {
//                return relationship;
//            }
//            relationship.close(context);
//        }
//        return null;
//    }
//
//    private void validate(List<BomItem> bomItemList) throws Exception {
//        if (bomItemList == null || bomItemList.size() == 0) {
//            throw new Exception("Items Cannot Be Empty!");
//        } else {
//            for (BomItem bomItem : bomItemList) {
//                if (bomItem.getItemID() == null) {
//                    IMPORT_BOM_LOGGER.log(Level.ERROR, "Exception Occured: Item Information Cannot Be Empty!");
//                    throw new Exception("Item Information Cannot Be Empty!");
//                } else if (bomItem.getLines() == null || bomItem.getLines().size() == 0) {
//                    IMPORT_BOM_LOGGER.log(Level.ERROR, "Exception Occured: Lines Cannot Be Empty!");
//                    throw new Exception("Lines Cannot Be Empty!");
//                }
//            }
//        }
//    }
//
//    private String getObjectIdFromObjectName(Context context, String name,String type) throws Exception {
//        String objectId = new CreateAndUpdateObjectController().getUniqueObjectId(context, type, name);
//        if (objectId == null || objectId == "") {
//            IMPORT_BOM_LOGGER.log(Level.DEBUG, "Item not present in Enovia!! Need to clone!");
//            ItemCreateController itemCreateController = new ItemCreateController();
//            CreateAndUpdateObjectController createObjectController = new CreateAndUpdateObjectController();
//            CreateObjectBean createObjectBean = new CreateObjectBean();
//            TNR tnr = new TNR();
//            tnr.setName(name);
//            tnr.setType(type);
//            tnr.setRevision("");
//            createObjectBean.setTnr(tnr);
//            createObjectBean.setIsAutoName(false);
//            createObjectBean.setAttributes( new HashMap<>());
//            itemCreateController.validate(context, createObjectController, createObjectBean);
//            objectId = itemCreateController.getCreatedItemId(context, createObjectController, createObjectBean);
////            IMPORT_BOM_LOGGER.log(Level.ERROR, "Exception Occured: Item Id Is Invalid!");
////            throw new Exception("Item Id Is Invalid!");
//        }
//        return objectId;
//    }
//
//    private AttributeList getAttributeListFromLineInfo(List<XmlMapElementAttribute> xmlMapElementAttributeList, Map<String, String> line) {
//        AttributeList attributeList = new AttributeList();
//        for (XmlMapElementAttribute xmlMapElementAttribute : xmlMapElementAttributeList) {
//            String sourceName = xmlMapElementAttribute.getSourceName();
//            String destinationName = xmlMapElementAttribute.getDestinationName();
//
//            AttributeType singleAttributeType = new AttributeType(destinationName);
//            Attribute singleAttribute = new Attribute(singleAttributeType, line.get(sourceName));
//
//            attributeList.add(singleAttribute);
//        }
//        return attributeList;
//    }
//
//    private Relationship getRelationshipByRelationshipType(Context context, RelationshipType relationshipType, BusinessObject parentBus, BusinessObject childBus) throws Exception {
//        Relationship relationship = checkRelExists(context, childBus);
//        if (relationship == null) {
//            relationship = parentBus.connect(context, relationshipType, true, childBus);
//            relationship.open(context);
//            
//            Vault vault = new Vault("");
//            String connectionInterface = "MBOM_MBOMInstance";
//            IMPORT_BOM_LOGGER.info("Connection interface " + connectionInterface);
//            BusinessInterface businessInterface = new BusinessInterface(connectionInterface, vault);
//            relationship.addBusinessInterface(context, businessInterface);
//        } else {
//            relationship.open(context);
//        }
//        return relationship;
//    }

}
