package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.common.CommonData;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class EquipmentData extends CommonData {
    private EquipmentChild comosModel;
}
