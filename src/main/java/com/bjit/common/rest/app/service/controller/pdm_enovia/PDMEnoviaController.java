package com.bjit.common.rest.app.service.controller.pdm_enovia;

import com.bjit.common.rest.app.service.dsservice.consumers.CSRFTokenConsumer;
import com.bjit.common.rest.app.service.dsservice.consumers.ConsumerContainers;
import com.bjit.common.rest.app.service.dsservice.consumers.IConsumer;
import com.bjit.common.rest.app.service.dsservice.consumers.LoginTicketConsumer;
import com.bjit.common.rest.app.service.dsservice.consumers.SecurityContextConsumer;
import com.bjit.common.rest.app.service.dsservice.models.csrf.CSRFTokenResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;
import com.bjit.common.rest.app.service.dsservice.models.login.LoginTicketModel;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.pdm_enovia.ValItemImportProcessor;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author Mashuk/BJIT
 */
@Controller
@RequestMapping(path = "/pdmEnoviaIntegration")
public class PDMEnoviaController {

    private static final org.apache.log4j.Logger IMPORT_ITEM_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(PDMEnoviaController.class);

    @RequestMapping(value = "/VALItemImport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity create(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean rootObject) {
//        Date controllerStartTime = DateTimeUtils.getTime(new Date());
        Instant itemImportStartTime = Instant.now();
        IMPORT_ITEM_CONTROLLER_LOGGER.debug("---------------------------------------- ||| PDM ENOVIA IMPORT ITEM CONTROLLER BEGIN ||| ----------------------------------------");
        IMPORT_ITEM_CONTROLLER_LOGGER.debug("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        final Context context = (Context) httpRequest.getAttribute("context");
        ResultUtil resultUtil = new ResultUtil();
        AttributeBusinessLogic attributeBusinessLogic = new AttributeBusinessLogic();
        try {
            /*---------------------------------------- ||| Check Environment Source ||| ----------------------------------------*/
            String source = PropertyReader.getProperty("default.input.source.pdm");
            if (NullOrEmptyChecker.isNullOrEmpty(rootObject.getSource())) {
                throw new Exception("Error: Environment Source was missing!");
            }
            if (!rootObject.getSource().equalsIgnoreCase(source)) {
                throw new Exception("Error: Expected Environment Source not found! Found: " + rootObject.getSource());
            }
            IMPORT_ITEM_CONTROLLER_LOGGER.debug(">>>>> Source Found: " + rootObject.getSource() + "\n\n\n");
            try {
                /*---------------------------------------- ||| Start Val Item Import Processor ||| ----------------------------------------*/
                ConsumerContainers consumerContainers = this.getConsumerContainers();
                ValItemImportProcessor valImportProcessor = new ValItemImportProcessor();
                valImportProcessor.startValImportProcess(context, rootObject, resultUtil, attributeBusinessLogic, consumerContainers);

                /*---------------------------------------- ||| Process Result Response ||| ----------------------------------------*/
                Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(resultUtil.successResultMap);
                Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(resultUtil.errorResultMap);

                if (hasSuccessfulList && hasErrorList) {
                    buildResponse = responseBuilder.setData(resultUtil.successResultMap.values()).setStatus(Status.FAILED)
                            .addErrorMessage(new ArrayList<>(resultUtil.errorResultMap.values())).buildResponse();
                } else if (hasSuccessfulList && !hasErrorList) {
                    buildResponse = responseBuilder.setData(resultUtil.successResultMap.values()).setStatus(Status.OK).buildResponse();
                } else if (!hasSuccessfulList && hasErrorList) {
                    buildResponse = responseBuilder.addErrorMessage(new ArrayList<>(resultUtil.errorResultMap.values())).setStatus(Status.FAILED).buildResponse();
                } else {
                    IMPORT_ITEM_CONTROLLER_LOGGER.error("System error occurred!");
                    throw new RuntimeException("System error occurred!");
                }

                return new ResponseEntity<>(buildResponse, HttpStatus.OK);

            } catch (RuntimeException e) {
                buildResponse = responseBuilder.addErrorMessage("Error: " + e.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            buildResponse = responseBuilder.addErrorMessage("Error: " + e.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            resultUtil.moveAllFiles();
            resultUtil.clearResultMaps();

//            Date controllerEndTime = DateTimeUtils.getTime(new Date());
//            IMPORT_ITEM_CONTROLLER_LOGGER.debug("Time elapsed for import service is : " + DateTimeUtils.elapsedTime(controllerStartTime, controllerEndTime, null, null) + "\n");
            Instant itemImportEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(itemImportStartTime, itemImportEndTime);
            IMPORT_ITEM_CONTROLLER_LOGGER.info("VAL Import Process has taken : '" + duration + "' milli-seconds");

            IMPORT_ITEM_CONTROLLER_LOGGER.debug("---------------------------------------- ||| PDM ENOVIA IMPORT ITEM CONTROLLER END ||| ----------------------------------------");
            IMPORT_ITEM_CONTROLLER_LOGGER.debug("###########################################################################################################################\n");
        }
    }

    private ConsumerContainers getConsumerContainers() throws Exception {
        Instant startTime = Instant.now();
        IConsumer<LoginTicketModel> loginTicketConsumer = new LoginTicketConsumer();
        loginTicketConsumer.consume();

        IConsumer<CSRFTokenResponseModel> csrfTokenResponseModelIConsumer = new CSRFTokenConsumer();
        loginTicketConsumer.nextConsumer(csrfTokenResponseModelIConsumer);

        IConsumer<SecurityContextResponseModel> securityContextConsumer = new SecurityContextConsumer();
        csrfTokenResponseModelIConsumer.nextConsumer(securityContextConsumer);
        ConsumerContainers consumerContainers = new ConsumerContainers();
        consumerContainers.setSecurityContextConsumer(securityContextConsumer);
        consumerContainers.setCsrfTokenResponseModelIConsumer(csrfTokenResponseModelIConsumer);
        Instant endTime = Instant.now();
        long duration = DateTimeUtils.getDuration(startTime, endTime);
        IMPORT_ITEM_CONTROLLER_LOGGER.info(" ++++++ ConsumerContainers creation took:" + duration + " miliseconds");
        return consumerContainers;
    }
}
