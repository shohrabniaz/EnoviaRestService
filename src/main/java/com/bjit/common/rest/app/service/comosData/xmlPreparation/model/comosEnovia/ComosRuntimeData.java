package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;

@Data
@ToString
@Component
@RequestScope
@NoArgsConstructor
//@AllArgsConstructor
public class ComosRuntimeData {
    private LogicalItem equipmentPlantData;
    private LogicalItem assemblyPlantData;
    private ProjectStructureData projectSpaceData;
    private HashMap<String, Deliverables> deliverables;
    private HashMap<String, Deliverables> connectedDeliverables;
}
