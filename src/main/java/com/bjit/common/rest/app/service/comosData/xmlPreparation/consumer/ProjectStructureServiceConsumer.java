package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 *
 * @author Toufiqul Khan-17
 */
@Component
@Qualifier("ProjectStructureServiceConsumer")
public class ProjectStructureServiceConsumer extends ConsumeComosService implements IComosData<ProjectStructureRequestData> {

    @Autowired
    IJSON json;

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.project.structure.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        ProjectStructureServiceConsumer assemblyNTLMAuthenticator = new ProjectStructureServiceConsumer();
        assemblyNTLMAuthenticator.getCredentials();
        assemblyNTLMAuthenticator.getServiceURL();
        return assemblyNTLMAuthenticator;
    }

    @Override
    public String getComosData(ProjectStructureRequestData assemblyRequestData) {
        String requestData = json.serialize(assemblyRequestData);
        return getComosResponse(requestData);
    }
}
