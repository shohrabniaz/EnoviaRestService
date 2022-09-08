package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import matrix.util.MatrixException;

public interface IConnectTaskAndDeliverable {
    String searchItem(String type, String name) throws Exception;
    Boolean connectItem(String taskId, String logicalItemId) throws MatrixException;
}
