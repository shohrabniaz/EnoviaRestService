package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.AttributeNotInRequestException;
import com.bjit.common.rest.app.service.enovia_pdm.exceptions.MastershipChangeException;
import com.bjit.common.rest.app.service.enovia_pdm.models.ChildData;
import com.bjit.common.rest.app.service.enovia_pdm.models.ParentChildModel;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperElementMemento;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMapperProcessor;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IMastershipAttributeBusinessLogic;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IStructureCollector;
import com.bjit.common.rest.app.service.enovia_pdm.utilities.IExpand;
import com.bjit.common.rest.app.service.enovia_pdm.utilities.IUnitConverter;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public class StructureCollector implements IStructureCollector {

    static final Logger STRUCTURE_COLLECTOR_LOGGER = Logger.getLogger(StructureCollector.class);
    private static final List<String> LENGTH_UNITS = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
    private static final List<String> AREA_UNITS = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
    private static final List<String> VOLUME_UNITS = Arrays.asList(PropertyReader.getProperty("volume.units").split("\\|"));
    private static final List<String> MASS_UNITS = Arrays.asList(PropertyReader.getProperty("mass.units").split("\\|"));
    private static final String DEFAULT_UNIT = PropertyReader.getProperty("pdm.default.inventory.unit");
    private static List<String> itemTypeList;
    private static List<String> relList;
    HashMap<String, ChildData> allItemsData = new HashMap<>();

    static {
        itemTypeList();
        relationshipTypeList();
    }

    public String comodityCode;
    public Set<String> set;
    public String commodityCode;
    @Autowired
    IMapperProcessor commonItemMapperProcessor;
    @Autowired
    IMastershipAttributeBusinessLogic mastershipAttributeBusinessLogic;
    @Autowired
    IExpand expand;
    @Autowired
    IUnitConverter unitConverter;

    private static void itemTypeList() {
        HashMap<String, String> properties = PropertyReader.getProperties("mastership.change.enovia.pdm.integration.item.expansion.type", Boolean.TRUE);

        itemTypeList = new ArrayList<>();
        properties.forEach((String propertyAttribute, String propertyValue) -> {
            itemTypeList.add(propertyValue);
        });
    }

    private static void relationshipTypeList() {
        HashMap<String, String> properties = PropertyReader.getProperties("mastership.change.enovia.pdm.integration.relationship.expansion.type", Boolean.TRUE);

        relList = new ArrayList<>();
        properties.forEach((String propertyAttribute, String propertyValue) -> {
            relList.add(propertyValue);
        });
    }

    @Override
    public HashMap<String, List<ParentChildModel>> fetchStructure(Context context, Item item) throws Exception {
        try {
            validateItem(context, item);
            HashMap<String, List<ParentChildModel>> parentChildRelationMap = fetchItemAndRelData(context, item.getId(), item.getType());
            return parentChildRelationMap;

        } catch (Exception ex) {
            STRUCTURE_COLLECTOR_LOGGER.error(ex);
//            throw new ItemNotFoundException(ex);
            throw ex;
        }
    }
    
    
    private void validateItem(Context context, Item item) {
        String itemId = Optional.ofNullable(item.getId()).filter(id -> !id.isEmpty()).orElseGet(() -> {
            String type = item.getType();
            String name = item.getName();
            String revision = item.getRevision();

            try {
                BusinessObject businessObject = new BusinessObject(type, name, revision, null);
                businessObject.open(context);
                String objectId = businessObject.getObjectId();
                return objectId;
            } catch (MatrixException ex) {
                STRUCTURE_COLLECTOR_LOGGER.error(ex);
                throw new RuntimeException(ex);
            }
        });
        item.setId(itemId);
    }

    private HashMap<String, List<ParentChildModel>> fetchItemAndRelData(Context context, String objectId, String typeName) throws Exception {
        BusinessObject parentBusinessObject = new BusinessObject(objectId);
        parentBusinessObject.open(context);
        String rootItemName = parentBusinessObject.getName();
        parentBusinessObject.close(context);

        HashMap<String, HashMap<String, String>> itemAttributeMap = commonItemMapperProcessor.processAttributeXMLMapper(new CommonItemParameters(), typeName);
        SelectList selectItemAttributeList = getAttributeList(itemAttributeMap, PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.map"));
        SelectList selectItemPropertyList = getPropertyList(itemAttributeMap, PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.property.map"));
        SelectList selectItemRelationAttributeList = getAttributeList(itemAttributeMap, PropertyReader.getProperty("mastership.change.enovia.pdm.integration.rel.map"));

        selectItemAttributeList.addAll(selectItemPropertyList);

        HashMap<String, List<ParentChildModel>> parentChildRelationMap = expand.expand(context, parentBusinessObject.getObjectId(), itemTypeList, relList, null, null, (short) 99, selectItemAttributeList, selectItemRelationAttributeList);
        Map<String, String> reversedItemAttributeMap = itemAttributeMap.get(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.map")).entrySet().stream().collect(Collectors.toMap(x -> x.getValue(), x -> x.getKey()));
        Map<String, String> reversedPorpertyMap = itemAttributeMap.get(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.item.property.map")).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Map<String, String> reversedRelAttributeMap = itemAttributeMap.get(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.rel.map")).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        parentChildRelationMap.forEach((String key, List<ParentChildModel> parentChildModelList) -> {
            try {
                STRUCTURE_COLLECTOR_LOGGER.info("Parent item is : " + key);

                // Root items attribute modification
                if (key.equalsIgnoreCase("standAloneItem")) {
                    setRootItemsAttribute(parentChildModelList.get(0), reversedItemAttributeMap, reversedPorpertyMap);
                    return;
                }

                if (key.equalsIgnoreCase(rootItemName)) {
                    setRootItemsAttribute(parentChildModelList.get(0), reversedItemAttributeMap, reversedPorpertyMap);
                }

                /**
                 * Why there is 3 loops. May be 1 loop do the job. Need refactor
                 */
                parentChildModelList.forEach(parentChildModel -> {
                    comodityCode = "";
                    setChildItemData(parentChildModel, reversedItemAttributeMap, reversedPorpertyMap);
                    setChildPropertyData(parentChildModel, reversedPorpertyMap);
                    setChildRelData(parentChildModel, reversedRelAttributeMap);
                });

                /**
                 * Deleted these loops after adding those at 144 and 145 line
                 */
//                parentChildModelList.forEach(parentChildModel -> {
//                    setChildPropertyData(parentChildModel, reversedPorpertyMap);
//                });
//
//                parentChildModelList.forEach(parentChildModel -> {
//                    setChildRelData(parentChildModel, reversedRelAttributeMap);
//                });
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                // throw new RuntimeException(exp);
            }
        });

        parentChildRelationMap.forEach((String parentName, List<ParentChildModel> childList) -> {
            childList.stream().forEach((ParentChildModel updateParentOwnerAndAttribute) -> {
                ChildData parentData = updateParentOwnerAndAttribute.getParentData();
                String parentId = parentData.getId();
                Optional.ofNullable(allItemsData.get(parentId)).ifPresent((childData) -> {
                    parentData.setOwner(Optional.ofNullable(parentData.getOwner()).orElseGet(() -> allItemsData.get(parentId).getOwner()));
                    parentData.setAttributeMap(Optional.ofNullable(parentData.getAttributeMap()).orElseGet(() -> allItemsData.get(parentId).getAttributeMap()));
                });
            });
        });

        return parentChildRelationMap;
    }

    private String makeCommodityCode(String pdmAttributeName, String itemAttributeValue, String comodityCode) {
        Pattern p = Pattern.compile("Commodity code ([A-Z]{2})");
        Matcher m = p.matcher(pdmAttributeName);
        if ((m.matches())) {
            System.out.printf("Name entered: %s\n", m.group(1));
            comodityCode += m.group(1) + "|" + itemAttributeValue;
        }
//        if (itemAttributeValue != "") {
//            comodityCode += m.group(1) + "|" + itemAttributeValue;
//        }
        return comodityCode;
    }

    private String processCommodityCode(String changeCommodityCode) {
        StringJoiner finalchangeCommodityCode = new StringJoiner("\n");
//        String finalchangeCommodityCode = "";
        Pattern commodity_pattern_US = Pattern.compile(".*(US\\|[0-9]*).*");
        Matcher commodity_matcher_US = commodity_pattern_US.matcher(changeCommodityCode);
        Pattern commodity_pattern_CH = Pattern.compile(".*(CH\\|[0-9]*).*");
        Matcher commodity_matcher_CH = commodity_pattern_CH.matcher(changeCommodityCode);
        Pattern commodity_pattern_EU = Pattern.compile(".*(EU\\|[0-9]*).*");
        Matcher commodity_matcher_EU = commodity_pattern_EU.matcher(changeCommodityCode);
        if (commodity_matcher_US.matches() && commodity_matcher_US.group(1).matches(".*\\d.*")) {

            System.out.printf(commodity_matcher_US.group(1));
//            finalchangeCommodityCode += commodity_matcher_US.group(1) + "\n";
            finalchangeCommodityCode.add(commodity_matcher_US.group(1));

        }
        if (commodity_matcher_CH.matches() && commodity_matcher_CH.group(1).matches(".*\\d.*")) {
            System.out.printf(commodity_matcher_CH.group(1));
//            finalchangeCommodityCode += commodity_matcher_CH.group(1) + "\n";
            finalchangeCommodityCode.add(commodity_matcher_CH.group(1));
        }

        if (commodity_matcher_EU.matches() && commodity_matcher_EU.group(1).matches(".*\\d.*")) {
            System.out.printf(commodity_matcher_EU.group(1));
//            finalchangeCommodityCode += commodity_matcher_EU.group(1) + "\n";
            finalchangeCommodityCode.add(commodity_matcher_EU.group(1));
        }
        return finalchangeCommodityCode.toString();
    }

    private void setRootItemsAttribute(ParentChildModel parentChildModel, Map<String, String> reversedItemAttributeMap, Map<String, String> reversedItemPropertyMap) {
        //Root items attribute modification        
        IMapperElementMemento mapperElementMemento = commonItemMapperProcessor.getMapperElementMemento();

        ChildData parentData = parentChildModel.getParentData();
        HashMap<String, String> parentItemAttributeMap = Optional.ofNullable(parentData.getAttributeMap()).orElseGet(() -> expand.getRootItemInfo().getParentData().getAttributeMap());
        HashMap<String, String> parentItemPropertyMap = Optional.ofNullable(parentData.getPropertyMap()).orElseGet(() -> expand.getRootItemInfo().getParentData().getPropertyMap());
        parentData.setOwner(expand.getRootItemInfo().getParentData().getOwner());
        parentData.getTnr().setRevision(expand.getRootItemInfo().getParentData().getTnr().getRevision());

        final BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        HashMap<String, String> rootAttributes = new HashMap<>();

        parentItemAttributeMap.entrySet().stream().collect(Collectors.toMap(mapData -> reversedItemAttributeMap.get(businessObjectOperations.getAttributeName(mapData.getKey())), mapData -> mapData.getValue()));

        parentItemAttributeMap.forEach((String attrKey, String attrValue) -> {
            try {
                String pdmAttributeName = reversedItemAttributeMap.get(businessObjectOperations.getAttributeName(attrKey));
                String itemAttributeValue = mastershipAttributeBusinessLogic.getItemAttribute(mapperElementMemento, pdmAttributeName, attrValue);
                if (pdmAttributeName.equalsIgnoreCase("Inventory unit") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    //return;
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.inventory.unit");
 //                   System.out.println(PropertyReader.getProperty("pdm.default.inventory.unit"));
                }
                
                if (pdmAttributeName.equalsIgnoreCase("Item group") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.item.group");
                }
                
                if (pdmAttributeName.equalsIgnoreCase("Engineering group") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.engineering.group");
                }
                
                if (pdmAttributeName.contains("Commodity code")) {

                    comodityCode += this.makeCommodityCode(pdmAttributeName, itemAttributeValue, comodityCode);

                }
                rootAttributes.put(pdmAttributeName, itemAttributeValue);
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                STRUCTURE_COLLECTOR_LOGGER.debug("Attribute will not be placed in the request data of PDM");
            }
            rootAttributes.put("Commodity code", comodityCode);
        });

        parentItemPropertyMap.forEach((String attrKey, String rootPropValue) -> {
            try {
                String pdmAttributeName = reversedItemPropertyMap.get(businessObjectOperations.getAttributeName(attrKey));
                String rootPropertyValue = mastershipAttributeBusinessLogic.getItemAttribute(mapperElementMemento, pdmAttributeName, rootPropValue);
                rootAttributes.put(pdmAttributeName, rootPropertyValue);
            } catch (MastershipChangeException exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
//                throw exp;
                STRUCTURE_COLLECTOR_LOGGER.error("Status is empty for the item");
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.debug(exp);
                STRUCTURE_COLLECTOR_LOGGER.debug("Attribute will not be placed in the reqeust data of PDM");
            }
        });

        processCommodityCode(rootAttributes);
        parentData.setAttributeMap(rootAttributes);
    }

    private void processCommodityCode(HashMap<String, String> rootAttributes) {
        String rootCommodityCode = rootAttributes.get("Commodity code");

        Optional.ofNullable(rootCommodityCode).ifPresent(commodityCode-> {
            System.out.printf("COMMODITY CODE DATA:   ", rootAttributes.get("Commodity code"));
            String processedCommodityCode = processCommodityCode(commodityCode);

            System.out.printf("finalchangeCommodityCode   ", processedCommodityCode);
            rootAttributes.put("Commodity code", processedCommodityCode);
            rootAttributes.remove("Commodity code EU");
            rootAttributes.remove("Commodity code CH");
            rootAttributes.remove("Commodity code US");
        });
    }

    private void setChildItemData(ParentChildModel parentChildModel, Map<String, String> reversedAttributeMap, Map<String, String> reversedItemPropertyMap) {
        // Child items attribute modification

        IMapperElementMemento mapperElementMemento = commonItemMapperProcessor.getMapperElementMemento();

        HashMap<String, String> childItemAttributeMap = parentChildModel.getChildData().getAttributeMap();
        HashMap<String, String> childItemPropertyMap = parentChildModel.getChildData().getPropertyMap();

        HashMap<String, String> itemAttributes = new HashMap<>();
//        childItemAttributeMap.entrySet().stream().collect(Collectors.toMap(mapData -> reversedAttributeMap.get(mapData.getKey()), mapData -> mapData.getValue()));

        childItemAttributeMap.forEach((String attrKey, String attrValue) -> {
            try {
                
                
                String pdmAttributeName = reversedAttributeMap.get(attrKey);
                //System.out.println(attrKey,attrValue);
//                STRUCTURE_COLLECTOR_LOGGER.error(attrKey);
//                STRUCTURE_COLLECTOR_LOGGER.error(attrValue);
                
                if (Optional.ofNullable(pdmAttributeName).isEmpty()) {
                    return;
                }
                

                String itemAttributeValue = mastershipAttributeBusinessLogic.getItemAttribute(mapperElementMemento, pdmAttributeName, attrValue);

                if (pdmAttributeName.equalsIgnoreCase("Inventory unit") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    //return;
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.inventory.unit");
 //                   System.out.println(PropertyReader.getProperty("pdm.default.inventory.unit"));
                }
                
                if (pdmAttributeName.equalsIgnoreCase("Item group") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.item.group");
                }
                
                if (pdmAttributeName.equalsIgnoreCase("Engineering group") && NullOrEmptyChecker.isNullOrEmpty(itemAttributeValue)) {
                    itemAttributeValue = PropertyReader.getProperty("pdm.default.engineering.group");
                }
                
                if (pdmAttributeName.contains("Commodity code")) {

                    comodityCode += this.makeCommodityCode(pdmAttributeName, itemAttributeValue, comodityCode);

                }

                itemAttributes.put(pdmAttributeName, itemAttributeValue);
            } catch (MastershipChangeException exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
//                throw exp;
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                STRUCTURE_COLLECTOR_LOGGER.error("Attribute will not be placed in the reqeust data of PDM");
//                throw exp;
            }
            itemAttributes.put("Commodity code", comodityCode);

        });

        childItemPropertyMap.forEach((String attrKey, String attrValue) -> {
            try {
                String pdmAttributeName = reversedItemPropertyMap.get(attrKey);

                if (Optional.ofNullable(pdmAttributeName).isEmpty()) {
                    return;
                }
                

                String itemAttributeValue = mastershipAttributeBusinessLogic.getItemAttribute(mapperElementMemento, pdmAttributeName, attrValue);
                itemAttributes.put(pdmAttributeName, itemAttributeValue);
            } catch (MastershipChangeException exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                throw exp;
            } catch (AttributeNotInRequestException exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                STRUCTURE_COLLECTOR_LOGGER.error("Attribute will not be placed in the reqeust data of PDM");
                throw exp;
            }
        });
        String changeCommodityCode = itemAttributes.get("Commodity code");
        System.out.printf("COMMODITY CODE DATA:   ", itemAttributes.get("Commodity code"));
        String finalchangeCommodityCode = processCommodityCode(changeCommodityCode);

        System.out.printf("finalchangeCommodityCode   ", finalchangeCommodityCode);
        itemAttributes.put("Commodity code", finalchangeCommodityCode);
        itemAttributes.remove("Commodity code EU");
        itemAttributes.remove("Commodity code CH");
        itemAttributes.remove("Commodity code US");
        parentChildModel.getChildData().setAttributeMap(itemAttributes);

//        Optional.ofNullable(allItemsData.get(parentChildModel.getParentData().getId())).ifPresent((ChildData parentData) -> { 
//            parentData.setAttributeMap(itemAttributes);
//            parentData.setOwner(parentChildModel.getChildData().getOwner());
//        });
        allItemsData.putIfAbsent(parentChildModel.getChildData().getId(), parentChildModel.getChildData());
    }

    private void setChildPropertyData(ParentChildModel parentChildModel, Map<String, String> propertyDataMap) {
        // Child items property modification

        HashMap<String, String> childItemAttributeMap = parentChildModel.getChildData().getPropertyMap();

        HashMap<String, String> itemProperties = new HashMap<>();
        childItemAttributeMap.entrySet().stream().collect(Collectors.toMap(mapData -> propertyDataMap.get(mapData.getKey()), mapData -> mapData.getValue()));

        childItemAttributeMap.forEach((attrKey, attrValue) -> {
            itemProperties.put(propertyDataMap.get(attrKey), attrValue);
        });

        parentChildModel.getChildData().setPropertyMap(itemProperties);
    }

    private void setChildRelData(ParentChildModel parentChildModel, Map<String, String> reversedRelAttributeMap) {
        // Child items attribute modification

        System.out.println(parentChildModel.getChildData().getTnr().toString());

        HashMap<String, String> childRelAttributeMap = parentChildModel.getItemRelationMap();

        HashMap<String, String> itemAttributes = new HashMap<>();
        childRelAttributeMap.entrySet().stream().collect(Collectors.toMap(mapData -> reversedRelAttributeMap.get(mapData.getKey()), mapData -> mapData.getValue()));

        childRelAttributeMap.forEach((attrKey, attrValue) -> {
            itemAttributes.put(reversedRelAttributeMap.get(attrKey), attrValue);
        });

        parentChildModel.setItemRelationMap(itemAttributes);
    }

    private SelectList getAttributeList(HashMap<String, HashMap<String, String>> itemAttributeMap, String attributeType) {
        SelectList selectBusinessAttributeList = new SelectList();

        itemAttributeMap.get(attributeType).forEach((sourceAttribute, destinationAttributeValue) -> {
            selectBusinessAttributeList.addAttribute(destinationAttributeValue);
        });

        return selectBusinessAttributeList;
    }

    private SelectList getPropertyList(HashMap<String, HashMap<String, String>> itemAttributeMap, String attributeType) {
        SelectList selectBusinessAttributeList = new SelectList();

        itemAttributeMap.get(attributeType).forEach((sourceAttribute, destinationAttributeValue) -> {
            selectBusinessAttributeList.add(destinationAttributeValue);
        });

        return selectBusinessAttributeList;
    }

    @Override
    public HashMap<String, List<ParentChildModel>> mergeStructure(HashMap<String, List<ParentChildModel>> parentChildModelListMap) throws Exception {
        HashMap<String, List<ParentChildModel>> parentChildModelMap = new HashMap<>();

        //JSON json = new JSON();
        //System.out.println("List Item : " + json.serialize(parentChildModelListMap));
        parentChildModelListMap.forEach((String parentName, List<ParentChildModel> childList) -> {
            try {
                List<ParentChildModel> parentChildModelList = new ArrayList<>();
                parentChildModelMap.put(parentName, parentChildModelList);
                childList.stream().forEach(parentChildModel -> {

                    String inventoryUnit = prepareAttributeDataAndGetInventoryUnit(parentName, parentChildModel);

                    if (!parentName.equalsIgnoreCase("standAloneItem")) {
                        prepareRelationshipData(parentChildModel, inventoryUnit);
                    }

                    ChildData childData = parentChildModel.getChildData();
                    Optional.ofNullable(childData).ifPresent((ChildData childItem) -> {
                        if (!childItem.getTnr().getType().equalsIgnoreCase(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.own.design.item"))) {
                            childItem.getAttributeMap().clear();
                            childItem.setOwner(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.commercial.items.owner"));
                            childItem.getTnr().setRevision(null);
                        }
                    });

                    parentChildModelList.add(parentChildModel);
                });
            } catch (Exception exp) {
                STRUCTURE_COLLECTOR_LOGGER.error(exp);
                throw exp;
            }
        });

        return parentChildModelMap;
    }

    private void prepareRelationshipData(ParentChildModel parentChildModel, String inventoryUnit) {
        HashMap<String, String> itemRelationMap = parentChildModel.getItemRelationMap();
        Integer numberOfItems = parentChildModel.getNetQuantity();
        String netQuantity = itemRelationMap.get("Net quantity");
        String length = itemRelationMap.get("Length");
        String width = itemRelationMap.get("Width");

        if (DEFAULT_UNIT.equalsIgnoreCase(inventoryUnit)) {
            pcsItemsLogic(itemRelationMap);
        } else if (MASS_UNITS.contains(inventoryUnit)) {
            massAndVolumeItemsLogic(inventoryUnit, netQuantity, itemRelationMap);
        } else if (VOLUME_UNITS.contains(inventoryUnit)) {
            massAndVolumeItemsLogic(inventoryUnit, netQuantity, itemRelationMap);
        } else if (AREA_UNITS.contains(inventoryUnit)) {
            areaItemsLogic(inventoryUnit, netQuantity, numberOfItems, itemRelationMap, length, width);
//                        areaItemsLogic(inventoryUnit, netQuantity, numberOfItems, numberOfParentItems, itemRelationMap, length, width);
        } else if (LENGTH_UNITS.contains(inventoryUnit)) {
            lengthItemsLogic(inventoryUnit, netQuantity, numberOfItems, itemRelationMap, length);
//                        lengthItemsLogic(inventoryUnit, netQuantity, numberOfItems, numberOfParentItems, itemRelationMap, length);
        } else {//throw exception
            throw new RuntimeException("inventoryUnit not followed criteria");
        }
    }

    private String prepareAttributeDataAndGetInventoryUnit(String parentName, ParentChildModel parentChildModel) {
        HashMap<String, String> itemAttributeMap = parentName.equalsIgnoreCase("standAloneItem") ? parentChildModel.getParentData().getAttributeMap() : parentChildModel.getChildData().getAttributeMap();
        String inventoryUnit = itemAttributeMap.get("Inventory unit");

        if (NullOrEmptyChecker.isNullOrEmpty(inventoryUnit)) {
            inventoryUnit = PropertyReader.getProperty("pdm.default.inventory.unit");
            itemAttributeMap.remove("Inventory unit");
        }
        return inventoryUnit;
    }

    private HashMap<String, String> lengthItemsLogic(String inventoryUnit, String netQuantity, Integer numberOfItems, HashMap<String, String> itemRelationMap, String length) throws NumberFormatException {
        String coefficient = unitConverter.reverseUnitConversion(inventoryUnit, (Double.parseDouble(netQuantity)) * numberOfItems).toString();
        itemRelationMap.replace("Net quantity", coefficient);
        itemRelationMap.put("Number of units", numberOfItems.toString());

        String convertedLength = unitConverter.reverseUnitConversion("mm", Double.parseDouble(length)).toString();
        itemRelationMap.replace("Length", convertedLength);

        itemRelationMap.remove("Width", "0");
        return itemRelationMap;
    }

    private HashMap<String, String> areaItemsLogic(String inventoryUnit, String netQuantity, Integer numberOfItems, HashMap<String, String> itemRelationMap, String length, String width) throws NumberFormatException {
        String coefficient = unitConverter.reverseUnitConversion(inventoryUnit, (Double.parseDouble(netQuantity)) * numberOfItems).toString();
        itemRelationMap.replace("Net quantity", coefficient);
        itemRelationMap.put("Number of units", numberOfItems.toString());

        if (inventoryUnit.equalsIgnoreCase("ft2")) {
            itemRelationMap.remove("Number of units");
        }

        String convertedLength = unitConverter.reverseUnitConversion("mm", Double.parseDouble(length)).toString();
        itemRelationMap.replace("Length", convertedLength);

        String convertedWidth = unitConverter.reverseUnitConversion("mm", Double.parseDouble(width)).toString();
        itemRelationMap.replace("Width", convertedWidth);
        return itemRelationMap;
    }

    private void massAndVolumeItemsLogic(String inventoryUnit, String netQuantity, HashMap<String, String> itemRelationMap) throws NumberFormatException {
        String coefficient = unitConverter.reverseUnitConversion(inventoryUnit, Double.parseDouble(netQuantity)).toString();
        itemRelationMap.replace("Net quantity", coefficient);
        itemRelationMap.remove("Length");
        itemRelationMap.remove("Width");
    }

    private void pcsItemsLogic(HashMap<String, String> itemRelationMap) {
        itemRelationMap.remove("Number of units");
        itemRelationMap.remove("Length");
        itemRelationMap.remove("Width");
    }
}
