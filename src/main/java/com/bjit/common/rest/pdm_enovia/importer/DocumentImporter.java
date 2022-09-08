package com.bjit.common.rest.pdm_enovia.importer;

import com.bjit.common.rest.app.service.controller.createobject.CreateAndUpdateObjectController;
import com.bjit.common.rest.app.service.dsservice.consumers.ConsumerContainers;
import com.bjit.common.rest.app.service.dsservice.consumers.DocumentCreatorAndFileUploaderDSService;
import com.bjit.common.rest.app.service.dsservice.consumers.IConsumer;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.DocumentResponseModel;
import com.bjit.common.rest.pdm_enovia.model.CheckinBean;
import com.bjit.common.rest.pdm_enovia.model.DocumentInfoBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.formatter.AttributeFormatter;
import com.bjit.common.rest.pdm_enovia.formatter.DocumentAttributeFormatter;
import com.bjit.common.rest.app.service.model.itemImport.Document;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonServiceUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Mashuk/BJIT
 */
public class DocumentImporter {

    private Integer countCheckedInFailedFiles = 0;
    String checkInResult = null;
    private final JSON json;
    private static final Logger DOCUMENT_IMPORTER_LOGGER = Logger.getLogger(DocumentImporter.class);

    public DocumentImporter() {
        json = new JSON();
    }

    public void documentImport(Context context, String templateObjectId, List<Document> documentTreetList, HashMap<String, String> propertyMap,
            Boolean isAutoName, String folderId, String baseObjectId, String source, ResultUtil resultUtil, AttributeBusinessLogic attributeBusinessLogic, ConsumerContainers consumerContainers) throws IOException, Exception {
        DOCUMENT_IMPORTER_LOGGER.debug("\n\n\n");
        DOCUMENT_IMPORTER_LOGGER.debug("------------- ||| Document Import Process Started ||| -------------");
        DOCUMENT_IMPORTER_LOGGER.info(">>>>> Import " + documentTreetList.size() + " Document/s");
        DOCUMENT_IMPORTER_LOGGER.debug("START IMPROTING : Time: " + CommonUtil.getSystemDate());

        List<Map<String, String>> listOfDocumentIdMap = new ArrayList<>();
        ListIterator<Document> iterator = documentTreetList.listIterator();
        List<DocumentInfoBean> documentInfoBeanList = new ArrayList<>();

        String documentTypeName = PropertyReader.getProperty("import.type.name.Enovia." + source + ".document");
        if (NullOrEmptyChecker.isNullOrEmpty(documentTypeName)) {
            DOCUMENT_IMPORTER_LOGGER.error(">>>>>>>>> Error: Error occured while getting Val Component Type's name from properties");
            throw new Exception("Error occured while getting 'Val Component Item' Type's name from properties");
        }

        while (iterator.hasNext()) {
            Document documentTree = iterator.next();
            CreateObjectBean documentObject = documentTree.getDocumentItem();
            documentObject.setTemplateBusinessObjectId(templateObjectId);
            documentObject.setIsAutoName(isAutoName);
            documentObject.setFolderId(folderId);
            documentObject.setSource(PropertyReader.getProperty("default.input.source.pdm"));
            documentObject.getTnr().setType(documentTypeName);
            documentObject.getTnr().setRevision(!NullOrEmptyChecker.isNullOrEmpty(documentObject.getTnr().getRevision()) ? documentObject.getTnr().getRevision() : "00");
            List<HashMap<String, String>> fileMapList = new ArrayList<>();
            if (!NullOrEmptyChecker.isNullOrEmpty(documentTree.getFiles())) {
                fileMapList = documentTree.getFiles();
            }
            List<String> fileNameList = new ArrayList<>();
            for (HashMap<String, String> fileMap : fileMapList) {
                if (!fileMap.containsKey("fileName")) {
                    String errorMessage = "File name is missing";
                    resultUtil.addErrorResult(documentObject.getTnr().getName(), resultUtil.getItemTNR(documentObject.getTnr().getName()), "Error: " + errorMessage);
                } else {
                    fileNameList.add(fileMap.get("fileName"));
                    resultUtil.fileNameList.add(fileMap.get("fileName"));
                }
            }
            try {
                AttributeFormatter attributeFormatter = new DocumentAttributeFormatter(documentObject, propertyMap);
                CreateObjectBean formattedDocumentBean = attributeFormatter.getFormattedObjectBean(resultUtil, attributeBusinessLogic);
                DocumentInfoBean documentInfoBean = CommonServiceUtil.getDocumentInfoBean(formattedDocumentBean, fileNameList);
                documentInfoBeanList.add(documentInfoBean);
            } catch (IOException e) {
                addErrorDocumentToResult(documentObject.getTnr(), fileNameList, countCheckedInFailedFiles, resultUtil);
                resultUtil.addErrorResult(documentObject.getTnr().getName(), resultUtil.getItemTNR(documentObject.getTnr().getName()), "Error: " + e.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            } catch (Exception ex) {
                addErrorDocumentToResult(documentObject.getTnr(), fileNameList, countCheckedInFailedFiles, resultUtil);
                resultUtil.addErrorResult(documentObject.getTnr().getName(), resultUtil.getItemTNR(documentObject.getTnr().getName()), "Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
            }
        }
        CheckinBean checkInBean = CommonServiceUtil.getCheckinBean(baseObjectId, null, documentInfoBeanList, listOfDocumentIdMap);

        DOCUMENT_IMPORTER_LOGGER.debug("\n\n");

        String importResult = Boolean.parseBoolean(PropertyReader.getProperty("env.3dspace.21x")) ? start21xImportProcess(context, checkInBean, documentTypeName, resultUtil, consumerContainers) : start18xImportProcess(context, checkInBean, documentTypeName, resultUtil);
        DOCUMENT_IMPORTER_LOGGER.debug("\n\n");
        DOCUMENT_IMPORTER_LOGGER.debug(">>>>> DOCUMENT IMPORT RESPONSE: " + importResult);

        DOCUMENT_IMPORTER_LOGGER.debug("END IMPORTING : Time: " + CommonUtil.getSystemDate());
        DOCUMENT_IMPORTER_LOGGER.info("------------- ||| Document Import Process Completed ||| -------------" + "\n\n");
    }

    public String start18xImportProcess(Context context, CheckinBean checkInBean, String documentTypeName, ResultUtil resultUtil) throws IOException, MatrixException {
        String documentImportResult = checkInDocument(context, checkInBean, documentTypeName, resultUtil);
        return documentImportResult;
    }

    public String start21xImportProcess(Context context, CheckinBean checkInBean, String documentTypeName, ResultUtil resultUtil, ConsumerContainers consumerContainers) throws IOException, MatrixException, Exception {
        String documentImportResult = checkInDocumentByDSService(context, checkInBean, documentTypeName, resultUtil, consumerContainers);
        return documentImportResult;
    }

    public String checkInDocument(Context context, CheckinBean checkInBean, String documentTypeName, ResultUtil resultUtil) throws IOException {
        checkInResult = null;
        String baseObjectId = checkInBean.getBaseObjectId();
        DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Base Object Id : " + baseObjectId);
        List<DocumentInfoBean> listOfDocumentInfo = checkInBean.getDocumentInfoList();
        CreateAndUpdateObjectController createController = new CreateAndUpdateObjectController();
        countCheckedInFailedFiles = 0;

        //IntStream.range(0, listOfDocumentInfo.size()).parallel().forEach((int documentInfoIterator) -> {
        for (int documentInfoIterator = 0; documentInfoIterator < listOfDocumentInfo.size(); documentInfoIterator++) {
            DOCUMENT_IMPORTER_LOGGER.debug("Starting transaction");
            try {
                CommonUtilities commonUtilities = new CommonUtilities();
                commonUtilities.doStartTransaction(context);
            } catch (FrameworkException | RuntimeException | InterruptedException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }

            DocumentInfoBean documentInfoBean = listOfDocumentInfo.get(documentInfoIterator);
            List<String> fileNameList = new ArrayList<>();
            TNR documentTNR;
            try {
                documentTNR = (TNR) documentInfoBean.getCreateObjectBean().getTnr().clone();
            } catch (CloneNotSupportedException ex) {
                resultUtil.addErrorResult(documentInfoBean.getCreateObjectBean().getTnr().getName(), resultUtil.getItemTNR(documentInfoBean.getCreateObjectBean().getTnr().getName()), "Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                addErrorDocumentToResult(documentInfoBean.getCreateObjectBean().getTnr(), fileNameList, countCheckedInFailedFiles, resultUtil);
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }
            documentInfoBean.getCreateObjectBean().getTnr().setType(documentTypeName);

            String importedDocumentId = null;
            try {
                importedDocumentId = createUpdateDocumentObject(context, documentInfoBean, createController, baseObjectId, documentTypeName, resultUtil);
                checkExistingFileAndDelete(context, new BusinessObject(importedDocumentId));
            } catch (MatrixException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(ex);
            } catch (IOException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }

            if (!NullOrEmptyChecker.isNull(documentInfoBean.getFiles())) {
                if (!documentInfoBean.getFiles().isEmpty()) {
                    fileNameList = documentInfoBean.getFiles();
                }
            }

            if (!fileNameList.isEmpty()) {
                if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                    try {
                        importedDocumentId = insertFileInDoc(context, importedDocumentId, fileNameList, documentTNR, resultUtil);
                    } catch (IOException ex) {
                        DOCUMENT_IMPORTER_LOGGER.error(ex);
                        throw new RuntimeException(ex);
                    }

                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                String[] documentIds = new String[1];
                documentIds[0] = importedDocumentId;
                importedDocumentId = attachDocument(context, baseObjectId, documentIds, documentTNR, resultUtil);
            }

            try {
                DOCUMENT_IMPORTER_LOGGER.debug("Commiting transaction");
                ContextUtil.commitTransaction(context);
            } catch (FrameworkException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                addSuccessfulDocumentToResult(documentTNR, fileNameList, importedDocumentId, resultUtil);
            } else {
                countCheckedInFailedFiles = addErrorDocumentToResult(documentTNR, fileNameList, countCheckedInFailedFiles, resultUtil);
            }

            if (countCheckedInFailedFiles < 1) {
                checkInResult = "File/s checked-in successfully!";
            } else {
                checkInResult = countCheckedInFailedFiles + " file/s couldn't be checked-in.";
            }
        }
        //});

        return checkInResult;
    }

    public String checkInDocumentByDSService(Context context, CheckinBean checkInBean, String documentTypeName, ResultUtil resultUtil, ConsumerContainers consumerContainers) throws IOException, MatrixException, Exception {
        checkInResult = null;
        String baseObjectId = checkInBean.getBaseObjectId();
        DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Base Object Id : " + baseObjectId);
        List<DocumentInfoBean> listOfDocumentInfo = checkInBean.getDocumentInfoList();
//        CreateAndUpdateObjectController createController = new CreateAndUpdateObjectController();
        countCheckedInFailedFiles = 0;

        CommonSearch commonSearch = new CommonSearch();
        //IntStream.range(0, listOfDocumentInfo.size()).parallel().forEach((int documentInfoIterator) -> {
        for (int documentInfoIterator = 0; documentInfoIterator < listOfDocumentInfo.size(); documentInfoIterator++) {
            DOCUMENT_IMPORTER_LOGGER.debug("Starting transaction");
            try {
                CommonUtilities commonUtilities = new CommonUtilities();
                commonUtilities.doStartTransaction(context);
            } catch (FrameworkException | RuntimeException | InterruptedException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }

            DocumentInfoBean documentInfoBean = listOfDocumentInfo.get(documentInfoIterator);
            List<String> fileNameList = new ArrayList<>();
            TNR documentTNR;
            try {
                documentTNR = (TNR) documentInfoBean.getCreateObjectBean().getTnr().clone();
            } catch (CloneNotSupportedException ex) {
                resultUtil.addErrorResult(documentInfoBean.getCreateObjectBean().getTnr().getName(), resultUtil.getItemTNR(documentInfoBean.getCreateObjectBean().getTnr().getName()), "Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                addErrorDocumentToResult(documentInfoBean.getCreateObjectBean().getTnr(), fileNameList, countCheckedInFailedFiles, resultUtil);
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }
            List<HashMap<String, String>> searchItem = null;
            try {
                documentInfoBean.getCreateObjectBean().getTnr().setType(documentTypeName);
                TNR documentTnr = new TNR();
                documentTnr.setType(documentInfoBean.getCreateObjectBean().getTnr().getType());
                documentTnr.setName(documentInfoBean.getCreateObjectBean().getTnr().getName());
                searchItem = commonSearch.searchItem(context, documentTnr);
            } catch (Exception exp) {
                DOCUMENT_IMPORTER_LOGGER.error(exp);
            }
//            Optional.ofNullable(searchItem).filter(existingDocuments -> !existingDocuments.isEmpty()).ifPresentOrElse((existingDocuments.) -> {}, ()->{});

            fileNameList = documentInfoBean.getFiles();

            DocumentCreatorAndFileUploaderDSService documentCreatorAndFileUploaderDSService = new DocumentCreatorAndFileUploaderDSService();
//            DocumentResponseModel createAndUpload = documentCreatorAndFileUploaderDSService.createAndUpload(documentTNR, fileNameList);
            DocumentResponseModel createAndUpload = null;
            if (searchItem == null || searchItem.isEmpty()) {
//                createAndUpload = documentCreatorAndFileUploaderDSService.createAndUpload(documentTNR, fileNameList);
                createAndUpload = documentCreatorAndFileUploaderDSService.createAndUpload(documentInfoBean, resultUtil, consumerContainers, context);
            } else {
//                createAndUpload = documentCreatorAndFileUploaderDSService.createAndUpload(documentTNR, fileNameList, searchItem.get(0).get("id"));
                createAndUpload = documentCreatorAndFileUploaderDSService.createAndUpload(documentInfoBean, searchItem.get(0).get("id"), resultUtil, consumerContainers, context);
            }

            String importedDocumentId = createAndUpload.getData().get(0).getId();

            if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                String[] documentIds = new String[1];
                documentIds[0] = importedDocumentId;
                context.resetRole(PropertyReader.getProperty("ds.service.document.and.file.checkin.role"));
                importedDocumentId = attachDocument(context, baseObjectId, documentIds, documentTNR, resultUtil);
            }

            updateDocument(context, baseObjectId, importedDocumentId);

            try {
                DOCUMENT_IMPORTER_LOGGER.debug("Commiting transaction");
                ContextUtil.commitTransaction(context);
            } catch (FrameworkException ex) {
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + ex.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                continue;
                //return;
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                if (!resultUtil.errorResultMap.containsKey(documentTNR.getName())) {
                    addSuccessfulDocumentToResult(documentTNR, fileNameList, importedDocumentId, resultUtil);
                    checkInResult = "File/s checked-in successfully!";
                } else {
                    checkInResult = "File/s couldn't be checked-in.";
                }
            } else {
                checkInResult = "File/s couldn't be checked-in.";
            }
        }
        //});

        return checkInResult;
    }

    private void updateDocument(Context context, String valItemId, String docId) {
        try {
            BusinessObject parentValObj = new BusinessObject(valItemId);
            parentValObj.open(context);
            String organization = parentValObj.getOrganizationOwner(context).getName();
            String project = parentValObj.getProjectOwner(context).getName();

            HashMap<String, String> propertyMap = new HashMap<>();
            propertyMap.put("project", project);
            propertyMap.put("organization", organization);

            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
            businessObjectOperations.updateObject(context, docId, propertyMap);
        } catch (MatrixException ex) {
            DOCUMENT_IMPORTER_LOGGER.error(ex.getMessage());
        } catch (InterruptedException ex) {
            DOCUMENT_IMPORTER_LOGGER.error(ex.getMessage());
        } catch (Exception ex) {
            DOCUMENT_IMPORTER_LOGGER.error(ex.getMessage());
        }
    }

    public static void checkExistingFileAndDelete(Context context, BusinessObject documentBusinessObject) {
        try {
            //to delete all physical file from the document
            documentBusinessObject.open(context);
            FileList files = documentBusinessObject.getFiles(context);
            if (files.size() > 0) {
                String deleteQueury = "delete bus " + documentBusinessObject.getObjectId() + " format generic file all";
                DOCUMENT_IMPORTER_LOGGER.info(deleteQueury);
                MqlUtil.mqlCommand(context, deleteQueury);
            }
            documentBusinessObject.close(context);
            SelectList selectBusStmts = new SelectList();
            selectBusStmts.add("Title");
            SelectList selectRelStmts = new SelectList();
            Pattern typePattern = new Pattern("Document");
            Pattern relPattern = new Pattern("Latest Version,Active Version");
            String busWhereExpression = "";
            String relWhereExpression = "";
            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relationshipIterator = null;
            Short expandLevel = new Short("1");

            expandResult = documentBusinessObject.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false, true,
                    expandLevel, busWhereExpression, relWhereExpression, false);
            relationshipIterator = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> fileBusinessObjectIdList = new ArrayList<>();

            while (relationshipIterator.next()) {
                //String connectionID = relItr.value().getName();
                RelationshipWithSelect relSelect = relationshipIterator.obj();
                //BusinessObjectWithSelect  busSelect = relSelect.getTarget();
                BusinessObject childFileBO = relSelect.getTo();
                String fileBusinessObjectId = childFileBO.getObjectId(context);
                if (!fileBusinessObjectIdList.contains(fileBusinessObjectId)) {
                    //String title = busSelect.getAttributeValues(context, "Title").getValue();
                    fileBusinessObjectIdList.add(fileBusinessObjectId);
                    // to delete file object from the document
                    //deleteFileBO(context, fileBusinessObjectId);
                }
            }
            fileBusinessObjectIdList.stream().parallel().forEach((String fileBusinessObjectId) -> {
                try {
                    deleteFileBO(context, fileBusinessObjectId);
                } catch (MatrixException ex) {
                    DOCUMENT_IMPORTER_LOGGER.error(ex);
                    throw new RuntimeException(ex);
                }
            });

        } catch (NumberFormatException | MatrixException ex) {
            DOCUMENT_IMPORTER_LOGGER.error(ex.getMessage());
        } catch (Exception ex) {
            DOCUMENT_IMPORTER_LOGGER.error(ex.getMessage());
        }
    }

    public static synchronized void deleteFileBO(Context context, String fileBOid) throws MatrixException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("delete bus ").append(fileBOid);
        String deleteQuery = commandBuilder.toString();
        DOCUMENT_IMPORTER_LOGGER.info(deleteQuery);
        MqlUtil.mqlCommand(context, deleteQuery);
    }

    public String createUpdateDocumentObject(Context context, DocumentInfoBean documentInfoBean, CreateAndUpdateObjectController createController, String baseObjectID, String documentTypeName, ResultUtil resultUtil) throws IOException {
        DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Staring document create/update");

        BusinessObject parentValObj = null;
        String organization = "";
        String project = "";
        try {
            parentValObj = new BusinessObject(baseObjectID);
            parentValObj.open(context);
            organization = parentValObj.getOrganizationOwner(context).getName();
            project = parentValObj.getProjectOwner(context).getName();
        } catch (MatrixException ex) {
            DOCUMENT_IMPORTER_LOGGER.debug(ex.getMessage());
        }

        CreateObjectBean createObjectBean = documentInfoBean.getCreateObjectBean();
        TNR documentTNR = createObjectBean.getTnr();
        String importedDocumentId = "";
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();
        Instant itemUpdateStartTime = null;
        try {
            importedDocumentId = createController.getUniqueObjectId(context, documentTypeName, documentTNR.getName());

            if (!NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                itemUpdateStartTime = Instant.now();
                long duration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);

                DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Document Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + duration + "' milli-seconds to find out in the DB (Document exists)");
            }

            if (NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                DOCUMENT_IMPORTER_LOGGER.debug("Document doesn't exist with search name: " + documentTNR.getName());
                DOCUMENT_IMPORTER_LOGGER.debug("Creating new Document!");
                createObjectBean = createController.ifSkeletonIdNotExistThenCheckTNRsProperties(createObjectBean, context);
                HashMap objectCloneParametersMap = createController.createObjectCloneParametersMap(context, createObjectBean);

                importedDocumentId = createController.cloneObjectByJPO(context, createObjectBean, objectCloneParametersMap);

                itemUpdateStartTime = Instant.now();
                long duration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);

                DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Document Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + duration + "' milli-seconds to create in the DB (Document Created)");
            }
            DOCUMENT_IMPORTER_LOGGER.info("Imported Document ID: " + importedDocumentId);

            HashMap<String, String> objectBeanAttributes = createObjectBean.getAttributes();
            objectBeanAttributes.put("project", project);
            objectBeanAttributes.put("organization", organization);
            List<Object> updateAttributes = createController.updateAttributesByMQLCommand(context, importedDocumentId, objectBeanAttributes);

            HashMap<String, String> updateAttributesErrorResultData = (HashMap<String, String>) updateAttributes.get(1);

            if (!updateAttributesErrorResultData.isEmpty()) {
                resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + updateAttributesErrorResultData.toString());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + updateAttributesErrorResultData.toString());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                return "";
            }

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);

            DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : Document Item Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + "' has taken : '" + updateDuration + "' milli-seconds to update in the DB");

            return importedDocumentId;

        } catch (Exception e) {
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
            ContextUtil.abortTransaction(context);
            return "";
        }
    }

    private String insertFileInDoc(Context context, String docId, List<String> fileList, TNR documentTNR, ResultUtil resultUtil) throws IOException {
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Calling file/s check-in JPO");

        String[] initargs = {};
        HashMap params = new HashMap();
        String fileUploadDirectory = PropertyReader.getProperty("stmt.checkin.file.upload.directory");
        DOCUMENT_IMPORTER_LOGGER.debug("File upload Direcotry: " + fileUploadDirectory);

        String fileOldDirectory = PropertyReader.getProperty("stmt.checkin.file.history.directory");
        DOCUMENT_IMPORTER_LOGGER.debug("File history Direcotry: " + fileOldDirectory);

        ListIterator<String> fileListIterator = fileList.listIterator();
        List files = new ArrayList<>();

        while (fileListIterator.hasNext()) {
            String fileName = fileListIterator.next();
            String nextFileNameInFileIteratorUploadDirectory = fileUploadDirectory + fileName;
            File fileInUploadDirectory = new File(nextFileNameInFileIteratorUploadDirectory);
            DOCUMENT_IMPORTER_LOGGER.debug("File name with upload directory : " + fileInUploadDirectory.getAbsolutePath());

            String nextFileNameInFileIteratorOldDirectory = fileOldDirectory + fileName;
            File fileInOldDirectory = new File(nextFileNameInFileIteratorOldDirectory);
            DOCUMENT_IMPORTER_LOGGER.debug("File name with history directory : " + fileInOldDirectory.getAbsolutePath());

            try {
                if (fileInUploadDirectory.exists()) {
                    Instant fileUploadStartTime = Instant.now();

                    long fileSize = fileInUploadDirectory.length();

                    DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : File name : '" + fileName + "' and size '" + fileSize + "' bytes");

                    DOCUMENT_IMPORTER_LOGGER.info(">>>> File Found in upload directory!");
                    DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, fileInUploadDirectory.getName(), 100000000, fileInUploadDirectory.getParentFile());
                    try ( InputStream input = new FileInputStream(fileInUploadDirectory)) {
                        OutputStream os = fileItem.getOutputStream();
                        int ret = input.read();
                        while (ret != -1) {
                            os.write(ret);
                            ret = input.read();
                        }
                        os.flush();

                        input.close();
                        DOCUMENT_IMPORTER_LOGGER.debug("diskFileItem.getString() = " + fileItem.getContentType());
                        files.add(fileItem);

                        Instant fileUploadEndTime = Instant.now();
                        long duration = DateTimeUtils.getDuration(fileUploadStartTime, fileUploadEndTime);

                        DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : File : '" + fileName + "'  has taken : '" + duration + "' milli-seconds to upload");

                    } catch (IOException e) {
                        resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
                        DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
                        DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                        ContextUtil.abortTransaction(context);
                        return "";
                    }
                } else if (fileInOldDirectory.exists()) {
                    Instant fileUploadStartTime = Instant.now();
                    long fileSize = fileInOldDirectory.length();

                    DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : File name : '" + fileName + "' and size '" + fileSize + "' bytes");

                    DOCUMENT_IMPORTER_LOGGER.info(">>>> File Found in history directory!");
                    DiskFileItem fileItem = new DiskFileItem("fileData", "null", true, fileInOldDirectory.getName(), 100000000, fileInOldDirectory.getParentFile());
                    try ( InputStream input = new FileInputStream(fileInOldDirectory)) {
                        OutputStream os = fileItem.getOutputStream();
                        int ret = input.read();
                        while (ret != -1) {
                            os.write(ret);
                            ret = input.read();
                        }
                        os.flush();

                        input.close();
                        DOCUMENT_IMPORTER_LOGGER.debug("diskFileItem.getString() = " + fileItem.getContentType());
                        files.add(fileItem);

                        Instant fileUploadEndTime = Instant.now();
                        long duration = DateTimeUtils.getDuration(fileUploadStartTime, fileUploadEndTime);

                        DOCUMENT_IMPORTER_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : File : '" + fileName + "'  has taken : '" + duration + "' milli-seconds to upload");

                    } catch (IOException e) {
                        resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
                        DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
                        DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                        ContextUtil.abortTransaction(context);
                        return "";
                    }
                } else {
                    resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + fileName + " File not exists in the system");
                    DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + fileName + " File not exists in the system");
                    return "";
                }
            } catch (Exception e) {
                resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
                DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
                ContextUtil.abortTransaction(context);
                return "";
            }
        }

        DOCUMENT_IMPORTER_LOGGER.info("Number of files in the document : " + files.size());

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
            }

            DOCUMENT_IMPORTER_LOGGER.info(">>>>> JPO RETURNED RESULT: " + resultReturnedFromEmxDNDBase);
            DOCUMENT_IMPORTER_LOGGER.info(">>>>> Files checked-in successfully with doc : " + docId + "\n\n");
            return docId;

        } catch (MatrixException e) {
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
            ContextUtil.abortTransaction(context);
            return "";
        }
    }

    private String attachDocument(Context context, String baseObjectId, String documentIds[], TNR documentTNR, ResultUtil resultUtil) {
        HashMap programMap = new HashMap();
        programMap.put("objectId", baseObjectId);
        programMap.put("documentIds", documentIds);

        try {
            DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Calling Document Attachment JPO");

            String attached = (String) JPO.invoke(context, "CheckInCheckOutUtil", null, "attachDocument", JPO.packArgs(programMap), String.class);

            DOCUMENT_IMPORTER_LOGGER.info(">>>>> JPO RETURNED RESULT: " + attached);
            DOCUMENT_IMPORTER_LOGGER.debug(">>>>> Document Attached Successfully");

            return documentIds[0];

        } catch (MatrixException e) {
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
            ContextUtil.abortTransaction(context);
            return "";
        } catch (Exception e) {
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
            ContextUtil.abortTransaction(context);
            return "";
        }
    }

    public static void addSuccessfulDocumentToResult(TNR documentTNR, List<String> fileNameList, String importedDocumentId, ResultUtil resultUtil) {
        try {
            resultUtil.addSuccessResult(resultUtil.getItemTNR(documentTNR.getName()), importedDocumentId);
            DOCUMENT_IMPORTER_LOGGER.info(">>>>> Imported Document Id : " + importedDocumentId);
        } catch (Exception e) {
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
        }
    }

    public static Integer addErrorDocumentToResult(TNR documentTNR, List<String> fileNameList, Integer countCheckedInFailedFiles, ResultUtil resultUtil) {
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(fileNameList)) {
                countCheckedInFailedFiles += fileNameList.size();
            }
        } catch (Exception e) {
            resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error(">>>>> Error: " + e.getMessage());
            DOCUMENT_IMPORTER_LOGGER.error("Aborting transaction");
        }
        return countCheckedInFailedFiles;
    }
}
