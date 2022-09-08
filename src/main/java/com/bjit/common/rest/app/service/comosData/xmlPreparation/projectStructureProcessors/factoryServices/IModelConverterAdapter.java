package com.bjit.common.rest.app.service.comosData.xmlPreparation.projectStructureProcessors.factoryServices;

public interface IModelConverterAdapter<T, R> {
    R convert(T object);
}
