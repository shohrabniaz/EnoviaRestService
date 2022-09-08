/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */
package com.bjit.common.rest.app.service.controller.export.product;


import com.bjit.common.code.utility.security.base64decrypt.Base64DecryptServiceImpl;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.ewc18x.utils.CustomException;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bjit.common.rest.app.service.export.product.ProductExportService;
import com.bjit.common.rest.app.service.model.export.product.AllJsonObjectResponse;
import com.bjit.common.rest.app.service.model.export.product.ExpandObjectForm;
import com.bjit.common.rest.app.service.utilities.ServiceFileUtil;
import com.bjit.ewc18x.utils.DateValidator;
import com.bjit.ewc18x.utils.EnoviaWebserviceCommon;
import com.bjit.ewc18x.utils.EwcUtilities;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author Suvonkar Kundu
 * @version 1.0
 * @since 2020-22-05
 */
@RestController
@RequestMapping(path = "/productExport")
public class ExportProductRestController {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ExportProductRestController.class);
    @Autowired
    private ProductExportService productExportService;
    @Autowired
    ExpandObjectForm expandObjectForm;
    Context context = null;
    EwcUtilities ewcUtilities = new EwcUtilities();
    DateValidator dateValidator = new DateValidator();
    CreateContext createContext = new CreateContext();

    /**
     * This method is call the exportProduct method to get the JSONString
     *
     * @return String expandProductJSON
     * @throws CustomException
     */
    @RequestMapping(value = "/products", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(
            value = "Export Product object by product type",
            notes = "This api inidicate that export product by product type.Which can take requestParams like product type,limit, attribute list and requestHeader like user,pass,Content-Type",
            produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful GET REST API executed", response = AllJsonObjectResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 404, message = "Entity Not Found"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity exportProduct(
            @ApiParam(value = "User indicate userName of Enovia System", required = true)
            @RequestHeader(value = "user") String userName,
            @ApiParam(value = "Passwoard must be base64 encrypt password.", required = true)
            @RequestHeader(value = "pass") String pass,
            @ApiParam(value = "Type indicate product object type", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam("Date Format :(mm/dd/yyyy HH:mm:ss a) or (mm/dd/yyyy) ,Those product create or update date after or equal start date that product object export")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam("Date Format :(mm/dd/yyyy HH:mm:ss a) or (mm/dd/yyyy) ,Those product create or update date before or equal end date that product object export")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam("Limit param indicates that how much export product object ")
            @RequestParam(value = "limit", required = false, defaultValue = "10000") int limit,
            @ApiParam("Used these attribute values (type,name,revision,description,current,email,owner,Marketing Text,Marketing Name,Display Name,Display Text,classification path)")
            @RequestParam(value = "attributeList", required = false) String attributes) throws CustomException, IOException {
        String json = "";
        JSONObject finalObject = new JSONObject();
        JSONArray mainJsonArray = new JSONArray();
        String finalJson;
        try {
//            final Context context = (Context) httpRequest.getAttribute("context");

            EnoviaWebserviceCommon enoviaWebserviceCommon = new EnoviaWebserviceCommon();
            Context context = null;
            Base64DecryptServiceImpl base64DecryptServiceImpl = new Base64DecryptServiceImpl();
            String userPassword = base64DecryptServiceImpl.decrypt(pass);
//          context = enoviaWebserviceCommon.getSecureContext(userName, userPassword);
            context = createContext.createCasContext(userName, userPassword, PropertyReader.getProperty("matrix.context.cas.connection.host"));
            if (dateValidator.isValid(startDate, endDate)) {
                endDate = updateEndDate(endDate);
                json = exportProductJSON(type, limit, startDate, endDate, attributes, context);
            } else {
                json = dateValidator.validationResponse();
            }
        } catch (Exception ex) {
            LOGGER.debug(ex.getMessage());
            LOGGER.info(ex.getMessage());
            mainJsonArray.put(ex.getMessage());
            finalObject.put("Results", ewcUtilities.jsonArrayToString(mainJsonArray));
            finalJson = ewcUtilities.jsonObjectToString(finalObject);
            String finalObjectStr = ewcUtilities.replaceLast(finalJson, "\"", "");
            finalObjectStr = finalObjectStr.substring(0, 11) + finalObjectStr.substring(12);
            finalJson = finalObjectStr.replaceAll("\":null", "\":\"\"");
            ObjectMapper mapper = new ObjectMapper();
            Object jsonValue = mapper.readValue(finalJson, Object.class);
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonValue);

        }
        return new ResponseEntity(json,HttpStatus.OK);
    }

    /**
     *
     * This method is call the exportProductByName method to get the JSONString
     *
     * @return String expandProductByNameJSON
     * @throws CustomException
     */
    @RequestMapping(value = "/product/name", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(
            value = "Export Product object by product name",
            notes = "This api inidicate that export product by product name,product type,product revision.Which can take requestParams like product name product type, product revision, attribute list and requestHeader like user,pass,Content-Type",
            produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful GET REST API executed", response = AllJsonObjectResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 404, message = "Entity Not Found"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity exportProductByName(
            @ApiParam(value = "User indicate userName of Enovia System", required = true)
            @RequestHeader(value = "user") String userName,
            @ApiParam(value = "Passwoard must be base64 encrypt password.", required = true)
            @RequestHeader(value = "pass") String pass,
            @ApiParam("Type indicate product object type")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(value = "Name indicate product object name.", required = true)
            @RequestParam(value = "name") String name,
            @ApiParam("Date Format :(mm/dd/yyyy HH:mm:ss a) or (mm/dd/yyyy) ,Those product create or update date after or equal start date that product object export")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam("Date Format :(mm/dd/yyyy HH:mm:ss a) or (mm/dd/yyyy) ,Those product create or update date before or equal end date that product object export")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam("Revision indicate product object revision")
            @RequestParam(value = "revision", required = false) String revision,
            @ApiParam("Used these attribute values (type,name,revision,description,current,email,owner,Marketing Text,Marketing Name,Display Name,Display Text,classification path)")
            @RequestParam(value = "attributeList", required = false) String attributes) throws CustomException, Exception {
        String json = "";
        JSONObject finalObject = new JSONObject();
        JSONArray mainJsonArray = new JSONArray();
        String finalJson;
        try {
//            final Context context = (Context) httpRequest.getAttribute("context");

            EnoviaWebserviceCommon enoviaWebserviceCommon = new EnoviaWebserviceCommon();
            Context context = null;
            Base64DecryptServiceImpl base64DecryptServiceImpl = new Base64DecryptServiceImpl();
            String userPassword = base64DecryptServiceImpl.decrypt(pass);
//            context = enoviaWebserviceCommon.getSecureContext(userName, userPassword);
            context = createContext.createCasContext(userName, userPassword, PropertyReader.getProperty("matrix.context.cas.connection.host"));
            if (dateValidator.isValid(startDate, endDate)) {
                endDate = updateEndDate(endDate);
                json = exportProductByNameJSON(type, name, revision, startDate, endDate, attributes, context);
            } else {
                json = dateValidator.validationResponse();
            }
        } catch (Exception ex) {
            LOGGER.debug(ex.getMessage());
            LOGGER.info(ex.getMessage());
            mainJsonArray.put(ex.getMessage());
            finalObject.put("Results", ewcUtilities.jsonArrayToString(mainJsonArray));
            finalJson = ewcUtilities.jsonObjectToString(finalObject);
            String finalObjectStr = ewcUtilities.replaceLast(finalJson, "\"", "");
            finalObjectStr = finalObjectStr.substring(0, 11) + finalObjectStr.substring(12);
            finalJson = finalObjectStr.replaceAll("\":null", "\":\"\"");
            ObjectMapper mapper = new ObjectMapper();

            Object jsonValue = mapper.readValue(finalJson, Object.class);
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonValue);

        }
        return new ResponseEntity(json,HttpStatus.OK);

    }

    /**
     * This method get json string response by calling getjsonOutput method
     *
     * @param type
     * @param limit
     * @param attributes
     * @return String json
     * @throws CustomException
     */
    private String exportProductJSON(String type, int limit, String startDate, String endDate,
            String attributes, Context context) throws CustomException, com.bjit.common.rest.app.service.utilities.CustomException {

        LOGGER.debug("Type : " + type + " limit :" + limit + " Attributes :" + attributes);
        LOGGER.info("Type : " + type + " limit :" + limit +" Start Date :" + startDate +" End Date:" + endDate + " Attributes :" + attributes);
        String json = "";
        if (attributes != null && attributes != "") {
            ServiceFileUtil serviceFileUtil = new ServiceFileUtil();
            serviceFileUtil.readAttributes();
            //Converting the attribute list in array list
            List<String> attributeList = new ArrayList<>();
            String[] attributeArr;
            if (attributes != null && !attributes.isEmpty()) {
                attributeArr = attributes.split(",");
                for (String attribute : attributeArr) {
                    attributeList.add(attribute.trim());
                }
            } else {
                //need to implement
                attributeList = serviceFileUtil.getSelectedAttributeList();
            }
            expandObjectForm.setType(type);
            expandObjectForm.setSelectedItem(attributeList);
            expandObjectForm.setGetFrom(Boolean.TRUE);
            expandObjectForm.setGetTo(Boolean.FALSE);
            ArrayList<String> finalSelectedAttributeList = new ArrayList<>();

            ArrayList<String> finalSelectedObjParamList = new ArrayList<>();

            Map<String, String> allItemMap = serviceFileUtil.getAttrActualNameAndDisplayNameMap();
            LOGGER.debug("Al items map: " + allItemMap);

            List<String> propertyList = serviceFileUtil.getPropertyNameList();
            LOGGER.debug("propertyList: " + propertyList);
            List<String> notPropertyNotAttributeList = serviceFileUtil.getNotPropertyAndAttributeNameList();
            LOGGER.debug("notPropertyNotAttributeList: " + notPropertyNotAttributeList);
            List<String> selectedAttrList = expandObjectForm.getSelectedItem();
            LOGGER.debug("Selected item: " + selectedAttrList.toString());

            for (String listItem : selectedAttrList) {
                LOGGER.debug("Current item: " + listItem);
                if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                    LOGGER.debug("property: " + listItem);
                    if (productExportService.hasValueInMap(allItemMap, listItem)) {
                        finalSelectedObjParamList.add("'" + listItem + "'");
                    }
                } else {
                    LOGGER.debug("obj atr: " + listItem);
                    finalSelectedAttributeList.add("'attribute[" + listItem + "]'");
                }
            }

            json = productExportService.getJsonOutput(type, limit, startDate, endDate, context, finalSelectedAttributeList, finalSelectedObjParamList, selectedAttrList);
        } else {
            try {
                try {

                    json = productExportService.getJsonOutput(type, limit, startDate, endDate, context);
                    LOGGER.debug("Final Json: " + json);

                } catch (CustomException ex) {
                    LOGGER.debug(ex.getMessage());
                    throw new CustomException(ex.getMessage());
                }
            } catch (CustomException ex) {
                LOGGER.debug(ex.getMessage());
                throw new CustomException(ex.getMessage());
            }
        }
        return json;
    }

    /**
     * This method get json string response by calling getjsonOutput method
     *
     * @param name
     * @param type
     * @param revision
     * @param attributes
     * @return String json
     * @throws CustomException
     */
    private String exportProductByNameJSON(String type, String name, String revision, String startDate, String endDate,
            String attributes, Context context) throws CustomException, com.bjit.common.rest.app.service.utilities.CustomException {
        String json = "";
        LOGGER.debug("Type : " + type + " name :" + name + " revision :" + revision + " Attributes :" + attributes);
        LOGGER.info("Type : " + type + " name :" + name +" Start Date :" + startDate +" End Date:" + endDate + " Attributes :" + attributes);
        if (attributes != null && attributes != "") {
            ServiceFileUtil serviceFileUtil = new ServiceFileUtil();
            serviceFileUtil.readAttributes();
            //Converting the attribute list in array list
            List<String> attributeList = new ArrayList<>();
            String[] attributeArr;
            if (attributes != null && !attributes.isEmpty()) {
                attributeArr = attributes.split(",");
                for (String attribute : attributeArr) {
                    attributeList.add(attribute.trim());
                }
            } else {
                //need to implement
                attributeList = serviceFileUtil.getSelectedAttributeList();
            }
            expandObjectForm.setName(name);
            expandObjectForm.setType(type);
            expandObjectForm.setSelectedItem(attributeList);
            expandObjectForm.setGetFrom(Boolean.TRUE);
            expandObjectForm.setGetTo(Boolean.FALSE);
            ArrayList<String> finalSelectedAttributeList = new ArrayList<>();
            ArrayList<String> finalSelectedObjParamList = new ArrayList<>();

            Map<String, String> allItemMap = serviceFileUtil.getAttrActualNameAndDisplayNameMap();
            LOGGER.debug("Al items map: " + allItemMap);

            List<String> propertyList = serviceFileUtil.getPropertyNameList();
            LOGGER.debug("propertyList: " + propertyList);
            List<String> notPropertyNotAttributeList = serviceFileUtil.getNotPropertyAndAttributeNameList();
            LOGGER.debug("notPropertyNotAttributeList: " + notPropertyNotAttributeList);
            List<String> selectedAttrList = expandObjectForm.getSelectedItem();
            LOGGER.debug("Selected item: " + selectedAttrList.toString());

            for (String listItem : selectedAttrList) {
                LOGGER.debug("Current item: " + listItem);
                if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                    LOGGER.debug("property: " + listItem);
                    if (productExportService.hasValueInMap(allItemMap, listItem)) {
                        finalSelectedObjParamList.add("'" + listItem + "'");
                    }
                } else {
                    LOGGER.debug("obj atr: " + listItem);
                    finalSelectedAttributeList.add("'attribute[" + listItem + "]'");
                }
            }

            json = productExportService.getJsonOutput(type, name, revision, startDate, endDate, context, finalSelectedAttributeList, finalSelectedObjParamList, selectedAttrList);
        } else {
            try {
                try {

                    json = productExportService.getJsonOutput(type, name, revision, startDate, endDate, context);
                    LOGGER.debug("Final Json: " + json);
                } catch (CustomException ex) {
                    LOGGER.debug(ex.getMessage());
                    throw new CustomException(ex.getMessage());
                }
            } catch (CustomException ex) {
                LOGGER.debug(ex.getMessage());
                throw new CustomException(ex.getMessage());
            }
        }
        return json;
    }

    public String updateEndDate(String date) {
        String time = " 11:59:59 PM";
        if ((date != null && !date.isEmpty() && date.length() == 10)) {
            date = date.concat(time);
        }
        return date;
    }
}
