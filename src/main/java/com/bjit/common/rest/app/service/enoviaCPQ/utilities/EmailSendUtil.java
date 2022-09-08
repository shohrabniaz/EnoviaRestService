/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.enoviaCPQ.utilities;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.enoviaCPQ.model.Item;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.service.impl.LN.ResponseResult;
import com.bjit.mapper.mapproject.util.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author BJIT
 */
public class EmailSendUtil {

    private static final org.apache.log4j.Logger CPQ_SERVICE_CONTROLLER = org.apache.log4j.Logger.getLogger(EmailSendUtil.class);

    public void emailSendingProcess(HashMap<Item, String> transferItemResMap) throws MatrixException {

        // Map<String, Map<ResponseResult>> recipients = new HashMap<>();
        Map<ResponseResult, String> recipients = new HashMap<>();
        EmailSender emailSender = new EmailSender();

        Context context = null;
        try {
            CreateContext createContext = new CreateContext();

            context = createContext.getAdminContext();
            if (!context.isConnected()) {
                throw new Exception(Constants.CONTEXT_EXCEPTION);
            }
        } catch (Exception exp) {
            CPQ_SERVICE_CONTROLLER.error(exp);
        }
        // Map<ResponseResult, String> results = new HashMap();
        for (Map.Entry<Item, String> entry : transferItemResMap.entrySet()) {
            Item item = entry.getKey();
            String cpqTransfer = entry.getValue();

            String objectId = item.getId();
            String itemName = item.getName();
            String rev = item.getRevision();

            BusinessObject businessObject = null;
            try {
                businessObject = new BusinessObject(objectId);
                businessObject.open(context);

            } catch (MatrixException ex) {
                throw ex;
            }

            String processedItemsForEmail = "";

            if (item.getItemTransfer().equalsIgnoreCase("success")) {
                List<String> mailListForAnItem = emailSender.initializeResultSender(context, businessObject, item);
                ResponseResult responseResult = new ResponseResult();
                /////////////////////////////////////////////////
                JSONParser parser = new JSONParser();
                String process = "";
                try {
                    JSONObject json = (JSONObject) parser.parse(cpqTransfer);
                    process = json.get("ProcessedItemCodes").toString();

                } catch (ParseException ex) {
                    Logger.getLogger(EmailSendUtil.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(EmailSendUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (process.isEmpty()) {
                    processedItemsForEmail = "No Processed Item Codes";
                    responseResult.setResultText(processedItemsForEmail);
                    responseResult.setSuccessful(false);

                } else {
                    processedItemsForEmail = process;
                    responseResult.setSuccessful(true);
                    responseResult.setResultText(processedItemsForEmail);
                }

                responseResult.setItem(itemName);
                responseResult.setId(objectId);
                responseResult.setRevision(rev);

                for (String mailTo : mailListForAnItem) {

//                    if (responseResult.isSuccessful()) {
//
//                        results.put(responseResult, "success");
//                    }
//                    if (!responseResult.isSuccessful()) {
//
//                        results.put(responseResult, "error");
//                    }
                    recipients.put(responseResult, mailTo);
                }
            } else {
                List<String> mailListForAnItem = emailSender.initializeResultSender(context, businessObject, item);
                ResponseResult responseResult = new ResponseResult();
                if (item.getMessage() == null) {
                    processedItemsForEmail = "An Error Occured.";
                } else {
                    processedItemsForEmail = item.getMessage();
                }

                responseResult.setResultText(processedItemsForEmail);
                responseResult.setSuccessful(false);
                responseResult.setItem(itemName);
                responseResult.setId(objectId);
                responseResult.setRevision(rev);

                for (String mailTo : mailListForAnItem) {
                    if (mailTo == null) {
                        mailTo = PropertyReader.getProperty("cpq.default.recipient");
                    }

//                    if (!responseResult.isSuccessful()) {
//
//                        results.put(responseResult, "error");
//                    }
                    recipients.put(responseResult, mailTo);
                }
            }

            businessObject.close(context);
        }
        Set<String> mail = new HashSet<>();
        recipients.forEach((key, value) -> {
            String emailSubject = PropertyReader.getProperty("cpq.email.subject");
            String tableHeader = PropertyReader.getProperty("cpq.email.tableHeader.success");

//            responseResult.forEach((reskey, resvalue) -> {
//
//                restTemp.add(reskey);
//
//            });
            if (!mail.contains(value)) {
                List<ResponseResult> restTemp = new ArrayList<>();
                recipients.forEach((secondkey, secondvalue) -> {
                    if (secondvalue.equals(value)) {
                        restTemp.add(secondkey);
                    }
                });
                emailSender.send(value, restTemp, emailSubject, tableHeader);
                restTemp.clear();
                mail.add(value);
            }
            ;
        });

//        for (Map.Entry<String, Map<ResponseResult, String>> mapEntry : recipients.entrySet()) {
//            String emailSubject = PropertyReader.getProperty("cpq.email.subject");
//            String tableHeader = "";
//            String mailTo = mapEntry.getKey();
//
//            Map<ResponseResult, String> resResult = mapEntry.getValue();
//            List<ResponseResult> responseResults = new ArrayList<>();
//
//            for (Map.Entry<ResponseResult, String> entry : resResult.entrySet()) {
//                responseResults.add(entry.getKey());
//            }
//            for (ResponseResult responseRes : responseResults) {
//                Boolean itemSuccess = responseRes.isSuccessful();
//                if (itemSuccess) {
//                    tableHeader = PropertyReader.getProperty("cpq.email.tableHeader.success");
//                }
//            }
//
//            emailSender.send(mailTo, responseResults, emailSubject, tableHeader);
//        }
    }
}
