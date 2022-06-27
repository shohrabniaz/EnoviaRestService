/**
 *
 */
package com.bjit.common.rest.pdm_enovia.mapper;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportData;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementObject;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementObjects;
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
import java.util.HashMap;

/**
 * @author TAREQ SEFATI
 *
 */
public class ItemMapper {

    private List<String> typeNames;
    private List<String> relationNames;
    private Map<String, String> typeSelectablesWithOutputName;
    private Map<String, String> relationSelectablesWithOutputName;
    private boolean expandUp;
    private boolean expandDown;
    private int expandLevel;
    private int dataFetchLimit;
    private String busWhereClause = "";
    private String relWhereClause = "";
    private HashMap<String, HashMap<String, ItemImportData>> typeSourceDestinationMap;
    public static HashMap<String, ItemImportMapping> valMap = new HashMap<>();

    private final Logger OBJECT_TYPES_AND_RELATIONS_LOGGER = Logger.getLogger(ItemMapper.class);

    public ItemMapper() {
        initializeMapsAndLists();
    }

    public ItemMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, CreateObjectBean createObjectBean) {
        initializeMapsAndLists();
        try {

            String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);

            ItemImportMapping mapper;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("val.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(ItemMapper.valMap.get(fileNameFromPath))) {
                    ItemMapper.valMap.put(fileNameFromPath, (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
                }
                mapper = ItemMapper.valMap.get(fileNameFromPath);
            } else {
                mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            }

//            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            AttributeMapper.businessLogic(mapper, createObjectBean);
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
    
    public ItemMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, CreateObjectBean createObjectBean, Boolean insiderCall) {
        initializeMapsAndLists();
        try {

            String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);

            ItemImportMapping mapper;
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("val.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(ItemMapper.valMap.get(fileNameFromPath))) {
                    ItemMapper.valMap.put(fileNameFromPath, (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
                }
                mapper = ItemMapper.valMap.get(fileNameFromPath);
            } else {
                mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            }

//            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            //AttributeMapper.businessLogic(mapper, createObjectBean);
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

    /*public ItemMapper(Context context, String mapsAbsoluteDirectory, Class<BomImportMapping> classType, CreateObjectBean createObjectBean) {
        initializeMapsAndLists();
        try {
            ItemImportMapping mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
            //AttributeBusinessLogic.businessLogic(mapper, createObjectBean);
            AttributeMapper.businessLogic(context, mapper, createObjectBean);
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
    }*/
    private void initializeMapsAndLists() {
        typeNames = new ArrayList<>();
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
            HashMap<String, ItemImportData> sourceAndDestinationMap = new HashMap<>();

            xmlMapElementAttribute.forEach((ItemImportXmlMapElementAttribute sourceDestination) -> {
                ItemImportData bomImportData = new ItemImportData();
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
    
    public ItemImportMapping getMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType, ItemImportMapping mapper) {
        try {

            String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);
            if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("val.map.singleton.instance"))) {
                if (NullOrEmptyChecker.isNull(ItemMapper.valMap.get(fileNameFromPath))) {
                    ItemMapper.valMap.put(fileNameFromPath, (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
                }
                mapper = ItemMapper.valMap.get(fileNameFromPath);
            } else {
                mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
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
        return mapper;
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

    public HashMap<String, HashMap<String, ItemImportData>> getTypeSourceDestinationMap() {
        return typeSourceDestinationMap;
    }

    public void setTypeSourceDestinationMap(HashMap<String, HashMap<String, ItemImportData>> typeSourceDestinationMap) {
        this.typeSourceDestinationMap = typeSourceDestinationMap;
    }
}
