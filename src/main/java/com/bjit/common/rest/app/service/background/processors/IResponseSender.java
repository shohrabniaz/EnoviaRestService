package com.bjit.common.rest.app.service.background.processors;

public interface IResponseSender<T>  {
    Boolean send(T object) throws Exception;
}
