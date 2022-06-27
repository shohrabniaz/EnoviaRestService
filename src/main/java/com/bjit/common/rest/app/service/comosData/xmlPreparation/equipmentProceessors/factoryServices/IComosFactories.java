package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import java.util.HashMap;

public interface IComosFactories {
    HashMap<String, IXMLDataFactory> getDataFactoryMap();
    HashMap<String, IRFLPDataFactory> getRFLPDataFactoryMap();
    HashMap<String, IComosItemTypeUtils> getComosItemTypeUtilsMap();
}
