/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.PlantFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IComosItemTypeUtils;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.IRFLPDataFactory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Omour Faruq
 */
@Log4j
@Component
@RequestScope
public class AssemblyPlantFactory extends PlantFactory {
    @Override
    public HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parentItem, Long sequence) {
        try {
            this.sequence = sequence;
            log.debug("Sequence data : " + this.sequence);
            RFLP rootRFLP = null;

            List<EquipmentChild> children = Optional.ofNullable(parentItem.getChilds()).orElse(new ArrayList<>());
            if (children.size() > 0) {
                String type = children.get(0).getType();
                IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(PropertyReader.getProperty("comos.assembly.prefix") + type);

                if (Optional.ofNullable(comosItemTypeUtils).isPresent()) {
                    String prefix = comosItemTypeUtils.getPrefix();
                    Integer level = comosItemTypeUtils.getLevel();

                    String currentXMLFileName = filenameGenerator.generateFileName(levelWiseFileName, xmlStructureMap, level, prefix, parentItem);

                    rootRFLP = xmlStructureMap.get(currentXMLFileName);

                    if (!Optional.ofNullable(parentItem.getIsAParentItem()).orElse(Boolean.FALSE)) {
                        RFLVPMItem rootLogicalData = this.getLogicalData(parentItem);
                        rootRFLP.getLogicalReference().getId().add(rootLogicalData);
                        parentItem.setIsAParentItem(true);
                    }
                }
            }

            // As rootRFLP is outside of the lambda block that's why we have set another reference variable
            RFLP parentChildRFLP = rootRFLP;
            children.forEach((EquipmentChild child) -> {
                child.setIsAParentItem(Boolean.FALSE);

                String type = child.getType();
                IRFLPDataFactory rflpDataFactory = comosFactories.getRFLPDataFactoryMap().get(PropertyReader.getProperty("comos.assembly.prefix") + type);

                Optional.ofNullable(rflpDataFactory).ifPresentOrElse((rflpDataFactoryObject) -> {
                    RFLP childRFLPData = rflpDataFactory.getRFLPData(parentItem, child, this.sequence);

                    IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(PropertyReader.getProperty("comos.assembly.prefix") + type);
                    this.sequence = comosItemTypeUtils.getCurrentSequence();
                    // parentChildRFLP will be modified inside the xmlAttributeGenerator.getRflp method
                    // As parentChildRFLP is an object type data then it's modification will be found everywhere
                    xmlAttributeGenerator.getRflp(parentChildRFLP, childRFLPData);
                }, () -> {
                    return;
                });
            });

            log.debug("Sequence data : " + this.sequence);
            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    protected void setRuntimeData(String enoviaType, String externalId, String logDevicePosition, String title) {
        comosRuntimeData.getAssemblyPlantData().setType(enoviaType);
        comosRuntimeData.getAssemblyPlantData().setName(externalId);
        comosRuntimeData.getAssemblyPlantData().setTitle(title);
        comosRuntimeData.getAssemblyPlantData().setLogDevicePosition(logDevicePosition);
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.ass.plant.factory.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.ass.plant.factory.prefix");
    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.ass.plant.factory.level"));
    }
}
