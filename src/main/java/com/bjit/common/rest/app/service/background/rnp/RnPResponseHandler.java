/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.background.rnp;

import com.bjit.common.rest.app.service.filter.HTTPServletResponseHandler;
import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.CommonUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Lazy
@Component
public class RnPResponseHandler {

    private static final Logger RNP_RESPONSE_HANDLER_LOGGER = Logger.getLogger(RnPResponseHandler.class);

    public byte[] getReport(File reportFile, HttpServletResponse httpServletResponse) throws MalformedURLException, IOException {
        try {
            String fileName = reportFile.getName();

            URLConnection connection = reportFile.toURL().openConnection();
            String mimeType = connection.getContentType();

            InputStream inputStream = new FileInputStream(reportFile);

            httpServletResponse.setContentType(mimeType);
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            return IOUtils.toByteArray(inputStream);
        } catch (Exception exp) {
            RNP_RESPONSE_HANDLER_LOGGER.error(exp);
            throw exp;
        }
    }

    public Object getDownloadableReport(String reportName, HttpServletResponse httpServletResponse) throws IOException {
        try {
            String rnpOutputDirectory = CommonUtil.createOutputDirectory();
            String reportDirectory = rnpOutputDirectory + File.separator + reportName;
            File reportFile = new File(reportDirectory);

            return getResponse(Boolean.FALSE, reportFile, httpServletResponse);
        } catch (Exception exp) {
            RNP_RESPONSE_HANDLER_LOGGER.error(exp);
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

    public Object getResponse(Boolean isBackgroundProcess, RnPModel rnpModel) throws IOException {

        rnpModel.getHttpResponse().reset();
        HTTPServletResponseHandler.setCORS(rnpModel.getHttpResponse());

        if (isBackgroundProcess) {
            return getBackgroundProcessResponse();
        } else {
            try {
                if (rnpModel.getIsFileGenerated()) {

                    if (rnpModel.isDownload()) {
                        return getReport(rnpModel.getDownloadableFile(), rnpModel.getHttpResponse());
                    } else {
                        String responseMesasge = PropertyReader.getProperty("report.single.level.file.generation.finished");

                        IResponse responseBuilder = new CustomResponseBuilder();
                        String buildResponse = responseBuilder.setData(responseMesasge).setStatus(Status.OK).buildResponse();

                        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(serviceErrorResponse("File not generated"), HttpStatus.OK);
                }
            } catch (Exception exp) {
                return new ResponseEntity<>(serviceErrorResponse(exp.getMessage()), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity getBackgroundProcessResponse() {
        String serviceSuccessResponse = serviceSuccessResponse(PropertyReader.getProperty("rnp.mail.background.process.response.message"));
        return new ResponseEntity<>(serviceSuccessResponse, HttpStatus.OK);
    }

    private String serviceSuccessResponse(Object message) {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = responseBuilder.setData(message).setStatus(Status.OK).buildResponse();
        return buildResponse;
    }

    private String serviceErrorResponse(Object message) {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = responseBuilder.addErrorMessage(message).setStatus(Status.FAILED).buildResponse();
        return buildResponse;
    }

    public ResponseEntity getErrorResponse(Exception exp) {
        RNP_RESPONSE_HANDLER_LOGGER.error(exp);

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();

        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
    }
}
