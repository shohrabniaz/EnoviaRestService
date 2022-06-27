package com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.exceptions.ComosXMLMapNotFoundException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.XMLAttributeGenerator;
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

import java.util.HashMap;
import java.util.Optional;

@Log4j
@Component
@RequestScope
public class HVACLineFactory implements IRFLPDataFactory, IComosItemTypeUtils, IComosLogicalXMLDataFactory, IComosLogicalInstanceDataFactory {

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
//    HVACLineConstants hvacLineConstants;

    @Autowired
    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
    EnoviaTypeSetterToConstantsFromMapper mapper;
    @Autowired
    @Qualifier("RFLVPMValidator")
    RFLVPMValidator rflvpmValidator;
    private Long sequence;

    @Autowired
    ComosRuntimeDataBuilder comosRuntimeDataBuilder;

    @Override
    public RFLVPMItem getLogicalData(EquipmentChild item) {
        try {
            String name = item.getId();
            log.debug("Item type : " + item.getThreeDxObjectType() + "Item name : " + name);

            HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(item.getThreeDxObjectType());

            item.setSequence(sequence);

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);
            rflvpmItem.setValue(item.getSequence().toString());
            String type = itemSourceDestinationData.get("type").get(item.getThreeDxObjectType());
            rflvpmItem.setType(type);
            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(name));
//            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(item.getCode()));
            String logDevicePosition = item.getCode();
            rflvpmItem.setLogDevicePosition(xmlAttributeGenerator.getXmlAttribute(logDevicePosition));


            String description = PropertyReader.getProperty("comos.HVAC.line.factory.description");
//            rflvpmItem.setvDescription(xmlAttributeGenerator.getXmlAttribute(Optional.ofNullable(item.getDescription()).orElse(
//                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse("")
//            )));
            String title = Optional.ofNullable(item.getDescription()).orElse(
                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse("")
            );
            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(title));



            rflvpmItem.setvOperationPressure(xmlAttributeGenerator.getAttribute("PRESSURE", "0N_m2"));
            rflvpmItem.setvOperatingTemperature(xmlAttributeGenerator.getAttribute("TEMPRTRE", "0Kdeg"));

            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(itemSourceDestinationData.get("discipline").get(item.getThreeDxObjectType())));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

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
            log.debug("Item type : " + childItem.getThreeDxObjectType() + "Item name : " + childItem.getId());

            HashMap<String, HashMap<String, String>> relationSourceDestinationData = getRelationSourceDestinationData(childItem.getThreeDxObjectType());

            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);
            rflvpmItem.setValue(sequence.toString());
            rflvpmItem.setType(relationSourceDestinationData.get("type").get(childItem.getThreeDxObjectType()));
            rflvpmItem.setMandatory(xmlAttributeGenerator.setInstanceItemsMandatoryAttribute(childItem.getId(), parentItem.getSequence().toString(), childItem.getSequence().toString()));
            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(relationSourceDestinationData.get("type").get(childItem.getThreeDxObjectType())));

            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);

            return rflvpmItem;
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
        try {
//        HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("EquipmentCategory").get("EquipmentCategory").get("item");
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("PipeCategory").get(type).get("item");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under PipeCategory in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    private HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
        try {
//        HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("EquipmentCategory").get("EquipmentCategory").get("relation");
            HashMap<String, HashMap<String, String>> itemAttributes = mapper.getConfigurableMap().get("PipeCategory").get(type).get("relation");
            return itemAttributes;
        } catch (NullPointerException exp) {
            String exceptionMessage = type + " has not been configured as a map under PipeCategory in the Comos XML map";
            log.error(exceptionMessage);
            throw new ComosXMLMapNotFoundException(exceptionMessage);
        } catch (Exception exp) {
            log.error(exp);
            throw exp;
        }
    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.HVACL.line.factory.type");
    }

    @Override
    public String getPrefix() {
//        return "C3";
//        return null;
        return PropertyReader.getProperty("comos.HVACL.line.factory.prefix");
    }

    @Override
    public Integer getLevel() {
//        return null;
        return Integer.parseInt(PropertyReader.getProperty("comos.HVACL.line.factory.level"));
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
