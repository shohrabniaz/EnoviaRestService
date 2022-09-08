package com.bjit.common.rest.app.service.comosData.xmlPreparation.assemblyProcessors.factoryImpls;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.equipmentProceessors.factoryImpls.ValveCategoryFactory;
import com.bjit.ewc18x.utils.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


@Log4j
@Component
//@Scope("prototype")
@RequestScope
public class ValveFactory extends ValveCategoryFactory {

//    @Autowired
//    BeanFactory beanFactory;
//
//    @Autowired
//    XMLAttributeGenerator xmlAttributeGenerator;
//
//    @Autowired
//    IFilenameGenerator filenameGenerator;
//
//    @Autowired
//    IComosFactories comosFactories;
//
//    @Autowired
//    ValveCategoryConstants valveCategoryConstants;
//
//    @Autowired
//    @Qualifier("EnoviaTypeSetterToConstantsFromMapper")
//    EnoviaTypeSetterToConstantsFromMapper mapper;
//
//    protected Long sequence;

//    @Override
//    public RFLVPMItem getLogicalData(Child item) {
//        try {
//            log.debug("Item type : " + item.getThreeDxObjectType() + "Item name : " + item.getId());
//            HashMap<String, HashMap<String, String>> itemSourceDestinationData = getItemSourceDestinationData(item.getThreeDxObjectType());
//
//            item.setSequence(sequence);
//
//            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);
//            rflvpmItem.setValue(item.getSequence().toString());
//            rflvpmItem.setType(itemSourceDestinationData.get("type").get(item.getThreeDxObjectType()));
//            rflvpmItem.setMandatory(xmlAttributeGenerator.setReferenceItemsMandatoryAttribute(item.getId()));
//            rflvpmItem.setvName(xmlAttributeGenerator.getXmlAttribute(item.getCode()));
//            String description = PropertyReader.getProperty("comos.valve.category.description");
//            rflvpmItem.setvDescription(xmlAttributeGenerator.getXmlAttribute(Optional.ofNullable(item.getDescription()).orElse(
//                    Optional.ofNullable(Optional.ofNullable(item.getAttributes()).orElse(new HashMap<>()).get(description)).orElse("")
//            )));
//
//            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(itemSourceDestinationData.get("discipline").get(item.getThreeDxObjectType())));
//
//            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);
//
//            return rflvpmItem;
//        } catch (Exception exp) {
//            log.error(exp.getMessage());
//            throw exp;
//        }
//    }
//
//    @Override
//    public RFLVPMItem getLogicalInstance(Child parentItem, Child childItem) {
//        try {
//            log.debug("Item type : " + childItem.getThreeDxObjectType() + "Item name : " + childItem.getId());
//            HashMap<String, HashMap<String, String>> relationSourceDestinationData = getRelationSourceDestinationData(childItem.getThreeDxObjectType());
//
//            parentItem.setIsAParentItem(true);
//
//            RFLVPMItem rflvpmItem = beanFactory.getBean(RFLVPMItem.class);
//            rflvpmItem.setValue(sequence.toString());
//            rflvpmItem.setType(relationSourceDestinationData.get("type").get(childItem.getThreeDxObjectType()));
//            rflvpmItem.setMandatory(xmlAttributeGenerator.setInstanceItemsMandatoryAttribute(childItem.getId(), parentItem.getSequence().toString(), childItem.getSequence().toString()));
//            rflvpmItem.setvDiscipline(xmlAttributeGenerator.getXmlAttribute(relationSourceDestinationData.get("discipline").get(childItem.getThreeDxObjectType())));
//            rflvpmItem = xmlAttributeGenerator.getCommonAttributes(rflvpmItem);
//
//            return rflvpmItem;
//        } catch (Exception exp) {
//            log.error(exp.getMessage());
//            throw exp;
//        }
//    }
//
//    private HashMap<String, HashMap<String, String>> getItemSourceDestinationData(String type) {
//        try {
//            return mapper.getConfigurableMap().get("ValveCategory").get(type).get("item");
//        } catch (NullPointerException exp) {
//            String exceptionMessage = type + " has not been configured as a map under ValveCategory in the Comos XML map";
//            log.error(exceptionMessage);
//            throw new ComosXMLMapNotFoundException(exceptionMessage);
//        } catch (Exception exp) {
//            log.error(exp);
//            throw exp;
//        }
//    }
//
//    private HashMap<String, HashMap<String, String>> getRelationSourceDestinationData(String type) {
//        try {
//            return mapper.getConfigurableMap().get("ValveCategory").get(type).get("relation");
//        } catch (NullPointerException exp) {
//            String exceptionMessage = type + " has not been configured as a map under ValveCategory in the Comos XML map";
//            log.error(exceptionMessage);
//            throw new ComosXMLMapNotFoundException(exceptionMessage);
//        } catch (Exception exp) {
//            log.error(exp);
//            throw exp;
//        }
//    }

    @Override
    public String getType() {
        return PropertyReader.getProperty("comos.assembly.valve.factory.type");
    }

    @Override
    public String getPrefix() {
//        return "C2";
        return PropertyReader.getProperty("comos.assembly.valve.factory.prefix");
    }

    @Override
    public Integer getLevel() {

        return Integer.parseInt(PropertyReader.getProperty("comos.assembly.valve.factory.level"));

    }

//    @Override
//    public Long getCurrentSequence() {
//        return this.sequence;
//    }

//    @Override
//    public RFLP getRFLPData(Child parentItem, Child childItem, Long sequence) {
//        try {
//            RFLP rflp = xmlAttributeGenerator.getRflp();
//            this.sequence = sequence;
//            this.sequence++;
//            RFLVPMItem logicalData = getLogicalData(childItem);
//            rflp.getLogicalReference().getId().add(logicalData);
//
//            this.sequence++;
//            RFLVPMItem logicalInstance = getLogicalInstance(parentItem, childItem);
//            rflp.getLogicalInstance().getId().add(logicalInstance);
//
//            return rflp;
//        } catch (Exception exp) {
//            log.error(exp.getMessage());
//            throw exp;
//        }
//    }
}
