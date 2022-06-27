/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ids.controller;


import com.bjit.ewc18x.message.EWMessages;
import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.model.UiStatusMessageForm;
import com.bjit.ewc18x.model.UpdateObjectForm;
import com.bjit.ewc18x.service.CustomAuthenticationService;
import com.bjit.ewc18x.service.ExpandObjectService;
import com.bjit.ewc18x.service.UpdateObjectService;
import com.bjit.ewc18x.utils.ApplicationMessage;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.EnoviaWebserviceCommon;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ewc18x.validator.CustomValidator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Administrator
 */
@Controller
public class IDSUpdateObjectController {

    private String id = "CBPWS";
    private String version = "1.0";
    private String validityHours = "1";
    private ArrayList<String> columnList = new ArrayList<String>();

    private static final Logger LOGGER = Logger.getLogger(IDSUpdateObjectController.class);

    @Autowired
    private UpdateObjectService businessObjectService;

    @Autowired
    private ExpandObjectService expandObjectService;

    @Autowired
    private CustomAuthenticationService customAuthenticationService;
    
//    @Autowired
//    PLMKeyServiceClientImpl pLMKeyServiceClient;

    /**
     * Used to get updateObject initial page
     * @param httpSession is a http session object, holds session attributes
     * @param model
     * @return updateObject page
     */
    @RequestMapping(value = "updateStructure", method = RequestMethod.GET)
    public String updateObject(@RequestParam(required = false) String userId,
            HttpSession httpSession, @ModelAttribute("updateObjectForm") UpdateObjectForm updateObjectForm, Model model) {
        LOGGER.debug("service=ImportStructure;com=request;method=get;msg=Fetching Import Structure Form");
        if (userId != null) {
            System.out.println(userId);
            updateObjectForm.setUserID(userId);
            System.out.println(updateObjectForm.getUserID());
            System.out.println("User Id form session: "+httpSession.getAttribute("username"));
            if (!userId.equals((String)httpSession.getAttribute("username"))) {
            System.out.println("username did not matched");
            httpSession.setAttribute("username", null);
            httpSession.setAttribute("plmKey", null);
            httpSession.setAttribute("securityContext", null);
            httpSession.setAttribute("context", null);
            }
//            String cbpKey = new EnoviaWebserviceCommon().getCbpKey(userId);
//            if (cbpKey == null) {
//                EWMessages messages = EWMessages.error().add("e.ew.vm.4.0004");
//                UiStatusMessageForm uiStatusMessageForm = new UiStatusMessageForm();
//                uiStatusMessageForm.setEwMessages(messages);
//                model.addAttribute("uiStatusMessageForm", uiStatusMessageForm);
//                LOGGER.debug("Could not find CbpKey!");
//                return "error";
//            } else {
//                updateObjectForm.setCbpKey(cbpKey);
//                LOGGER.debug("Found CbpKey!");
//            }
        } else {
            UiStatusMessageForm uiStatusMessageForm = new UiStatusMessageForm();
            EWMessages messages = EWMessages.error().add("e.ew.vm.4.0004");
            uiStatusMessageForm.setEwMessages(messages);
            model.addAttribute("uiStatusMessageForm", uiStatusMessageForm);
            LOGGER.debug("service=ImportStructure;msg=" + "User is not requesting from 3DSpace");
            return "error";
        }
        model.addAttribute("updateObjectForm", updateObjectForm);
        return "ids/updateStructure";
    }
    
    /**
     * used to forward request to service and update objects' attributes values
     * @param httpSession is a http session object, holds session attributes
     * @param updateObjectForm is a model object for UpdateObjectForm class 
     * @param result is for checking error
     * @param model
     * @return updateObject page
     */
    @RequestMapping(value = "updateStructure", method = RequestMethod.POST)
    public String updateObjectValues(HttpSession httpSession, @ModelAttribute("updateObjectForm") UpdateObjectForm updateObjectForm, BindingResult result, Model model) {
        LOGGER.info("service=ImportStructure;com=request;method=post;user=" + updateObjectForm.getUserID() + ";msg=Received Import Request");
        CustomValidator userValidator = new CustomValidator();
        userValidator.validateData(updateObjectForm, result, httpSession);
        if (result.hasErrors()) {
            String error = result.toString();
            updateObjectForm.setOutput("");
            LOGGER.error("service=ImportStructure;msg=" + ApplicationMessage.ERR001 + " " + ApplicationMessage.PG006 + " error : " + error);
            return "ids/updateStructure";
        }
        //check if any file is selected
        String fileName = updateObjectForm.getFile().getOriginalFilename();
        if ("".equals(fileName) || fileName == null) {
            EWMessages messages = EWMessages.error().add("e.ew.vm.4.0001");
            updateObjectForm.setEwMessages(messages);
            return "ids/updateStructure";
        }
        //check if the file is in xls format
        int nameLength = fileName.length();
        String format = fileName.substring(nameLength - 3, nameLength);
        LOGGER.debug("service=ImportStructure;msg=Update object file format : " + format);
        if (!format.equalsIgnoreCase("xls")) {
            EWMessages messages = EWMessages.error().add("e.ew.vm.4.0002");
            updateObjectForm.setEwMessages(messages);
            return "ids/updateStructure";
        }
        long fileSize = updateObjectForm.getFile().getSize();
        if (fileSize > 1000000) {
            EWMessages messages = EWMessages.error().add("e.ew.vm.4.0003");
            updateObjectForm.setEwMessages(messages);
            return "ids/updateStructure";
        }
        try {
           // Context context = null;
            Context context = (Context) httpSession.getAttribute("context");
            if (context == null) {
                context = new EnoviaWebserviceCommon().getContext(updateObjectForm.getUserID(), updateObjectForm.getUserID(), true, updateObjectForm.getSecurityContext());
                httpSession.setAttribute("context", context);
            } else {
                 LOGGER.debug("service=ImportStructure;msg=Context from session: "+context.checkContext());
                 //throw new CustomException("Error generating context.");
            }
            File file = saveMultipartFile(updateObjectForm.getFile());
//            Context context = (Context) httpSession.getAttribute("context");
//            LOGGER.debug("is context found: "+context.checkContext());
            AttributesForm attributes = new AttributesForm();
            try {
                attributes.readValues("Attributes.conf");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(IDSUpdateObjectController.class.getName()).log(Level.SEVERE, null, ex);
            }
            LOGGER.debug("service=ImportStructure;msg=adding extension attribute to the list.");
            try {
                attributes.readValues("ExtensionAttributes.txt");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(IDSUpdateObjectController.class.getName()).log(Level.SEVERE, null, ex);
            }
            updateObjectForm = businessObjectService.populateEnvironment(updateObjectForm);
            String adminKey = PropertyReader.getProperty("admin.key");
            //String cbpKey = pLMKeyServiceClient.generatePLMKeyFromStructuredContext(adminKey, updateObjectForm.getUserID(), updateObjectForm.getUserID(), id, updateObjectForm.getSecurityContext(), updateObjectForm.getIContextURI(), updateObjectForm.getPort(), updateObjectForm.getIValidityURI(), validityHours);
           // LOGGER.debug("service=ImportStructure;msg=" + "Generated CBP key : " + cbpKey);
            LOGGER.info("service=ImportStructure;msg=" + "Calling Import Service for request from user : " + updateObjectForm.getUserID());
            //businessObjectService.setAttributeValues(cbpKey ,file, updateObjectForm, attributes.getSingleRequestAttr(), attributes.getNotUpdatableAttr(), attributes.getNotUpdatableProperties(), context);
            businessObjectService.setAttributeValues(file, updateObjectForm, attributes, context);
            LOGGER.info("service=ImportStructure;com=response;method=post;user=" + updateObjectForm.getUserID() + ";status=success;msg=Sending Import Result");
        } catch (CustomException | FileNotFoundException e) {
            //updateObjectForm.setDemoexception(e.getMessage());
            EWMessages messages = EWMessages.error().addMsgText(e.getMessage());
            updateObjectForm.setEwMessages(messages);
            LOGGER.info("service=ImportStructure;com=response;method=post;user=" + updateObjectForm.getUserID() + ";status=failure;msg=" + e.getMessage());
        }
        LOGGER.debug("service=ImportStructure;msg=Set to updateObjectForm attribute: " + updateObjectForm);
        LOGGER.debug("service=ImportStructure;msg=Ended Updating Structure");
        model.addAttribute("updateObjectForm", updateObjectForm);
        return "ids/updateStructure";
    }

    public File saveMultipartFile(MultipartFile file) throws CustomException {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                // Creating the directory to store file
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists()) {
                    dir.mkdirs();
                    LOGGER.debug("service=ImportStructure;msg=Created directory: " + dir.getAbsolutePath());
                }

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getName());
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
                LOGGER.debug("service=ImportStructure;msg=Server File Location: " + serverFile.getAbsolutePath());
                return serverFile;
            } catch (FileNotFoundException ex) {
                LOGGER.error("service=ImportStructure;msg=Failed to create directory/file on server.");
                ex.printStackTrace();
                throw new CustomException(ex.getMessage());
            } catch (IOException ex) {
                LOGGER.error("service=ImportStructure;msg=Failed to read/write file.");
                ex.printStackTrace();
                throw new CustomException(ex.getMessage());
            }
        } else {
            return null;
        }
    }
}
