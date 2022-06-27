package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IXMLDataFactory;

import java.util.HashMap;
import java.util.List;

public interface IDataFactory extends IXMLDataFactory {
    HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> structureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parent, List<EquipmentChild> childItem, Long sequence);
}
