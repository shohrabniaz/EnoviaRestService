package com.bjit.common.rest.app.service.comosData.xmlPreparation.structureProcessor;

public interface IPrepareStructure {
    Boolean prepareComosStructure();
    <T> Boolean prepareDeliverableTaskAndLogicalItemMap(T requestData, String email, String serviceName);
}
