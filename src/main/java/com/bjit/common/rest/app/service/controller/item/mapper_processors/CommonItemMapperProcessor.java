/**
 *
 */
package com.bjit.common.rest.app.service.controller.item.mapper_processors;

import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.BomImportData;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportXmlMapElementObject;
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
import java.util.Arrays;
import java.util.HashMap;
import matrix.db.Context;

/**
 * @author BJIT
 *
 */
public class CommonItemMapperProcessor implements IXmlMapperProcessor {

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

    protected CommonItemParameters commonItemParameters;

    private final Logger ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER = Logger.getLogger(CommonItemMapperProcessor.class);

    public static HashMap<String, Mapping> RnPmaps = new HashMap<>();
    public static HashMap<String, ItemImportMapping> itemImportMapper = new HashMap<>();

    @Override
    public HashMap<String, String> processAttributeXMLMapper(CommonItemParameters commonItemParameters) throws Exception {
        this.commonItemParameters = commonItemParameters;

        Context context = commonItemParameters.getContext();
        CreateObjectBean createObjectBean = commonItemParameters.getCreateObjectBean();
        String mapsAbsoluteDirectory = commonItemParameters.getXmlMapDirectory();
        Class<ItemImportMapping> classType = commonItemParameters.getClassType();
        AttributeBusinessLogic attributeBusinessLogic = commonItemParameters.getAttributeBusinessLogic();
        BusinessObjectUtil businessObjectUtil = commonItemParameters.getBusinessObjectUtil();

        initializeMapsAndLists();

        Boolean isUpdatable = searchItemsExistence(createObjectBean, context);

        try {

            ItemImportMapping mapper = getXmlMapper(mapsAbsoluteDirectory, classType);

            HashMap<String, String> destinationSourceXmlMap = new HashMap<>();
            List<String> interfaceListFromXMLMap = new ArrayList<>();
            mapper.getXmlMapElementObjects().getXmlMapElementObject().forEach((ItemImportXmlMapElementObject elementObject) -> {

                String runTimeInterfaces = elementObject.getRunTimeInterfaceList();
                getSplittedRuntimeIterfaceList(runTimeInterfaces, interfaceListFromXMLMap);

                elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute().forEach((ItemImportXmlMapElementAttribute elementAttribute) -> {

                    String sourceName = elementAttribute.getSourceName();
                    String destinationName = elementAttribute.getDestinationName();

                    destinationSourceXmlMap.put(destinationName, sourceName);
                });
            });

            HashMap<String, String> createObjectBeansAttributesUpdated = isUpdatable ? attributeBusinessLogic.updatableBusinessLogic(businessObjectUtil, context, mapper, createObjectBean) : attributeBusinessLogic.businessLogic(context, businessObjectUtil, mapper, createObjectBean);

            String runTimeInterfaces = createObjectBeansAttributesUpdated.get("runtimeInterfaceList");
            getSplittedRuntimeIterfaceList(runTimeInterfaces, interfaceListFromXMLMap);
            createObjectBeansAttributesUpdated.remove("runtimeInterfaceList");

            setDestinationSourceMap(destinationSourceXmlMap);
            setRunTimeInterfaceList(interfaceListFromXMLMap);

            return createObjectBeansAttributesUpdated;
        } catch (JAXBException exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (FileNotFoundException | RuntimeException exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    protected void getSplittedRuntimeIterfaceList(String runTimeInterfaces, List<String> interfaceListFromXMLMap) {
        if (!NullOrEmptyChecker.isNullOrEmpty(runTimeInterfaces)) {
            String[] interfaceSplitByComma = runTimeInterfaces.split(",");
            List<String> splittedInterfaceList = Arrays.asList(interfaceSplitByComma);
            interfaceListFromXMLMap.addAll(splittedInterfaceList);
        }
    }

    protected Boolean searchItemsExistence(CreateObjectBean createObjectBean, Context context) {
        Boolean isUpdatable = Boolean.FALSE;
        try {
            if (createObjectBean.getIsAutoName()) {
                return false;
            }

            HashMap<String, String> whereClausesMap = new HashMap();
            whereClausesMap.put("type", createObjectBean.getTnr().getType());
            CommonSearch commonSearch = new CommonSearch();
            List<HashMap<String, String>> searchItem = commonSearch.searchItem(context, createObjectBean.getTnr(), whereClausesMap);

            String objectId = searchItem.get(0).get("id");
            this.commonItemParameters.setObjectId(objectId);

            isUpdatable = !searchItem.isEmpty();
            this.commonItemParameters.setItemExists(isUpdatable);
        } catch (Exception exp) {
            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.error(exp.getMessage());
        }
        return isUpdatable;
    }

    protected ItemImportMapping getXmlMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType) throws Exception {
        String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);
        ItemImportMapping mapper;
        if (Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("odi.item.map.singleton.instance"))) {
            if (NullOrEmptyChecker.isNull(CommonItemMapperProcessor.itemImportMapper.get(fileNameFromPath))) {
                CommonItemMapperProcessor.itemImportMapper.put(fileNameFromPath, (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
            }
            mapper = CommonItemMapperProcessor.itemImportMapper.get(fileNameFromPath);
        } else {
            mapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
        }
        return mapper;
    }

    @Override
    public HashMap<String, String> getDestinationSourceMap() {
        return this.destinationSourceMap;
    }

    protected void setDestinationSourceMap(HashMap<String, String> destinationSourceMap) {
        this.destinationSourceMap = destinationSourceMap;
    }

    protected void initializeMapsAndLists() {
        typeNames = new ArrayList<>();
        typeShortNameMap = new HashMap<>();
        relationNames = new ArrayList<>();
        typeSelectablesWithOutputName = new LinkedHashMap<>();
        relationSelectablesWithOutputName = new LinkedHashMap<>();
        typeSourceDestinationMap = new HashMap<>();
    }

//    private void getSourceDestinationMap(ItemImportMapping mapper) {
//        ItemImportXmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
//        List<ItemImportXmlMapElementObject> elementObjectList = elementObjects.getXmlMapElementObject();
//        elementObjectList.forEach((ItemImportXmlMapElementObject elementObject) -> {
//            List<ItemImportXmlMapElementAttribute> xmlMapElementAttribute = elementObject.getXmlMapElementAttributes().getXmlMapElementAttribute();
//            HashMap<String, BomImportData> sourceAndDestinationMap = new HashMap<>();
//
//            xmlMapElementAttribute.forEach((ItemImportXmlMapElementAttribute sourceDestination) -> {
//                BomImportData bomImportData = new BomImportData();
//                bomImportData.setDestinationValue(sourceDestination.getDestinationName());
//
//                HashMap<String, String> sourceValues = new HashMap<>();
//                bomImportData.setRangeValues(sourceValues);
//
//                sourceAndDestinationMap.put(sourceDestination.getSourceName(), bomImportData);
//
//                if (!NullOrEmptyChecker.isNull(sourceDestination.getValues())) {
//                    List<ItemImportValue> sourceValueList = sourceDestination.getValues().getValue();
//
//                    if (!NullOrEmptyChecker.isNullOrEmpty(sourceValueList)) {
//                        sourceValueList.forEach(srcAndValues -> {
//                            sourceValues.put(srcAndValues.getSrc(), srcAndValues.getValue());
//                        });
//                    }
//                }
//            });
//            typeSourceDestinationMap.put(elementObject.getType(), sourceAndDestinationMap);
//        });
//    }
//
//    private void getDataFromMapperFile(Mapping mapper) {
//        if (NullOrEmptyChecker.isNull(mapper.getXmlMapElementObjects())) {
//            return;
//        }
//
//        XmlMapElementObjects elementObjects = mapper.getXmlMapElementObjects();
//        expandUp = elementObjects.getExpandUp();
//        expandDown = elementObjects.getExpandDown();
//        expandLevel = elementObjects.getExpandLevel();
//        dataFetchLimit = elementObjects.getDataFetchLimit();
//        busWhereClause = elementObjects.getExpandBusWhereClause();
//        relWhereClause = elementObjects.getExpandRelWhereClause();
//        List<XmlMapElementObject> elementObjectList = NullOrEmptyChecker.isNullOrEmpty(elementObjects.getXmlMapElementObject()) ? new ArrayList<>() : elementObjects.getXmlMapElementObject();
//
//        //Populating each object type in typeNames variable
//        for (XmlMapElementObject object : elementObjectList) {
//            typeNames.add(object.getType());
//            ITEM_TYPES_AND_RELATIONS_OF_CUSTOM_XML_MAP_LOGGER.debug("In OBJECT_TYPES_AND_RELATIONS_LOGGER:: " + object.getTypeShortName());
//            typeShortNameMap.put(object.getType(), object.getTypeShortName());
//        }
//
//        //Populating all selectables from an object's all attribute
//        if (!elementObjectList.isEmpty()) {
//            for (int i = 0; i < elementObjectList.size(); i++) {
//                XmlMapElementAttributes elementAttributes = elementObjectList.get(i).getXmlMapElementAttributes();
//                List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
//                for (XmlMapElementAttribute elementAttribute : elementAttributeList) {
//                    if (!typeSelectablesWithOutputName.containsKey(elementAttribute.getSelectable())) {
//                        typeSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
//                    }
//                }
//            }
//        }
//
//        if (NullOrEmptyChecker.isNull(mapper.getXmlMapElementBOMRelationships())) {
//            return;
//        }
//
//        XmlMapElementBOMRelationships elementRelationships = mapper.getXmlMapElementBOMRelationships();
//        List<XmlMapElementBOMRelationship> relationshipList = NullOrEmptyChecker.isNullOrEmpty(elementRelationships.getXmlMapElementBOMRelationship()) ? new ArrayList<>() : elementRelationships.getXmlMapElementBOMRelationship();
//
//        //Populating each relationship name in relationNames variable
//        for (XmlMapElementBOMRelationship relationship : relationshipList) {
//            //System.out.println(relationship.getName());
//            relationNames.add(relationship.getRelName());
//        }
//
//        //Populating all selectables from a relationship's all attribute
//        if (!relationshipList.isEmpty()) {
//            for (int i = 0; i < relationshipList.size(); i++) {
//                XmlMapElementAttributes elementAttributes = relationshipList.get(i).getXmlMapElementAttributes();
//                List<XmlMapElementAttribute> elementAttributeList = elementAttributes.getXmlMapElementAttribute();
//                for (XmlMapElementAttribute elementAttribute : elementAttributeList) {
//                    if (!relationSelectablesWithOutputName.containsKey(elementAttribute.getSelectable())) {
//                        relationSelectablesWithOutputName.put(elementAttribute.getSelectable(), elementAttribute.getFieldLabel());
//                    }
//                }
//            }
//        }
//    }
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

    @Override
    public List<String> getRunTimeInterfaceList() {
        return runTimeInterfaceList;
    }

    protected void setRunTimeInterfaceList(List<String> runTimeInterfaceList) {
        this.runTimeInterfaceList = runTimeInterfaceList;
    }
}
