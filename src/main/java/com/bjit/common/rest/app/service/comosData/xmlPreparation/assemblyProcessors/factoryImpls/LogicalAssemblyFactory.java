package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.UnitFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IRFLPDataFactory;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log4j
@Component
@RequestScope
public class LogicalAssemblyFactory extends UnitFactory {
    @Override
    public HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parentLogicalAssemblyItem, Long sequence) {
        try {
            this.sequence = sequence;

            List<EquipmentChild> logicalAssemblysEquipmentList = Optional.ofNullable(parentLogicalAssemblyItem.getChilds()).orElse(new ArrayList<>());

            logicalAssemblysEquipmentList.forEach((EquipmentChild equipmentItem) -> {
                equipmentItem.setIsAParentItem(Boolean.FALSE);

                RFLP rootRFLP = null;
                String equipmentType = equipmentItem.getThreeDxObjectType();
                IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(PropertyReader.getProperty("comos.assembly.prefix") + equipmentType);
                if (comosItemTypeUtils == null) {
                    return;
                }

                String equipmentPrefix = comosItemTypeUtils.getPrefix();
                Integer equipmentLevel = comosItemTypeUtils.getLevel();

                String currentXMLFileName = filenameGenerator.generateFileName(levelWiseFileName, xmlStructureMap, equipmentLevel, equipmentPrefix, parentLogicalAssemblyItem);

                rootRFLP = xmlStructureMap.get(currentXMLFileName);

                if (!parentLogicalAssemblyItem.getIsAParentItem()) {
                    this.sequence++;
                    RFLVPMItem rootLogicalData = this.getLogicalData(parentLogicalAssemblyItem);
                    rootRFLP.getLogicalReference().getId().add(rootLogicalData);
                    parentLogicalAssemblyItem.setIsAParentItem(true);
                }

                IRFLPDataFactory rflpDataFactory = comosFactories.getRFLPDataFactoryMap().get(PropertyReader.getProperty("comos.assembly.prefix") + equipmentType);
                // As rootRFLP is outside of the lambda block that's why we have set another reference variable
                RFLP parentChildRFLP = rootRFLP;
                try {
//                    RFLP childRFLPData = rflpDataFactory.getRFLPData(parentLogicalAssemblyItem, equipmentItem, this.sequence);
                    RFLP childRFLPData = rflpDataFactory.getRFLPData(parentLogicalAssemblyItem, equipmentItem, this.sequence, currentXMLFileName);

                    this.sequence = comosItemTypeUtils.getCurrentSequence();
                    log.debug("Sequence data : " + this.sequence);
                    // parentChildRFLP will be modified inside the xmlAttributeGenerator.getRflp method
                    // As parentChildRFLP is an object type data then it's modification will be found everywhere
                    xmlAttributeGenerator.getRflp(parentChildRFLP, childRFLPData);
                } catch (Exception exp) {
                    log.error(exp);
                    throw exp;
                }
            });

            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.logical.assembly.factory.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.logical.assembly.factory.prefix");
    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.logical.assembly.factory.level"));
    }
}
