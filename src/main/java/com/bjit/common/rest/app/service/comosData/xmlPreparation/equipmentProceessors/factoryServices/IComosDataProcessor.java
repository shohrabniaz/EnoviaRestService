package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;

public interface IComosDataProcessor {
    RFLP processData(String data);
}
