package com.bjit.common.rest.app.service.comosData.project_structure.services;

public interface ITaskImportProcess<T, K> {
    K projectAndTaskCreate(T importBean) throws Exception;
}
