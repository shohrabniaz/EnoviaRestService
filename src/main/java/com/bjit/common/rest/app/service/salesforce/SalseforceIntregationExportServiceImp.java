/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.salesforce;

import com.bjit.common.rest.app.service.salseforce.utilities.EmailSender;
import com.bjit.common.rest.app.service.salseforce.utilities.ItemInfo;
import com.bjit.common.rest.app.service.salseforce.utilities.SalseforceIntregationExportUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.service.impl.LN.ResponseResult;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.util.MatrixException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Suvonkar Kundu
 */
public class SalseforceIntregationExportServiceImp implements SalesforceIntregationExportService {

    private static String xmlFileURL = PropertyReader.getProperty("xml.file.url"); //directory where the XML files are generated by the trigger
    private static final org.apache.log4j.Logger Salesforce_Intregation_Service_Logger = org.apache.log4j.Logger.getLogger(SalseforceIntregationExportServiceImp.class); //Logger
    SalseforceIntregationExportUtil salseforceIntregationExportUtil = new SalseforceIntregationExportUtil();//Class initialization to call various util methods
    private String retryLimit = PropertyReader.getProperty("salesforce.error.limit");//Determines how many times we will retry the transfer process for failled item

    @Override
    public String getIntregatedMVItemId() throws IOException {

        JSONArray finalResponse = new JSONArray();
        JSONArray response = null;

        if (salseforceIntregationExportUtil.isAnyFileExist(xmlFileURL)) {
            //String tokenGenerationResult = salseforceIntregationExportUtil.callingSalesforceTokenGenerationAPI(); getSalesforceAPIToken
            String tokenGenerationResult = null;
            try {
                tokenGenerationResult = "Bearer " + salseforceIntregationExportUtil.getSalesforceAPIToken();
            } catch (MalformedURLException ex) {
                Logger.getLogger(SalseforceIntregationExportServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(SalseforceIntregationExportServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(SalseforceIntregationExportServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            //String sessionId = salseforceIntregationExportUtil.getSessionID(tokenGenerationResult);
            Map<String, ItemInfo> tnrMV = salseforceIntregationExportUtil.xmlFileToModelSalesForce();
            EmailSender emailSender = new EmailSender();//Initialize class to call it's email transfer methods
            List<ResponseResult> responseResult = new ArrayList();//This holds the information to prepare email body
            Map<JSONArray, HttpStatus> salesforceAPIResponse = new HashMap<>();//This will hold Salesforce API response JSON and status code
            Map<String, List<ResponseResult>> recipients = new HashMap<>();//This map will hold recipient's email address and their corresponding items
            Map<String, ItemInfo> donotTransferItemMap = new HashMap();
            Map<String, ItemInfo> duplicateItemsMap = new HashMap();
            Set<ItemInfo> errorItemList = new HashSet();
            Set<ItemInfo> successfulItemList = new HashSet();
            String productAPINameParam = "";
            Boolean sendToAms = false;

            for (Map.Entry<String, ItemInfo> entry : tnrMV.entrySet()) {
//                Checks if what is the message from trigger. If not succes, then the item is not further processed and marked as an error item
                if (!entry.getValue().getMessage().equalsIgnoreCase(" ")) {
                    donotTransferItemMap.put(entry.getKey(), entry.getValue());
                    salseforceIntregationExportUtil.moveXmlFile("error", entry.getKey());
                    Salesforce_Intregation_Service_Logger.info("Error integration due to AUT_Lifecycle_Status for item : " + entry.toString());
                    continue;
                }
                if (!productAPINameParam.contains(entry.getValue().getName())) {
                    productAPINameParam = productAPINameParam + entry.getValue().getName() + ",";
                } else {
                    duplicateItemsMap.put(entry.getKey(), entry.getValue());
                    salseforceIntregationExportUtil.moveXmlFile("error", entry.getKey());
                }
            }
            productAPINameParam = productAPINameParam.substring(0, productAPINameParam.length() - 1);

            for (Map.Entry<String, ItemInfo> entry : donotTransferItemMap.entrySet()) {
                Salesforce_Intregation_Service_Logger.info("Enovia to Salesforce Transfer Process failed for message field : " + entry.getValue().getName() + " Model Version Product");
                tnrMV.remove(entry.getKey());
            }
            for (Map.Entry<String, ItemInfo> entry : duplicateItemsMap.entrySet()) {
                Salesforce_Intregation_Service_Logger.info("Enovia to Salesforce Transfer Process failed for duplication : " + entry.getValue().getName() + " Model Version Product");
                tnrMV.remove(entry.getKey());
            }
            if (!productAPINameParam.equalsIgnoreCase("")) {
                try {
                    String costUpdateUrl = salseforceIntregationExportUtil.getCostUpdateAPIUrl(productAPINameParam);
                    ResponseEntity<String> costUpdateResult = salseforceIntregationExportUtil.callingCostUpdateAPI(costUpdateUrl);
                    Salesforce_Intregation_Service_Logger.info("Result of the cost update API call : " + costUpdateResult.getBody());
                } catch (Exception ex) {
                    Salesforce_Intregation_Service_Logger.info("Cost Update API call failed");
                }
                String url = salseforceIntregationExportUtil.getExportProductAPIUrl(productAPINameParam);
                JSONObject result = salseforceIntregationExportUtil.getMVItemsInformation(url);
                JSONObject finalResult = salseforceIntregationExportUtil.getFinalMVItemsInformation(result, tnrMV);

                Salesforce_Intregation_Service_Logger.info("Result of the export product API call : " + finalResult);

                try {
                    salesforceAPIResponse = salseforceIntregationExportUtil.callingSalesforceIntregationAPI(tokenGenerationResult, finalResult.toString());
                } catch (Exception ex) {
                    Salesforce_Intregation_Service_Logger.error("Salesforce Server Error Occured");
                    for (Map.Entry<String, ItemInfo> entry : tnrMV.entrySet()) {
                        errorItemList.add(entry.getValue());
                    }
                }
//                Check if the SalesForce service is unavailable. If yes then puts the item in retry items list and 
                for (int i = 1; i <= Integer.parseInt(retryLimit); i++) {
                    if (salesforceAPIResponse.size() != 0 && salseforceIntregationExportUtil.isSalesforceServiceAvailable(salesforceAPIResponse).equalsIgnoreCase("200")) {
                        Map<String, String> failedItemsMap = salseforceIntregationExportUtil.itemsFromApiResponse(salesforceAPIResponse);
                        for (Map.Entry<String, ItemInfo> entry : tnrMV.entrySet()) {
                            if (failedItemsMap.containsKey(entry.getValue().getName())) {
                                entry.getValue().setMessage(failedItemsMap.get(entry.getValue().getName()));
                                errorItemList.add(entry.getValue());
                                salseforceIntregationExportUtil.moveXmlFile("error", entry.getKey());
                                Salesforce_Intregation_Service_Logger.info("Enovia to Salesforce Transfer Process failed for : " + entry.getValue().getName() + " Model Version Product");
                            } else {
                                successfulItemList.add(entry.getValue());
                                salseforceIntregationExportUtil.moveXmlFile("success", entry.getKey());
                                Salesforce_Intregation_Service_Logger.info("Enovia to Salesforce Transfer Process success for : " + entry.getValue().getName() + " Model Version Product");
                            }
                        }
                        break;
                    } else {
                        Salesforce_Intregation_Service_Logger.info("Error message from salesforce, retry " + i);
                        try {
                            salesforceAPIResponse = salseforceIntregationExportUtil.callingSalesforceIntregationAPI(tokenGenerationResult, finalResult.toString());
                        } catch (Exception ex) {
                            Salesforce_Intregation_Service_Logger.error("Salesforce Server Error Occured");
                            for (Map.Entry<String, ItemInfo> entry : tnrMV.entrySet()) {
                                errorItemList.add(entry.getValue());
                            }
                        }
                        if (salesforceAPIResponse.size() != 0 && i == Integer.parseInt(retryLimit) && salseforceIntregationExportUtil.isSalesforceServiceAvailable(salesforceAPIResponse).equalsIgnoreCase("503")) {
                            sendToAms = true;
                        }
                    }
                }

                for (Map.Entry<JSONArray, HttpStatus> salesforceResponse : salesforceAPIResponse.entrySet()) {
                    response = salesforceResponse.getKey();
                }

                Salesforce_Intregation_Service_Logger.info("Response of the Salesforce API call" + response);
                finalResponse.put(response);

                if (sendToAms) {
                    List<ResponseResult> itemList = new ArrayList();
                    for (Map.Entry<String, ItemInfo> item : tnrMV.entrySet()) {
                        ResponseResult resResponse = new ResponseResult(item.getValue().getName(), item.getValue().getRevision(), "ERROR", false);
                        resResponse.setId(item.getValue().getId());
                        itemList.add(resResponse);
                    }
                    recipients.put(PropertyReader.getProperty("salesforce.ams.email"), itemList);
                }
            }

//            Email Sending Part Starts from here
//            Successful Email Preparation
            for (ItemInfo item : successfulItemList) {
                ResponseResult resResponse = new ResponseResult(item.getName(), item.getRevision(), "OK", true);
                resResponse.setId(item.getId());
                List<String> recipientEmail = new ArrayList();
                try {
                    recipientEmail = salseforceIntregationExportUtil.emailSending(item);
                    for (String iter : recipientEmail) {
                        if (recipients.containsKey(iter)) {
                            responseResult = recipients.get(iter);
                        } else {
                            responseResult = new ArrayList();
                        }
                        responseResult.add(resResponse);
                        recipients.put(iter, responseResult);
                    }

                } catch (MatrixException ex) {
                    Salesforce_Intregation_Service_Logger.info("Error message: " + ex.getMessage().toString());
                }
            }

//          Failed Item due to message inside product email preparation
            for (Map.Entry<String, ItemInfo> item : donotTransferItemMap.entrySet()) {
                ResponseResult resResponse = new ResponseResult(item.getValue().getName(), item.getValue().getRevision(), item.getValue().getMessage(), false);
                resResponse.setId(item.getValue().getId());
                List<String> recipientEmail = new ArrayList();
                try {
                    recipientEmail = salseforceIntregationExportUtil.emailSending(item.getValue());
                    for (String iter : recipientEmail) {
                        if (recipients.containsKey(iter)) {
                            responseResult = recipients.get(iter);
                        } else {
                            responseResult = new ArrayList();
                        }
                        responseResult.add(resResponse);
                        recipients.put(iter, responseResult);
                    }

                } catch (MatrixException ex) {
                    Salesforce_Intregation_Service_Logger.info("Error message: " + ex.getMessage().toString());
                }
            }

//            Failed Item Transfer due to salesforce server down issue
            for (ItemInfo item : errorItemList) {
                ResponseResult resResponse = new ResponseResult(item.getName(), item.getRevision(), item.getMessage(), false);
                resResponse.setId(item.getId());
                List<String> recipientEmail = new ArrayList();
                try {
                    recipientEmail = salseforceIntregationExportUtil.emailSending(item);
                    for (String iter : recipientEmail) {
                        if (recipients.containsKey(iter)) {
                            responseResult = recipients.get(iter);
                        } else {
                            responseResult = new ArrayList();
                        }
                        responseResult.add(resResponse);
                        recipients.put(iter, responseResult);
                    }

                } catch (MatrixException ex) {
                    Salesforce_Intregation_Service_Logger.info("Error message: " + ex.getMessage().toString());
                }
            }

            String emailSubject = PropertyReader.getProperty("salesforce.email.subject");
            String tableHeader = PropertyReader.getProperty("salesforce.email.tableHeader");
//            All the recipients will get their emails as per their items release
            for (Map.Entry<String, List<ResponseResult>> recipient : recipients.entrySet()) {
                emailSender.send(recipient.getKey(), recipient.getValue(), emailSubject, tableHeader);
            }

        } else {
            Salesforce_Intregation_Service_Logger.info("MV XML file do not exist for salesforce intregation");
            Salesforce_Intregation_Service_Logger.debug("MV XML file do not exist for salesforce intregation");

        }

        return finalResponse.toString();
    }
}
