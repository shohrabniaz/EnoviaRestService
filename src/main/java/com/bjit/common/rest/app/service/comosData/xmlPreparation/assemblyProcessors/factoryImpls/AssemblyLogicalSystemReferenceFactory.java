package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.UnitFactory;
import com.bjit.ewc18x.utils.PropertyReader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;

@Log4j
@Component
public class AssemblyLogicalSystemReferenceFactory extends UnitFactory {

    private static final Logger AssemblyLogicalSystemReferenceFactory_LOGGER = Logger.getLogger(AssemblyLogicalSystemReferenceFactory.class);

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        RFLVPMItem rflvpmItem = super.getLogicalData(item);
        rflvpmItem.setLogDevicePosition(xmlAttributeGenerator.getXmlAttribute(item.getCode()));
        return rflvpmItem;
    }
    
    @Override
    protected String getItemType(EquipmentChild item) {
        return item.getThreeDxObjectType();
    }
    
    @Override
    protected String getChildItemType(EquipmentChild childItem) {
        return childItem.getThreeDxObjectType();
    }

    @Override
    protected HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("LogicalAssembly").get(type).get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under LogicalAssembly in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }
    
    @Override
    protected HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("LogicalAssembly").get(type).get("relation");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under LogicalAssembly in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }
    
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
        return PropertyReader.getProperty("comos.assembly.logical.referefce.factory.type");
        
    }
    
    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.assembly.logical.referefce.factory.prefix");
        
    }
    
    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.assembly.logical.referefce.factory.level"));
    }
}
