package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;

import com.bjit.common.rest.app.service.comosData.defaultdata.DefaultMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.*;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import java.util.Optional;

public class PojoModelBuilder {
//
//    private static Attribute getVName(String vName) {
//        Attribute vname = new Attribute();
//        vname.setType("String");
//        vname.setValue(vName);
//        return vname;
//    }
//
//    private static Attribute getRevision() {
//        Attribute revision = new Attribute();
//        revision.setType("String");
//        revision.setId("2");
//        revision.setValue(DefaultMap.REVISION);
//        return revision;
//    }
//
//    private static Attribute getVIsFlexible() {
//        Attribute vname = new Attribute();
//        vname.setType("Boolean");
//        vname.setValue("false");
//        return vname;
//    }
//
//    private static Attribute getOriginatedDate() {
//        Attribute owner = new Attribute();
//        owner.setType("Date");
//        owner.setValue(DefaultMap.ORIGINATED_DATE);
//        return owner;
//    }
//
//    private static Attribute getPolicy() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.POLICY);
//        return owner;
//    }
//
//    private static Attribute getModifiedDate() {
//        Attribute owner = new Attribute();
//        owner.setType("Date");
//        owner.setValue(DefaultMap.MODIFIED_DATE);
//        return owner;
//    }
//
//    private static Attribute getCurrent() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.CURRENT);
//        return owner;
//    }
//
//    private static Attribute getReservedBy() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.OWNER);
//        return owner;
//    }
//
//    private static Attribute getOwner() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.OWNER);
//        return owner;
//    }
//
//    private static Attribute getOrganization() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.ORGANIZATION);
//        return owner;
//    }
//
//    private static Attribute getProject() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.PROJECT);
//        return owner;
//    }
//
//    private static Attribute getFillingRatio() {
//        Attribute fillingRatio = new Attribute();
//        fillingRatio.setType("Real");
//        fillingRatio.setValue("0");
//        return fillingRatio;
//    }
//
//    private static Attribute getOperationPressure() {
//        Attribute fillingRatio = new Attribute();
//        fillingRatio.setType("PRESSURE");
//        fillingRatio.setValue("0N_m2");
//        return fillingRatio;
//    }
//
//    private static Attribute getOperatingTemperature() {
//        Attribute fillingRatio = new Attribute();
//        fillingRatio.setType("TEMPRTRE");
//        fillingRatio.setValue("0Kdeg");
//        return fillingRatio;
//    }
//
//    private static Attribute getAttributeDiscipline(String type) {
//        Attribute discipline = new Attribute();
//        discipline.setType("String");
//        discipline.setValue(getDiscipline(type));
//        return discipline;
//    }
//
//    private static Attribute getItemDiscipline(String type) {
//        Attribute discipline = new Attribute();
//        discipline.setType("String");
//        discipline.setValue(type);
//        return discipline;
//    }
//
//    private static Attribute getDescription(String description) {
//        Attribute descriptionAttr = new Attribute();
//        descriptionAttr.setType("String");
//        descriptionAttr.setValue(description);
//        return descriptionAttr;
//    }
//
//    private static String getType(String type) {
//        if (type.equalsIgnoreCase("plant")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("unit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("subunit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("equipment")) {
//            return DefaultMap.EQUIPMENT_TYPE;
//        } else if (type.equalsIgnoreCase("pipe")) {
//            return DefaultMap.PIPE_TYPE;
//        } else if (type.equalsIgnoreCase("valve")) {
//            return DefaultMap.VALVE_TYPE;
//        } else if (type.equalsIgnoreCase("plant_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("unit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("subunit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("equipment_instance")) {
//            return DefaultMap.EQUIPMENT_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("pipe_instance")) {
//            return DefaultMap.PIPE_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("valve_instance")) {
//            return DefaultMap.VALVE_INSTANCE_TYPE;
//        }
//        return null;
//    }
//
//    private static String getDiscipline(String type) {
//        if (type.equalsIgnoreCase("plant")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("unit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("subunit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("equipment")) {
//            return DefaultMap.EQUIPMENT_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("pipe")) {
//            return DefaultMap.PIPE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("valve")) {
//            return DefaultMap.VALVE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("plant_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("unit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("subunit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("equipment_instance")) {
//            return DefaultMap.EQUIPMENT_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("pipe_instance")) {
//            return DefaultMap.PIPE_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("valve_instance")) {
//            return DefaultMap.VALVE_INSTANCE_DISCIPLINE;
//        }
//        return null;
//    }
//
//    private static TextGraphicProperties getTextGraphicProperties() {
//        TextGraphicProperties textGraphicProperties = new TextGraphicProperties();
//        textGraphicProperties.setDisplayName(DefaultMap.DISPLAY_NAME);
//        return textGraphicProperties;
//    }
//
//    private static TextGraphicProperties getTextGraphicProperties(String instanceId) {
//        TextGraphicProperties textGraphicProperties = new TextGraphicProperties();
//        textGraphicProperties.setDisplayName(DefaultMap.DISPLAY_NAME);
//        textGraphicProperties.setInstanceId(instanceId);
//        return textGraphicProperties;
//    }
//
//    public static RFLVPMItem populateLogicalReferenceTypeData(String type, String code, String description, String name, String discipline, Long sequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(sequence.toString());
//        rflvpmItem.setType(getType(type));
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(name));
//        rflvpmItem.setOwner(getOwner());
//        Attribute vName = getVName(type);
//        String logicalItemsVName = code + " - " + vName.getValue();
//        vName.setValue(logicalItemsVName);
//        rflvpmItem.setvName(vName);
//        rflvpmItem.setvDescription(getDescription(description));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
////    public static RFLVPMItem populateEquipmentTypeData(String comosType, String code, String description, String name, String discipline, Long sequence) {
//    public static RFLVPMItem populateEquipmentTypeData(EquipmentChild child) {
//        String type = child.getThreeDxObjectType();
//        String description = Optional.ofNullable(child.getDescription()).orElse(
//                Optional.ofNullable(child.getAttributes().get("DescriptionInEnglish")).orElse("")
//        );
//
//
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(child.getSequence().toString());
//        rflvpmItem.setType(type);
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(child.getId()));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(child.getCode()));
//        rflvpmItem.setvDescription(getDescription(description));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setvDiscipline(getItemDiscipline(type));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateLogicalPipeTypeData(EquipmentChild child) {
//        String type = child.getThreeDxObjectType();
//        String description = Optional.ofNullable(child.getDescription()).orElse(
//                Optional.ofNullable(child.getAttributes().get("DescriptionInEnglish")).orElse("")
//        );
//
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(child.getSequence().toString());
//        rflvpmItem.setType(type);
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(child.getId()));
//        rflvpmItem.setvName(getVName(child.getCode()));
//        rflvpmItem.setvDescription(getDescription(description));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setvFillingRation(getFillingRatio());
//        rflvpmItem.setvOperationPressure(getOperationPressure());
//        rflvpmItem.setvOperatingTemperature(getOperatingTemperature());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setvDiscipline(getItemDiscipline(type));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateLogicalPipePortTypeData(String portType, String name, String discipline, Long ownerSequence, Long portSequence, Long instanceId) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(portSequence.toString());
//        rflvpmItem.setType(portType);
//        rflvpmItem.setMandatory(setPipeItemsPortMandatoryAttribute(name, ownerSequence.toString(), instanceId.toString()));
//        rflvpmItem.setvDiscipline(getItemDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//        rflvpmItem.setSecondTextGraphicProperties(getTextGraphicProperties(instanceId.toString()));
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateValveTypeData(EquipmentChild child) {
//        String type = child.getThreeDxObjectType();
//        String description = Optional.ofNullable(child.getDescription()).orElse(
//                Optional.ofNullable(child.getAttributes().get("DescriptionInEnglish")).orElse("")
//        );
//
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(child.getSequence().toString());
//        rflvpmItem.setType(type);
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(child.getId()));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(child.getCode()));
//        rflvpmItem.setvDescription(getDescription(description));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setvDiscipline(getItemDiscipline(type));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateLogicalInstanceTypeData(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setvName(getVName(trdSpaceType));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateEquipmentInstanceTypeData(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateLogicalPipeInstanceTypeData(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    public static RFLVPMItem populateLogicalValveInstanceTypeData(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    private static Mandatory setInstanceItemsMandatoryAttribute(String externalId, String ownerReference, String reference) {
//        Attribute plmExternalId = new Attribute();
//        plmExternalId.setType("String");
//        plmExternalId.setValue(externalId);
//
//        BoundingBox boundingBox = new BoundingBox();
//        boundingBox.setXMax(DefaultMap.INSTANCE_X_MAX);
//        boundingBox.setXMin(DefaultMap.INSTANCE_X_MIN);
//        boundingBox.setYMax(DefaultMap.INSTANCE_Y_MAX);
//        boundingBox.setYMin(DefaultMap.INSTANCE_Y_MIN);
//
//        Relation relation = new Relation();
//        relation.setOwnerReference(ownerReference);
//        relation.setReference(reference);
//
//        Mandatory mandatory = new Mandatory();
//        mandatory.setPlmExternalId(plmExternalId);
//        mandatory.setBoundingBox(boundingBox);
//        mandatory.setRelation(relation);
//
//        return mandatory;
//    }
//
//    private static Mandatory setReferenceItemsMandatoryAttribute(String externalId) {
//        Attribute plmExternalId = new Attribute();
//        plmExternalId.setType("String");
//        plmExternalId.setValue(externalId);
//
//        BoundingBox boundingBox = new BoundingBox();
//        boundingBox.setXMax(DefaultMap.REFERENCE_X_MAX);
//        boundingBox.setXMin(DefaultMap.REFERENCE_X_MIN);
//        boundingBox.setYMax(DefaultMap.REFERENCE_Y_MAX);
//        boundingBox.setYMin(DefaultMap.REFERENCE_Y_MIN);
//
//        Mandatory mandatory = new Mandatory();
//        mandatory.setPlmExternalId(plmExternalId);
//        mandatory.setBoundingBox(boundingBox);
//
//        return mandatory;
//    }
//
//    private static Mandatory setPipeItemsPortMandatoryAttribute(String externalId, String ownerReference, String instanceId) {
//        Attribute plmExternalId = new Attribute();
//        plmExternalId.setType("String");
//        plmExternalId.setValue(externalId);
//
//        Attribute vDirection = new Attribute();
//        vDirection.setType("V_LPPortDirectionEnum");
//        vDirection.setValue("InOut");
//
//        Relation relation = new Relation();
//        relation.setOwnerReference(ownerReference);
//
//        PositionSize firstPosition = new PositionSize();
//        firstPosition.setxAxis("0");
//        firstPosition.setyAxis("0");
//        firstPosition.setSize("4");
//
//        PositionSize secondPosition = new PositionSize();
//        secondPosition.setInstanceId(instanceId);
//        secondPosition.setxAxis("0");
//        secondPosition.setyAxis("0.695833");
//        secondPosition.setSize("4");
//
//        Mandatory mandatory = new Mandatory();
//        mandatory.setPlmExternalId(plmExternalId);
//        mandatory.setDirection(vDirection);
//        mandatory.setRelation(relation);
//        mandatory.setFirstPosition(firstPosition);
//        mandatory.setSecondPosition(secondPosition);
//
//        return mandatory;
//    }
}
