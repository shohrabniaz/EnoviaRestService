package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListRequestData;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Log4j
@Component
@Qualifier("DeliStatusListServiceConsumer")
public class DeliStatusListServiceConsumer extends ConsumeComosService implements IComosData<DeliStatusListRequestData> {
    @Autowired
    IJSON json;

    @PostConstruct
    private void initializeCredentials() {
        this.getCredentials();
        this.getServiceURL();
    }

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.deliverable.status.list.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        DeliStatusListServiceConsumer deliAssigneeServiceConsumer = new DeliStatusListServiceConsumer();
        deliAssigneeServiceConsumer.getCredentials();
        deliAssigneeServiceConsumer.getServiceURL();
        return deliAssigneeServiceConsumer;
    }

    @Override
    public String getComosData(DeliStatusListRequestData comosRequestData) {
        String requestData = json.serialize(comosRequestData);
        return getComosResponse(requestData);
    }
}
