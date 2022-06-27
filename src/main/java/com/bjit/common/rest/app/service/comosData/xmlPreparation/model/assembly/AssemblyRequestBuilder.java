package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentRequestEnvelope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;


@Service
@RequestScope
public class AssemblyRequestBuilder {
    private AssemblyRequestData assemblyRequestData;

    public AssemblyRequestData getAssemblyRequestData() {
        assemblyRequestData = new AssemblyRequestData();
        return assemblyRequestData;
    }

    public AssemblyRequestData getAssemblyRequestData(ComosIntegration comosIntegrationRequestData){
        this.assemblyRequestData = getAssemblyRequestData();
        assemblyRequestData.setCompassId(comosIntegrationRequestData.getRequest().getCompassId());
        assemblyRequestData.setMillId(comosIntegrationRequestData.getRequest().getMillId());
        assemblyRequestData.setEquipmentId(comosIntegrationRequestData.getRequest().getEquipmentId());
        return this.assemblyRequestData;
    }

    public AssemblyRequestEnvelope getAssemblyRequestEnvelopeData(ComosIntegration comosIntegrationRequestData){
        AssemblyRequestData assemblyRequestData = getAssemblyRequestData(comosIntegrationRequestData);

        AssemblyRequestEnvelope envelope = new AssemblyRequestEnvelope();
        envelope.setConnectInStructure(Boolean.TRUE);
        envelope.setAssemblyRequestData(assemblyRequestData);

        return envelope;
    }
}
