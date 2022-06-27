package com.bjit.common.rest.app.service.background.processors;

public interface IBackGroundProcessor<T> {
    T process() throws Exception;
}
