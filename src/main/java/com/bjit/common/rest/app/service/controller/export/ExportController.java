/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export;

import com.bjit.common.rest.app.service.controller.createcheckin.*;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.controller.export.facade.ExportFacade;
import com.bjit.common.rest.app.service.controller.export.model.ExportModel;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
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
@RequestMapping(path = "/transfer")
public class ExportController {

    private static final org.apache.log4j.Logger EXPORT_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(CreateAndCheckinController.class);
    
    @RequestMapping(value = "/transferStructure", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity bomExport(HttpServletRequest httpRequest, @RequestBody final ExportModel exportModel) {
        EXPORT_CONTROLLER_LOGGER.info("---------------------------------------- ||| EXPORT CONTROLLER BEGIN ||| ----------------------------------------");
        EXPORT_CONTROLLER_LOGGER.info("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        try {
            /*---------------------------------------- ||| Start Transaction Clone Business Object ||| ----------------------------------------*/
            EXPORT_CONTROLLER_LOGGER.info(ControllersConstantMessages.STARTING_TRANSACTION);
            ContextUtil.startTransaction(context, true);
            EXPORT_CONTROLLER_LOGGER.info(ControllersConstantMessages.STARTED_TRANSACTION);
            
            //ExportProcessor exportProcessor = new ExportProcessor(exportModel, context);
            //Results expandedData = exportProcessor.getExpandedData();
            
            ExportFacade exportFacade = new ExportFacade();
            List<Map<String, String>> expandedData = exportFacade.processExport(context, exportModel.getTnr(), exportModel.getExportType(), businessObjectOperations);

            /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
            EXPORT_CONTROLLER_LOGGER.info(ControllersConstantMessages.COMMITTING_TRANSACTION);
            ContextUtil.commitTransaction(context);
            EXPORT_CONTROLLER_LOGGER.info(ControllersConstantMessages.COMMITTED_TRANSACTION);

            buildResponse = responseBuilder.setData(expandedData).setStatus(Status.OK).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);

        } catch (FrameworkException exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTING_TRANSACTION);
            ContextUtil.abortTransaction(context);
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTED_TRANSACTION);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (NullPointerException exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTING_TRANSACTION);
            ContextUtil.abortTransaction(context);
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTED_TRANSACTION);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTING_TRANSACTION);
            ContextUtil.abortTransaction(context);
            EXPORT_CONTROLLER_LOGGER.error(ControllersConstantMessages.ABORTED_TRANSACTION);

            buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            EXPORT_CONTROLLER_LOGGER.info("---------------------------------------- ||| EXPORT CONTROLLER END ||| ----------------------------------------");
            EXPORT_CONTROLLER_LOGGER.info("###########################################################################################################################\n");
        }
    }
}
