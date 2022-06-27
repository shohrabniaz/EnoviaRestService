package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosIntegration;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalStructureRequestModels;

public interface IComosModelAdapter {
    LogicalStructureRequestModels getEquipmentStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels);
    LogicalStructureRequestModels getAssemblyStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels);
    LogicalStructureRequestModels getProjectStructureServiceRequestData(ComosIntegration requestData, LogicalStructureRequestModels requestModels);
}
