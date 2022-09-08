package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonRequestEnvelope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentRequestEnvelope extends CommonRequestEnvelope {
    private EquipmentRequestData equipmentRequestData;
}
