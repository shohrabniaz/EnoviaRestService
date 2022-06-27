package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;

public interface IComosLogicalInstanceDataFactory {
    RFLVPMItem getLogicalInstance(EquipmentChild parentItem, EquipmentChild childItem);
}
