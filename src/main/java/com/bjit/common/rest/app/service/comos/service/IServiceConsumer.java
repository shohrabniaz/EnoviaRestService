package com.bjit.common.rest.app.service.comos.service;


import java.io.IOException;

public interface IServiceConsumer<T> {
    T getServiceResponse() throws IOException;
}
