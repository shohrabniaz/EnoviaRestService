/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.ServiceRequestSequencer;
import com.bjit.common.rest.app.service.enovia_pdm.service.IMasterShipChange;
import com.bjit.common.rest.app.service.enovia_pdm.service.MasterShipChangeService;
import com.bjit.common.rest.app.service.mail.mastershipChange.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * @author BJIT
 */
public class MailResponseHandler<T, K, L> implements IParameterizedResponseHandler<T, K, L> {

    private static final Logger MAIL_RESPONSE_HANDLER_LOGGER = Logger.getLogger(MailResponseHandler.class);
    @Autowired
    MCMail mail;
    //    @Lazy
    @Autowired
    @Qualifier("preConfiguredMCMail")
    MCMailConfigPreparation mcMailConfigPreparation;
    //    @Lazy
    @Autowired
    MCMailParamerterBuilder mcMailParamerterBuilder;
    @Autowired
    MCModel mcModel;

    @Autowired
    BeanFactory beanFactory;

//    @Override
//    public T doHandle(K mapOfPDMResponses, L request) throws Exception {
//        try {
//            HashMap<String, List<ResponseModel>> pdmResponses = (HashMap<String, List<ResponseModel>>) mapOfPDMResponses;
//
//            MCMailParameters preparedMailParameter = mcMailParamerterBuilder.prepareMailParameter(pdmResponses);
//
//            getMailReceiver((HttpServletRequest) request);
//            MCMailConfig mcConfiguredMail = mcMailConfigPreparation.getMCConfiguredMail("mastership.change", "error", mcModel);
//
////            mail.sendMail(mcConfiguredMail, preparedMailParameter);
//            return (T) "Mail sent successfully";
//
//        } catch (MalformedURLException | MessagingException exp) {
//            MAIL_RESPONSE_HANDLER_LOGGER.error(exp);
//            throw exp;
//        } catch (Exception exp) {
//            MAIL_RESPONSE_HANDLER_LOGGER.error(exp);
//            throw exp;
//        }
//    }

    @Override
    public T doHandle(K mapOfPDMResponses, IMasterShipChange masterShipChangeService) throws Exception {
        try {
            HashMap<String, List<ResponseModel>> pdmResponses = (HashMap<String, List<ResponseModel>>) mapOfPDMResponses;
            List<ResponseModel> itemResponseMap = masterShipChangeService.getparentResponseList();
            HashMap<String,String> mapOfEmailAddressByItem = masterShipChangeService.getmapOfEmailAddressByItem();
            HashMap<String, List<ResponseModel>> pdmResponsesByEmail = new HashMap<>();
            Set<String> emailSet = new HashSet<>();
            mapOfEmailAddressByItem.forEach((item,email)->{
                emailSet.add(email);
            });

            List<ResponseModel> responseByEmail = new ArrayList<>();

            emailSet.forEach(email->{
                itemResponseMap.forEach(responseModel -> {
                    if(responseModel.getMail().equalsIgnoreCase(email)){
                       responseByEmail.add(responseModel);
                    }
                });
                
                pdmResponsesByEmail.put("unsuccessful",responseByEmail);
                try {
                    sendingMailToUser(email, pdmResponsesByEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                responseByEmail.clear();
            });

            return (T) "Mail sent successfully";

        } catch (Exception exp) {
            MAIL_RESPONSE_HANDLER_LOGGER.error(exp);
            throw exp;
        }
    }

    private T sendingMailToUser(String emailReceiver , HashMap<String, List<ResponseModel>> pdmResponses) throws Exception {
        MCMailParameters preparedMailParameter = mcMailParamerterBuilder.prepareMailParameter(pdmResponses);

        getMailReceiver(emailReceiver);
        MCMailConfig mcConfiguredMail = mcMailConfigPreparation.getMCConfiguredMail("mastership.change", "error", mcModel);

        mail.sendMail(mcConfiguredMail, preparedMailParameter);
        String sentMessage = "Mail sent successfully to" + emailReceiver;
        return (T) sentMessage;
    }

    @Override
    public T doHandle(K mapOfPDMResponses, L request) throws Exception {
        try {
            HashMap<String, List<ResponseModel>> pdmResponses = (HashMap<String, List<ResponseModel>>) mapOfPDMResponses;

            MCMailParameters preparedMailParameter = mcMailParamerterBuilder.prepareMailParameter(pdmResponses);

            getMailReceiver((String) request);
            MCMailConfig mcConfiguredMail = mcMailConfigPreparation.getMCConfiguredMail("mastership.change", "error", mcModel);

            mail.sendMail(mcConfiguredMail, preparedMailParameter);
            return (T) "Mail sent successfully";

        } catch (MalformedURLException | MessagingException exp) {
            MAIL_RESPONSE_HANDLER_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            MAIL_RESPONSE_HANDLER_LOGGER.error(exp);
            throw exp;
        }
    }

//    private void getMailReceiver(HttpServletRequest httpRequest) {
//        String header = httpRequest.getHeader("emailReceiver");
//        header = Optional.ofNullable(header).filter(data -> !data.isEmpty()).orElseThrow(() -> new RuntimeException("Mail Receiver not found"));//(PropertyReader.getProperty("mastership.change.mail.to"));
//        mcModel.setReceiverEmail(header);
//    }

    private void getMailReceiver(String errorMailReceiver) {
        Optional.ofNullable(errorMailReceiver).orElseThrow(() -> new RuntimeException("Mail Receiver not found"));
        mcModel.setReceiverEmail(errorMailReceiver);
    }
}
