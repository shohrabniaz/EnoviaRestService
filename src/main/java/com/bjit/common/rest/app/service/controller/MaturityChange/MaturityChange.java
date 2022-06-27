/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.MaturityChange;

/**
 *
 * @author Tahmid
 */
import com.bjit.common.code.utility.context.Passport;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.maturity.MaturityChangeService;
//import com.bjit.common.rest.app.service.context.Passport;
//import com.bjit.common.rest.app.service.context.Passport;
import com.bjit.common.rest.app.service.model.MaturityChange.MaturityChangeBean;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.search.SearchService;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import javax.servlet.http.HttpServletRequest;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.Constants;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/change-maturity")
public class MaturityChange {

    private static final org.apache.log4j.Logger MATURITY_CHANGE_LOGGER = org.apache.log4j.Logger.getLogger(MaturityChange.class);
    private Context context = null;

    @Autowired
    private MaturityChangeService maturityChangeService;

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/promote", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity promoteObject(HttpServletRequest httpRequest, HttpServletResponse response, @RequestHeader HttpHeaders headers,
            @RequestBody final MaturityChangeBean maturityChangeBean) throws Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        ResponseEntity responseEntity;
        String buildResponse;
        boolean checkPhysicalIdValidity = true;
        if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getTostate())) {
            buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.state.missing"))
                    .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                    .buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();

        String user = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
        String pass = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
        String host = PropertyReader.getProperty("matrix.context.cas.connection.host");

        try {
            MATURITY_CHANGE_LOGGER.debug("Name: " + maturityChangeBean.getName());
            MqlQueries mqlQuery = new MqlQueries();

            try {
                CreateContext createContext = new CreateContext();

                context = createContext.getAdminContext();
                if (!context.isConnected()) {
                    throw new Exception(Constants.CONTEXT_EXCEPTION);
                }
            } catch (Exception exp) {
                return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
            }
            if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getId())) {
                if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getType())
                        || NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getName())
                        || NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getRev())) {
                    buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.parameter.validation.error"))
                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                            .buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
                try {
                    String physicalId = mqlQuery.getPhysicalIdFromTNR(context, maturityChangeBean.getType(), maturityChangeBean.getName(), maturityChangeBean.getRev());
                    if (NullOrEmptyChecker.isNullOrEmpty(physicalId)) {
                        buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.item.missing")).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
                        return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                    }
                    maturityChangeBean.setId(physicalId);
                    checkPhysicalIdValidity = false;
                } catch (Exception exp) {
                    buildResponse = responseBuilder.addErrorMessage(exp.getMessage())
                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                            .buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            }
            if (checkPhysicalIdValidity) {
                if (!mqlQuery.doesExist(context, maturityChangeBean.getId())) {
                    buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.item.missing"))
                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                            .buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            }
            try {

                Map<String, String> cas = maturityChangeService.getLogin();

                Map<String, String> csrf = maturityChangeService.getCSRFToken(cas, user, pass);

                String uri = host + PropertyReader.getProperty("maturity.change.promote.url");
                JSON jsonSerializer = new JSON();
                String serializedMaturityChangeBean = jsonSerializer.serialize(maturityChangeBean);
                MATURITY_CHANGE_LOGGER.debug("serializedMaturityChangeBean: " + serializedMaturityChangeBean);
                StringBuilder request = new StringBuilder("{\"id\":").append("\"").append(maturityChangeBean.getId()).append("\"")
                        .append(", \"nextState\":").append("\"").append(maturityChangeBean.getTostate()).append("\"").append("}");
                StringBuilder requestJsonBuilder = new StringBuilder("{\"data\" : [")
                        .append(request.toString())
                        .append("]}");

                String securityContext = PropertyReader.getProperty("maturity.change.security.context.value");
                responseEntity = maturityChangeService.getRestResponse(uri, requestJsonBuilder, securityContext, httpRequest, csrf);

                MATURITY_CHANGE_LOGGER.debug("Response: " + responseEntity.getBody());
            } catch (Exception exp) {
                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            return responseEntity;
        } catch (Exception e) {
            buildResponse = responseBuilder.addErrorMessage(e.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

//    @ResponseBody
//    @RequestMapping(value = "/v2/promote", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity promoteObjectService(HttpServletRequest httpRequest, HttpServletResponse response, @RequestHeader HttpHeaders headers,
//            @RequestBody final MaturityChangeBean maturityChangeBean) throws Exception {
//        IResponse responseBuilder = new CustomResponseBuilder();
//        ResponseEntity responseEntity;
//        String buildResponse;
//        boolean checkPhysicalIdValidity = true;
//        if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getTostate())) {
//            buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.state.missing"))
//                    .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
//                    .buildResponse();
//            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//        }
//
//        ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
//        String user = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
//        String pass = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));
//        String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
//
//        try {
//            MATURITY_CHANGE_LOGGER.debug("Name: " + maturityChangeBean.getName());
//            MqlQueries mqlQuery = new MqlQueries();
//            try {
//                CreateContext createContext = new CreateContext();
//                context = createContext.getAdminContext();
//                if (!context.isConnected()) {
//                    throw new Exception(PropertyReader.getProperty("context.generation.failure"));
//                }
//            } catch (Exception exp) {
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage())
//                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
//                        .buildResponse();
//                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//            }
//            if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getPhysicalid())) {
//                if (NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getType())
//                        || NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getName())
//                        || NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getRev())) {
//                    buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.parameter.validation.error"))
//                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
//                            .buildResponse();
//                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//                }
//                try {
//                    String physicalId = mqlQuery.getPhysicalIdFromTNR(context, maturityChangeBean.getType(), maturityChangeBean.getName(), maturityChangeBean.getRev());
//                    if (NullOrEmptyChecker.isNullOrEmpty(physicalId)) {
//                        buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.item.missing")).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
//                        return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//                    }
//                    maturityChangeBean.setPhysicalid(physicalId);
//                    checkPhysicalIdValidity = false;
//                } catch (Exception exp) {
//                    buildResponse = responseBuilder.addErrorMessage(exp.getMessage())
//                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
//                            .buildResponse();
//                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//                }
//            }
//            if (checkPhysicalIdValidity) {
//                if (!mqlQuery.doesExist(context, maturityChangeBean.getPhysicalid())) {
//                    buildResponse = responseBuilder.addErrorMessage(PropertyReader.getProperty("maturity.change.item.missing"))
//                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
//                            .buildResponse();
//                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//                }
//            }
//            try {
//                String ticket = Passport.getTicket(host, user, pass).split(";")[0];
//                MATURITY_CHANGE_LOGGER.debug("ticket: " + ticket);
//                String uri = host + PropertyReader.getProperty("maturity.change.promote.url") + ticket;
//                JSON jsonSerializer = new JSON();
//                String serializedMaturityChangeBean = jsonSerializer.serialize(maturityChangeBean);
//                MATURITY_CHANGE_LOGGER.debug("serializedMaturityChangeBean: " + serializedMaturityChangeBean);
//                StringBuilder requestJsonBuilder = new StringBuilder("{\"data\" : [")
//                        .append(serializedMaturityChangeBean)
//                        .append("]}");
//                if (!NullOrEmptyChecker.isNullOrEmpty(maturityChangeBean.getSecurityContext())) {
//                    headers.add(PropertyReader.getProperty("maturity.change.security.context.key"), maturityChangeBean.getSecurityContext());
//                   
//                } else {
//                    headers.add(PropertyReader.getProperty("maturity.change.security.context.key"), PropertyReader.getProperty("maturity.change.security.context.value"));
//                }
//                responseEntity = getRestResponse(uri, requestJsonBuilder, headers);
//                MATURITY_CHANGE_LOGGER.debug("Response: " + responseEntity.getBody());
//            } catch (Exception exp) {
//                buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
//                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//            }
//            return responseEntity;
//        } catch (Exception e) {
//            buildResponse = responseBuilder.addErrorMessage(e.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
//            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//        } finally {
//            if (context != null) {
//                context.close();
//            }
//        }
//    }
}
