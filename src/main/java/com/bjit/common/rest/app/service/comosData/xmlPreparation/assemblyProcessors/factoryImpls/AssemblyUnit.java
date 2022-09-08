package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.UnitFactory;
import com.bjit.ewc18x.utils.PropertyReader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class AssemblyUnit extends UnitFactory {

//    @Override
//    public RFLVPMItem getLogicalData(Child item) {
//        RFLVPMItem logicalData = super.getLogicalData(item);
//        logicalData.setvName(xmlAttributeGenerator.getXmlAttribute(item.getCode()));
//        return logicalData;
//    }

//    @Override
//    protected HashMap<String, HashMap<String, String>> getMapData(Child item) {
//        HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(item.getThreeDxObjectType());
//        return itemSourceDestinationData;
//    }

    @Override
    protected RFLP prepareParentData(List<EquipmentChild> children, HashMap<String, String> levelWiseFileName, HashMap<String, RFLP> xmlStructureMap, EquipmentChild parentItem, RFLP rootRFLP) {
        // This block has been kept empty intentionally as the child of the item doesn't be considered in current scope
        return rootRFLP;
    }

    @Override
    protected void getChildrenItemAndRelationalData(RFLP rootRFLP, List<EquipmentChild> children, EquipmentChild parentItem) {
        // This block has been kept empty intentionally as the child of the item doesn't be considered in current scope
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.assembly.unit.factory.type");

    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.assembly.unit.factory.prefix");

    }
}
