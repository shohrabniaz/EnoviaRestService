package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import java.io.IOException;

public interface IServiceConsumer<T> {
    T getServiceResponse() throws IOException;
}
