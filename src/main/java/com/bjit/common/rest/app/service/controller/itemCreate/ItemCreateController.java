/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.itemCreate;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjit.common.rest.app.service.controller.createobject.CreateAndUpdateObjectController;
import com.bjit.common.rest.app.service.model.create.item.ItemCreateBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.createobject.UpdateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CommonResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import matrix.db.BusinessObject;

import matrix.db.Context;
import org.apache.log4j.Level;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Tomal
 */
@Controller
@RequestMapping(path = "/createAndUpdateItem")
public class ItemCreateController {

    private static final org.apache.log4j.Logger CREATE_ITEM_LOGGER = org.apache.log4j.Logger.getLogger(ItemCreateController.class);

    @RequestMapping(value = "/createItems", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public CommonResponse createItems(HttpServletRequest httpRequest, @RequestBody ItemCreateBean itemCreateBean) {
        CommonResponse response = new CommonResponse();
        List<Object> resultMapList = new ArrayList<Object>();
        Context context = null;
        context = (Context) httpRequest.getAttribute("context");
        //AuthenticationProcess userAuthentication = new AuthenticationProcess();

        CreateAndUpdateObjectController createObjectController = new CreateAndUpdateObjectController();
        List<HashMap<String, Object>> items = itemCreateBean.getItems();

        try {
            ContextUtil.startTransaction(context, true);
            for (HashMap<String, Object> itemMap : items) {
                Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

                CreateObjectBean createObjectBean = getCreateObjectBean(itemMap);
                TNR tnr = createObjectBean.getTnr();
                tnr.setRevision("");

                try {
                    validate(context, createObjectController, createObjectBean);
                    String createdItemId = getCreatedItemId(context, createObjectController, createObjectBean);
                    if (createdItemId != null) {
                        BusinessObject createdBus = new BusinessObject(createdItemId);
                        createdBus.open(context);
                        tnr.setName(createdBus.getName());
                        tnr.setRevision(createdBus.getRevision());
                        resultMap.put("item", tnr);
                        resultMap.put("id", createdItemId);
                        CREATE_ITEM_LOGGER.log(Level.INFO, "Successfully Item Created With Id: " + createdItemId);
                    }
                } catch (Exception e) {
                    resultMapList = new ArrayList<Object>();
                    resultMap = new LinkedHashMap<String, Object>();
                    response.setStatus(Status.FAILED);
                    resultMap.put("result", "Following Error Occured: " + e.getMessage());
                    resultMapList.add(resultMap);
                    response.setMessages(resultMapList);

                    List<Object> emptyList = new ArrayList<Object>();
                    response.setData(emptyList);

                    CREATE_ITEM_LOGGER.log(Level.ERROR, "Following Error Occured: " + e.getMessage());
                    ContextUtil.abortTransaction(context);
                    CREATE_ITEM_LOGGER.log(Level.ERROR, "Connection Error: " + e.getMessage());
                    return response;
                }
                List<Object> emptyList = new ArrayList<Object>();
                response.setMessages(emptyList);
                response.setStatus(Status.OK);
                resultMapList.add(resultMap);
                response.setData(resultMapList);
            }
            ContextUtil.commitTransaction(context);
        } catch (FrameworkException ex) {
            ContextUtil.abortTransaction(context);
            CREATE_ITEM_LOGGER.log(Level.ERROR, "Connection Error: " + ex.getMessage());
            return response;
        }

        CREATE_ITEM_LOGGER.log(Level.INFO, "Items Addition Completed Sucessfully. ");
        return response;
    }

    private CreateObjectBean getCreateObjectBean(HashMap<String, Object> itemMap) {
        HashMap<String, String> attributeMap = new HashMap<>();
        TNR tnr = new TNR();
        CreateObjectBean createObjectBean = new CreateObjectBean();
        for (String key : itemMap.keySet()) {
            if (key.equalsIgnoreCase("rel")) {
                HashMap<String, String> relMap = (HashMap<String, String>) itemMap.get(key);
            } else if (key.equalsIgnoreCase("files")) {
                List<String> fileList = (List<String>) itemMap.get(key);
            } else if (key.equalsIgnoreCase("Name")) {
                tnr.setName((String) itemMap.get(key));
            } else if (key.equalsIgnoreCase("Type")) {
                tnr.setType((String) itemMap.get(key));
//                                        attributeMap.put(key, (String) item.get(key));
            } else {
                attributeMap.put(key, (String) itemMap.get(key));
            }
        }
        createObjectBean.setTnr(tnr);
        createObjectBean.setAttributes(attributeMap);
        if (tnr.getName() == null || tnr.getName().equals("")) {
            createObjectBean.setIsAutoName(true);
        } else {
            createObjectBean.setIsAutoName(false);
        }
        createObjectBean.setAttributeGlobalRead(false);
        return createObjectBean;
    }

    public void validate(Context context, CreateAndUpdateObjectController createObjectController, CreateObjectBean createObjectBean) throws Exception {
        TNR tnr = createObjectBean.getTnr();
        if (tnr.getType() == null || tnr.getType() == "") {
            throw new Exception("Please Enter Item Type.");
        }
//        else if (tnr.getName() == null || tnr.getName() == "") {
//            throw new Exception("Please Enter Item Name.");
//        }

        createObjectController.checkObjectsExistence(context, createObjectBean);
        CREATE_ITEM_LOGGER.log(Level.INFO, "Duplicate Checking Completed. ");

    }

    public String getCreatedItemId(Context context, CreateAndUpdateObjectController createObjectController, CreateObjectBean createObjectBean) throws Exception {
        String itemId = "";
        createObjectBean = createObjectController.ifSkeletonIdNotExistThenCheckTNRsProperties(createObjectBean, context);
        CREATE_ITEM_LOGGER.log(Level.INFO, "Object Id And TNR Properties Checking Completed. ");

        HashMap objectCloneParametersMap = createObjectController.createObjectCloneParametersMap(context, createObjectBean);
        CREATE_ITEM_LOGGER.log(Level.INFO, "Object Clone Parameters Mapping Completed. ");

        itemId = createObjectController.cloneObjectByJPO(context, createObjectBean, objectCloneParametersMap);
        CREATE_ITEM_LOGGER.log(Level.INFO, "Object Cloning Completed. ");
        return itemId;

    }

    @RequestMapping(value = "/createItem", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity createObject(HttpServletRequest httpRequest, @RequestBody CreateObjectBean createObjectBean) throws Exception {
        CREATE_ITEM_LOGGER.log(Level.INFO, "######################################## CREATE ITEM CONTROLLER BEGIN ########################################\n");

        HashMap<String, String> pdmAttributeAdapterMap = new HashMap<>();
        pdmAttributeAdapterMap.put("draft", "IN_WORK");
        pdmAttributeAdapterMap.put("released", "RELEASED");
        pdmAttributeAdapterMap.put("expiring", "FROZEN");
        pdmAttributeAdapterMap.put("forbidden", "OBSOLETE");

        createObjectBean.getAttributes().put("current", pdmAttributeAdapterMap.get(createObjectBean.getAttributes().get("current").toLowerCase()));

        CreateAndUpdateObjectController CreateAndUpdateObjectController = new CreateAndUpdateObjectController();
        ResponseEntity createObject = CreateAndUpdateObjectController.createObject(httpRequest, createObjectBean);

        CREATE_ITEM_LOGGER.log(Level.INFO, "######################################## CREATE ITEM CONTROLLER COMPLETE ########################################");
        return createObject;
    }

    @RequestMapping(value = "/updateItem", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateObject(HttpServletRequest httpRequest, @RequestBody UpdateObjectBean updateObjectBean) throws Exception {
        CREATE_ITEM_LOGGER.log(Level.INFO, "######################################## UPDATE ITEM CONTROLLER BEGIN ########################################\n");

        HashMap<String, String> pdmAttributeAdapterMap = new HashMap<>();
        pdmAttributeAdapterMap.put("draft", "IN_WORK");
        pdmAttributeAdapterMap.put("released", "RELEASED");
        pdmAttributeAdapterMap.put("expiring", "FROZEN");
        pdmAttributeAdapterMap.put("forbidden", "OBSOLETE");

        updateObjectBean.getAttributeListMap().put("current", pdmAttributeAdapterMap.get(updateObjectBean.getAttributeListMap().get("current").toLowerCase()));

        CreateAndUpdateObjectController updateItem = new CreateAndUpdateObjectController();
        ResponseEntity updateObject = updateItem.updateObject(httpRequest, updateObjectBean);

        CREATE_ITEM_LOGGER.log(Level.INFO, "######################################## UPDATE ITEM CONTROLLER COMPLETE ########################################");
        return updateObject;
    }
}
