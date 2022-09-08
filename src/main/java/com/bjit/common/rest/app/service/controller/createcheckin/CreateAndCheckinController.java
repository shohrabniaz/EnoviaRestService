/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin;

import com.bjit.common.rest.app.service.controller.createcheckin.models.CheakinModel;
import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.controller.createcheckin.processors.FileCheckinProcessor;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.create_update_checkin.CreateCheckinBean;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author BJIT
 */
@Controller
@RequestMapping(path = "/CreateAndCheckin")
public class CreateAndCheckinController {

    private static final org.apache.log4j.Logger CREATE_AND_CHECKING_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(CreateAndCheckinController.class);

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createAndCheckin(HttpServletRequest httpRequest, @RequestBody final CreateCheckinBean createAndCheckinBean) {
        CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("---------------------------------------- ||| CREATE AND CHECKIN CONTROLLER BEGIN ||| ----------------------------------------");
        CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        try {
            validateCreateCheckinBean(createAndCheckinBean);

            /*---------------------------------------- ||| Start Transaction Clone Business Object ||| ----------------------------------------*/
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("Starting Transaction");
            ContextUtil.startTransaction(context, true);

            /*---------------------------------------- ||| Process for cloning object ||| ----------------------------------------*/
            ObjectCreationProcessor objectCreationProcessor = new ObjectCreationProcessor();
            CreateObjectBean itemInfo = createAndCheckinBean.getItemInfo();
            
            String clonedObjectId = objectCreationProcessor.processCreateObjectOperation(context, itemInfo, businessObjectOperations, Boolean.TRUE);
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("Cloned ObjectId : " + clonedObjectId);
            
            interfaceAdditionProcess(createAndCheckinBean, context, businessObjectOperations, clonedObjectId, itemInfo);

            /*---------------------------------------- ||| Process for file checkin ||| ----------------------------------------*/
            CheakinModel checkinModel = new CheakinModel();
            checkinModel.setBaseObjectId(clonedObjectId);

            checkinModel.setDocumentInfoList(createAndCheckinBean.getDocInfo());

            TNR clonedTNR = businessObjectOperations.getObjectTNR(context, clonedObjectId);
            checkinModel.setTnr(clonedTNR);

            FileCheckinProcessor fileCheckinProcessor = new FileCheckinProcessor();
            List<String> newDocumentIds = fileCheckinProcessor.checkinFile(context, checkinModel, businessObjectOperations);

            /*---------------------------------------- ||| Process for doc attachment ||| ----------------------------------------*/
            businessObjectOperations.attachDocument(context, clonedObjectId, newDocumentIds.toArray(new String[0]));

            /*---------------------------------------- ||| Documents TNRs ||| ----------------------------------------*/
            List<Object> documentTNRs = new ArrayList<>();
            newDocumentIds.forEach((String documentObjectId) -> {
                try {
                    documentTNRs.add(businessObjectOperations.getObjectTNR(context, documentObjectId));
                } catch (MatrixException exp) {
                    CREATE_AND_CHECKING_CONTROLLER_LOGGER.error(exp.getMessage());
                    throw new RuntimeException(exp);
                }
            });

            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("Committing Transaction");
            ContextUtil.commitTransaction(context);

            TNR objectTNR = businessObjectOperations.getObjectTNR(context, clonedObjectId);
            buildResponse = responseBuilder.setData(objectTNR).addNewProperty("objectId", clonedObjectId).addNewProperty("Documents", documentTNRs).setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (FrameworkException exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error("Aborting Transaction");
            ContextUtil.abortTransaction(context);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (NullPointerException exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error("Aborting Transaction");
            ContextUtil.abortTransaction(context);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error("Aborting Transaction");
            ContextUtil.abortTransaction(context);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("---------------------------------------- ||| CREATE AND CHECKIN CONTROLLER END ||| ----------------------------------------");
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }

    private void interfaceAdditionProcess(final CreateCheckinBean createAndCheckinBean, final Context context, BusinessObjectOperations businessObjectOperations, String clonedObjectId, CreateObjectBean itemInfo) {
        /*---------------------------------------- ||| Process for interface addition ||| ----------------------------------------*/
        List<String> interfaceList = createAndCheckinBean.getInterfaceList();
        
        if(NullOrEmptyChecker.isNullOrEmpty(interfaceList)){
            return;
        }
        
        interfaceList.forEach((String interfaceName) -> {
            try{
                businessObjectOperations.addInterface(context, clonedObjectId, interfaceName, "");
                CREATE_AND_CHECKING_CONTROLLER_LOGGER.info("Interface '" + interfaceName + "' successfully added with '" + itemInfo.getTnr().getName() + "' object of type '" + itemInfo.getTnr().getType() + "'");
            }
            catch(MatrixException exp){
                CREATE_AND_CHECKING_CONTROLLER_LOGGER.error(exp);
                throw new RuntimeException(exp);
            }
        });
    }

    private void validateCreateCheckinBean(CreateCheckinBean createAndCheckinBean) {
        String errorMessage;
        if (NullOrEmptyChecker.isNull(createAndCheckinBean)) {
            errorMessage = "No data found in the bean object";
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNull(createAndCheckinBean.getItemInfo())) {
            errorMessage = "Item data not found";
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    /*private List<DocumentsInfo> getDocumentList(CreateCheckinBean createAndCheckinBean) throws Exception {
        try {
            List<DocumentsInfo> documentsInfoList = new ArrayList<>();
            List<DocumentsInfo> docInfo = createAndCheckinBean.getDocInfo();

            docInfo.forEach((DocumentsInfo docs) -> {
                DocumentsInfo documentInfo = new DocumentsInfo();
                documentInfo.setDocument(docs.getDocument());
                documentInfo.setFileName(docs.getFileName());
                
                documentsInfoList.add(documentInfo);
            });

            return documentsInfoList;
        } catch (Exception exp) {
            CREATE_AND_CHECKING_CONTROLLER_LOGGER.error(exp);
            throw exp;
        }
    }*/
}
