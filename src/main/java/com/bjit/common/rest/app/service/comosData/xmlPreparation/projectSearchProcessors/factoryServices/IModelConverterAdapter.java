package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectSearchProcessors.factoryServices;

public interface IModelConverterAdapter<T, R> {
    R convert(T object);
}
