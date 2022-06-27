/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth;

import com.bjit.ewc18x.utils.PropertyReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Optional;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author BJIT
 */
@Log4j
public abstract class NTLMAuthenticator extends Authenticator {

    protected String id;
    protected String pass;

    protected void getCredentials() {
        this.id = Optional.ofNullable(this.id).orElse(PropertyReader.getProperty("comos.ntlm.authentication.id"));
        this.pass = Optional.ofNullable(this.pass).orElse(PropertyReader.getProperty("comos.ntlm.authentication.password"));
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        try {
            this.getCredentials();
            return new PasswordAuthentication(id, pass.toCharArray());
        } catch (Exception exp) {
            log.error(exp.getMessage());
            throw exp;
        }
    }
}
