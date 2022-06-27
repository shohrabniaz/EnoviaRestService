package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.DeliverableTaskAndLogicalItemMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.Deliverables;

import java.util.HashMap;
import java.util.List;

public interface IComosStructureCollector {
    Boolean connectItem(String deliverableTaskId, String logicalItemId);

    //    void collectStructure(ComosIntegration requestData);
    void connectItems(List<Deliverables> deliverablesList);
    Deliverables expandTask(Deliverables deliverables) throws Exception;
    Boolean expandTask(String taskObjectId, String logicalObjectId);

    Deliverables findLogicalItems(Deliverables deliItem);
    List<String> connectItems(HashMap<String, DeliverableTaskAndLogicalItemMap> filenameAndDataMap);
}
