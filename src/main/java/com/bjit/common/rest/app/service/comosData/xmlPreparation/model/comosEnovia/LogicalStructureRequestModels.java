package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
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
public class LogicalStructureRequestModels {
    private EquipmentRequestData equipmentRequestData;
    private AssemblyRequestData assemblyRequestData;
    private ProjectStructureRequestData projectStructureRequestData;
}
