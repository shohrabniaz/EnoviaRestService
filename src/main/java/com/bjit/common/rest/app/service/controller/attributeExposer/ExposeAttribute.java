/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.attributeExposer;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.ObjectAttribute.CustomMap;
import com.bjit.common.rest.app.service.model.ObjectAttribute.ObjectAttributeBean;
import com.bjit.common.rest.app.service.model.ObjectAttribute.ObjectAttributeValueBean;
import com.bjit.common.rest.app.service.model.ObjectAttribute.Version;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Tahmid
 */
@RestController
@RequestMapping(path = "/attribute-values")
public class ExposeAttribute {

    @Autowired
    private CommonPropertyReader commonproperties;

    private static final org.apache.log4j.Logger EXPOSE_ATTRIBUTE_LOGGER = org.apache.log4j.Logger.getLogger(ExposeAttribute.class);
    private Context context = null;

    /*
    * This service is mainly built for EKL API of Catia V6, which does not allow headers in request.
     */
    @ResponseBody
    @PostMapping(value = "/attributeValues")
    public ResponseEntity<?> getAttributeValues(HttpServletRequest httpRequest,
            HttpServletResponse response,
            @RequestParam("responseType") String responseType,
            @RequestBody ObjectAttributeBean objAttrBean) throws IOException, Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = "";
        boolean isResponseTypeXML = responseType.equals(MediaType.APPLICATION_XML_VALUE) ? true : false;
        if (NullOrEmptyChecker.isNullOrEmpty(responseType)) {
            responseType = MediaType.APPLICATION_JSON_VALUE;
        } else if (!responseType.equals(MediaType.APPLICATION_XML_VALUE)
                && !responseType.equals(MediaType.APPLICATION_JSON_VALUE)) {
            responseBuilder.addErrorMessage(commonproperties.getPropertyValue("response.type.not.allowed"))
                    .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED);
            buildResponse = isResponseTypeXML ? responseBuilder.buildResponse(MediaType.APPLICATION_XML_VALUE) : responseBuilder.buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            EXPOSE_ATTRIBUTE_LOGGER.debug("Name: " + objAttrBean.getName());
            MqlQueries mqlQuery = new MqlQueries();
            try {
                CreateContext createContext = new CreateContext();
                context = createContext.getAdminContext();
                if (!context.isConnected()) {
                    throw new Exception(PropertyReader.getProperty("context.generation.failure"));
                }
            } catch (Exception exp) {
                responseBuilder.addErrorMessage(exp.getMessage())
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED);
                buildResponse = isResponseTypeXML ? responseBuilder.buildResponse(MediaType.APPLICATION_XML_VALUE) : responseBuilder.buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

            try {
                TNR tnr = new TNR();
                tnr.setType(objAttrBean.getType());
                tnr.setName(objAttrBean.getName());
                tnr.setRevision(objAttrBean.getRev());

                HashMap<String, String> whereClausesMap = new HashMap<>();

                List<String> selectDataList = new ArrayList<>();
                selectDataList.addAll(objAttrBean.getProperties());
                selectDataList.add("revision");
                selectDataList.addAll(objAttrBean.getAttributes());

                ObjectAttributeValueBean objAttrValueBean = new ObjectAttributeValueBean();
                mqlQuery
                        .searchItem(context, tnr, whereClausesMap, selectDataList, null)
                        .forEach(propertyValueMap -> {
                            Version version = new Version();
                            String revValue = propertyValueMap.get("revision");
                            version.setValue(revValue);
                            if (!objAttrBean.getProperties().contains("revision")) {
                                propertyValueMap.remove("revision");
                            }
                            CustomMap properties = new CustomMap();
                            propertyValueMap.forEach((key, val) -> properties.addEntry(key, val));
                            version.setProperties(properties);
                            objAttrValueBean.getVersion().add(version);
                        });
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .buildResponse(ObjectAttributeValueBean.class, objAttrValueBean, responseType);
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);

            } catch (Exception exp) {
                responseBuilder
                        .addErrorMessage(exp.getMessage())
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED);
                buildResponse = isResponseTypeXML ? responseBuilder.buildResponse(MediaType.APPLICATION_XML_VALUE) : responseBuilder.buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            responseBuilder
                    .addErrorMessage(e.getMessage())
                    .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED);
            buildResponse = isResponseTypeXML ? responseBuilder.buildResponse(MediaType.APPLICATION_XML_VALUE) : responseBuilder.buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }
}
