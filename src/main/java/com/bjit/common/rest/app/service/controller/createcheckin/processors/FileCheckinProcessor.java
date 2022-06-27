/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin.processors;

import com.bjit.common.rest.app.service.controller.createcheckin.models.CheakinModel;
import com.bjit.common.rest.app.service.controller.createcheckin.models.DocumentsInfo;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.commons.fileupload.disk.DiskFileItem;

/**
 *
 * @author BJIT
 */
public class FileCheckinProcessor {

    private static final org.apache.log4j.Logger FILE_CHECKIN_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(FileCheckinProcessor.class);

    public List<String> checkinFile(Context context, CheakinModel checkinModel, BusinessObjectOperations businessObjectOperations) throws Exception {
        try {
            validateCheckinBean(context, checkinModel, businessObjectOperations);

            ObjectCreationProcessor objectCreationProcessor = new ObjectCreationProcessor();
            List<String> documentIds = new ArrayList<>();

            if (NullOrEmptyChecker.isNullOrEmpty(checkinModel.getDocumentInfoList())) {
                throw new NullPointerException("Document is Null or Empty");
            }

            checkinModel.getDocumentInfoList().forEach((DocumentsInfo document) -> {
                try {
                    String clonedDocumentId = createDocuments(context, document, objectCreationProcessor, businessObjectOperations);
                    FILE_CHECKIN_PROCESSOR_LOGGER.info("Cloned document Id : " + clonedDocumentId);
                    documentIds.add(clonedDocumentId);

                    List<File> fileList = new ArrayList<>();

                    if (NullOrEmptyChecker.isNullOrEmpty(document.getFileName())) {
                        throw new NullPointerException("File is Null or Empty");
                    }

                    List<DiskFileItem> listOfFiles = new ArrayList<>();

                    document.setFileName(document.getFileName().stream().distinct().collect(Collectors.toList()));

                    document.getFileName().forEach((String fileName) -> {
                        fileList.add(getDocumentFile(fileName));
                    });

                    fileList.forEach((File file) -> {
                        try {
                            DiskFileItem fileItem = generateFile(file);
                            listOfFiles.add(fileItem);
                        } catch (IOException exp) {
                            FILE_CHECKIN_PROCESSOR_LOGGER.info(exp.getMessage());
                            throw new RuntimeException(exp);
                        }
                    });

                    HashMap<String, Object> fileParameters = fileParameters(clonedDocumentId, listOfFiles);
                    businessObjectOperations.addFileToTheObject(context, fileParameters, Boolean.TRUE);

                } catch (RuntimeException exp) {
                    FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
                    throw new RuntimeException(exp.getMessage());
                } catch (Exception exp) {
                    FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
                    throw new RuntimeException(exp.getMessage());
                }
            });

            return documentIds;
        } catch (NullPointerException exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (IOException exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private void validateCheckinBean(Context context, CheakinModel checkinModel, BusinessObjectOperations businessObjectOperations) throws Exception {
        String errorMessage;
        try {
            if (NullOrEmptyChecker.isNull(checkinModel)) {
                errorMessage = "Checkin Object is null";
                FILE_CHECKIN_PROCESSOR_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            if (NullOrEmptyChecker.isNullOrEmpty(checkinModel.getBaseObjectId())) {
                businessObjectOperations.validateTNR(checkinModel.getTnr(), Boolean.TRUE, Boolean.TRUE);
                String objectId = businessObjectOperations.getObjectId(context, checkinModel.getTnr(), "vplm");
                checkinModel.setBaseObjectId(objectId);
            }

            List<DocumentsInfo> documentInfoList = checkinModel.getDocumentInfoList();
            if (NullOrEmptyChecker.isNullOrEmpty(documentInfoList)) {
                errorMessage = "Document is Null or Empty";
                FILE_CHECKIN_PROCESSOR_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            //DocumentsInfo get = (DocumentsInfo) documentInfoList;
            HashMap<String, String> documentNameMap = new HashMap<>();
            documentInfoList.forEach((DocumentsInfo document) -> {

                if (NullOrEmptyChecker.isNull(document.getDocument())) {
                    String errorMsg = "Document Object is null";
                    FILE_CHECKIN_PROCESSOR_LOGGER.error(errorMsg);
                    throw new NullPointerException(errorMsg);
                }

                if (!document.getDocument().getIsAutoName()) {
                    String documentName = document.getDocument().getTnr().getName();
                    if (documentNameMap.containsKey(documentName)) {
                        throw new RuntimeException("Error in creating of object of Type: '" + document.getDocument().getTnr().getType() + "' Name: '" + document.getDocument().getTnr().getName() + (NullOrEmptyChecker.isNullOrEmpty(document.getDocument().getTnr().getRevision()) ? "" : "' Revision: '" + document.getDocument().getTnr().getRevision()) + "'. Duplicate document name found in the request");
                    }
                    documentNameMap.put(documentName, documentName);
                }

                List<String> fileNameList = document.getFileName();
                if (NullOrEmptyChecker.isNullOrEmpty(fileNameList)) {
                    String errorMsg = "File is Null or Empty";
                    FILE_CHECKIN_PROCESSOR_LOGGER.error(errorMsg);
                    throw new NullPointerException(errorMsg);
                }

                fileNameList.forEach((String fileName) -> {
                    File file = getDocumentFile(fileName);
                    if (!file.exists()) {
                        String errorMsg = "'" + fileName + "' not found in the '" + file.getPath() + "' directory";
                        FILE_CHECKIN_PROCESSOR_LOGGER.error(errorMsg);
                        throw new NullPointerException(errorMsg);
                    }
                });
            });

            documentNameMap.clear();

        } catch (MatrixException exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;

        } catch (Exception exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }

    }

    private String createDocuments(Context context, DocumentsInfo document, ObjectCreationProcessor objectCreationProcessor, BusinessObjectOperations businessObjectOperations) throws Exception {
        try {
            CreateObjectBean createDocument = document.getDocument();
            String clonedDocId = objectCreationProcessor.processCreateObjectOperation(context, createDocument, businessObjectOperations, Boolean.TRUE);
            return clonedDocId;
        } catch (Exception exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }

    }

    private File getDocumentFile(String fileName) {
        String localDirectory = PropertyReader.getProperty("stmt.checkin.file.upload.directory");
        FILE_CHECKIN_PROCESSOR_LOGGER.debug("Local DIrecotry : " + localDirectory);

        File file = new File(localDirectory + fileName);
        FILE_CHECKIN_PROCESSOR_LOGGER.debug("File name with directory : " + file.getAbsolutePath());
        return file;
    }

    private DiskFileItem generateFile(File file) throws IOException {
        DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, file.getName(), 100000000, file.getParentFile());
        try {
            try (InputStream input = new FileInputStream(file)) {
                OutputStream os = fileItem.getOutputStream();
                int ret = input.read();
                while (ret != -1) {
                    os.write(ret);
                    ret = input.read();
                }
                os.flush();

                FILE_CHECKIN_PROCESSOR_LOGGER.debug("diskFileItem.getString() = " + fileItem.getContentType());
                return fileItem;
            }

        } catch (IOException exp) {
            FILE_CHECKIN_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private HashMap<String, Object> fileParameters(String clonedDocumentId, List<DiskFileItem> listOfFiles) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("files", listOfFiles);
        params.put("language", "en_US");
        params.put("objectId", clonedDocumentId);
        params.put("relationship", "Active Version");
        params.put("documentCommand", "");
        params.put("folder", "c:\\temp\\"); // Fixed directory
        params.put("objectAction", "create");
        params.put("timezone", "");
        return params;
    }
}
