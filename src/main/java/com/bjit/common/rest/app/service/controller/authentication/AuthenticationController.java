/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.controller.authentication.token.HMAC.WebToken;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Omour Faruq
 */
@RestController
@RequestMapping(path = "/authentication")
public class AuthenticationController {

    private static final Logger AUTHENTICATION_CONTROLLER_LOGGER = Logger.getLogger(AuthenticationController.class);
    private final AuthenticationProcess userAuthentication = new AuthenticationProcess();

    @RequestMapping(value = "/generateToken", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity serviceSecurity(HttpServletRequest httpRequest, @RequestParam Optional<String> credentialVerification, @RequestParam Optional<String> isUserContext) throws Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        System.out.println("\n");
        AUTHENTICATION_CONTROLLER_LOGGER.debug("--------------- ||| Token generation process has been started ||| ---------------");
        AUTHENTICATION_CONTROLLER_LOGGER.debug("#################################################################################");
        try {
            AUTHENTICATION_CONTROLLER_LOGGER.debug("Generating Tokens");

            String userId = httpRequest.getHeader("userId");
            String password = httpRequest.getHeader("password");

            if (nullOrEmpty(userId) || nullOrEmpty(password)) {
                return new ResponseEntity<>(responseBuilder.addErrorMessage("Please provide valid credentials").setStatus(Status.FAILED).buildResponse(), HttpStatus.PRECONDITION_FAILED);
            }

            String checkCasEnv = httpRequest.getHeader("isCas");
            checkCasEnv = (checkCasEnv == null || checkCasEnv.equalsIgnoreCase("")) ? PropertyReader.getProperty("matrix.context.env.connection.isCas") : checkCasEnv;

            Boolean endUserValidation = Boolean.parseBoolean(isUserContext.orElse("false"));

            String contextHost = PropertyReader.getProperty("matrix.context.cas.connection.host");
            String authenticateUserModel = endUserValidation ? userAuthentication.AuthenticateUserAndTokenWithBackEndUser(httpRequest, contextHost, checkCasEnv) : userAuthentication.AuthenticateUser(httpRequest, contextHost, checkCasEnv);

//            boolean validateCredentialsByCreatingContext = Boolean.parseBoolean(PropertyReader.getProperty("context.validation.throuch.create"));
            Boolean validateCreadenttialsByGeneratingContext = Boolean.parseBoolean(credentialVerification.orElse("false"));

            if (endUserValidation) {
                validateCreadenttialsByGeneratingContext = endUserValidation;
            }

            if (validateCreadenttialsByGeneratingContext) {
                Context context;
                try {
                    context = userAuthentication.getContext();
                } catch (Exception exp) {
                    AUTHENTICATION_CONTROLLER_LOGGER.error(exp.getMessage());
                    //String buildResponse = responseBuilder.addErrorMessage("The credentials you have used is not authentic or the context creation service may not be up and running").setStatus(Status.FAILED).buildResponse();
                    String buildResponse = responseBuilder.addErrorMessage("The credentials you have used is not authentic").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
                }

                if (context.isConnected()) {
                    WebToken webToken = new WebToken();
                    String token = webToken.CreateToken(authenticateUserModel);
                    String buildResponse = responseBuilder.setData(token).setStatus(Status.OK).buildResponse();
                    return new ResponseEntity<>(buildResponse, addTokenToResponse(token), HttpStatus.OK);
                }

                String buildResponse = responseBuilder.addErrorMessage("Context is not connected").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            } else {
                ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();

                String userIdInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.username"));
                String contextUserName = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
                String passwordInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.pass"));
                String contextPassword = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));

                if (userIdInPropertiesFile.equals(userId) && passwordInPropertiesFile.equals(password)) {
                    authenticateUserModel = userAuthentication.AuthenticateUser(contextUserName, contextPassword, contextHost, Boolean.parseBoolean(checkCasEnv));

                    WebToken webToken = new WebToken();
                    String token = webToken.CreateToken(authenticateUserModel);
                    String buildResponse = responseBuilder.setData(token).setStatus(Status.OK).buildResponse();
                    return new ResponseEntity<>(buildResponse, addTokenToResponse(token), HttpStatus.OK);
                }
                String buildResponse = responseBuilder.addErrorMessage("The credentials you have used is not authentic").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }

        } catch (Exception exp) {
            AUTHENTICATION_CONTROLLER_LOGGER.error("Error occurred due to : " + exp.getMessage());
            String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } finally {
            AUTHENTICATION_CONTROLLER_LOGGER.debug("--------------- ||| Token Generation process has been completed ||| ---------------");
            AUTHENTICATION_CONTROLLER_LOGGER.debug("###################################################################################");
            System.out.println("\n");
        }
    }

    @RequestMapping(value = "/v2/generateToken", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity v2ServiceSecurity(HttpServletRequest httpRequest, @RequestParam Optional<String> credentialVerification, @RequestParam Optional<String> isUserContext) throws Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        AUTHENTICATION_CONTROLLER_LOGGER.debug("--------------- ||| Token generation process has been started ||| ---------------");
        AUTHENTICATION_CONTROLLER_LOGGER.debug("#################################################################################");
        try {
            AUTHENTICATION_CONTROLLER_LOGGER.debug("Generating Tokens");

            ContextPasswordSecurity contextPasswordSecurity = new ContextPasswordSecurity();
            String user = httpRequest.getHeader("userId");
            String userId = contextPasswordSecurity.decryptPassword(user);
            String pass = httpRequest.getHeader("password");
            String password = contextPasswordSecurity.decryptPassword(pass);

            if (nullOrEmpty(userId) || nullOrEmpty(password)) {
                return new ResponseEntity<>(
                        responseBuilder
                                .addErrorMessage("Please provide valid credentials")
                                .setStatus(Status.FAILED)
                                .buildResponse(),
                        HttpStatus.PRECONDITION_FAILED);
            }

            String checkCasEnv = httpRequest.getHeader("isCas");
            checkCasEnv = (checkCasEnv == null || checkCasEnv.equalsIgnoreCase(""))
                    ? PropertyReader.getProperty("matrix.context.env.connection.isCas")
                    : checkCasEnv;

            Boolean endUserValidation = Boolean.parseBoolean(isUserContext.orElse("false"));

            String contextHost = PropertyReader.getProperty("matrix.context.cas.connection.host");
            String authenticateUserModel = endUserValidation
                    ? userAuthentication.AuthenticateUserAndTokenWithBackEndUser(httpRequest, contextHost, checkCasEnv, true)
                    : userAuthentication.AuthenticateUser(httpRequest, contextHost, checkCasEnv, true);

            Boolean validateCreadenttialsByGeneratingContext = Boolean.parseBoolean(credentialVerification.orElse("false"));

            if (endUserValidation) {
                validateCreadenttialsByGeneratingContext = endUserValidation;
            }

            if (validateCreadenttialsByGeneratingContext) {
                Context context;
                try {
                    context = userAuthentication.getContext();
                } catch (Exception exp) {
                    AUTHENTICATION_CONTROLLER_LOGGER.error(exp.getMessage());
                    String buildResponse = responseBuilder.addErrorMessage("The credentials you have used is not authentic").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
                }

                if (context.isConnected()) {
                    WebToken webToken = new WebToken();
                    String token = webToken.CreateToken(authenticateUserModel);
                    String buildResponse = responseBuilder.setData(token).setStatus(Status.OK).buildResponse();
                    return new ResponseEntity<>(buildResponse, addTokenToResponse(token), HttpStatus.OK);
                }

                String buildResponse = responseBuilder.addErrorMessage("Context is not connected").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            } else {
                String userIdInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.username"));
                String contextUserName = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
                String passwordInPropertiesFile = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("pdm.integration.user.cred.pass"));
                String contextPassword = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));

                if (userIdInPropertiesFile.equals(userId) && passwordInPropertiesFile.equals(password)) {
                    authenticateUserModel = userAuthentication.AuthenticateUser(contextUserName, contextPassword, contextHost, Boolean.parseBoolean(checkCasEnv));

                    WebToken webToken = new WebToken();
                    String token = webToken.CreateToken(authenticateUserModel);
                    String buildResponse = responseBuilder.setData(token).setStatus(Status.OK).buildResponse();
                    return new ResponseEntity<>(buildResponse, addTokenToResponse(token), HttpStatus.OK);
                }
                String buildResponse = responseBuilder.addErrorMessage("The credentials you have used is not authentic").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }

        } catch (Exception exp) {
            AUTHENTICATION_CONTROLLER_LOGGER.error("Error occurred due to : " + exp.getMessage());
            String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } finally {
            AUTHENTICATION_CONTROLLER_LOGGER.debug("--------------- ||| Token Generation process has been completed ||| ---------------");
            AUTHENTICATION_CONTROLLER_LOGGER.debug("###################################################################################");
            System.out.println("\n");
        }
    }

    public Boolean nullOrEmpty(String checkString) {
        return checkString == null || checkString.equalsIgnoreCase("");
    }

//    @RequestMapping(value = "/verifyToken", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity veryfyToken(HttpServletRequest httpRequest) throws Exception {
        IResponse responseBuilder = new CustomResponseBuilder();
        try {
            System.out.println("\n");
            AUTHENTICATION_CONTROLLER_LOGGER.debug("--------------- ||| Token verification process has been started ||| ---------------");
            AUTHENTICATION_CONTROLLER_LOGGER.debug("###################################################################################");

            try {
                String token = httpRequest.getHeader("token");

                if (nullOrEmpty(token)) {
                    String buildResponse = responseBuilder.addErrorMessage("Invalid token. Please provide a valid token").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
                }

                WebToken webToken = new WebToken();
                String newToken = webToken.VerifyToken(token);

                AUTHENTICATION_CONTROLLER_LOGGER.info("Token has verified");

                String authenticationModel = webToken.GetPropertyFromToken(newToken, "Model");

                AuthenticationUserModel AuthenticateUser = userAuthentication.getAuthenticUserModel(authenticationModel);
                String buildResponse = responseBuilder.setData(AuthenticateUser).setStatus(Status.OK).buildResponse();
                return new ResponseEntity<>(buildResponse, addTokenToResponse(newToken), HttpStatus.OK);

            } catch (Exception exp) {
                AUTHENTICATION_CONTROLLER_LOGGER.error("Error occurred due to : " + exp.getMessage());
                String buildResponse = responseBuilder.addErrorMessage("Invalid token. Please provide a valid token").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }
        } catch (Exception exp) {
            AUTHENTICATION_CONTROLLER_LOGGER.error("Error occurred due to : " + exp.getMessage());
            String buildResponse = responseBuilder.addErrorMessage("Invalid token. Please provide valid a token").setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } finally {
            AUTHENTICATION_CONTROLLER_LOGGER.info("--------------- ||| Token verification process has been completed ||| ---------------");
            AUTHENTICATION_CONTROLLER_LOGGER.info("#####################################################################################");
            System.out.println("\n");
        }
    }

    private HttpHeaders addTokenToResponse(String token) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("token", token);
        return responseHeaders;
    }
}
