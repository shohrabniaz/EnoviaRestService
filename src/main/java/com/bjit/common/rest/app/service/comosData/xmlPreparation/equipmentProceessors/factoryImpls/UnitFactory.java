package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.constants.UnitConstants;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.EnoviaTypeSetterToConstantsFromMapper;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.validators.RFLVPMValidator;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Log4j
@Component
//@Scope("prototype")
@RequestScope
public class UnitFactory implements IXMLDataFactory, IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

    @Autowired
    protected IFilenameGenerator filenameGenerator;
    @Autowired
    protected XMLAttributeGenerator xmlAttributeGenerator;
    @Autowired
    protected IComosFactories comosFactories;
    @Autowired
    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
    protected EnoviaTypeSetterToConstantsFromMapper mapper;
    protected Long sequence;
    @Autowired
    BeanFactory beanFactory;
    @Autowired
    UnitConstants unitConstants;
    @Autowired
    @Qualifier("RFLVPMValidator")
    RFLVPMValidator rflvpmValidator;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        try {
            String type = getItemType(item);
            String name = item.getId();
            log.debug("Item type : " + type + "Item name : " + name);

            HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(type);

            item.setSequence(sequence);

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(item.getSequence().toString());
            String itemType = itemSourceDestinationData.get("type").get(type);
            rflvpmItem.setType(itemType);
            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(name));
//            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(item.getCode() + " - " + type));
            String logDevicePosition = item.getCode() + " - " + type;
            rflvpmItem.setLogDevicePosition(xmlAttributeGenerator.getXmlAttribute(logDevicePosition));

            String description = PropertyReader.getProperty("comos.unit.factory.description");
//            rflvpmItem.setvDescription(xmlAttributeGenerator.getXmlAttribute(Optional.ofNullable(item.getDescription()).orElse(
//                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse(""))));
            String title = Optional.ofNullable(item.getDescription()).orElse(
                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse(""));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(title));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(itemSourceDestinationData.get("discipline").get(type)));

            comosRuntimeDataBuilder.addDeliverable(itemType, name, title, logDevicePosition, item.getAttributes());

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    protected String getItemType(EquipmentChild item) {
        return item.getType();
    }

    @Override
    public RFLVPMItem getLogicalInstance(EquipmentChild parentItem, EquipmentChild childItem) {
        try {
            String type = getChildItemType(childItem);
            log.debug("Item type : " + type + "Item name : " + childItem.getId());
            HashMap<String, HashMap<String, String>> relationalSourceDestinationData = getRelationSourceDestinationData(type);


            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(sequence.toString());
            rflvpmItem.setType(relationalSourceDestinationData.get("type").get(type));
            rflvpmItem.setMandatory(xmlAttributeGenerator.setInstanceItemsMandatoryAttribute(childItem.getId(), parentItem.getSequence().toString(), childItem.getSequence().toString()));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute("System" + String.format("%06d", sequence)));
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(relationalSourceDestinationData.get("discipline").get(type)));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    protected String getChildItemType(EquipmentChild childItem) {
        return childItem.getType();
    }

    protected HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get(type).get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under Unit in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    protected HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get(type).get("relation");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under Unit in the Comos XML map";
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

            rootRFLP = prepareParentData(children, levelWiseFileName, xmlStructureMap, parentItem, rootRFLP);
            getChildrenItemAndRelationalData(rootRFLP, children, parentItem);

            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    protected RFLP prepareParentData(List<EquipmentChild> children, HashMap<String, String> levelWiseFileName, HashMap<String, RFLP> xmlStructureMap, EquipmentChild parentItem, RFLP rootRFLP) {
        if (!children.isEmpty()) {
            IComosItemTypeUtils comosItemTypeUtils = getiComosItemTypeUtils(children);
            String prefix = comosItemTypeUtils.getPrefix();
            Integer level = comosItemTypeUtils.getLevel();

            String currentXMLFileName = filenameGenerator.generateFileName(levelWiseFileName, xmlStructureMap, level, prefix, parentItem);

            rootRFLP = xmlStructureMap.get(currentXMLFileName);

            if (!parentItem.getIsAParentItem()) {
                RFLVPMItem rootLogicalData = this.getLogicalData(parentItem);
                rootRFLP.getLogicalReference().getId().add(rootLogicalData);
                parentItem.setIsAParentItem(true);
            }
        }
        return rootRFLP;
    }

    protected void getChildrenItemAndRelationalData(RFLP rootRFLP, List<EquipmentChild> children, EquipmentChild parentItem) {
        // As rootRFLP is outside of the lambda block that's why we have set another reference variable
        RFLP parentChildRFLP = rootRFLP;
        children.forEach((EquipmentChild child) -> {
            child.setIsAParentItem(Boolean.FALSE);

            String type = getItemType(child);
            IRFLPDataFactory rflpDataFactory = comosFactories.getRFLPDataFactoryMap().get(type);
            RFLP childRFLPData = rflpDataFactory.getRFLPData(parentItem, child, this.sequence);

            IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(type);
            this.sequence = comosItemTypeUtils.getCurrentSequence();
            // parentChildRFLP will be modified inside the xmlAttributeGenerator.getRflp method
            // As parentChildRFLP is an object type data then it's modification will be found everywhere
            xmlAttributeGenerator.getRflp(parentChildRFLP, childRFLPData);
        });
    }

    protected IComosItemTypeUtils getiComosItemTypeUtils(List<EquipmentChild> children) {
        String type = getItemType(children.get(0));
        IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(type);
        return comosItemTypeUtils;
    }

    @Override
    @Deprecated
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence) {
        try {
            RFLP rflp = xmlAttributeGenerator.getRflp();
            this.sequence = sequence;
            this.sequence++;
            RFLVPMItem logicalData = getLogicalData(childItem);
            rflp.getLogicalReference().getId().add(logicalData);

            this.sequence++;
            RFLVPMItem logicalInstance = getLogicalInstance(parentItem, childItem);
            rflp.getLogicalInstance().getId().add(logicalInstance);

            return rflp;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public RFLP getRFLPData(EquipmentChild parentItem, EquipmentChild childItem, Long sequence, String filename) {
        try {
            RFLP rflp = xmlAttributeGenerator.getRflp();
            HashMap<String, Long> idMap = rflvpmValidator.getUniqueChildItemMap().get(filename);
            if(Optional.ofNullable(idMap).isEmpty()){
                idMap = new HashMap<>();
                rflvpmValidator.getUniqueChildItemMap().put(filename,idMap);
            }

            if (!idMap.containsKey(childItem.getId())) {
                this.sequence = sequence;
                this.sequence++;
                RFLVPMItem logicalData = getLogicalData(childItem);
                rflp.getLogicalReference().getId().add(logicalData);

                idMap.put(childItem.getId(), this.sequence);
            }
            else {
                childItem.setSequence(idMap.get(childItem.getId()));
            }

            this.sequence++;
            RFLVPMItem logicalInstance = getLogicalInstance(parentItem, childItem);
            rflp.getLogicalInstance().getId().add(logicalInstance);

            return rflp;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.unit.factory.type");

    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.unit.factory.prefix");

    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.unit.factory.level"));
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
    }
}
