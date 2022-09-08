/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import java.net.Authenticator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;

/**
 *
 * @author BJIT
 */
public abstract class ConsumeComosService extends NTLMAuthenticator {

    private static final Logger ConsumeComosService_LOGGER = Logger.getLogger(ConsumeComosService.class);
    protected String path;

    protected abstract NTLMAuthenticator getAuthenticator();

    protected abstract String getServiceURL();
    
    @LogExecutionTime
    protected String getComosResponse(String requestData) throws IllegalArgumentException, NullPointerException {
        Authenticator.setDefault(getAuthenticator());
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(new URLConnectionEngine()).build();

        ResteasyWebTarget target = client.target(UriBuilder.fromPath(getServiceURL()));
        Response response = target.request().post(Entity.json(requestData));

        int status = response.getStatus();
        String responseData = response.readEntity(String.class);
        // work with the client
        client.close();

        ConsumeComosService_LOGGER.info(responseData);
        return responseData;
    }
}
