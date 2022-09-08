/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.controller;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationUserModel;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.MastershipChangeException;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.NoFileFoundException;
import com.bjit.common.rest.app.service.enovia_pdm.handlers.IParameterizedResponseHandler;
import com.bjit.common.rest.app.service.enovia_pdm.handlers.IResponseHandler;
import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.ServiceRequestSequencer;
import com.bjit.common.rest.app.service.enovia_pdm.processors.FileProcessor;
import com.bjit.common.rest.app.service.enovia_pdm.processors.ResponseModelGenerator;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IFileProcessor;
import com.bjit.common.rest.app.service.enovia_pdm.service.IMasterShipChange;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.ewc18x.utils.PropertyReader;
import com.sun.mail.smtp.SMTPAddressFailedException;
import java.util.HashMap;
import java.util.List;
import javax.mail.SendFailedException;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import matrix.db.Context;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author BJIT
 */
@RestController
@RequestMapping(path = "/pdm/export")
public class MastershipChangeController {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    ResponseModelGenerator responseModelGenerator;

    @Autowired
    IFileProcessor fileProcessor;

    @Autowired
    @Qualifier("serviceResponseHandler")
    IResponseHandler serviceResponseHandler;

    @Autowired
    @Qualifier("mailResponseHandler")
    IParameterizedResponseHandler mailResponseHandler;

    @Autowired CommonUtilities commonUtilities;

    private static final Logger MASTERSHIP_CHANGE_CONTROLLER_LOGGER = Logger.getLogger(MastershipChangeController.class);
    String serviceResponse;
    static Boolean processRunningFlag = false;

    @GetMapping("/bom")
    public String exportPDMBOM(HttpServletRequest httpRequest) {
        try {
            try {
                if (!processRunningFlag) {
                    processRunningFlag = true;
                } else {
                    IResponse responseBuilder = beanFactory.getBean(IResponse.class);
                    String serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("Process Is Already Running").buildResponse();
                    return serviceResponse;
                }
                final Context context = (Context) httpRequest.getAttribute("context");

                HashMap<String, List<ResponseModel>> generateResponse = doChange(httpRequest.getHeader("emailReceiver"), context);
                serviceResponse = (String) serviceResponseHandler.handle(generateResponse);

            } catch (SMTPAddressFailedException exp) {

                MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

                IResponse responseBuilder = beanFactory.getBean(IResponse.class);

                serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
            } catch (SendFailedException exp) {
                MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

                IResponse responseBuilder = beanFactory.getBean(IResponse.class);

                serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
            } catch (MastershipChangeException exp) {
                try {
                    MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

                    mailResponseHandler.doHandle(exp.getErrorException(), httpRequest);

                    IResponse responseBuilder = beanFactory.getBean(IResponse.class);
                    serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
                } catch (Exception ex) {
                    MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);
                    IResponse responseBuilder = beanFactory.getBean(IResponse.class);
                    serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
                }
            } catch (Exception exp) {
                MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

                IResponse responseBuilder = beanFactory.getBean(IResponse.class);

                serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
            }
            return serviceResponse;
        } catch (NoFileFoundException exp) {
            MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

            IResponse responseBuilder = beanFactory.getBean(IResponse.class);

            return responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
        } catch (Exception exp) {
            MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

            IResponse responseBuilder = beanFactory.getBean(IResponse.class);

            return responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
        } finally {
            processRunningFlag = false;
        }
    }

    @GetMapping("/mastershipChangeScheduler")
    public String mastershipChangeScheduler() {
        try {
            if (!processRunningFlag) {
                processRunningFlag = true;
            } else {
                IResponse responseBuilder = beanFactory.getBean(IResponse.class);
                String serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage("Process Is Already Running").buildResponse();
                return serviceResponse;
            }

            HashMap<String, List<ResponseModel>> generateResponse = doChange(PropertyReader.getProperty("pdm.default.mastership.email"), commonUtilities.generateContext());
            serviceResponse = (String) serviceResponseHandler.handle(generateResponse);

        } catch (SMTPAddressFailedException exp) {
            MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

            IResponse responseBuilder = beanFactory.getBean(IResponse.class);

            serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
        } catch (SendFailedException exp) {
            MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

            IResponse responseBuilder = beanFactory.getBean(IResponse.class);

            serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
        } catch (Exception exp) {
            MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);

            IResponse responseBuilder = beanFactory.getBean(IResponse.class);

            serviceResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(exp.getMessage()).buildResponse();
        } finally {
            processRunningFlag = false;
        }
        return serviceResponse;
    }

    private HashMap<String, List<ResponseModel>> doChange(String errorMailReceiver, Context context) throws Exception {
        /**
         * Process running operation
         */
        IMasterShipChange masterShipChangeService = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, IMasterShipChange.class, "masterShipChangeService");

        List<String> changedMastershipResponse = masterShipChangeService.change(context);
//        HashMap<String, ServiceRequestSequencer> responseSequencerHashMap = masterShipChangeService.getResponseSequencerHashMap();

//        HashMap<String, List<ResponseModel>> generateResponse = responseModelGenerator.generate(changedMastershipResponse);
        HashMap<String, List<ResponseModel>> generateResponse = responseModelGenerator.generate(changedMastershipResponse, masterShipChangeService);

        generateResponse.forEach((responseType, listOfResponseModel) -> {
            if(responseType.equalsIgnoreCase("unsuccessful") && listOfResponseModel.size()>0) {
                try {
                    mailResponseHandler.doHandle(generateResponse, masterShipChangeService);
                } catch (Exception exp) {
                    MASTERSHIP_CHANGE_CONTROLLER_LOGGER.error(exp);
                }
            }
        });
        
        return generateResponse;
    }

}
