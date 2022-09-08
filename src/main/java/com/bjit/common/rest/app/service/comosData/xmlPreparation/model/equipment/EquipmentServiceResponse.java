package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.ComosCommonResponse;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class EquipmentServiceResponse extends ComosCommonResponse {
    private EquipmentData data;
}
