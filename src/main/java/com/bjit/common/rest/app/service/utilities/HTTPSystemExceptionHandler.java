/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.exception.EnoviaSystemError;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Sayeedul
 */
public class HTTPSystemExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(HTTPSystemExceptionHandler.class);

    private List<String> systemErrors = null;

    private static final String EX_001 = "System error: please check with the system admin."; 
    private static final String EX_002 = "System error: {}. please check with the system admin."; 

    public void setSystemErrors(List<String> exList) {
        if (exList != null) {
            systemErrors = exList;
        }
    }

    public HTTPSystemExceptionHandler() {
        systemErrors = new ArrayList<>();
        systemErrors.add("java.io.IOException");
        systemErrors.add("javax.net.ssl.SSLProtocolException");
    }

    public HTTPSystemExceptionHandler(List<String> exList) {
        systemErrors = exList;
    }

    /**
     *
     * @param rex
     * @throws EnoviaSystemError
     */
    public void hasSystemError(RestClientException rex) throws EnoviaSystemError {
        EnoviaSystemError envException = findExceptionType(rex);
        if (envException != null) {
            throw new EnoviaSystemError(envException.getErrCode(), envException.getErrDesc());
        }
    }

    public void hasSystemError(Exception rex) throws EnoviaSystemError {
        EnoviaSystemError envException = findExceptionType(rex);
        if (envException != null) {
            throw new EnoviaSystemError(envException.getErrCode(), envException.getErrDesc());
        }
    }

    private EnoviaSystemError findExceptionType(Object e) {
        if (e == null) {
            return new EnoviaSystemError("EX_001", EX_001);
        } 
        else if (systemErrors.contains(e.getClass().toString())) {
            LOGGER.info("Error matched with: " + e.getClass().toString());
            return new EnoviaSystemError("EX_002", EX_002);
        }
        return null;
    }
}
