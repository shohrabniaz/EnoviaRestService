package com.bjit.common.rest.app.service.dsservice.consumers;

import java.util.ArrayList;
import java.util.List;
import com.bjit.common.rest.app.service.dsservice.models.login.LoginTicketModel;
import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest.DocumentModel;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest.DocumentRequestModel;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest.DocumentRequestRelatedData;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentrequest.Brochure;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.DocumentDataModel;
import com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse.DocumentResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.fcsjob.FCSJobTicketsModel;
import com.bjit.common.rest.app.service.dsservice.models.fcsjob.DataElementsModel;
import com.bjit.common.rest.app.service.exception.NotFoundException;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.model.DocumentInfoBean;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import matrix.db.Context;
import org.apache.commons.beanutils.BeanUtils;


public class DocumentCreatorAndFileUploaderDSService {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DocumentCreatorAndFileUploaderDSService.class);
    private DocumentInfoBean documentInfoBean = null;

    public DocumentResponseModel createAndUpload(DocumentInfoBean documentInfoBean, ResultUtil resultUtil, ConsumerContainers consumerContainers, Context context) throws Exception {
        IConsumer<SecurityContextResponseModel> securityContextConsumer = consumerContainers.getSecurityContextConsumer();
        this.documentInfoBean = documentInfoBean;
        validateNumberOfFiles();
        LOGGER.info(" ++++++ START: createAndUpload");
        Instant startTime = Instant.now();
        DocumentResponseModel documentResponseModel = chainOfResponsibility(resultUtil, securityContextConsumer, context);
        Instant endTime = Instant.now();
        long duration = DateTimeUtils.getDuration(startTime, endTime);
        LOGGER.info(" ++++++ END: createAndUpload. createAndUpload took:" + duration + " miliseconds");
        return documentResponseModel;
    }

    public DocumentResponseModel createAndUpload(DocumentInfoBean documentInfoBean, String documentId, ResultUtil resultUtil,
            ConsumerContainers consumerContainers, Context context) throws Exception {
        this.documentInfoBean = documentInfoBean;
        validateNumberOfFiles();
        LOGGER.info(" ++++++ START: createAndUpload2");
        Instant startTime = Instant.now();
        DocumentResponseModel documentResponseModel = chainOfResponsibility(documentId, resultUtil, consumerContainers, context);
        Instant endTime = Instant.now();
        long duration = DateTimeUtils.getDuration(startTime, endTime);
        LOGGER.info(" ++++++ END: createAndUpload2. createAndUpload took:" + duration + " miliseconds");
        return documentResponseModel;
    }

    public void validateNumberOfFiles(){
        if(this.documentInfoBean.getFiles().isEmpty()){
            throw new NotFoundException(PropertyReader.getProperty("document.item.import.file.not.found.exception"));
        }
    }

    private DocumentResponseModel chainOfResponsibility(ResultUtil resultUtil, IConsumer<SecurityContextResponseModel> securityContextConsumer, Context context) throws Exception {
        try {
            /*
            IConsumer<LoginTicketModel> loginTicketConsumer = new LoginTicketConsumer();
            loginTicketConsumer.consume();

            IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer = new CSRFTokenConsumer();
            loginTicketConsumer.nextConsumer(csrfTokenResponseModelIConsumer);

            IConsumer<SecurityContextResponseModel> securityContextConsumer = new SecurityContextConsumer();
            csrfTokenResponseModelIConsumer.nextConsumer(securityContextConsumer);
             */
            HashMap<String, String> filenameAndCheckinTicketMap = new HashMap<>();

            IConsumer<String> fileCheckinConsumer = null;
            List<String> exceptionListOfFileConsumer = new ArrayList<>();
            List<String> fileNameList = this.documentInfoBean.getFiles();
            for (String filename : fileNameList) {
                try {
                    IConsumer<FCSJobTicketsModel> checkinJobTicketConsumer = new CheckinJobTicketConsumer();
                    FCSJobTicketsModel jobticketModel = securityContextConsumer.nextConsumer(checkinJobTicketConsumer);

                    fileCheckinConsumer = new FileCheckinConsumer(getFileLocation(filename), jobticketModel.getData().get(0).getDataelements().getTicket());
                    String checkinTicket = checkinJobTicketConsumer.nextConsumer(fileCheckinConsumer);
                    filenameAndCheckinTicketMap.put(filename, checkinTicket);
                } catch (Exception ex) {
                    /*
                     * (known) exception could be occurred for file not exist in physical location
                     */
                    LOGGER.error(ex);
                    String errorMsg = CommonUtil.formatFileNotFoundErrorMessage(ex, filename);
                    exceptionListOfFileConsumer.add(errorMsg);
                }
            }

            IConsumer<DocumentResponseModel> documentCreationConsumer = new DocumentCreationConsumer();
            DocumentRequestModel preparedDocumentModel = prepareDocumentModel(filenameAndCheckinTicketMap);
            documentCreationConsumer.setBusinessObject(preparedDocumentModel);

            DocumentResponseModel documentResponseModel = fileCheckinConsumer.nextConsumer(documentCreationConsumer);
            String importedDocumentId = documentResponseModel.getData().get(0).getId();
            /**
             * in this case api response will go to "messages" section
             * exceptionListOfFileConsumer contains list of 'NOT OK' file- adding to api response
             * filenameAndCheckinTicketMap contains rest of the OK file (uploaded in fcs server)
             */
            if (!NullOrEmptyChecker.isNullOrEmpty(exceptionListOfFileConsumer)) {
                /**
                 * intercept DocumentResponseModel, overall objective is not
                 * acheived, so inverse response model so that it can not go to
                 * "data" section in api response
                 */
                StringBuilder strBuilder = new StringBuilder();
                for (String errMsg: exceptionListOfFileConsumer) {
                    strBuilder.append(errMsg).append(" ");
                }
                TNR documentTNR = this.documentInfoBean.getCreateObjectBean().getTnr();
                if (NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                     resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + strBuilder.toString());
                } else {
                     resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), importedDocumentId, "Error:" + strBuilder.toString());
                }
            }
            /*
             * if we update object state in this point then mql and next DocumentUpdateConsumer both don't work.
             * it just hanged and specify read time out error. Thats why we will extract-remove state from bean
             * and perform update request after update consumer.
             */
            String state = this.removeState();

            //doc creation done, now updating doc
            IConsumer<DocumentResponseModel> documentUpdateConsumer = new DocumentUpdateConsumer(importedDocumentId);
            DocumentRequestModel preparedDocumentModel2 =this.prepareDocumentModel(importedDocumentId);
            documentUpdateConsumer.setBusinessObject(preparedDocumentModel2);
            DocumentResponseModel documentResponseModel2 = fileCheckinConsumer.nextConsumer(documentUpdateConsumer);
            String dcumentId = documentResponseModel2.getData().get(0).getId();

            //updating maturity state of document
            //updating object state separately cause in ds service 'In work' to 'Obsolete' updating not working
            this.updateObjectState(context, importedDocumentId, state);

            return documentResponseModel;
        } catch (Exception exp) {
            LOGGER.error(exp);
            throw exp;
        }
    }

    private DocumentResponseModel chainOfResponsibility(String documentId, ResultUtil resultUtil, ConsumerContainers consumerContainers,
            Context context) throws Exception {
        try {
            IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer = consumerContainers.getCsrfTokenResponseModelIConsumer();
            IConsumer<SecurityContextResponseModel> securityContextConsumer = consumerContainers.getSecurityContextConsumer();
            
            IConsumer<DocumentResponseModel> allFileInADocument = new AllFilesInADocumentConsumer(documentId);
            DocumentResponseModel documentWithFileList = csrfTokenResponseModelIConsumer.nextConsumer(allFileInADocument);
            List<String> fileNameList = this.documentInfoBean.getFiles();
//            List<String> fileIdsInTheDoc = getFileIdsInTheDoc(documentWithFileList, fileNameList);
            List<String> fileIdsInTheDoc = getFileIdsInTheDoc(documentWithFileList);

            fileIdsInTheDoc.forEach(fileId -> {
                try {
                    IConsumer<DocumentResponseModel> deleteAFileFromADocument = new DeleteFileInADocumentConsumer(documentId, fileId);
                    DocumentResponseModel documentDeleteResponse = csrfTokenResponseModelIConsumer.nextConsumer(deleteAFileFromADocument);
                } catch (Exception ex) {
                    LOGGER.error(ex);
                    throw new RuntimeException(ex);
                }

            });
            HashMap<String, String> filenameAndCheckinTicketMap = new HashMap<>();

            IConsumer<String> fileCheckinConsumer = null;
            List<String> exceptionListOfFileConsumer = new ArrayList<>();
            for (String filename : fileNameList) {
                try {
                    IConsumer<FCSJobTicketsModel> checkinJobTicketConsumer = new CheckinJobTicketConsumer();
                    FCSJobTicketsModel jobticketModel = securityContextConsumer.nextConsumer(checkinJobTicketConsumer);

                    fileCheckinConsumer = new FileCheckinConsumer(getFileLocation(filename), jobticketModel.getData().get(0).getDataelements().getTicket());
                    String checkinTicket = checkinJobTicketConsumer.nextConsumer(fileCheckinConsumer);
                    filenameAndCheckinTicketMap.put(filename, checkinTicket);
                } catch (Exception ex) {
                    /*
                     * (known) exception could be occurred for file not exist in physical location
                     */
                    LOGGER.error(ex);
                    String errorMsg = CommonUtil.formatFileNotFoundErrorMessage(ex, filename);
                    exceptionListOfFileConsumer.add(errorMsg);
                }
            }
            String state = this.removeState();
            IConsumer<DocumentResponseModel> documentUpdateConsumer = new DocumentUpdateConsumer(documentId);
            DocumentRequestModel preparedDocumentModel = prepareDocumentModel(filenameAndCheckinTicketMap, documentId);
            documentUpdateConsumer.setBusinessObject(preparedDocumentModel);

            DocumentResponseModel documentResponseModel = fileCheckinConsumer.nextConsumer(documentUpdateConsumer);
            String importedDocumentId = documentResponseModel.getData().get(0).getId();

            //updating maturity state of document
            //updating object state separately cause in ds service 'In work' to 'Obsolete' updating not working
            this.updateObjectState(context, documentId, state);
            /**
             * in this case api response will go to "messages" section
             * exceptionListOfFileConsumer contains list of 'NOT OK' file-
             * adding to api response filenameAndCheckinTicketMap contains rest
             * of the OK file (uploaded in fcs server)
             */
            if (!NullOrEmptyChecker.isNullOrEmpty(exceptionListOfFileConsumer)) {
                /**
                 * intercept DocumentResponseModel, overall objective is not
                 * acheived, so inverse response model so that it can not go to
                 * "data" section in api response
                 */
                StringBuilder strBuilder = new StringBuilder();
                for (String errMsg : exceptionListOfFileConsumer) {
                    strBuilder.append(errMsg).append(" ");
                }
                TNR documentTNR = this.documentInfoBean.getCreateObjectBean().getTnr();
                if (NullOrEmptyChecker.isNullOrEmpty(importedDocumentId)) {
                    resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), "Error: " + strBuilder.toString());
                } else {
                    resultUtil.addErrorResult(documentTNR.getName(), resultUtil.getItemTNR(documentTNR.getName()), importedDocumentId, "Error:" + strBuilder.toString());
                }
            }
            return documentResponseModel;
        } catch (Exception exp) {
            LOGGER.error(exp);
            throw exp;
        }
    }

    List<String> getFileIdsInTheDoc(DocumentResponseModel fileResponse, List<String> requestFileList) {
        List<String> toBeDeletedFileIds = fileResponse.getData()
                .stream()
                .filter((DocumentDataModel fileIterator) -> requestFileList.contains(fileIterator.getDataelements().getTitle()) && checkFileisExists(fileIterator.getDataelements().getTitle()))
                .map(fileIterator -> fileIterator.getId()).collect(Collectors.toList());
        return toBeDeletedFileIds;
    }

    List<String> getFileIdsInTheDoc(DocumentResponseModel documentFileResponse) {
        List<String> toBeDeletedFileIds = documentFileResponse.getData().stream().map(data -> data.getId()).collect(Collectors.toList());
        return toBeDeletedFileIds;
    }

    Boolean checkFileisExists(String filename) {
        try {
            File file = new File(getFileLocation(filename));
            boolean fileExists = file.exists() && !file.isDirectory();
            return fileExists;
        } catch (Exception exp) {
            LOGGER.error(exp);
            return false;
        }
    }

    private List<String> getFileLocation() {
        String fileUploadDirectory = PropertyReader.getProperty("stmt.checkin.file.upload.directory");
        List<String> fileNameList = this.documentInfoBean.getFiles();
        List<String> absoluteDirectoryList = fileNameList.stream().filter(filelist -> !filelist.isEmpty()).map(filename -> (fileUploadDirectory + filename)).collect(Collectors.toList());
        System.out.println("File List : " + absoluteDirectoryList);
        return absoluteDirectoryList;
    }

    /**
     * It only returns the file location, but actually does not know whether
     * file is already exist or not
     *
     * @param filename
     * @return
     *
     * @author Touhidul Islam
     */
    private String getFileLocation(String filename) {
        String fileUploadDirectory = PropertyReader.getProperty("stmt.checkin.file.upload.directory");
        String absoluteDirectory = fileUploadDirectory + filename;
        System.out.println("File List : " + absoluteDirectory);
        return absoluteDirectory;
    }

    private DocumentRequestModel prepareDocumentModel(HashMap<String, String> filenameAndCheckinTicketMap) {
        /**
         * File related part
         */
        List<Brochure> listOfDataElements = new ArrayList<>();
        filenameAndCheckinTicketMap.forEach((String filename, String checkinTicket) -> {
            DataElementsModel dataElementsModel = new DataElementsModel();
            dataElementsModel.setTitle(filename);
            dataElementsModel.setReceipt(checkinTicket.trim());

            Brochure docFile = new Brochure();
            docFile.setDataelements(dataElementsModel);

            listOfDataElements.add(docFile);
        });

        /**
         * Doc related part
         */
        DocumentRequestRelatedData documentRequestRelatedData = new DocumentRequestRelatedData();
        documentRequestRelatedData.setFiles(listOfDataElements);

        TNR documentTNR = this.documentInfoBean.getCreateObjectBean().getTnr();
        HashMap<String, String> attributes = documentInfoBean.getCreateObjectBean().getAttributes();
        DataElementsModel elementsModel = new DataElementsModel();
        try {
            BeanUtils.populate(elementsModel, attributes);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.error("Automatic property bindings fails. Fallback Bind now.");
            elementsModel.setDescription(attributes.get("description"));
        }
        elementsModel.setName(documentTNR.getName());
        elementsModel.setTitle(attributes.get("Title"));
        elementsModel.setPolicty("Document Release");

        DocumentModel documentModel = new DocumentModel();
        documentModel.setType("Document");
        documentModel.setTempid(documentTNR.getName());
        documentModel.setDataelements(elementsModel);
        documentModel.setRelateddata(documentRequestRelatedData);

        List<DocumentModel> documentModels = new ArrayList<>();
        documentModels.add(documentModel);

        DocumentRequestModel documentRequestModel = new DocumentRequestModel();
        documentRequestModel.setData(documentModels);
        return documentRequestModel;
    }

    private DocumentRequestModel prepareDocumentModel(HashMap<String, String> filenameAndCheckinTicketMap, String documentId) {
        /**
         * File related part
         */
        List<Brochure> listOfDataElements = new ArrayList<>();
        filenameAndCheckinTicketMap.forEach((String filename, String checkinTicket) -> {
            DataElementsModel dataElementsModel = new DataElementsModel();
            dataElementsModel.setTitle(filename);
            dataElementsModel.setReceipt(checkinTicket.trim());

            Brochure docFile = new Brochure();
            docFile.setDataelements(dataElementsModel);
            docFile.setUpdateAction("CREATE");

            listOfDataElements.add(docFile);
        });

        /**
         * Doc related part
         */
        DocumentRequestRelatedData documentRequestRelatedData = new DocumentRequestRelatedData();
        documentRequestRelatedData.setFiles(listOfDataElements);

        TNR documentTNR = this.documentInfoBean.getCreateObjectBean().getTnr();
        HashMap<String, String> attributes = this.documentInfoBean.getCreateObjectBean().getAttributes();
        DataElementsModel elementsModel = new DataElementsModel();
        try {
            BeanUtils.populate(elementsModel, attributes);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.error("Automatic property bindings fails. Fallback Bind now.");
            elementsModel.setDescription(attributes.get("description"));
        }
        elementsModel.setName(documentTNR.getName());
        elementsModel.setTitle(attributes.get("Title"));

        DocumentModel documentModel = new DocumentModel();
        documentModel.setRelateddata(documentRequestRelatedData);
        documentModel.setId(documentId);
        documentModel.setDataelements(elementsModel);

        List<DocumentModel> documentModels = new ArrayList<>();
        documentModels.add(documentModel);

        DocumentRequestModel documentRequestModel = new DocumentRequestModel();
        documentRequestModel.setData(documentModels);
        return documentRequestModel;
    }
    
    private DocumentRequestModel prepareDocumentModel(String documentId) {
        /**
         * Doc related part
         */
        TNR documentTNR = this.documentInfoBean.getCreateObjectBean().getTnr();
        HashMap<String, String> attributes = this.documentInfoBean.getCreateObjectBean().getAttributes();
        DataElementsModel elementsModel = new DataElementsModel();
        try {
            BeanUtils.populate(elementsModel, attributes);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.error("Automatic property bindings fails. Fallback Bind now.");
            elementsModel.setDescription(attributes.get("description"));
        }

        elementsModel.setName(documentTNR.getName());
        elementsModel.setTitle(attributes.get("Title"));

        DocumentModel documentModel = new DocumentModel();
        documentModel.setId(documentId);
        documentModel.setDataelements(elementsModel);

        List<DocumentModel> documentModels = new ArrayList<>();
        documentModels.add(documentModel);

        DocumentRequestModel documentRequestModel = new DocumentRequestModel();
        documentRequestModel.setData(documentModels);
        return documentRequestModel;
    }

    private String removeState() {
        HashMap<String, String> attributes = this.documentInfoBean.getCreateObjectBean().getAttributes();
        String state = attributes.get("current");
        attributes.remove("current");
        return state;
    }

    /**
     * updating object state separately cause in ds service 'In work' to
     * 'Obsolete' updating not working
     *
     * @param context
     * @param importedDocumentId
     * @param state
     */
    private void updateObjectState(Context context, String importedDocumentId, String state) {
        LOGGER.info("START Updating state only" + importedDocumentId + "->" + state);
        if (state == null || state.equals("")) {
            return;
        }
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        try {
            businessObjectOperations.updateObject(context, importedDocumentId, "current", state);
        } catch (Exception ex) {
            LOGGER.error("Error occured to update object state " + ex.getMessage());
        }
        LOGGER.info("State Updating done");
    }
}
