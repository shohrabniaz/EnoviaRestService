//package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServiceImpls;
//
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices.IStructurePreparation;
//import com.bjit.common.rest.app.service.comosData.defaultdata.DefaultMap;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalInstance;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.LogicalReference;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLVPMItem;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.plant.Child;
//import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.plant.EquipmentServiceResponse;
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Value;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//
////@Component
////@Qualifier("ComosStructurePreparationLevelWiseFileSeparation")
////@Qualifier("ComosStructurePreparation2")
////@Scope("prototype")
////public class ComosStructurePreparationLevelWiseFileSeparation implements IStructurePreparation<HashMap<String, RFLP>> {
//public class ComosStructurePreparation_Prev_Impl implements IStructurePreparation<HashMap<String, RFLP>> {
//    private final HashMap<String, String> levelWiseFileName = new HashMap<>();
//    private final HashMap<String, List<String>> fileWiseParentIndex = new HashMap<>();
//    @Value("${comos.number.of.units.in.an.xml.file}")
//    private int MAXIMUM_XML_CHILDREN_NUMBER;
//    private Long sequencedData = 1l;
//    private String firstFileName = null;
//
//    @Override
//    public HashMap<String, RFLP> prepareStructure(String jsonString) {
//        EquipmentServiceResponse response = getServiceResponseModel(jsonString);
//        Child plant = response.getData().getComosModel();
//        String millId = response.getData().getMillId();
//        String equipmentId = response.getData().getEquipmentId();
//
//        String xmlFileName = "mill-id_" + millId + "_eq-id_" + equipmentId;  //mill-id_112880_eq-id_112880.039_1_0001
//        firstFileName = xmlFileName;
//        xmlFileName += "_U_1_0001";
//        levelWiseFileName.put("1", xmlFileName);
//
//        RFLP rflp = getRflp();
//
//        HashMap<String, RFLP> structureMap = new HashMap<>();
//        structureMap.put(xmlFileName, rflp);
//
//        Integer fetchFirstLevel = 1;
//
//        prepareStructure(structureMap, plant, plant.getChilds(), xmlFileName, fetchFirstLevel, "System");
//
//        return structureMap;
//    }
//
//    private void prepareStructure(HashMap<String, RFLP> structureMap, Child parent, List<Child> children, String xmlFileName, int level, String objectType) {
//        String parentName = parent.getId();
//        String parentType = parent.getType();
//
//        children = Optional.ofNullable(children).orElse(new ArrayList<>());
//
//        if (children != null) {
//            for (Child child : children) {
//                String childType = child.getType();
//
//                if (childType.equalsIgnoreCase("DevicePosition") && parentType.equalsIgnoreCase("SubUnit")) {
//
//
//                } else if (parentType.equalsIgnoreCase("SubUnit")) {
//                    if (childType.equalsIgnoreCase("EquipmentCategory")) {
//                        List<Child> equipmentChildren = child.getChilds();
//                        prepareStructure(structureMap, parent, equipmentChildren, xmlFileName, level, "Equipment");
//                    }
//                    if (childType.equalsIgnoreCase("PipeCategory")) {
//                        List<Child> equipmentChildren = child.getChilds();
//                        prepareStructure(structureMap, parent, equipmentChildren, xmlFileName, level, "Pipe");
//                    }
//                    if (childType.equalsIgnoreCase("ValveCategory")) {
//                        List<Child> equipmentChildren = child.getChilds();
//                        prepareStructure(structureMap, parent, equipmentChildren, xmlFileName, level, "Valve");
//                    }
//                    continue;
//                } else if (level > 3) {
//                    break;
//                }
//
//                if (childType.equalsIgnoreCase("unit")) {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level, "U");
//                } else if (childType.equalsIgnoreCase("subunit")) {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level, "SU");
//                } else if (objectType.equalsIgnoreCase("equipment")) {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level, "C1");
//                } else if (objectType.equalsIgnoreCase("valve")) {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level, "C2");
//                } else if (objectType.equalsIgnoreCase("pipe")) {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level, "C3");
//                } else {
//                    xmlFileName = getCurrentXMLFileName(structureMap, xmlFileName, level);
//                }
//
//                RFLP rflp = structureMap.get(xmlFileName);
//                List<RFLVPMItem> referenceIds = rflp.getLogicalReference().getId();
//                List<RFLVPMItem> instanceIds = rflp.getLogicalInstance().getId();
//
//                int itemSizeInTheList = referenceIds.size();
//
//                List<String> parentListInTheFile = fileWiseParentIndex.get(xmlFileName);
//                parentListInTheFile = Optional.ofNullable(parentListInTheFile).orElse(new ArrayList<>());
//
//                HashMap<String, String> parentAttributes = Optional.ofNullable(parent.getAttributes()).orElse(new HashMap<>());
//                String parentDescription = Optional.ofNullable(parent.getDescription()).orElse(
//                        Optional.ofNullable(parentAttributes.get("DescriptionInEnglish")).orElse("")
//                );
//
//                if (itemSizeInTheList == 0) {
//                    String discipline = parentType;
//                    RFLVPMItem parentData = PojoModelBuilder.populateLogicalReferenceTypeData(parentType, parent.getCode(), parentDescription, parentName, discipline, parent.getSequence());
//                    referenceIds.add(parentData);
//
//                    parentListInTheFile.add(parentType + "_" + parentName);
//                    fileWiseParentIndex.put(xmlFileName, parentListInTheFile);
//                }
//
//                if (!parentListInTheFile.contains(parentType + "_" + parentName)) {
//                    String discipline = parentType;
//                    RFLVPMItem parentData = PojoModelBuilder.populateLogicalReferenceTypeData(parentType, parent.getCode(), parentDescription, parentName, discipline, parent.getSequence());
//                    referenceIds.add(parentData);
//
//                    parentListInTheFile.add(parentType + "_" + parentName);
//                    fileWiseParentIndex.put(xmlFileName, parentListInTheFile);
//                }
//
//                String childDiscipline = childType;
//
//                HashMap<String, String> childAttributes = Optional.ofNullable(child.getAttributes()).orElse(new HashMap<>());
//
//                System.out.println("Child code : " + child.getCode());
//                System.out.println("Child description : " + child.getDescription());
//                System.out.println("Child attribute description : " + childAttributes.get("DescriptionInEnglish"));
//
//                String childDescription = Optional.ofNullable(child.getDescription()).orElse(
//                        Optional.ofNullable(childAttributes.get("DescriptionInEnglish")).orElse("")
//                );
//                RFLVPMItem childData = null;
//                if (objectType.equalsIgnoreCase("Equipment")) {
////                    childData = PojoModelBuilder.populateEquipmentTypeData(objectType, child.getCode(), childDescription, child.getId(), objectType, child.getSequence());
//                    childData = PojoModelBuilder.populateEquipmentTypeData(child);
//                } else if (objectType.equalsIgnoreCase("Valve")) {
////                    childData = PojoModelBuilder.populateValveTypeData(objectType, child.getCode(), childDescription, child.getId(), objectType, child.getSequence());
//                    childData = PojoModelBuilder.populateValveTypeData(child);
//                } else if (objectType.equalsIgnoreCase("Pipe")) {
////                    childData = PojoModelBuilder.populateLogicalPipeTypeData(objectType, child.getCode(), childDescription, child.getId(), objectType, child.getSequence());
//                    childData = PojoModelBuilder.populateLogicalPipeTypeData(child);
//                } else {
//                    childData = PojoModelBuilder.populateLogicalReferenceTypeData(childType, child.getCode(), childDescription, child.getId(), childDiscipline, child.getSequence());
//                }
//
//                referenceIds.add(childData);
//
//                String childInstanceType = "";
//
//                long childInstanceSequence = ++sequencedData;
//                RFLVPMItem instance = null;
//                if (objectType.equalsIgnoreCase("equipment")) {
//                    childInstanceType = objectType + "_instance";
//                    instance = PojoModelBuilder.populateEquipmentInstanceTypeData(childInstanceType, objectType + String.format("%06d", childInstanceSequence) + DefaultMap.REVISION, child.getId(), childInstanceType, parent.getSequence(), child.getSequence(), childInstanceSequence);
//                } else if (objectType.equalsIgnoreCase("pipe")) {
//                    childInstanceType = objectType + "_instance";
//                    instance = PojoModelBuilder.populateLogicalPipeInstanceTypeData(childInstanceType, objectType + String.format("%06d", childInstanceSequence) + DefaultMap.REVISION, child.getId(), childInstanceType, parent.getSequence(), child.getSequence(), childInstanceSequence);
//                } else if (objectType.equalsIgnoreCase("valve")) {
//                    childInstanceType = objectType + "_instance";
//                    instance = PojoModelBuilder.populateLogicalValveInstanceTypeData(childInstanceType, objectType + String.format("%06d", childInstanceSequence) + DefaultMap.REVISION, child.getId(), childInstanceType, parent.getSequence(), child.getSequence(), childInstanceSequence);
//                } else {
//                    childInstanceType = childType + "_instance";
//                    instance = PojoModelBuilder.populateLogicalInstanceTypeData(childInstanceType, objectType + String.format("%06d", childInstanceSequence) + DefaultMap.REVISION, child.getId(), childInstanceType, parent.getSequence(), child.getSequence(), childInstanceSequence);
//                }
//
//                instanceIds.add(instance);
//
//                prepareStructure(structureMap, child, child.getChilds(), xmlFileName, level + 1, "System");
//            }
//        }
//    }
//
//    private String getCurrentXMLFileName(HashMap<String, RFLP> structureMap, String xmlFileName, Integer level) {
//        try {
//            RFLP rflp = structureMap.get(levelWiseFileName.get(level.toString()));
//            List<RFLVPMItem> referenceIds = rflp.getLogicalReference().getId();
//
//            int referenceSize = referenceIds.size();
//            xmlFileName = xmlFilename(structureMap, level, referenceSize);
//        } catch (NullPointerException exp) {
//            xmlFileName = xmlFilename(structureMap, level, 0);
//        }
//
//        return xmlFileName;
//    }
//
//    private String getCurrentXMLFileName(HashMap<String, RFLP> structureMap, String xmlFileName, int level, String category) {
//        try {
//            String levelCategory = category + "_" + level;
//
//            RFLP rflp = structureMap.get(levelWiseFileName.get(levelCategory));
//            List<RFLVPMItem> referenceIds = rflp.getLogicalReference().getId();
//
//            int referenceSize = referenceIds.size();
//            xmlFileName = xmlFilename(structureMap, level, referenceSize, category);
//        } catch (NullPointerException exp) {
//            xmlFileName = xmlFilename(structureMap, level, 0, category);
//        }
//
//        return xmlFileName;
//    }
//
//    private String xmlFilename(HashMap<String, RFLP> structureMap, Integer level, Integer size) {
//        String firstLevelFilename = firstFileName;
//        String leveledFilename = levelWiseFileName.get(level.toString());
//        String levelString = level.toString();
//
//        if (size < MAXIMUM_XML_CHILDREN_NUMBER) {
//            if (leveledFilename == null || leveledFilename.isEmpty()) {
//                leveledFilename = firstLevelFilename + "_" + level + "_" + String.format("%04d", 1);
//
//                levelWiseFileName.put(levelString, leveledFilename);
//                structureMap.put(leveledFilename, getRflp());
//            }
//        } else {
//            if (leveledFilename == null || leveledFilename.isEmpty()) {
//                leveledFilename = firstLevelFilename + "_" + level + "_" + String.format("%04d", 1);
//
//                levelWiseFileName.put(levelString, leveledFilename);
//                structureMap.put(leveledFilename, getRflp());
//            } else {
//                String listSize = null;
//                try {
//                    listSize = leveledFilename.split("_")[5];
//                } catch (ArrayIndexOutOfBoundsException exp) {
//                    listSize = "0000";
//                }
//                Integer lastData = Integer.parseInt(listSize) + 1;
//                leveledFilename = firstLevelFilename + "_" + level + "_" + String.format("%04d", lastData);
//
//                levelWiseFileName.put(levelString, leveledFilename);
//            }
//
//            structureMap.put(leveledFilename, getRflp());
//        }
//
//        return leveledFilename;
//    }
//
//    private String xmlFilename(HashMap<String, RFLP> structureMap, Integer level, Integer size, String category) {
//        String firstLevelFilename = firstFileName;
//        String levelCategory = category + "_" + level;
//        String leveledFilename = levelWiseFileName.get(levelCategory);
//
//        if (size < MAXIMUM_XML_CHILDREN_NUMBER) {
//            if (leveledFilename == null || leveledFilename.isEmpty()) {
//                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", 1);
//
//                levelWiseFileName.put(levelCategory, leveledFilename);
//                structureMap.put(leveledFilename, getRflp());
//            }
//        } else {
//            if (leveledFilename == null || leveledFilename.isEmpty()) {
//                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", 1);
//
//                levelWiseFileName.put(levelCategory, leveledFilename);
//                structureMap.put(leveledFilename, getRflp());
//            } else {
//                String listSize = null;
//                try {
//                    listSize = leveledFilename.split("_")[6];
//                } catch (ArrayIndexOutOfBoundsException exp) {
//                    listSize = "0000";
//                }
//                Integer lastData = Integer.parseInt(listSize) + 1;
//                leveledFilename = firstLevelFilename + "_" + levelCategory + "_" + String.format("%04d", lastData);
//
//                levelWiseFileName.put(levelCategory, leveledFilename);
//            }
//
//            structureMap.put(leveledFilename, getRflp());
//        }
//
//        return leveledFilename;
//    }
//
//    private RFLP getRflp() {
//        List<RFLVPMItem> logicalReferencesList = new ArrayList<>();
//        List<RFLVPMItem> logicalInstanceList = new ArrayList<>();
////        List<RFLVPMItem> logicalPortList = new ArrayList<>();
//
//        LogicalReference logicalReference = new LogicalReference();
//        logicalReference.setId(logicalReferencesList);
//
//        LogicalInstance logicalInstance = new LogicalInstance();
//        logicalInstance.setId(logicalInstanceList);
//
////        LogicalPort logicalPort = new LogicalPort();
////        logicalPort.setId(logicalPortList);
//
//        RFLP rflp = new RFLP();
//        rflp.setLogicalReference(logicalReference);
//        rflp.setLogicalInstance(logicalInstance);
////        rflp.setLogicalPort(logicalPort);
//        return rflp;
//    }
//
//    private EquipmentServiceResponse getServiceResponseModel(String jsonString) {
//        Gson gson = new Gson();
//        EquipmentServiceResponse serviceResponse = gson.fromJson(jsonString, EquipmentServiceResponse.class);
//
//        Child comosParent = serviceResponse.getData().getComosModel();
//        comosParent.setSequence(sequencedData);
//        setSequence(comosParent);
//
//        return serviceResponse;
//    }
//
//    private void setSequence(Child comosParent) {
//        List<Child> comosChildren = comosParent.getChilds();
//        comosChildren = Optional.ofNullable(comosChildren).orElse(new ArrayList<>());
//
//        for (Child comosChild : comosChildren) {
//            comosChild.setSequence(++sequencedData);
//            setSequence(comosChild);
//        }
//    }
//
//    @Override
//    public HashMap<String, RFLP> getCombinedResponseData(String jsonString) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//}
