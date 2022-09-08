package com.bjit.common.rest.app.service.comosData.xmlPreparation.builder;

import com.bjit.common.rest.app.service.comosData.defaultdata.DefaultMap;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalReference;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Attribute;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Mandatory;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalInstance;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.BoundingBox;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.Relation;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.TextGraphicProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComosXMLBuilder {
//    Long sequence = 1l;
//    Integer equipmentSequencer = 0;
//    Integer counter = 0;
//    List<RFLVPMItem> logicalReferencesList = null;
//    List<RFLVPMItem> logicalInstanceList = null;
//
//    @Autowired
//    Environment env;
//
////    public HashMap<String, RFLP> prepareComosXML(ComosServiceResponse serviceResponse){
////        HashMap<String, RFLP> rflpMap = new HashMap<>();
////        RFLP rflp = new RFLP();
////        Child comosModel = serviceResponse.getData().getComosModel();
////        String plantName = comosModel.getId();
////        rflpMap.put(plantName, rflp);
////
////        LogicalReference logicalReference = new LogicalReference();
////        rflp.setLogicalReference(logicalReference);
////        List<RFLVPMItem> logicalReferencesList = new ArrayList<>();
////        logicalReference.setId(logicalReferencesList);
////
////        /**
////         * Plant data gatherer
////         */
////        RFLVPMItem plantModel = populateComosXMLPojoModel(comosModel.getType(), plantName, comosModel.getType(), sequence);
////        logicalReferencesList.add(plantModel);
////
////        LogicalInstance logicalInstance = new LogicalInstance();
////        rflp.setLogicalInstance(logicalInstance);
////        List<RFLVPMItem> logicalInstanceList = new ArrayList<>();
////        logicalInstance.setId(logicalInstanceList);
////
////        populatePOJOModel(comosModel, logicalReferencesList, logicalInstanceList);
////
////        return rflpMap;
////    }
////
////    private void populatePOJOModel(Child comosModel, List<RFLVPMItem> logicalReferencesList, List<RFLVPMItem> logicalInstanceList){
////        Long plantSequence = sequence;
////
////        /**
////         * Unit data gatherer
////         */
////        comosModel.getChilds().forEach((Child unit) -> {
////            /**
////             * Reference type data
////             */
////            Long unitSequence = ++sequence;
////            RFLVPMItem unitModel = populateComosXMLPojoModel(unit.getType(), unit.getId(), unit.getType(), unitSequence);
////            logicalReferencesList.add(unitModel);
////
////            setInstanceTypeData(unit, logicalInstanceList, "System", plantSequence, unitSequence, ++sequence);
////            /**
////             * Subunit data gatherer
////             */
////            unit.getChilds().forEach((Child subunit) -> {
////                /**
////                 * Reference type data
////                 */
////                Long subunitSequence = ++sequence;
////                RFLVPMItem subunitModel = populateComosXMLPojoModel(subunit.getType(), subunit.getId(), subunit.getType(), subunitSequence);
////                logicalReferencesList.add(subunitModel);
////
////                setInstanceTypeData(subunit, logicalInstanceList, "System", unitSequence, subunitSequence, ++sequence);
////                /**
////                 * Category data gatherer
////                 */
////                subunit.getChilds().forEach((Child category) -> {
////                    if (category.getType().equalsIgnoreCase("EquipmentCategory")) {
////                        equipmentSequencer = 0;
////                        /**
////                         * EquipmentDataGatherer
////                         */
////                        category.getChilds().forEach((Child equipment) -> {
////                            /**
////                             * Equipment type data
////                             */
////                            Long equipmentSequence = ++sequence;
////                            RFLVPMItem equipmentModel = populateComosXMLPojoModel(equipment.getType(), "Equipment" + ++equipmentSequencer, equipment.getId(), equipment.getType(), equipmentSequence);
////                            logicalReferencesList.add(equipmentModel);
////
////                            setInstanceTypeData(equipment, logicalInstanceList, "Equipment", subunitSequence, equipmentSequence, ++sequence);
////                        });
////                    }
////                });
////            });
////        });
////    }
//
//    public HashMap<String, RFLP> prepareComosXML(EquipmentServiceResponse serviceResponse){
//
//
//        EquipmentChild comosModel = serviceResponse.getData().getComosModel();
//
//        HashMap<String, RFLP> rflpMap = populatePOJOModel(comosModel);
//
//        return rflpMap;
//    }
//
//    private RFLP preparePlantData(EquipmentChild comosModel, List<RFLVPMItem> logicalReferencesList, List<RFLVPMItem> logicalInstanceList) {
//        RFLP rflp = new RFLP();
//        LogicalReference logicalReference = new LogicalReference();
//        LogicalInstance logicalInstance = new LogicalInstance();
//
//        String plantName = comosModel.getId();
//
//        rflp.setLogicalReference(logicalReference);
//
//        logicalReference.setId(logicalReferencesList);
//
//        /**
//         * Plant data gatherer
//         */
//        RFLVPMItem plantModel = populateComosXMLPojoModel(comosModel.getType(), plantName, comosModel.getType(), sequence);
//        logicalReferencesList.add(plantModel);
//
//        rflp.setLogicalInstance(logicalInstance);
//        logicalInstance.setId(logicalInstanceList);
//
//        return rflp;
//    }
//
//    private HashMap<String, RFLP> populatePOJOModel(EquipmentChild comosModel){
//        Long plantSequence = sequence;
//        HashMap<String, RFLP> rflpMap = new HashMap<>();
//
//        /**
//         * Unit data gatherer
//         */
//        comosModel.getChilds().forEach((EquipmentChild unit) -> {
//            if(counter==0){
//                logicalReferencesList = new ArrayList<>();
//                logicalInstanceList = new ArrayList<>();
//                RFLP rflp = preparePlantData(comosModel, logicalReferencesList, logicalInstanceList);
//                rflpMap.put(unit.getId(), rflp);
//            }
//            counter = counter + 1;
//            counter = counter < Integer.parseInt(env.getProperty("comos.number.of.units.in.an.xml.file")) ? counter : 0;
//
//            /**
//             * Reference type data
//             */
//            Long unitSequence = ++sequence;
//            RFLVPMItem unitModel = populateComosXMLPojoModel(unit.getType(), unit.getId(), unit.getType(), unitSequence);
//            logicalReferencesList.add(unitModel);
//
//            setInstanceTypeData(unit, logicalInstanceList, "System", plantSequence, unitSequence, ++sequence);
//            /**
//             * Subunit data gatherer
//             */
//            unit.getChilds().forEach((EquipmentChild subunit) -> {
//                /**
//                 * Reference type data
//                 */
//                Long subunitSequence = ++sequence;
//                RFLVPMItem subunitModel = populateComosXMLPojoModel(subunit.getType(), subunit.getId(), subunit.getType(), subunitSequence);
//                logicalReferencesList.add(subunitModel);
//
//                setInstanceTypeData(subunit, logicalInstanceList, "System", unitSequence, subunitSequence, ++sequence);
//                /**
//                 * Category data gatherer
//                 */
//                subunit.getChilds().forEach((EquipmentChild category) -> {
//                    if (category.getType().equalsIgnoreCase("EquipmentCategory")) {
//                        equipmentSequencer = 0;
//                        /**
//                         * EquipmentDataGatherer
//                         */
//                        category.getChilds().forEach((EquipmentChild equipment) -> {
//                            /**
//                             * Equipment type data
//                             */
//                            Long equipmentSequence = ++sequence;
//                            RFLVPMItem equipmentModel = populateComosXMLPojoModel(equipment.getType(), "Equipment" + ++equipmentSequencer, equipment.getId(), equipment.getType(), equipmentSequence);
//                            logicalReferencesList.add(equipmentModel);
//
//                            setInstanceTypeData(equipment, logicalInstanceList, "Equipment", subunitSequence, equipmentSequence, ++sequence);
//                        });
//                    }
//                });
//            });
//        });
//
//        return rflpMap;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    private void setInstanceTypeData(EquipmentChild child, List<RFLVPMItem> logicalInstanceList, String comosTypeData, Long ownerSequence, Long childSequence, Long vNameSequence){
//        /**
//         * Instance type data
//         */
//        String threeDSpaceTypeData = comosTypeData + String.format("%06d", vNameSequence) + DefaultMap.REVISION;
//        String equipmentInstanceType = child.getType() + "_instance";
//        Long instanceSequence = ++sequence;
//        RFLVPMItem equipmentInstance = populateComosXMLPojoModel(equipmentInstanceType, threeDSpaceTypeData, child.getId(), equipmentInstanceType, ownerSequence, childSequence, instanceSequence);
//        logicalInstanceList.add(equipmentInstance);
//    }
//
//    private RFLVPMItem populateComosXMLPojoModel(String type, String name, String discipline, Long sequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(sequence.toString());
//        rflvpmItem.setType(getType(type));
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(name));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(type));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setOriginated(getOriginatedDate());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setModified(getModifiedDate());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setReservedBy(getReservedBy());
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setOrganization(getOrganization());
//        rflvpmItem.setProject(getProject());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    private RFLVPMItem populateComosXMLPojoModel(String comosType, String trdSpaceType, String name, String discipline, Long sequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(sequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setReferenceItemsMandatoryAttribute(name));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(trdSpaceType));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setOriginated(getOriginatedDate());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setModified(getModifiedDate());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setReservedBy(getReservedBy());
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setOrganization(getOrganization());
//        rflvpmItem.setProject(getProject());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    private RFLVPMItem populateComosXMLPojoModel(String type, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(type));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(type));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setOriginated(getOriginatedDate());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setModified(getModifiedDate());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setReservedBy(getReservedBy());
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setOrganization(getOrganization());
//        rflvpmItem.setProject(getProject());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//    private RFLVPMItem populateComosXMLPojoModel(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
//        RFLVPMItem rflvpmItem = new RFLVPMItem();
//        rflvpmItem.setValue(instanceSequence.toString());
//        rflvpmItem.setType(getType(comosType));
//        rflvpmItem.setMandatory(setInstanceItemsMandatoryAttribute(name, ownerSequence.toString(), sequence.toString()));
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setvName(getVName(trdSpaceType));
//        rflvpmItem.setRevision(getRevision());
//        rflvpmItem.setOriginated(getOriginatedDate());
//        rflvpmItem.setPolicy(getPolicy());
//        rflvpmItem.setModified(getModifiedDate());
//        rflvpmItem.setCurrent(getCurrent());
//        rflvpmItem.setReservedBy(getReservedBy());
//        rflvpmItem.setOwner(getOwner());
//        rflvpmItem.setOrganization(getOrganization());
//        rflvpmItem.setProject(getProject());
//        rflvpmItem.setvDiscipline(getAttributeDiscipline(discipline));
//        rflvpmItem.setTextGraphicProperties(getTextGraphicProperties());
//
//        return rflvpmItem;
//    }
//
//
//
//
//
//
//    private String getType(String type) {
//        if (type.equalsIgnoreCase("plant")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("unit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("subunit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_TYPE;
//        } else if (type.equalsIgnoreCase("deviceposition")) {
//            return DefaultMap.EQUIPMENT_TYPE;
//        } else if (type.equalsIgnoreCase("plant_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("unit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("subunit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_TYPE;
//        } else if (type.equalsIgnoreCase("deviceposition_instance")) {
//            return DefaultMap.EQUIPMENT_INSTANCE_TYPE;
//        }
//        return null;
//    }
//
//    private String getDiscipline(String type) {
//        if (type.equalsIgnoreCase("plant")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("unit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("subunit")) {
//            return DefaultMap.LOGICAL_SYSTEM_REFERENCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("deviceposition")) {
//            return DefaultMap.EQUIPMENT_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("plant_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("unit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("subunit_instance")) {
//            return DefaultMap.LOGICAL_SYSTEM_INSTANCE_DISCIPLINE;
//        } else if (type.equalsIgnoreCase("deviceposition_instance")) {
//            return DefaultMap.EQUIPMENT_INSTANCE_DISCIPLINE;
//        }
//        return null;
//    }
//
//    private Mandatory setReferenceItemsMandatoryAttribute(String externalId) {
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
//    private Mandatory setInstanceItemsMandatoryAttribute(String externalId, String ownerReference, String reference) {
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
//    private Attribute getVName(String vName) {
//        Attribute vname = new Attribute();
//        vname.setType("String");
//        vname.setValue(vName);
//        return vname;
//    }
//
//    private Attribute getRevision() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.REVISION);
//        return owner;
//    }
//
//    private Attribute getOriginatedDate() {
//        Attribute owner = new Attribute();
//        owner.setType("Date");
//        owner.setValue(DefaultMap.ORIGINATED_DATE);
//        return owner;
//    }
//
//    private Attribute getPolicy() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.POLICY);
//        return owner;
//    }
//
//    private Attribute getModifiedDate() {
//        Attribute owner = new Attribute();
//        owner.setType("Date");
//        owner.setValue(DefaultMap.MODIFIED_DATE);
//        return owner;
//    }
//
//    private Attribute getCurrent() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.CURRENT);
//        return owner;
//    }
//
//    private Attribute getReservedBy() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.OWNER);
//        return owner;
//    }
//
//    private Attribute getOwner() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.OWNER);
//        return owner;
//    }
//
//    private Attribute getOrganization() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.ORGANIZATION);
//        return owner;
//    }
//
//    private Attribute getProject() {
//        Attribute owner = new Attribute();
//        owner.setType("String");
//        owner.setValue(DefaultMap.PROJECT);
//        return owner;
//    }
//
//    private Attribute getAttributeDiscipline(String type) {
//        Attribute discipline = new Attribute();
//        discipline.setType("String");
//        discipline.setValue(getDiscipline(type));
//        return discipline;
//    }
//
//    private TextGraphicProperties getTextGraphicProperties() {
//        TextGraphicProperties textGraphicProperties = new TextGraphicProperties();
//        textGraphicProperties.setDisplayName(DefaultMap.DISPLAY_NAME);
//        return textGraphicProperties;
//    }
}