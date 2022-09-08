package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@Qualifier("SubtasksServiceConsumer")
public class SubtasksServiceConsumer extends ConsumeComosService implements IComosData<SubtasksRequestData> {

    @Autowired
    IJSON json;

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.subtasks.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        SubtasksServiceConsumer subtasksNTLMAuthenticator = new SubtasksServiceConsumer();
        subtasksNTLMAuthenticator.getCredentials();
        subtasksNTLMAuthenticator.getServiceURL();
        return subtasksNTLMAuthenticator;
    }

    @Override
    public String getComosData(SubtasksRequestData subtasksRequestData) {
        String requestData = json.serialize(subtasksRequestData);
        return getComosResponse(requestData);
    }
}
