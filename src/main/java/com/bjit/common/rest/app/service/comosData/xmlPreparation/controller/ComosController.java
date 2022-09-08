package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller;

//import com.bjit.common.rest.app.service.comosData.defaultdata.DefaultMap;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentChild;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.equipment.EquipmentServiceResponse;
//import com.google.gson.Gson;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import java.io.*;
//import org.apache.log4j.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.*;

//@RestController
//@RequestMapping("/comos")
public class ComosController {
//    private static final Logger ComosController_LOGGER = Logger.getLogger(ComosController.class);
//    Long sequence = 1l;
//    Integer equipmentSequencer = 0;
//
//    @GetMapping("/export/xml")
//    public String createJAXBModel() {
//        try {
////            String path = "C:/Users/BJIT/Desktop/comos_service_response/service_response.json";
//
//            List<String> fileList = getRespondedFiles();
//            fileList.forEach((String filename) -> {
//                HashMap<String, RFLP> rflpMap = new HashMap<>();
//
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
//
//                    Gson gson = new Gson();
//                    EquipmentServiceResponse serviceResponse = gson.fromJson(bufferedReader, EquipmentServiceResponse.class);
//
//                    EquipmentChild comosModel = serviceResponse.getData().getComosModel();
//                    String plantName = comosModel.getId();
//                    Long plantSequence = sequence;
//
//                    /**
//                     * Plant data gatherer
//                     */
//                    RFLVPMItem plantModel = populateComosXMLPojoModel(comosModel.getType(), plantName, comosModel.getType(), sequence);
//
////            List<RFLP> rflpList = new ArrayList<>();
//
//                    /**
//                     * Unit data gatherer
//                     */
//                    comosModel.getChilds().forEach((EquipmentChild unit) -> {
//                        RFLP rflp = new RFLP();
////                rflpList.add(rflp);
//                        rflpMap.put(unit.getId(), rflp);
//
//                        /**
//                         * Reference type data
//                         */
//                        LogicalReference logicalReference = new LogicalReference();
//                        rflp.setLogicalReference(logicalReference);
//                        List<RFLVPMItem> logicalReferencesList = new ArrayList<>();
//                        logicalReference.setId(logicalReferencesList);
//                        logicalReferencesList.add(plantModel);
//
//                        Long unitSequence = ++sequence;
//                        RFLVPMItem unitModel = populateComosXMLPojoModel(unit.getType(), unit.getId(), unit.getType(), unitSequence);
//                        logicalReferencesList.add(unitModel);
//                        /**
//                         * Instance type data
//                         */
//                        LogicalInstance logicalInstance = new LogicalInstance();
//                        rflp.setLogicalInstance(logicalInstance);
//                        List<RFLVPMItem> logicalInstanceList = new ArrayList<>();
//                        logicalInstance.setId(logicalInstanceList);
//
//                        String unitInstanceType = unit.getType() + "_instance";
//                        Long unitInstanceSequence = ++sequence;
//                        RFLVPMItem unitInstance = populateComosEquipmentXMLPojoModel(unitInstanceType, "System" + String.format("%06d", unitInstanceSequence) + DefaultMap.REVISION, unit.getId(), unitInstanceType, plantSequence, unitSequence, unitInstanceSequence);
//                        logicalInstanceList.add(unitInstance);
//
//
//                        /**
//                         * Subunit data gatherer
//                         */
//                        unit.getChilds().forEach((EquipmentChild subunit) -> {
//                            Long subunitSequence = ++sequence;
//                            RFLVPMItem subunitModel = populateComosXMLPojoModel(subunit.getType(), subunit.getId(), subunit.getType(), subunitSequence);
//                            logicalReferencesList.add(subunitModel);
//
//                            String subunitInstanceType = subunit.getType() + "_instance";
//                            Long subunitInstanceSequence = ++sequence;
//                            RFLVPMItem subunitInstance = populateComosEquipmentXMLPojoModel(subunitInstanceType, "System" + String.format("%06d", subunitInstanceSequence) + DefaultMap.REVISION, subunit.getId(), subunitInstanceType, unitSequence, subunitSequence, subunitInstanceSequence);
//                            logicalInstanceList.add(subunitInstance);
//
//                            /**
//                             * Category data gatherer
//                             */
//                            subunit.getChilds().forEach((EquipmentChild category) -> {
//                                if (category.getType().equalsIgnoreCase("EquipmentCategory")) {
//                                    equipmentSequencer = 0;
//                                    /**
//                                     * EquipmentDataGatherer
//                                     */
//                                    category.getChilds().forEach((EquipmentChild equipment) -> {
//                                        Long equipmentSequence = ++sequence;
//                                        RFLVPMItem equipmentModel = populateComosEquipmentXMLPojoModel(equipment.getType(), "Equipment" + ++equipmentSequencer, equipment.getId(), equipment.getType(), equipmentSequence);
//                                        logicalReferencesList.add(equipmentModel);
//
//                                        String equipmentInstanceType = equipment.getType() + "_instance";
//                                        Long equipmentInstanceSequence = ++sequence;
//                                        RFLVPMItem equipmentInstance = populateComosEquipmentXMLPojoModel(equipmentInstanceType, "Equipment" + String.format("%06d", equipmentSequencer) + DefaultMap.REVISION, equipment.getId(), equipmentInstanceType, subunitSequence, equipmentSequence, equipmentInstanceSequence);
//                                        logicalInstanceList.add(equipmentInstance);
//                                    });
//                                }
//                            });
//                        });
//                    });
//
//                    writeXMLFile(rflpMap);
//                } catch (Exception exp) {
//                    ComosController_LOGGER.info(exp.getMessage());
//                    System.out.println(exp.getMessage());
//                }
//
//            });
//        } catch (Exception exp) {
//            ComosController_LOGGER.info(exp.getMessage());
//            System.out.println(exp.getMessage());
//        }
//        ComosController_LOGGER.info("Printed");
//        return "Printed";
//    }
//
//    @GetMapping("/export/plant/xml")
//    public String createJAXBPlantModel() {
//        try {
//            List<String> fileList = getRespondedFiles();
//
//            fileList.forEach((String filename) -> {
//                try {
//                    HashMap<String, RFLP> rflpMap = new HashMap<>();
//                    RFLP rflp = new RFLP();
//
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
//
//                    Gson gson = new Gson();
//                    EquipmentServiceResponse serviceResponse = gson.fromJson(bufferedReader, EquipmentServiceResponse.class);
//
//
//                    EquipmentChild comosModel = serviceResponse.getData().getComosModel();
//                    String plantName = comosModel.getId();
//                    rflpMap.put(plantName, rflp);
//
//                    Long plantSequence = sequence;
//                    LogicalReference logicalReference = new LogicalReference();
//                    rflp.setLogicalReference(logicalReference);
//                    List<RFLVPMItem> logicalReferencesList = new ArrayList<>();
//                    logicalReference.setId(logicalReferencesList);
//
//                    /**
//                     * Plant data gatherer
//                     */
//                    RFLVPMItem plantModel = populateComosXMLPojoModel(comosModel.getType(), plantName, comosModel.getType(), sequence);
//                    logicalReferencesList.add(plantModel);
//
//                    LogicalInstance logicalInstance = new LogicalInstance();
//                    rflp.setLogicalInstance(logicalInstance);
//                    List<RFLVPMItem> logicalInstanceList = new ArrayList<>();
//                    logicalInstance.setId(logicalInstanceList);
//
//                    /**
//                     * Unit data gatherer
//                     */
//                    comosModel.getChilds().forEach((EquipmentChild unit) -> {
//                        /**
//                         * Reference type data
//                         */
//                        Long unitSequence = ++sequence;
//                        RFLVPMItem unitModel = populateComosXMLPojoModel(unit.getType(), unit.getId(), unit.getType(), unitSequence);
//                        logicalReferencesList.add(unitModel);
//
//                        /**
//                         * Instance type data
//                         */
//                        String unitInstanceType = unit.getType() + "_instance";
//                        Long unitInstanceSequence = ++sequence;
//                        RFLVPMItem unitInstance = populateComosEquipmentXMLPojoModel(unitInstanceType, "System" + String.format("%06d", unitInstanceSequence) + DefaultMap.REVISION, unit.getId(), unitInstanceType, plantSequence, unitSequence, unitInstanceSequence);
//                        logicalInstanceList.add(unitInstance);
//
//
//                        /**
//                         * Subunit data gatherer
//                         */
//                        unit.getChilds().forEach((EquipmentChild subunit) -> {
//                            /**
//                             * Reference type data
//                             */
//                            Long subunitSequence = ++sequence;
//                            RFLVPMItem subunitModel = populateComosXMLPojoModel(subunit.getType(), subunit.getId(), subunit.getType(), subunitSequence);
//                            logicalReferencesList.add(subunitModel);
//
//                            /**
//                             * Instance type data
//                             */
//                            String subunitInstanceType = subunit.getType() + "_instance";
//                            Long subunitInstanceSequence = ++sequence;
//                            RFLVPMItem subunitInstance = populateComosEquipmentXMLPojoModel(subunitInstanceType, "System" + String.format("%06d", subunitInstanceSequence) + DefaultMap.REVISION, subunit.getId(), subunitInstanceType, unitSequence, subunitSequence, subunitInstanceSequence);
//                            logicalInstanceList.add(subunitInstance);
//
//                            /**
//                             * Category data gatherer
//                             */
//                            subunit.getChilds().forEach((EquipmentChild category) -> {
//                                if (category.getType().equalsIgnoreCase("EquipmentCategory")) {
//                                    equipmentSequencer = 0;
//                                    /**
//                                     * EquipmentDataGatherer
//                                     */
//                                    category.getChilds().forEach((EquipmentChild equipment) -> {
//                                        /**
//                                         * Equipment type data
//                                         */
//                                        Long equipmentSequence = ++sequence;
//                                        RFLVPMItem equipmentModel = populateComosEquipmentXMLPojoModel(equipment.getType(), "Equipment" + ++equipmentSequencer, equipment.getId(), equipment.getType(), equipmentSequence);
//                                        logicalReferencesList.add(equipmentModel);
//
//                                        /**
//                                         * Instance type data
//                                         */
//                                        String equipmentInstanceType = equipment.getType() + "_instance";
//                                        Long equipmentInstanceSequence = ++sequence;
//                                        RFLVPMItem equipmentInstance = populateComosEquipmentXMLPojoModel(equipmentInstanceType, "Equipment" + String.format("%06d", equipmentSequencer) + DefaultMap.REVISION, equipment.getId(), equipmentInstanceType, subunitSequence, equipmentSequence, equipmentInstanceSequence);
//                                        logicalInstanceList.add(equipmentInstance);
//                                    });
//                                }
//                            });
//                        });
//                    });
//                    writeXMLFile(rflpMap);
//                } catch (Exception exp) {
//                    ComosController_LOGGER.info(exp.getMessage());
//                    System.out.println(exp.getMessage());
//                }
//            });
//
//        } catch (Exception exp) {
//            ComosController_LOGGER.info(exp.getMessage());
//            System.out.println(exp.getMessage());
//        }
//        ComosController_LOGGER.info("File Printed");
//        return "File Printed";
//    }
//
//    private List<String> getRespondedFiles() throws IOException {
//        List<String> fileList = Files.walk(Paths.get("C:/Users/BJIT/Desktop/comos_service_response/"))
//                .filter(Files::isRegularFile)
//                .filter((Path filePath) -> filePath.toString().endsWith(".json"))
//                .map(Path::toString)
//                .collect(Collectors.toList());
//        return fileList;
//    }
//
//    private void writeXMLFile(HashMap<String, RFLP> rflpMap) {
//        rflpMap.forEach((String key, RFLP value) -> {
//
//            JAXBContext jaxbContext = null;
//            try {
//                jaxbContext = JAXBContext.newInstance(RFLP.class);
//                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//                StringWriter sw = new StringWriter();
//                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//                jaxbMarshaller.marshal(value, sw);
//                String xmlString = sw.toString();
//
//                try (PrintWriter out = new PrintWriter("C:/Users/BJIT/Desktop/comos file/" + key + ".xml")) {
//                    out.println(xmlString);
//                    ComosController_LOGGER.info(xmlString);
//                } catch (FileNotFoundException e) {
//                    ComosController_LOGGER.info(e.getMessage());
//                    e.printStackTrace();
//                }
//            } catch (JAXBException e) {
//                ComosController_LOGGER.info(e.getMessage());
//                e.printStackTrace();
//            }
//        });
//    }
//
////    private void getChildItems(Child item){
////        item.getChilds();
////        RFLVPMItem childItemModel = populateComosXMLPojoModel(comosModel.getType(), plantName, comosModel.getType(), sequence);
////    }
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
//    private RFLVPMItem populateComosEquipmentXMLPojoModel(String comosType, String trdSpaceType, String name, String discipline, Long sequence) {
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
//    private RFLVPMItem populateComosEquipmentXMLPojoModel(String comosType, String trdSpaceType, String name, String discipline, Long ownerSequence, Long sequence, Long instanceSequence) {
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