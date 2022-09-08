/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.service;

import com.bjit.common.rest.app.service.bomExport.BomExportUtil;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.enoviaCPQ.model.Item;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.EmailSendUtil;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FileProcessorUtils;

import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import matrix.util.MatrixException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author BJIT
 */
@Service
public class CPQTransferServiceImpl implements CPQTransferService {

    private static final org.apache.log4j.Logger CPQ_TRANSFER_LOGGER = org.apache.log4j.Logger.getLogger(CPQTransferServiceImpl.class);

    @Override
    public Map<String, ResponseEntity> bomExportjson(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) {

        Map<String, ResponseEntity> bomExportBody = new HashMap<>();
        ResponseEntity bomExportJson = null;
        String filename = "";
        try {
            FileProcessorUtils fileProcessorUtils = new FileProcessorUtils();
            HashMap<Item, String> transferItemWithMsgMap = new HashMap<>();
            String triggerredFileDirectory = PropertyReader.getProperty("cpq.env.config.properties.dir");

            HashMap<String, Item> processXMLFiles = new HashMap<>();
            try {
                processXMLFiles = fileProcessorUtils.processXMLFiles();
            } catch (Exception ex) {
                Logger.getLogger(CPQTransferServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!processXMLFiles.isEmpty()) {

                for (Map.Entry<String, Item> entry : processXMLFiles.entrySet()) {

                    filename = entry.getKey();
                    Item itemToCheckMsg = entry.getValue();
                    if (itemToCheckMsg.getMessage() == null) {
                        bomExportJson = getBomExportJson(httpRequest, httpServletResponse, filename, processXMLFiles);
                    }else{
                           filename = filename+ "=" + itemToCheckMsg.getMessage();
                    }
                    

                    bomExportBody.put(filename, bomExportJson);

                }
            
            } else {
                CPQ_TRANSFER_LOGGER.info("No XML file found in the directory!\n\n");
            }

        } catch (Exception ex) {
            Logger.getLogger(CPQTransferServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bomExportBody;

    }

    @Override
    public String cpqTransfer(ResponseEntity bomExportjson) {
        ResponseEntity<String> result = null;
        try {
            CPQ_TRANSFER_LOGGER.info("BOM Response Body : " + bomExportjson.getBody().toString());

            String url = PropertyReader.getProperty("enovia.cpq.transfer.url");
            String receiver = PropertyReader.getProperty("enovia.cpq.transfer.receiver");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            builder.queryParam("Receiver", receiver);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(bomExportjson.getBody().toString(), headers);
            result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            // CPQ_TRANSFER_LOGGER.error("Error Occurred  : " + e.getMessage());

            return "";
        }
        return result.getBody();

    }

    ResponseEntity getBomExportJson(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, String filename, HashMap<String, Item> processXMLFiles) {

        Item item = processXMLFiles.get(filename);

        BomExportUtil bomExportUtil = new BomExportUtil();
        HashMap<String, String> urlParams;
        Context context = null;
        ResponseEntity responseEntity = null;

        try {
            CreateContext createContext = new CreateContext();

            context = createContext.getAdminContext();
            if (!context.isConnected()) {
                throw new Exception(Constants.CONTEXT_EXCEPTION);
            }
        } catch (Exception exp) {
            return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
        }

        String attributeString = PropertyReader.getProperty("enovia.cpq.bom.export.attribute");
        String docType = PropertyReader.getProperty("enovia.cpq.bom.export.doc.type");
        try {

            urlParams = bomExportUtil.getUrlParamValues("CreateAssembly", item.getName(), item.getRevision(), "", "", "", "en", "en", "", "", "", "false");
            responseEntity = bomExportUtil.generateBomExportData(httpRequest, httpServletResponse, urlParams, attributeString, context, docType);
        } catch (IOException ex) {
            Logger.getLogger(CPQTransferServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MatrixException ex) {
            Logger.getLogger(CPQTransferServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
        return responseEntity;
    }

}
