package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import java.util.HashMap;

public interface IFilenameGenerator {
    HashMap<String, String> setMillIdAndEquipmentId(String millId, String equipmentId, String prefix, Integer level);
    String generateFileName(HashMap<String, String> levelWiseFileName, HashMap<String, RFLP> structureMap, int level, String category, EquipmentChild parent);
}
