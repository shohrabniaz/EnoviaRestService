/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.GTS;

import com.bjit.common.rest.app.service.GTS.translation.TranslationService;
import com.bjit.common.rest.app.service.manager.GTS.TranslationManager;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.Translation.Translation;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.payload.translation_response.ITranslationResponse;
import com.bjit.common.rest.app.service.payload.translation_response.TranslationResponseBuilder;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
@RequestMapping(path = "/vsix")
public class TranslationController {

    @Autowired
    private TranslationService translationService;
    private static final Logger TRANSLATION_CONTROLLER_LOGGER = Logger.getLogger(TranslationController.class);
    long startQueryTime = 0;

    /**
     * This method is called to update enovia item object title according to
     * bundle id and gts translation
     *
     * @param httpRequest
     * @param response
     * @param translation
     * @return ResponseEntity
     * @throws java.lang.Exception
     */
    @ResponseBody
    @PostMapping(value = "/translation/title/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Object updateTitleForBundle(HttpServletRequest httpRequest, HttpServletResponse response,
            @RequestBody Translation translation) throws Exception {
        ITranslationResponse responseBuilder = new TranslationResponseBuilder();
        String buildResponse = "";
        TRANSLATION_CONTROLLER_LOGGER.info("API Execution Start ------------- ");
        String user = httpRequest.getHeader("user");
        String pass = httpRequest.getHeader("pass");
        String host = PropertyReader.getProperty("matrix.context.cas.connection.host");
        Context context = null;
        TranslationManager translationManager = new TranslationManager(translationService);
        try {
            CreateContext createContext = new CreateContext();
            context = createContext.getContext(user, pass, host, true);
            if (!context.isConnected()) {
                throw new Exception("context is not connected");
            }
        } catch (Exception exp) {
            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        return translationManager.updateObjectsTitle(context, translation);
    }

    /**
     * This method is called to update gts translations which are not updated in
     * enovia
     *
     * @param httpRequest
     * @param response
     * @return ResponseEntity
     * @throws java.lang.Exception
     */
    @ResponseBody
    @GetMapping(value = "/translation/title/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateEnoviaTitleFromGTS(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
        TRANSLATION_CONTROLLER_LOGGER.info("API Execution Start ------------- ");
        ResponseEntity responseEntity = null;
        String buildResponse = "";
        ITranslationResponse responseBuilder = new TranslationResponseBuilder();
        TranslationManager translationManager = new TranslationManager(translationService);
        String language = PropertyReader.getProperty("gts.translation.text.modification.language");
        translationManager.setTranslationLanguage(language);
        List<Map<String, Object>> gtsBundleDetailsList = null;

        try {

            ResponseEntity<LinkedHashMap> responseObj = translationManager.getTranslationFromGts();
            LinkedHashMap<String, Object> responseMap = responseObj.getBody();
            String status = (String) responseMap.get("status");
            if (status != null && status.equalsIgnoreCase("Not Found")) {
                String message = (String) responseMap.get("message");
                buildResponse = responseBuilder
                        .setStatus(Status.OK)
                        .setMessage(message)
                        .setData("[]")
                        .buildResponse();
                TRANSLATION_CONTROLLER_LOGGER.info(buildResponse);
                responseEntity = new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } else {
                gtsBundleDetailsList = (List<Map<String, Object>>) responseMap.get("data");

                CreateContext createContext = new CreateContext();
                Context context = createContext.getAdminContext();
                try {
                    if (!context.isConnected()) {
                        throw new Exception(PropertyReader.getProperty("context.generation.failure"));
                    }
                } catch (Exception exp) {
                    buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                }
                Translation translation = translationManager.getTranslationFromBundleDetailsList(gtsBundleDetailsList);
                responseEntity = translationManager.updateObjectsTitle(context, translation);
            }

            String responseBody = (String) responseEntity.getBody();
            TRANSLATION_CONTROLLER_LOGGER.info("responseBody: " + responseBody);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> attributes = new HashMap<>();

            // convert JSON string to Map
            attributes = mapper.readValue(responseBody, Map.class);
            status = (String) attributes.get("status");
            if (status.equals("OK")) {
                TRANSLATION_CONTROLLER_LOGGER.info("Going to update scheduler timestamp.");
                translationManager.updateSchedulerTimestamp();
            }
            return responseEntity;
        } catch (IOException e) {
            TRANSLATION_CONTROLLER_LOGGER.info("Translation update response read error, cause : " + e.getMessage());
            buildResponse = responseBuilder
                    .setStatus(Status.FAILED)
                    .setMessage(e.getMessage())
                    .setData("[]")
                    .buildResponse();
            TRANSLATION_CONTROLLER_LOGGER.info(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (Exception e) {
            TRANSLATION_CONTROLLER_LOGGER.info("Translation update error, cause : " + e.getMessage());
            buildResponse = responseBuilder
                    .setStatus(Status.FAILED)
                    .setMessage(e.getMessage())
                    .setData("[]")
                    .buildResponse();
            TRANSLATION_CONTROLLER_LOGGER.info(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        }
    }
}
