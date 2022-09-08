package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Toufiqul Khan-17
 */
@Component
@Qualifier("AssemblyServiceConsumer")
public class AssemblyServiceConsumer extends ConsumeComosService implements IComosData<AssemblyRequestData> {

    @Autowired
    IJSON json;

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.assembly.structure.threedxcomos.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        AssemblyServiceConsumer assemblyNTLMAuthenticator = new AssemblyServiceConsumer();
        assemblyNTLMAuthenticator.getCredentials();
        assemblyNTLMAuthenticator.getServiceURL();
        return assemblyNTLMAuthenticator;
    }

    @LogExecutionTime
    @Override
    public String getComosData(AssemblyRequestData assemblyRequestData) {
        String requestData = json.serialize(assemblyRequestData);
        return getComosResponse(requestData);
    }
}
