/**
 *
 */
package com.bjit.mapper.mapproject.expand;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.BomImportData;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObjects;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import com.bjit.mapper.mapproject.builder.MapperBuilder;
import com.bjit.mapper.mapproject.util.CommonUtil;
import com.bjit.mapper.mapproject.xml_mapping_model.Mapping;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementAttribute;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementAttributes;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationship;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementBOMRelationships;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObject;
import com.bjit.mapper.mapproject.xml_mapping_model.XmlMapElementObjects;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import matrix.db.BusinessObject;
import matrix.db.Context;

/**
 * @author TAREQ SEFATI
 *
 */
public class ObjectTypesAndRelations {

    private List<String> typeNames;
    private HashMap<String, String> typeShortNameMap;
    private List<String> relationNames;
    private Map<String, String> typeSelectablesWithOutputName;
    private Map<String, String> relationSelectablesWithOutputName;
    private boolean expandUp;
    private boolean expandDown;
    private int expandLevel;
    private int dataFetchLimit;
    private String busWhereClause = "";
    private String relWhereClause = "";
    private HashMap<String, HashMap<String, BomImportData>> typeSourceDestinationMap;
    private HashMap<String, String> destinationSourceMap;
    private List<String> runTimeInterfaceList;

    private final Logger OBJECT_TYPES_AND_RELATIONS_LOGGER = Logger.getLogger(ObjectTypesAndRelations.class);

    public static HashMap<String, ItemImportMapping> itemImportMapper = null;
    public static HashMap<String, Mapping> RnPmaps = new HashMap<>();

    public ObjectTypesAndRelations() {
        initializeMapsAndLists();
        try {
            Mapping mapper = new MapperBuilder().getMapper(MapperBuilder.XML);
            getDataFromMapperFile(mapper);

        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public ObjectTypesAndRelations(String mapsAbsoluteDirectory) {
        initializeMapsAndLists();
        try {
            String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);

            Mapping mapper;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("RnP.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(ObjectTypesAndRelations.RnPmaps.get(fileNameFromPath))) {
                    RnPmaps.put(fileNameFromPath, new MapperBuilder().getMapper(MapperBuilder.XML, mapsAbsoluteDirectory));
                }
                mapper = RnPmaps.get(fileNameFromPath);
            } else {
                mapper = new MapperBuilder().getMapper(MapperBuilder.XML, mapsAbsoluteDirectory);
            }

//            Mapping mapper = new MapperBuilder().getMapper(MapperBuilder.XML, mapsAbsoluteDirectory);
            getDataFromMapperFile(mapper);
        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public ObjectTypesAndRelations(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType) {
        initializeMapsAndLists();
        try {
            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            getSourceDestinationMap(mapper);
        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public ObjectTypesAndRelations(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, CreateObjectBean createObjectBean, AttributeBusinessLogic attributeBusinessLogic) {
        initializeMapsAndLists();
        try {
            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            attributeBusinessLogic.businessLogic(mapper, createObjectBean);
        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (RuntimeException runTimeExp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(runTimeExp.getMessage());
            throw runTimeExp;
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public ObjectTypesAndRelations(String mapsAbsoluteDirectory, CreateObjectBean createObjectBean, AttributeBusinessLogic attributeBusinessLogic, Class<ItemImportMapping> classType) {
        initializeMapsAndLists();
        try {
            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            setRunTimeInterfaceList(fetchRuntimeInterfaceList(mapper, createObjectBean.getTnr().getType()));
            attributeBusinessLogic.businessLogic(mapper, createObjectBean);
        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (RuntimeException runTimeExp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(runTimeExp.getMessage());
            throw runTimeExp;
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public Boolean checkUpdateableObject(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createObjectBean) throws FrameworkException, Exception {
        try {
            businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);

            TNR tnr = createObjectBean.getTnr();
            String type = tnr.getType();
            String name = tnr.getName();
            String rev = tnr.getRevision();
            HashMap<String, String> attributeMap = new HashMap<>();

            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            String propertyValue = commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision");
            attributeMap.put(propertyValue, rev);
            ArrayList<BusinessObject> existingItemList = businessObjectUtil.findBO(context, type, name, "", attributeMap);

//            String objectId = existingItemList.get(0).getObjectId();
//
//            return !NullOrEmptyChecker.isNullOrEmpty(objectId);
            return !NullOrEmptyChecker.isNullOrEmpty(existingItemList);
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public ObjectTypesAndRelations(Context context, BusinessObjectUtil businessObjectUtil, boolean isUpdatable, String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, CreateObjectBean createObjectBean, AttributeBusinessLogic attributeBusinessLogic) throws Exception {
        initializeMapsAndLists();
        try {
            ItemImportMapping mapper;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("odi.item.map.singleton.instance"))) {
                ObjectTypesAndRelations.itemImportMapper = Optional.ofNullable(ObjectTypesAndRelations.itemImportMapper).orElse(new HashMap<>());

                if (!ObjectTypesAndRelations.itemImportMapper.containsKey(createObjectBean.getTnr().getType())) {
                    ObjectTypesAndRelations.itemImportMapper.put(createObjectBean.getTnr().getType(), (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
                }
                mapper = ObjectTypesAndRelations.itemImportMapper.get(createObjectBean.getTnr().getType());
            } else {
                mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            }
            HashMap<String, String> destinationSourceXmlMap = new HashMap<>();
            List<String> interfaceListFromXMLMap = new ArrayList<>();
            mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {

                String runTimeInterfaces = elementObject.getRunTimeInterfaceList();
                if (!NullOrEmptyChecker.isNullOrEmpty(runTimeInterfaces)) {
                    String[] interfaceSplitByComma = runTimeInterfaces.split(",");
                    List<String> splittedInterfaceList = Arrays.asList(interfaceSplitByComma);
                    interfaceListFromXMLMap.addAll(splittedInterfaceList);
                }

                elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                    String sourceName = elementAttribute.getSourceName();
                    String destinationName = elementAttribute.getDestinationName();

                    destinationSourceXmlMap.put(destinationName, sourceName);
                });
            });

            setDestinationSourceMap(destinationSourceXmlMap);
            setRunTimeInterfaceList(interfaceListFromXMLMap);

            //AttributeBusinessLogic.businessLogic(mapper, createObjectBean);
            HashMap<String, String> businessLogic = isUpdatable ? attributeBusinessLogic.updatableBusinessLogic(businessObjectUtil, context, mapper, createObjectBean) : attributeBusinessLogic.businessLogic(context, businessObjectUtil, mapper, createObjectBean);

            if (businessLogic.containsKey("businessInterfaceList")) {
                List<String> interfaceList = Arrays.asList(businessLogic.get("businessInterfaceList").split(","));

                interfaceList.parallelStream().filter(businessInterface -> !NullOrEmptyChecker.isNullOrEmpty(businessInterface)).forEach((String interfaceName) -> {
                    interfaceListFromXMLMap.add(interfaceName);
                });
                businessLogic.remove("businessInterfaceList");
            }
        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (RuntimeException runTimeExp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(runTimeExp.getMessage());
            throw runTimeExp;
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public ObjectTypesAndRelations(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, CreateObjectBean createObjectBean, AttributeBusinessLogic attributeBusinessLogic) throws Exception {
        initializeMapsAndLists();

        Boolean isUpdatable = checkUpdateableObject(context, businessObjectUtil, businessObjectOperations, createObjectBean);
        try {

            ItemImportMapping mapper;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("odi.item.map.singleton.instance"))) {
                ObjectTypesAndRelations.itemImportMapper = Optional.ofNullable(ObjectTypesAndRelations.itemImportMapper).orElse(new HashMap<>());

//                if (NullOrEmptyChecker.isNull(ObjectTypesAndRelations.itemImportMapper)) {
//                    ObjectTypesAndRelations.itemImportMapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
//                }
                if (!ObjectTypesAndRelations.itemImportMapper.containsKey(createObjectBean.getTnr().getType())) {
                    ObjectTypesAndRelations.itemImportMapper.put(createObjectBean.getTnr().getType(), (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
                }
                mapper = ObjectTypesAndRelations.itemImportMapper.get(createObjectBean.getTnr().getType());
            } else {
                mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            }

//            BomImportMapping mapper = (BomImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            HashMap<String, String> destinationSourceXmlMap = new HashMap<>();
            List<String> interfaceListFromXMLMap = new ArrayList<>();
            mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {

                String runTimeInterfaces = elementObject.getRunTimeInterfaceList();
                if (!NullOrEmptyChecker.isNullOrEmpty(runTimeInterfaces)) {
                    String[] interfaceSplitByComma = runTimeInterfaces.split(",");
                    List<String> splittedInterfaceList = Arrays.asList(interfaceSplitByComma);
                    interfaceListFromXMLMap.addAll(splittedInterfaceList);
                }

                elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                    String sourceName = elementAttribute.getSourceName();
                    String destinationName = elementAttribute.getDestinationName();

                    destinationSourceXmlMap.put(destinationName, sourceName);
                });
            });

            setDestinationSourceMap(destinationSourceXmlMap);
            setRunTimeInterfaceList(interfaceListFromXMLMap);

            //AttributeBusinessLogic.businessLogic(mapper, createObjectBean);
            HashMap<String, String> businessLogic = isUpdatable ? attributeBusinessLogic.updatableBusinessLogic(businessObjectUtil, context, mapper, createObjectBean) : attributeBusinessLogic.businessLogic(context, businessObjectUtil, mapper, createObjectBean);

            if (businessLogic.containsKey("businessInterfaceList")) {
                List<String> interfaceList = Arrays.asList(businessLogic.get("businessInterfaceList").split(","));

                interfaceList.parallelStream().filter(businessInterface -> !NullOrEmptyChecker.isNullOrEmpty(businessInterface)).forEach((String interfaceName) -> {
                    interfaceListFromXMLMap.add(interfaceName);
                });
                businessLogic.remove("businessInterfaceList");
            }

        } catch (JAXBException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (FileNotFoundException exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        } catch (RuntimeException runTimeExp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(runTimeExp.getMessage());
            throw runTimeExp;
        } catch (Exception exp) {
            OBJECT_TYPES_AND_RELATIONS_LOGGER.error(exp.getMessage());
        }
    }

    public HashMap<String, String> getDestinationSourceMap() {
        return this.destinationSourceMap;
    }

    private void setDestinationSourceMap(HashMap<String, String> destinationSourceMap) {
        this.destinationSourceMap = destinationSourceMap;
    }

    private void initializeMapsAndLists() {
        typeNames = new ArrayList<>();
        typeShortNameMap = new HashMap<>();
        relationNames = new ArrayList<>();
        typeSelectablesWithOutputName = new LinkedHashMap<>();
        relationSelectablesWithOutputName = new LinkedHashMap<>();
        typeSourceDestinationMap = new HashMap<>();
    }

    private void getSourceDestinationMap(ItemImportMapping mapper) {
        ItemImportXmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
        List<ItemImportXmlMapElementObject> elementObjectList = elementObjects.getXmlMapElementObject();
        elementObjectList.forEach((ItemImportXmlMapElementObject elementObject) -> {
            List<ItemImportXmlMapElementAttribute> xmlMapElementAttribute = elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute();
            HashMap<String, BomImportData> sourceAndDestinationMap = new HashMap<>();

            xmlMapElementAttribute.forEach((ItemImportXmlMapElementAttribute sourceDestination) -> {
                BomImportData bomImportData = new BomImportData();
                bomImportData.setDestinationValue(sourceDestination.getDestinationName());

                HashMap<String, String> sourceValues = new HashMap<>();
                bomImportData.setRangeValues(sourceValues);

                sourceAndDestinationMap.put(sourceDestination.getSourceName(), bomImportData);

                if (!NullOrEmptyChecker.isNull(sourceDestination.getValues())) {
                    List<ItemImportValue> sourceValueList = sourceDestination.getValues().getValue();

                    if (!NullOrEmptyChecker.isNullOrEmpty(sourceValueList)) {
                        sourceValueList.forEach(srcAndValues -> {
                            sourceValues.put(srcAndValues.getSrc(), srcAndValues.getValue());
                        });
                    }
                }
            });
            typeSourceDestinationMap.put(elementObject.getType(), sourceAndDestinationMap);
        });
    }

    private void getDataFromMapperFile(Mapping mapper) {
        if (NullOrEmptyChecker.isNull(mapper.getXmlMapElementObjects())) {
            return;
        }

        XmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
        expandUp = elementObjects.getExpandUp();
        expandDown = elementObjects.getExpandDown();
        expandLevel = elementObjects.getExpandLevel();
        dataFetchLimit = elementObjects.getDataFetchLimit();
        busWhereClause = elementObjects.getExpandBusWhereClause();
        relWhereClause = elementObjects.getExpandRelWhereClause();
        List<XmlMapElementObject> elementObjectList = NullOrEmptyChecker.isNullOrEmpty(elementObjects.getXmlMapElementObject()) ? new ArrayList<>() : elementObjects.getXmlMapElementObject();

        //Populating each object type in typeNames variable
        for (XmlMapElementObject object : elementObjectList) {
            typeNames.add(object.getType());
            OBJECT_TYPES_AND_RELATIONS_LOGGER.debug("In OBJECT_TYPES_AND_RELATIONS_LOGGER:: " + object.getTypeShortName());
            typeShortNameMap.put(object.getType(), object.getTypeShortName());
        }

        //Populating all selectables from an object's all attribute
        if (!elementObjectList.isEmpty()) {
            for (int i = 0; i < elementObjectList.size(); i++) {
                XmlMapElementAttributes elementAttributes = elementObjectList.get(i).getXmlMapElementAttributes();
                List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
                for (XmlMapElementAttribute elementAttribute : elementAttributeList) {
                    if (!typeSelectablesWithOutputName.containsKey(elementAttribute.getSelectable())) {
                        typeSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
                    }
                }
            }
        }

        if (NullOrEmptyChecker.isNull(mapper.getXmlMapElementBOMRelationships())) {
            return;
        }

        XmlMapElementBOMRelationships elementRelationships = mapper.getXmlMapElementBOMRelationships();
        List<XmlMapElementBOMRelationship> relationshipList = NullOrEmptyChecker.isNullOrEmpty(elementRelationships.getXmlMapElementBOMRelationship()) ? new ArrayList<>() : elementRelationships.getXmlMapElementBOMRelationship();

        //Populating each relationship name in relationNames variable
        for (XmlMapElementBOMRelationship relationship : relationshipList) {
            //System.out.println(relationship.getName());
            relationNames.add(relationship.getRelName());
        }

        //Populating all selectables from a relationship's all attribute
        if (!relationshipList.isEmpty()) {
            for (int i = 0; i < relationshipList.size(); i++) {
                XmlMapElementAttributes elementAttributes = relationshipList.get(i).getXmlMapElementAttributes();
                List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
                for (XmlMapElementAttribute elementAttribute : elementAttributeList) {
                    if (!relationSelectablesWithOutputName.containsKey(elementAttribute.getSelectable())) {
                        relationSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
                    }
                }
            }
        }
    }

    public List<String> fetchRuntimeInterfaceList(ItemImportMapping mapper, String objectType) {
        List<String> interfaceListFromXMLMap = new ArrayList<>();
        mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {
            if (elementObject.getType().equalsIgnoreCase(objectType)) {
                String runTimeInterfaces = elementObject.getRunTimeInterfaceList();
                if (!NullOrEmptyChecker.isNullOrEmpty(runTimeInterfaces)) {
                    String[] interfaceSplitByComma = runTimeInterfaces.split(",");
                    List<String> splittedInterfaceList = Arrays.asList(interfaceSplitByComma);
                    interfaceListFromXMLMap.addAll(splittedInterfaceList);
                }
            }
        });
        return interfaceListFromXMLMap;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public Map<String, String> getTypeSelectablesWithOutputName() {
        return typeSelectablesWithOutputName;
    }

    public List<String> getRelationshipNames() {
        return relationNames;
    }

    public Map<String, String> getRelationSelectablesWithOutputName() {
        return relationSelectablesWithOutputName;
    }

    public boolean getExpandUp() {
        return expandUp;
    }

    public boolean getExpandDown() {
        return expandDown;
    }

    public String getBusWhereClause() {
        return busWhereClause != null ? busWhereClause : "";
    }

    public String getRelWhereClause() {
        return relWhereClause != null ? relWhereClause : "";
    }

    public int getExpandLevel() {
        return expandLevel;
    }

    public int getDataFetchLimit() {
        return dataFetchLimit;
    }

    public HashMap<String, HashMap<String, BomImportData>> getTypeSourceDestinationMap() {
        return typeSourceDestinationMap;
    }

    public void setTypeSourceDestinationMap(HashMap<String, HashMap<String, BomImportData>> typeSourceDestinationMap) {
        this.typeSourceDestinationMap = typeSourceDestinationMap;
    }

    public HashMap<String, String> getTypeShortNameMap() {
        return typeShortNameMap;
    }

    public void setTypeShortNameMap(HashMap<String, String> typeShortNameMap) {
        this.typeShortNameMap = typeShortNameMap;
    }

    public List<String> getRunTimeInterfaceList() {
        return runTimeInterfaceList;
    }

    private void setRunTimeInterfaceList(List<String> runTimeInterfaceList) {
        this.runTimeInterfaceList = runTimeInterfaceList;
    }

    private void setRunTimeInterfaceList(String runTimeInterfaceList) {
        if (NullOrEmptyChecker.isNullOrEmpty(this.runTimeInterfaceList)) {
            this.runTimeInterfaceList = new ArrayList<>();
        }
        this.runTimeInterfaceList.add(runTimeInterfaceList);
    }
}
