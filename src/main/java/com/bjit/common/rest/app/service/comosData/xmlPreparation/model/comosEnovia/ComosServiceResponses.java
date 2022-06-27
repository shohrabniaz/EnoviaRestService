package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.assembly.AssemblyServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.projectStructure.ProjectStructureServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Component
@RequestScope
public class ComosServiceResponses {
    private EquipmentServiceResponse equipmentServiceResponse;
    private AssemblyServiceResponse assemblyServiceResponse;
    private ProjectStructureServiceResponse projectStructureServiceResponse;
}
