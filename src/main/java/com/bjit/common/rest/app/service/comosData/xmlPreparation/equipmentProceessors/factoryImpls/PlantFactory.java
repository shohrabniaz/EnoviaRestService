package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.LogicalItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.EnoviaTypeSetterToConstantsFromMapper;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Log4j
@Component
@RequestScope
public class PlantFactory implements IXMLDataFactory, IComosLogicalXMLDataFactory, IComosItemTypeUtils {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    protected ComosRuntimeData comosRuntimeData;

    @Autowired
    protected XMLAttributeGenerator xmlAttributeGenerator;

    @Autowired
    protected IComosFactories comosFactories;

    @Autowired
    protected IFilenameGenerator filenameGenerator;

    @Autowired
    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
    EnoviaTypeSetterToConstantsFromMapper mapper;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    protected Long sequence;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        try {
            log.debug("Item type : " + item.getType() + "Item name : " + item.getId());

            HashMap<String, HashMap<String, String>> sourceDestinationData = getSourceDestinationData(item.getType());

            item.setSequence(sequence);

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(item.getSequence().toString());

            String enoviaType = sourceDestinationData.get("type").get(item.getType());
            rflvpmItem.setType(enoviaType);

            String name = item.getId();
            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(name));

            String logDevicePosition = item.getCode() + " - " + item.getType();
            rflvpmItem.setLogDevicePosition(xmlAttributeGenerator.getXmlAttribute(logDevicePosition));

            String description = PropertyReader.getProperty("comos.pipe.category.description");
            String title = Optional.ofNullable(item.getDescription()).orElse(
                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse(""));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(title));

            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(sourceDestinationData.get("discipline").get(item.getType())));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

            setRuntimeData(enoviaType, name, logDevicePosition, title);
            comosRuntimeDataBuilder.addDeliverable(enoviaType, name, title, logDevicePosition, item.getAttributes());

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    protected void setRuntimeData(String enoviaType, String externalId, String logDevicePosition, String title) {
        LogicalItem equipmentPlantData = comosRuntimeData.getEquipmentPlantData();
        equipmentPlantData.setType(enoviaType);
        equipmentPlantData.setName(externalId);
        equipmentPlantData.setTitle(title);
        equipmentPlantData.setLogDevicePosition(logDevicePosition);
    }

    protected HashMap<String, HashMap<String, String>> getSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get(type).get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under Plant in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parentItem, Long sequence) {
        try {
            this.sequence = sequence;

            RFLP rootRFLP = null;

            List<EquipmentChild> children = Optional.ofNullable(parentItem.getChilds()).orElse(new ArrayList<>());
            if (children.size() > 0) {
                String type = children.get(0).getType();
                IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(type);
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

            // As rootRFLP is outside of the lambda block that's why we have set another reference variable
            RFLP parentChildRFLP = rootRFLP;
            children.forEach((EquipmentChild child) -> {
                child.setIsAParentItem(Boolean.FALSE);

                String type = child.getType();
                IRFLPDataFactory rflpDataFactory = comosFactories.getRFLPDataFactoryMap().get(type);
                RFLP childRFLPData = rflpDataFactory.getRFLPData(parentItem, child, this.sequence);

                IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(type);
                this.sequence = comosItemTypeUtils.getCurrentSequence();
                // parentChildRFLP will be modified inside the xmlAttributeGenerator.getRflp method
                // As parentChildRFLP is an object type data then it's modification will be found everywhere
                xmlAttributeGenerator.getRflp(parentChildRFLP, childRFLPData);
            });

            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }


    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.plant.factory.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.plant.factory.prefix");
    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.plant.factory.level"));
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
//return Long.parseLong(PropertyReader.getProperty("comos.plant.factory.current.sequence"));
    }
}
