/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.document.checkin;

import com.bjit.common.rest.app.service.controller.createobject.CreateAndUpdateObjectController;
import com.bjit.common.rest.app.service.model.checkin.CheckinBean;
import com.bjit.common.rest.app.service.model.checkin.DocumentInfoBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import static com.bjit.common.rest.app.service.controller.createobject.CreateAndUpdateObjectController.PACKAGE_MAP;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.ExpansionWithSelect;
import matrix.db.FileList;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.util.Pattern;
import matrix.util.SelectList;

/**
 *
 * @author BJIT
 */
@Controller
@RequestMapping(path = "/checkinObject")
public class CheckinController {

    private static final org.apache.log4j.Logger CHECKIN_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(CheckinController.class);

    @RequestMapping(value = "/checkin", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity checkin(HttpServletRequest httpRequest, @RequestBody final CheckinBean checkinBean) throws MatrixException, Exception {
        CHECKIN_CONTROLLER_LOGGER.info("######################################## CHECKIN CONTROLLER BEGIN ########################################\n");
        IResponse responseBuilder = new CustomResponseBuilder();
        Context context = null;
        try {
            try {
                DisableSSLCertificate.DisableCertificate();
            } catch (KeyManagementException | NoSuchAlgorithmException ex) {
                CHECKIN_CONTROLLER_LOGGER.error(ex.getMessage());
            }

            /*matrix.db.Context context = null;
        try {
            context = CreateContext.getUserCredentialsFromHeader(request);
            CHECKIN_CONTROLLER.info("Created Context");
        } catch (Exception exp) {
            CHECKIN_CONTROLLER.error(exp.getMessage());
            exp.printStackTrace(System.out);
        }*/
 /*checkinBean.getDocumentInfoList().forEach((DocumentInfoBean fileInDocument) -> {
            String fileName = fileInDocument.getFileName();
            String absoluteFileLocation = PropertyReader.getProperty("cim.checkin.file.upload.directory") + fileName;
            
            File file = new File(absoluteFileLocation);
            if(file.exists()){
            }
        });*/
            context = (Context) httpRequest.getAttribute("context");
            /*---------------------------------------- ||| Start Transaction File Checkin ||| ----------------------------------------*/
            CHECKIN_CONTROLLER_LOGGER.info("Starting transaction");
            ContextUtil.startTransaction(context, true);

            String baseObjectId = checkinBean.getBaseObjectId();
            System.out.println("\n\n\n 102 :: baseObjectId ::: "+baseObjectId);
            if (NullOrEmptyChecker.isNullOrEmpty(baseObjectId)) {
                TNR tnr = checkinBean.getTnr();

                if (tnr == null) {
                    CHECKIN_CONTROLLER_LOGGER.error("Base Id and Type or Name or Revision is null");
                    String buildResponse = responseBuilder.addErrorMessage("Please Provide valid 'Type', 'Name', 'Revision' (TNR)").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }

                if (NullOrEmptyChecker.isNullOrEmpty(tnr.getType()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getName()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getRevision())) {
                    CHECKIN_CONTROLLER_LOGGER.error("Base Id and Type or Name or Revision is null");

                    /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                    CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                    ContextUtil.abortTransaction(context);

                    context.close();
                    String buildResponse = responseBuilder.addErrorMessage("Please Provide valid 'Type', 'Name', 'Revision' (TNR)").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                    //return "Base Id and Type or Name or Revision is null";
                }

                try {
                    BusinessObject businessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), "vplm");
                    businessObject.open(context);

                    baseObjectId = businessObject.getObjectId();
                    System.out.println("\n\n\n 130 :: baseObjectId ::: "+baseObjectId);
                } catch (MatrixException exp) {
                    CHECKIN_CONTROLLER_LOGGER.error(exp);
                    String buildResponse = responseBuilder.addErrorMessage("Please Provide valid 'Type', 'Name', 'Revision' (TNR)").setStatus(Status.FAILED).buildResponse();

                    /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                    CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                    ContextUtil.abortTransaction(context);

                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            }

            System.out.println("Object Id : " + baseObjectId);
            CHECKIN_CONTROLLER_LOGGER.debug("Object Id : " + baseObjectId);
        
            BusinessObject parentValObj = null;
            String organization = "";
            String project = "";
            try {
                parentValObj = new BusinessObject(baseObjectId);
                parentValObj.open(context);
                organization = parentValObj.getOrganizationOwner(context).getName();
                project = parentValObj.getProjectOwner(context).getName();
            } catch (MatrixException ex) {
                CHECKIN_CONTROLLER_LOGGER.debug(ex.getMessage());
            }

            List<DocumentInfoBean> listOfDocumentInfo = checkinBean.getDocumentInfoList();
            CreateAndUpdateObjectController createController = new CreateAndUpdateObjectController();
            List<Map<String, String>> tempDocumentIds = checkinBean.getDocumentIds();

            if (NullOrEmptyChecker.isNull(listOfDocumentInfo) && NullOrEmptyChecker.isNull(tempDocumentIds)) {
                CHECKIN_CONTROLLER_LOGGER.error("Please Provide 'documentInfoList' or 'documentIds'");
                String buildResponse = responseBuilder.addErrorMessage("Please Provide 'documentInfoList' or 'documentIds'").setStatus(Status.FAILED).buildResponse();

                /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);

                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

            Map<String, String> populateMap;
            DocumentInfoBean documentInfoBean;
            CreateObjectBean createObjectBean;
            HashMap objectCloneParametersMap;
            String newDocumentId;
            int countFailedObject = 0; // Number of failed object to be created
            HashMap<String, String> businessObjectIdNameMap = new HashMap<>();
            HashMap<String, String> errorResponseMap = new HashMap<>();
//        List<String> documentIdList = new ArrayList<>();
//            disconnectExistingDocumentFromItem(context, baseObjectId);
            StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("[");
            for (int documentInfoIterator = 0; documentInfoIterator < listOfDocumentInfo.size(); documentInfoIterator++) {
                //String type;
                String name;
                try {
                    populateMap = new HashMap<>();
                    documentInfoBean = listOfDocumentInfo.get(documentInfoIterator);
                    createObjectBean = documentInfoBean.getCreateObjectBean();
                    String type = createObjectBean.getTnr().getType();
                    name = createObjectBean.getTnr().getName();

                    if (NullOrEmptyChecker.isNullOrEmpty(type) || NullOrEmptyChecker.isNullOrEmpty(name)) {
                        CHECKIN_CONTROLLER_LOGGER.error("Please Provide 'Type' and 'Name' properly. 'listOfDocumentInfo' index is : " + documentInfoIterator);
                        throw new NullPointerException("Please Provide 'Type' and 'Name' properly");
                    }

                    newDocumentId = createController.getUniqueObjectId(context, type, name);
                    if (newDocumentId == null) {                        
                        createObjectBean = createController.ifSkeletonIdNotExistThenCheckTNRsProperties(createObjectBean, context);
                        objectCloneParametersMap = createController.createObjectCloneParametersMap(context, createObjectBean);
                        //newDocumentId = createController.cloneObjectByJPO(context, createObjectBean, objectCloneParametersMap, new ObjectOperationStatus());
                        newDocumentId = createController.cloneObjectByJPO(context, createObjectBean, objectCloneParametersMap);
                    }
            
                    HashMap<String, String> objectBeanAttributes = createObjectBean.getAttributes();
                    objectBeanAttributes.put("project", project);
                    objectBeanAttributes.put("organization", organization);
                    List<Object> updateAttributes = createController.updateAttributesByMQLCommand(context, newDocumentId, objectBeanAttributes);
//                    List<Object> updateAttributes = createController.updateAttributes(context, newDocumentId, createObjectBean.getAttributes());

                    HashMap<String, String> updateAttributesErrorResultData = (HashMap<String, String>) updateAttributes.get(1);

                    if (updateAttributesErrorResultData.size() > 0) {
                        /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                        CHECKIN_CONTROLLER_LOGGER.error("Aborting Transaction");
                        ContextUtil.abortTransaction(context);

                        CHECKIN_CONTROLLER_LOGGER.error(updateAttributesErrorResultData);
                        throw new Exception(updateAttributesErrorResultData.toString());
                    }

                    //documentIdList.add(newDocumentId);
                    populateMap.put(newDocumentId, documentInfoBean.getFileName());
                    tempDocumentIds.add(populateMap);
                    businessObjectIdNameMap.put(newDocumentId, name);
                } catch (Exception exp) {
                    CHECKIN_CONTROLLER_LOGGER.debug(exp.getMessage());

                    if (countFailedObject > 0) {
                        errorMessageBuilder.append(",");
                    }

                    errorMessageBuilder
                            .append("\"")
                            .append(documentInfoIterator)
                            .append("\"")
                            .append(":")
                            .append("\"")
                            .append(exp.getMessage())
                            .append("\"");
                    ++countFailedObject;

                    /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                    CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                    ContextUtil.abortTransaction(context);
                    throw exp;
                }
            }

            errorMessageBuilder.append("]");

            CHECKIN_CONTROLLER_LOGGER.debug("Number of document cration process failed : " + countFailedObject);

            if (countFailedObject > 0) {
                errorResponseMap.put("failedDocCreation", errorMessageBuilder.toString());
            }
            checkinBean.setDocumentIds(tempDocumentIds);
            /* ListIterator<List<String>> documentListIterator = checkinBean.getDocuments().listIterator();
        
        
        while (documentListIterator.hasNext()) {
            try {
                List<String> newDocumentsFileList = documentListIterator.next();
                String newDocumentId = createDocument(context);
                insertFileInDoc(context,newDocumentId, newDocumentsFileList);
                CHECKIN_CONTROLLER.info("New document id is : " + newDocumentId);

                if (newDocumentId == null || newDocumentId.equalsIgnoreCase("")) {
                    CHECKIN_CONTROLLER.info("As there is no new document id so the system starting the process for new list of files");
                    System.out.println("As there is no new document id so the system starting the process for new list of files");
                    continue;
                }

                documentIdList.add(newDocumentId);

            } catch (Exception exp) {
                System.out.println("Exception : " + exp.getMessage());
                CHECKIN_CONTROLLER.error(exp.getMessage());
            }
        }*/
            List<Map<String, String>> documentIds = checkinBean.getDocumentIds();
            String strTableRowIds[] = new String[documentIds.size()];

            int documentIdIterator = 0;
//        ListIterator<String> listIterator = documentIdList.listIterator();

//        while (listIterator.hasNext()) {
//            strTableRowIds[documentIdIterator++] = listIterator.next();
//        }
            for (int index = 0; index < documentIds.size(); index++) {
                String singleDocId = "";
                try {
                    Map<String, String> tempMap = documentIds.get(index);
                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        try {
                            singleDocId = entry.getKey();
                            CHECKIN_CONTROLLER_LOGGER.debug("Document Id : " + singleDocId);
                            String fileName = entry.getValue();
                            BusinessObject docBO = new BusinessObject(singleDocId);
                            checkExistingFileAndDelete(context, docBO);
                            List<String> fileList = new ArrayList<>();
                            if(!NullOrEmptyChecker.isNullOrEmpty(fileName)) {
                                fileList.add(fileName);
                                insertFileInDoc(context, singleDocId, fileList);
                            }
                            strTableRowIds[documentIdIterator++] = singleDocId;
                        } catch (Exception exp) {
                            CHECKIN_CONTROLLER_LOGGER.debug("Error : " + exp.getMessage());
                            errorResponseMap.put(businessObjectIdNameMap.get(singleDocId), exp.getMessage());

                            /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                            CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                            ContextUtil.abortTransaction(context);
                            throw exp;
                        }

                    }
                } catch (Exception exp) {
                    CHECKIN_CONTROLLER_LOGGER.trace(exp);
                    errorResponseMap.put(businessObjectIdNameMap.get(singleDocId), exp.getMessage());

                    /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                    CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                    ContextUtil.abortTransaction(context);
                    throw exp;
                }
            }
            CHECKIN_CONTROLLER_LOGGER.debug("Number of document ids created : " + strTableRowIds.length);
//            if (strTableRowIds.length < 1) {
//                CHECKIN_CONTROLLER_LOGGER.error("As there no new document created by the system, so it is going to terminate the process");
//                //System.out.println("As there no new document created by the system, so it is going to terminate the process");
//                String errorMessage = errorMessageBuilder.toString();
//                CHECKIN_CONTROLLER_LOGGER.error("Error in creating document : " + errorMessage);
//
//                /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
//                CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
//                ContextUtil.abortTransaction(context);
//
//                //return new ResponseEntity<>("Couldn't create any new document. Errors : " + errorMessageBuilder.toString(), HttpStatus.NOT_ACCEPTABLE);
//                String buildResponse = responseBuilder.addErrorMessage(errorMessage).setStatus(Status.FAILED).buildResponse();
//                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
//            }

            String attachDocument = null;
            try {
                attachDocument = attachDocument(context, baseObjectId, strTableRowIds);
                CHECKIN_CONTROLLER_LOGGER.info("Document attached successfully");

            } catch (Exception exp) {
                CHECKIN_CONTROLLER_LOGGER.trace(exp);
                errorResponseMap.put("failedDocAttachment", exp.getMessage());
                /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
                CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);

                throw exp;

                //return new ResponseEntity<>(exp.getMessage(), HttpStatus.EXPECTATION_FAILED);
            }

            /*---------------------------------------- ||| Commit Transaction File Checkin ||| ----------------------------------------*/
            CHECKIN_CONTROLLER_LOGGER.error("Committing transaction");
            ContextUtil.commitTransaction(context);

            return errorResponseMap.size() > 0
                    ? new ResponseEntity<>(responseBuilder.addErrorMessage(errorResponseMap).setStatus(Status.OK).buildResponse(), HttpStatus.OK)
                    : new ResponseEntity<>(responseBuilder.setStatus(Status.OK).setData(attachDocument).buildResponse(), HttpStatus.OK);
        } catch (Exception exp) {
            /*---------------------------------------- ||| Abort Transaction File Checkin ||| ----------------------------------------*/
            CHECKIN_CONTROLLER_LOGGER.error("Aborting transaction");
            ContextUtil.abortTransaction(context);

            String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            CHECKIN_CONTROLLER_LOGGER.info("######################################## CHECKIN CONTROLLER COMPLETE ########################################");
        }
    }

    private String createDocument(Context context) throws MatrixException {
        try {
            HashMap programMap = new HashMap();
            programMap.put("DocumentName", "Doc");
            String newDocumentId = (String) JPO.invoke(context, "CheckInCheckOutUtil", null, "createDocument", JPO.packArgs(programMap), String.class);
            //String newDocumentId = (String) JPO.invoke(context, "FaruqOnTest", null, "createBusinessObject", JPO.packArgs(programMap), String.class);
            /*String newDocumentId = "";
            try {
                newDocumentId = cloneDocument(context);
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
            }*/
            CHECKIN_CONTROLLER_LOGGER.debug("Document Id : " + newDocumentId);
            return newDocumentId;
        } catch (Exception exp) {
            System.out.println(exp.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private String cloneDocument(Context context) throws Exception {
        String initargs[] = {};

        HashMap objectCloneParametersMap = new HashMap();

        HashMap folderIdForJPOSourceMap = new HashMap();
        folderIdForJPOSourceMap.put("FolderId", "");
        objectCloneParametersMap.put("SpecificationMap", folderIdForJPOSourceMap);
        HashMap<String, String> attributeGlobalReadMap = new HashMap<>();
        attributeGlobalReadMap.put("attribute_GlobalRead", "false");
        objectCloneParametersMap.put("AttributeMap", attributeGlobalReadMap);
        objectCloneParametersMap.put("Name", "this is name");
        //objectCloneParametersMap.put("Name", objectName);
        objectCloneParametersMap.put("Type", "Document");

        HashMap objectCreationParamatersMap = new HashMap();
        objectCreationParamatersMap.put("objectId", "59468.24021.34216.39787");
        objectCreationParamatersMap.put("paramList", objectCloneParametersMap);

        try {
            String clonedObjectId = (String) JPO.invoke(context, "emxWorkspaceVault", initargs,
                    "cloneObject", JPO.packArgs(objectCreationParamatersMap), String.class);
            System.out.println("Cloned Object id is : " + clonedObjectId);

            return clonedObjectId;
        } catch (MatrixException exp) {
            System.out.println("Document Couldn't be cloned");
            System.out.println(exp.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
        } catch (Exception exp) {
            System.out.println("Document Couldn't be cloned");
            System.out.println(exp.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
        }

        return null;
    }

    public String generateObjectName(Context context, String objectType, String templateObjectId) throws MatrixException, Exception {
        String typePackage = PACKAGE_MAP.get(objectType);
        String autoName;

        try {
            if (typePackage == null) {
                String initargs[] = {};
                HashMap jpoParameters = new HashMap();
                jpoParameters.put("objectId", templateObjectId);
                System.out.println("TEmplate id is : " + templateObjectId);
                autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameJpo", JPO.packArgs(jpoParameters), String.class);
                System.out.println(autoName);
                return autoName;
            }

            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("type", objectType);
            jpoParameters.put("typePackage", typePackage);
            autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(jpoParameters), String.class);
            return autoName;
        } catch (MatrixException exp) {
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
            throw exp;
        }

    }

    private String insertFileInDoc(Context context, String docId, List<String> fileList) throws Exception {
        String[] initargs = {};
        HashMap params = new HashMap();
//        String localDirectory = PropertyReader.getProperty("cim.checkin.file.upload.directory");
//        CHECKIN_CONTROLLER_LOGGER.debug("Local DIrecotry : " + localDirectory);
        
        String uploadDirectory = PropertyReader.getProperty("cim.checkin.file.upload.directory");
        CHECKIN_CONTROLLER_LOGGER.debug("File Upload Direcotry : " + uploadDirectory);
        
        String oldDirectory = PropertyReader.getProperty("cim.checkin.file.history.directory");
        CHECKIN_CONTROLLER_LOGGER.debug("File History Direcotry : " + oldDirectory);

        ListIterator<String> fileListIterator = fileList.listIterator();
        List files = new ArrayList<>();
        List<String> filesErrorList = new ArrayList<>();
        while (fileListIterator.hasNext()) {
//            String nextFileNameInFileIterator = localDirectory + fileListIterator.next();
            String nextFileNameInFileIterator = fileListIterator.next();

            try {
                File fileInUploadDirectory = new File(uploadDirectory+nextFileNameInFileIterator);
                CHECKIN_CONTROLLER_LOGGER.debug("File name with upload directory : " + fileInUploadDirectory.getAbsolutePath());
                
                File fileInOldDirectory = new File(oldDirectory+nextFileNameInFileIterator);
                CHECKIN_CONTROLLER_LOGGER.debug("File name with history directory : " + fileInOldDirectory.getAbsolutePath());
                
                
                if(fileInUploadDirectory.exists()) {
                    CHECKIN_CONTROLLER_LOGGER.debug("File found in upload directory!");
                    DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, fileInUploadDirectory.getName(), 100000000, fileInUploadDirectory.getParentFile());
                    try {
                        InputStream input = new FileInputStream(fileInUploadDirectory);
                        OutputStream os = fileItem.getOutputStream();
                        int ret = input.read();
                        while (ret != -1) {
                            os.write(ret);
                            ret = input.read();
                        }
                        os.flush();

                        input.close();
                        System.out.println("diskFileItem.getString() = " + fileItem.getContentType());
                        files.add(fileItem);
                    } catch (IOException exp) {
                        exp.printStackTrace(System.out);
                        CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
                    }
                } else if(fileInOldDirectory.exists()) {
                    CHECKIN_CONTROLLER_LOGGER.debug("File found in history directory!");
                    DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, fileInOldDirectory.getName(), 100000000, fileInOldDirectory.getParentFile());
                    try {
                        InputStream input = new FileInputStream(fileInOldDirectory);
                        OutputStream os = fileItem.getOutputStream();
                        int ret = input.read();
                        while (ret != -1) {
                            os.write(ret);
                            ret = input.read();
                        }
                        os.flush();

                        input.close();
                        System.out.println("diskFileItem.getString() = " + fileItem.getContentType());
                        files.add(fileItem);
                    } catch (IOException exp) {
                        exp.printStackTrace(System.out);
                        CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
                    }
                } else {
                    CHECKIN_CONTROLLER_LOGGER.error(nextFileNameInFileIterator + " File not exists in the system");
                }
                
//                File file = new File(nextFileNameInFileIterator);
//                CHECKIN_CONTROLLER_LOGGER.debug("File name with directory : " + file.getAbsolutePath());
//
//                if (file.exists()) {
//                    DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, file.getName(), 100000000, file.getParentFile());
//                    try {
//                        InputStream input = new FileInputStream(file);
//                        OutputStream os = fileItem.getOutputStream();
//                        int ret = input.read();
//                        while (ret != -1) {
//                            os.write(ret);
//                            ret = input.read();
//                        }
//                        os.flush();
//
//                        input.close();
//                        System.out.println("diskFileItem.getString() = " + fileItem.getContentType());
//                        files.add(fileItem);
//                    } catch (IOException exp) {
//                        exp.printStackTrace(System.out);
//                        CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
//                    }
//                } else {
////                    throw new Exception(file.getAbsolutePath() + " File not exists in the system");
//                    CHECKIN_CONTROLLER_LOGGER.error(file.getAbsolutePath() + " File not exists in the system");
//                }
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
                CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
                filesErrorList.add(exp.getMessage());
                //throw exp;
            }
        }

        CHECKIN_CONTROLLER_LOGGER.debug("Number of files in the list : " + files.size());

//        if (files.size() < 1) {
//            System.out.println("As there are no files in the list so the system couldn't create a document");
//            CHECKIN_CONTROLLER_LOGGER.error("As there are no files in the list so the system couldn't create a document");
//            throw new Exception("Files not found");
//            //return null;
//        }

        CHECKIN_CONTROLLER_LOGGER.debug("document id : " + docId);

        params.put("files", files);
        String sOID = docId;
        params.put("language", "en_US");
        params.put("objectId", sOID);
        params.put("relationship", "Active Version");
        params.put("documentCommand", "");
        params.put("folder", "c:\\temp\\"); // Fixed directory
        params.put("objectAction", "create");
        params.put("timezone", "");

        try {
            String resultReturnedFromEmxDNDBase = JPO.invoke(context, "emxDnDBase", initargs, "checkinFile", JPO.packArgs(params), String.class);

            if (resultReturnedFromEmxDNDBase.contains("ERRORYou cannot check in")) {
                resultReturnedFromEmxDNDBase = resultReturnedFromEmxDNDBase.replace("ERRORYou", "You");
                //throw new MatrixException(resultReturnedFromEmxDNDBase);
            }

            CHECKIN_CONTROLLER_LOGGER.debug("RETURNED RESULT: " + resultReturnedFromEmxDNDBase);
            CHECKIN_CONTROLLER_LOGGER.info("Files checked-in successfully with doc : " + docId);

        } catch (MatrixException exp) {
            System.out.println(exp.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(exp.getMessage());
            throw exp;
        }

        if (filesErrorList.size() > 0) {
            String fileErrors = filesErrorList.toString();
            filesErrorList.clear();
            throw new Exception(fileErrors);
        }

        return docId;
    }

    private String attachDocument(Context context, String baseObjectId, String strTableRowIds[]) throws Exception {
        HashMap programMap = new HashMap();
        programMap.put("objectId", baseObjectId);
        programMap.put("documentIds", strTableRowIds);

        try {
            System.out.println("Calling Document Attachment JPO");
            CHECKIN_CONTROLLER_LOGGER.info("Calling Document Attachment JPO");

            String attached = (String) JPO.invoke(context, "CheckInCheckOutUtil", null, "attachDocument", JPO.packArgs(programMap), String.class);

            System.out.println("Document Attached Successfully");
            CHECKIN_CONTROLLER_LOGGER.info("Document Attached Successfully");

            return "Document attachment succuessfully done";

        } catch (MatrixException ex) {
            System.out.println("Exception : " + ex.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            CHECKIN_CONTROLLER_LOGGER.error(ex.getMessage());
            throw ex;
        } finally {
            //context.close();
        }
    }
    public void checkExistingFileAndDelete(Context context, BusinessObject docBO){
        try {
            //to delete all physical file from the document
            docBO.open(context);
            FileList files = docBO.getFiles(context);
            if (files.size() > 0) {
                String deleteQuery = "delete bus " + docBO.getObjectId() + " format generic file all";
                CHECKIN_CONTROLLER_LOGGER.info(deleteQuery);
                MqlUtil.mqlCommand(context, deleteQuery);
            }
            docBO.close(context);
            SelectList selectBusStmts = new SelectList();
            selectBusStmts.add("Title");
            SelectList selectRelStmts = new SelectList();
            Pattern typePattern = new Pattern("Document") ;
            Pattern relPattern = new Pattern("Latest Version,Active Version");
            String busWhereExpression = "";
            String relWhereExpression = "";
            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relItr = null;
            Short expandLevel = new Short("1");
            
            expandResult = docBO.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false , true,
                    expandLevel, busWhereExpression, relWhereExpression, false);
            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> boList = new ArrayList<>();
            while (relItr.next()) {       
                //String connectionID = relItr.value().getName();
                RelationshipWithSelect relSelect = relItr.obj();
                //BusinessObjectWithSelect  busSelect = relSelect.getTarget();
                BusinessObject childFileBO = relSelect.getTo();
                String fileBOid = childFileBO.getObjectId(context);
                if (!boList.contains(fileBOid)) {
                    //String title = busSelect.getAttributeValues(context, "Title").getValue();
                    boList.add(fileBOid);
                    // to delete file object from the document
                    deleteFileBO(context, fileBOid);
                }                         
            }           
        } catch (Exception ex) {
            CHECKIN_CONTROLLER_LOGGER.error(ex.getMessage());
        }
    }
    public void deleteFileBO(Context context, String fileBOid) throws MatrixException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("delete bus ").append(fileBOid);
        String deleteQuery = commandBuilder.toString();
        CHECKIN_CONTROLLER_LOGGER.info(deleteQuery);
        MqlUtil.mqlCommand(context, deleteQuery);
    }
    
    public void disconnectExistingDocumentFromItem(Context context, String baseItemID){
        try {
            BusinessObject valItemBO = new BusinessObject(baseItemID);
            SelectList selectBusStmts = new SelectList();
            SelectList selectRelStmts = new SelectList();
            Pattern typePattern = new Pattern("PLMDocConnection") ;
            Pattern relPattern = new Pattern("VPLMrel/PLMConnection/V_Owner");
            String busWhereExpression = "";
            String relWhereExpression = "";
            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relItr = null;
            Short expandLevel = new Short("1");
            
            expandResult = valItemBO.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false , true,
                    expandLevel, busWhereExpression, relWhereExpression, false);
            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> boList = new ArrayList<>();
            while (relItr.next()) {       
                String connectionID = relItr.value().getName();
                RelationshipWithSelect relSelect = relItr.obj();
                //BusinessObjectWithSelect  busSelect = relSelect.getTarget();
                BusinessObject childFileBO = relSelect.getTo();
                String docConnectionBOid = childFileBO.getObjectId(context);
                if (!boList.contains(docConnectionBOid)) {
                    String disconnectQuery = "disconnect connection "+connectionID;
                    CHECKIN_CONTROLLER_LOGGER.info(disconnectQuery);
                    MqlUtil.mqlCommand(context, disconnectQuery);
                    boList.add(docConnectionBOid);
                }
            }          
        } catch (Exception ex) {
            CHECKIN_CONTROLLER_LOGGER.error(ex.getMessage());
        }
    }
}
