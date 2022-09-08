/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.background.himelli;

import com.bjit.common.rest.app.service.filter.HTTPServletResponseHandler;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 *
 * @author BJIT
 */
@Lazy
@Component
public class HimelliResponseHandler {

    private static final Logger LOGGER = Logger.getLogger(HimelliResponseHandler.class);

    @Value("${himelli.mail.background.process.response.message}")
    private String reportMessage;
    @Value("${himelli.report.file.generation.location}")
    private String userHome;

    public byte[] getReport(File reportFile, HttpServletResponse httpServletResponse) throws IOException {
        try {
            String fileName = reportFile.getName();

            URLConnection connection = reportFile.toURL().openConnection();
            String mimeType = connection.getContentType();

            InputStream inputStream = new FileInputStream(reportFile);

            httpServletResponse.setContentType(mimeType);
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            return IOUtils.toByteArray(inputStream);
        } catch (Exception exp) {
            LOGGER.error(exp);
            throw exp;
        }
    }

    public Object getResponse(Boolean isBackgroundProcess, File reportFile, HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.reset();
        HTTPServletResponseHandler.setCORS(httpServletResponse);

        if (isBackgroundProcess) {
            return getBackgroundProcessResponse();
        } else {
            try {
                return getReport(reportFile, httpServletResponse);
            } catch (Exception exp) {
                return new ResponseEntity<>(serviceErrorResponse(exp.getMessage()), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<?> getBackgroundProcessResponse() {
        String serviceSuccessResponse = serviceSuccessResponse(reportMessage);
        return new ResponseEntity<>(serviceSuccessResponse, HttpStatus.OK);
    }

    private String serviceSuccessResponse(Object message) {
        IResponse responseBuilder = new CustomResponseBuilder();
        return responseBuilder.setData(message).setStatus(Status.OK).buildResponse();
    }

    private String serviceErrorResponse(Object message) {
        IResponse responseBuilder = new CustomResponseBuilder();
        return responseBuilder.addErrorMessage(message).setStatus(Status.FAILED).buildResponse();
    }

    public ResponseEntity getErrorResponse(Exception exp) {
       LOGGER.error(exp);

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();

        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
    }

    public Object getDownloadableHimelliReport(String reportName, HttpServletResponse httpServletResponse) throws IOException {
        try {
            String downloadFilePath = userHome + File.separator + "generated_reports" + File.separator;
            String reportDirectory = downloadFilePath + reportName;
            File reportFile = new File(reportDirectory);

            return getResponse(Boolean.FALSE, reportFile, httpServletResponse);
        } catch (Exception exp) {
            LOGGER.error(exp);
            throw exp;
        }
    }
}
