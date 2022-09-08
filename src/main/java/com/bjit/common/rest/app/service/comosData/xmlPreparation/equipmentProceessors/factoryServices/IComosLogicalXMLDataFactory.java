package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;

public interface IComosLogicalXMLDataFactory {
    RFLVPMItem getLogicalData(EquipmentChild item);
}
