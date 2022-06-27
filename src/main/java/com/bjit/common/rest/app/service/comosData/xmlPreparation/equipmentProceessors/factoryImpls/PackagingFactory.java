package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryServices.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.ComosItems;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.EnoviaTypeSetterToConstantsFromMapper;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.mapper.Item;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.validators.RFLVPMValidator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j
@Component
@RequestScope
@Qualifier("PackagingFactory")
public class PackagingFactory implements IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

    @Autowired
    BeanFactory beanFactory;

//    @Autowired
//    IFilenameGenerator filenameGenerator;

    @Autowired
    XMLAttributeGenerator xmlAttributeGenerator;

//    @Autowired
//    IComosFactories comosFactories;
//
//    @Autowired
//    SubUnitConstants subUnitConstants;

    @Autowired
    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
    EnoviaTypeSetterToConstantsFromMapper mapper;

    @Autowired
    @Qualifier("RFLVPMValidator")
    RFLVPMValidator rflvpmValidator;

    private Long sequence;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        try {
            log.debug("Item type : " + item.getThreeDxObjectType() + "Item name : " + item.getId());

            String categoryName = getCategoryName(item.getType());

            HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(item.getType());

            item.setSequence(sequence);

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);

            rflvpmItem.setValue(item.getSequence().toString());
            HashMap<String, String> itemTypeMap = itemSourceDestinationData.get("type");
            Set<String> itemTypeMapKeySet = itemTypeMap.keySet();
            rflvpmItem.setType(itemTypeMap.get(itemTypeMapKeySet.toArray()[0]));
            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(item.getId()));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(categoryName));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);
            HashMap<String, String> itemDisciplineMap = itemSourceDestinationData.get("discipline");
            Set<String> itemDisciplineMapKeySet = itemDisciplineMap.keySet();
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(itemSourceDestinationData.get("discipline").get(itemDisciplineMapKeySet.toArray()[0])));

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
            rflvpmItem.setType(relationSourceDestinationData.get("type").get("RFLVPMLogicalSystemInstance"));
            rflvpmItem.setMandatory(xmlAttributeGenerator.setInstanceItemsMandatoryAttribute(childItem.getId(), parentItem.getSequence().toString(), childItem.getSequence().toString()));
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute("System" + String.format("%06d", sequence)));
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(relationSourceDestinationData.get("discipline").get("RFLVPMLogicalSystemInstance")));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private String getCategoryName(String categoryType) {
        List<ComosItems> comosItems = mapper.getMapper().getItemList();
        for (ComosItems comosItem : comosItems) {
            if (comosItem.getType().equalsIgnoreCase(categoryType)) {
                List<Item> itemList = comosItem.getItemList();
                for (Item item : itemList) {
                    String packageName = item.getPackageName();
                    if (packageName != null && !packageName.isEmpty()) {
                        return packageName;
                    }
                }
            }
        }
        throw new NullPointerException("Package name has not been configured for '" + categoryType + "'");
    }

    private HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get("Package").get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under ValveCategory in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
        try {
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get(type).get("Package").get("relation");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under Package in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public String getType() {
        return "Package";
    }

    @Override
    public String getPrefix() {
        return "C";
    }

    @Override
    public Integer getLevel() {
        return 3;
    }

    @Override
    public Long getCurrentSequence() {
        return this.sequence;
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
