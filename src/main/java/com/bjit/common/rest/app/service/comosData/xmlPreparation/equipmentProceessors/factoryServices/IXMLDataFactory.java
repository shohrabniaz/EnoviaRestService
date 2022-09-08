package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import java.util.HashMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;

public interface IXMLDataFactory {

    String getType();

    HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> structureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parent, Long sequence);
}
