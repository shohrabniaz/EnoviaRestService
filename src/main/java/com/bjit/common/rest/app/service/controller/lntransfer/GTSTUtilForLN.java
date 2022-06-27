/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer;

import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.ex.integration.transfer.actions.GTSNightlyUpdateTransferAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Tahmid
 */
@RestController
@RequestMapping(path = "/gts-services")
public class GTSTUtilForLN {

    private static final org.apache.log4j.Logger GTS_UTIL_FOR_LN_LOGGER = org.apache.log4j.Logger.getLogger(GTSTUtilForLN.class);

    @RequestMapping(value = "/nightlyUpdatesLN", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void getUpdatedBundleIds(HttpServletRequest httpRequest,
            HttpServletResponse response) throws KeyManagementException, NoSuchAlgorithmException {
        GTS_UTIL_FOR_LN_LOGGER.info("Nightly update service gets called...");
        DisableSSLCertificate.DisableCertificate();
        boolean isService = true;
        new GTSNightlyUpdateTransferAction(isService);
    }
}
