package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;


@Service
@RequestScope
public class EquipmentRequestBuilder {
    private EquipmentRequestData equipmentRequestData;

    public EquipmentRequestData getEquipmentRequestData() {
        equipmentRequestData = new EquipmentRequestData();
        return equipmentRequestData;
    }
    
    public EquipmentRequestData getEquipmentRequestData(ComosIntegration comosIntegrationRequestData){
        this.equipmentRequestData = getEquipmentRequestData();
        equipmentRequestData.setCompassId(comosIntegrationRequestData.getRequest().getCompassId());
        equipmentRequestData.setMillId(comosIntegrationRequestData.getRequest().getMillId());
        equipmentRequestData.setEquipmentId(comosIntegrationRequestData.getRequest().getEquipmentId());
        equipmentRequestData.setComosDeviceStructureLevel(comosIntegrationRequestData.getRequest().getComosDeviceStructureLevel());
        equipmentRequestData.setCategory(comosIntegrationRequestData.getRequest().getCategory());
        return this.equipmentRequestData;
    }

    public EquipmentRequestEnvelope getEquipmentRequestEnvelopeData(ComosIntegration comosIntegrationRequestData){
        EquipmentRequestData equipmentRequestData = getEquipmentRequestData(comosIntegrationRequestData);

        EquipmentRequestEnvelope envelope = new EquipmentRequestEnvelope();
        envelope.setConnectInStructure(Boolean.TRUE);
        envelope.setEquipmentRequestData(equipmentRequestData);

        return envelope;
    }
}
