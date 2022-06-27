package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosEnovia.ComosRuntimeDataBuilder;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.ComosItems;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.EnoviaTypeSetterToConstantsFromMapper;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.Item;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.validators.RFLVPMValidator;
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
public class SubUnitFactory implements IXMLDataFactory, IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    IFilenameGenerator filenameGenerator;

    @Autowired
    XMLAttributeGenerator xmlAttributeGenerator;

    @Autowired
    IComosFactories comosFactories;

//    @Autowired
//    SubUnitConstants subUnitConstants;

    @Autowired
    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
    EnoviaTypeSetterToConstantsFromMapper mapper;

    @Autowired
    @Qualifier("RFLVPMValidator")
    RFLVPMValidator rflvpmValidator;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    //    @Autowired
//    @Qualifier("PackagingFactory")
//    IRFLPDataFactory packagingFactory;
    private Long sequence;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        try {
            String name = item.getId();
            log.debug("Item type : " + item.getType() + "Item name : " + name);

            HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(item.getType());

            item.setSequence(sequence);

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(item.getSequence().toString());
            String type = itemSourceDestinationData.get("type").get(item.getType());
            rflvpmItem.setType(type);
            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(name));
//            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(item.getCode() + " - " + item.getType()));
            String logDevicePosition = item.getCode() + " - " + item.getType();
            rflvpmItem.setLogDevicePosition(xmlAttributeGenerator.getXmlAttribute(logDevicePosition));

            String description = PropertyReader.getProperty("comos.subunit.factory.description");
//            rflvpmItem.setvDescription(xmlAttributeGenerator.getXmlAttribute(Optional.ofNullable(item.getDescription()).orElse(
//                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse(""))));
            String title = Optional.ofNullable(item.getDescription()).orElse(
                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse(""));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(title));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(itemSourceDestinationData.get("discipline").get(item.getType())));

            comosRuntimeDataBuilder.addDeliverable(type, name, title, logDevicePosition, item.getAttributes());

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public RFLVPMItem getLogicalInstance(EquipmentChild parentItem, EquipmentChild childItem) {
        try {
            log.debug("Item type : " + childItem.getType() + "Item name : " + childItem.getId());

            HashMap<String, HashMap<String, String>> relationSourceDestinationData = getRelationSourceDestinationData(childItem.getType());

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(sequence.toString());
//            rflvpmItem.setType(subUnitConstants.INSTANCE_TYPE);
            rflvpmItem.setType(relationSourceDestinationData.get("type").get(childItem.getType()));
            rflvpmItem.setMandatory(xmlAttributeGenerator.setInstanceItemsMandatoryAttribute(childItem.getId(), parentItem.getSequence().toString(), childItem.getSequence().toString()));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute("System" + String.format("%06d", sequence)));
//            rflvpmItem.setTextGraphicProperties(xmlAttributeGenerator.getTextGraphicProperties(DefaultConstants.DISPLAY_NAME));
//            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(subUnitConstants.INSTANCE_DISCIPLINE));
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(relationSourceDestinationData.get("discipline").get(childItem.getType())));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get(type).get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under SubUnit in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get(type).get("relation");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under SubUnit in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.subunit.factory.type");
    }

    @Override
    public String getPrefix() {
        return PropertyReader.getProperty("comos.subunit.factory.prefix");
    }

    @Override
    public Integer getLevel() {
        return Integer.parseInt(PropertyReader.getProperty("comos.subunit.factory.level"));
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
    }

    @Override
    public HashMap<String, RFLP> getXMLData(HashMap<String, RFLP> xmlStructureMap, HashMap<String, String> levelWiseFileName, EquipmentChild parentItem, Long sequence) {
        try {
            this.sequence = sequence;

            List<EquipmentChild> subUnitChildren = Optional.ofNullable(parentItem.getChilds()).orElse(new ArrayList<>());

            subUnitChildren.forEach((EquipmentChild categoryItem) -> {
                categoryItem.setIsAParentItem(Boolean.FALSE);

                RFLP rootRFLP = null;
                List<EquipmentChild> catChildren = Optional.ofNullable(categoryItem.getChilds()).orElse(new ArrayList<>());
                if (!catChildren.isEmpty()) {
                    String catType = categoryItem.getType();
                    IComosItemTypeUtils comosItemTypeUtils = comosFactories.getComosItemTypeUtilsMap().get(catType);
                    if (comosItemTypeUtils == null) {
                        return;
                    }

                    String catPrefix = comosItemTypeUtils.getPrefix();
                    Integer catLevel = comosItemTypeUtils.getLevel();

                    String currentXMLFileName = filenameGenerator.generateFileName(levelWiseFileName, xmlStructureMap, catLevel, catPrefix, parentItem);

                    rootRFLP = xmlStructureMap.get(currentXMLFileName);

                    if (!parentItem.getIsAParentItem()) {
                        RFLVPMItem rootLogicalData = this.getLogicalData(parentItem);
                        rootRFLP.getLogicalReference().getId().add(rootLogicalData);
                        parentItem.setIsAParentItem(true);
                    }

                    RFLP catRFLPData = comosFactories.getRFLPDataFactoryMap().get("Package").getRFLPData(parentItem, categoryItem, this.sequence);
                    this.sequence = comosFactories.getComosItemTypeUtilsMap().get("Package").getCurrentSequence();
                    this.sequence++;
                    xmlAttributeGenerator.getRflp(rootRFLP, catRFLPData);

                    IRFLPDataFactory rflpDataFactory = comosFactories.getRFLPDataFactoryMap().get(catType);
                    // As rootRFLP is outside of the lambda block that's why we have set another reference variable
                    RFLP parentChildRFLP = rootRFLP;
                    catChildren.forEach((EquipmentChild catChild) -> {
                        try {
                            //                        RFLP childRFLPData = rflpDataFactory.getRFLPData(parentItem, catChild, this.sequence);
                            log.debug("Item type : " + categoryItem.getType() + "Item name : " + categoryItem.getId());
                            log.debug("Item type : " + catChild.getThreeDxObjectType() + "Item name : " + catChild.getId());

//                            if(catChild.getId().equalsIgnoreCase("A48WSHB0C9")){
//                                System.out.println("");
//                            }
                            RFLP childRFLPData = rflpDataFactory.getRFLPData(categoryItem, catChild, this.sequence);

                            this.sequence = comosItemTypeUtils.getCurrentSequence();
                            // parentChildRFLP will be modified inside the xmlAttributeGenerator.getRflp method
                            // As parentChildRFLP is an object type data then it's modification will be found everywhere
                            xmlAttributeGenerator.getRflp(parentChildRFLP, childRFLPData);
                        } catch (Exception exp) {
                            log.error(exp);
                            throw exp;
                        }

                    });
                }
            });

            return xmlStructureMap;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private String getPackageName(String catType) {
        List<ComosItems> comosItemsList = mapper.getMapper().getItemList();
        for (ComosItems comosItems : comosItemsList) {
            if (comosItems.getType().equalsIgnoreCase(catType)) {
                List<Item> itemList = comosItems.getItemList();
                for (Item item : itemList) {
                    return item.getPackageName();
                }
            }
        }
        return null;
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
}
