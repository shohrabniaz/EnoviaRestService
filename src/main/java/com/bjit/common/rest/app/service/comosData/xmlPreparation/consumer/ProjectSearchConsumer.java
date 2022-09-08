package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectSearchRequestData;

import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.Authenticator;
import java.util.Optional;
import javax.ws.rs.core.*;
import org.apache.log4j.Logger;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

@Log4j
@Component
@Qualifier("ProjectSearchConsumer")
public class ProjectSearchConsumer extends ConsumeComosService implements IComosData<ProjectSearchRequestData> {

    @Autowired
    IJSON json;

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.projectSearch.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        ProjectSearchConsumer projectSearchNTLMAuthenticator = new ProjectSearchConsumer();
        projectSearchNTLMAuthenticator.getCredentials();
        projectSearchNTLMAuthenticator.getServiceURL();
        return projectSearchNTLMAuthenticator;
    }

    @Override
    public String getComosData(ProjectSearchRequestData projectSearchRequestData) {

        Authenticator.setDefault(getAuthenticator());

        String target_url = PropertyReader.getProperty("comos.projectSearch.service.url")  + projectSearchRequestData.getCompassId();
        log.info(target_url);
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(new URLConnectionEngine()).build();
//        ResteasyWebTarget target = client.target("https://plantplm.valmet.com/comos3dx/api/Comos/GetProjects?queryParam=C001605917");
        ResteasyWebTarget target = client.target(target_url);

        Response response = target.request().get();
        int status = response.getStatus();
        String responseData = response.readEntity(String.class);
        // work with the client
        client.close();
        log.info(responseData);
        return responseData;
    }

}
