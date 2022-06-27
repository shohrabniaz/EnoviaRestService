package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import java.io.IOException;

public interface IStructurePreparation<T, K, R> {
    T prepareStructure(R requestData) throws IOException;
    K getServiceData(R requestData) throws IOException;
}
