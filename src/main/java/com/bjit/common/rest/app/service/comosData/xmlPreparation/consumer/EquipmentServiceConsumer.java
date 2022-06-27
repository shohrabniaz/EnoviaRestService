package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.ConsumeComosService;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.IComosData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth.NTLMAuthenticator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.utilities.IJSON;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Log4j
@Component
@Qualifier("EquipmentServiceConsumer")
public class EquipmentServiceConsumer extends ConsumeComosService implements IComosData<EquipmentRequestData> {

    @Autowired
    IJSON json;

    @Autowired
    BeanFactory beanFactory;

    @PostConstruct
    private void initializeCredentials() {
        this.getCredentials();
        this.getServiceURL();
    }

    @Override
    protected String getServiceURL() {
        this.path = Optional.ofNullable(this.path).orElse(PropertyReader.getProperty("comos.plant.structure.threedxcomos.service.url"));
        return this.path;
    }

    @Override
    protected NTLMAuthenticator getAuthenticator() {
        EquipmentServiceConsumer plantNTLMAuthenticator = new EquipmentServiceConsumer();
        plantNTLMAuthenticator.getCredentials();
        plantNTLMAuthenticator.getServiceURL();
        return plantNTLMAuthenticator;
    }

    @Override
    public String getComosData(EquipmentRequestData comosRequestData) {
        String requestData = json.serialize(comosRequestData);
        return getComosResponse(requestData);
    }
}
