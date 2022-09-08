package com.bjit.common.rest.app.service.enovia_pdm.utilities;

import com.bjit.common.rest.app.service.enovia_pdm.models.ChildData;
import com.bjit.common.rest.app.service.enovia_pdm.models.ParentChildModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
//@Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
@Scope(value = "prototype")
public class Expand implements IExpand {

    private static final Logger EXPAND_LOGGER = Logger.getLogger(Expand.class);
    private final HashMap<String, HashMap<String, String>> itemAttributeMap;
    ParentChildModel rootItemInfo;
//    HashMap<String, ChildData> listMapOfAllItems = new HashMap<>();

    public Expand() {
        itemAttributeMap = new HashMap<>();
    }

    @Override
    public HashMap<String, List<ParentChildModel>> expand(Context context, String rootObjectId, List<String> busPatternList, List<String> relPatternList, String businessObjectWhereExpression, String relationshipWhereExpression, Short expandLevel, SelectList selectBusinessAttributeList, SelectList selectRelationAttributeList) throws Exception {

        EXPAND_LOGGER.info("Expansion has started");

//        HashMap<String, List<ParentChildModel>> childParentHashMap = new HashMap<>();
        BusinessObject rootBusinessObject = new BusinessObject(rootObjectId);

        ExpansionWithSelect expandSelect = expandRootItem(busPatternList, relPatternList, rootBusinessObject, context, selectBusinessAttributeList, selectRelationAttributeList, expandLevel);

//        List<ParentChildModel> rootItemInfo = getRootItemInfo(selectBusinessAttributeList, childParentHashMap, expandSelect);
        ParentChildModel rootItemInfo = getRootItemInfo(context,rootBusinessObject,selectBusinessAttributeList, expandSelect.getRootWithSelect() , rootObjectId);
        rootItemInfo.getParentData().setId(rootObjectId);
        this.rootItemInfo = rootItemInfo;
//        TNR parentTnr = rootItemInfo.getParentData().getTnr();
//        listMapOfAllItems.put(rootObjectId, rootItemInfo.getParentData());
//        listMapOfParentItems.put(rootObjectId, rootItemInfo.getParentData());

        RelationshipWithSelectList relationships = expandSelect.getRelationships();
        HashMap<String, ParentChildModel> relationshipsAmongParentChild = new HashMap<>();
        relationships.getIterator().forEach((RelationshipWithSelect relationshipWithSelect) -> {
            try {
                relationshipWithSelect.open(context);
                BusinessObject parentItem = relationshipWithSelect.getFrom();

                String relationshipId = relationshipWithSelect.getName();

                BusinessObjectWithSelect busSelect = relationshipWithSelect.getTarget();
                ParentChildModel parentChildModel = prepareParentChildModel(context, busSelect, parentItem, relationshipId);

                HashMap<String, String> childObjectAttributeMap = getChildAttributes(selectBusinessAttributeList, busSelect, parentChildModel);
                parentChildModel.getChildData().setAttributeMap(childObjectAttributeMap);

                System.out.println("Type : " + busSelect.getSelectData("type") + " name : " + busSelect.getSelectData("name") + " revision : " + busSelect.getSelectData("MBOM_MBOMPDM.MBOM_PDM_Revision"));

                HashMap<String, String> childRelAttributeMap = getChildRelationshipAttributes(selectRelationAttributeList, relationshipWithSelect);
                parentChildModel.setItemRelationMap(childRelAttributeMap);

                HashMap<String, String> childPropertyMap = getItemProperties(busSelect);
                parentChildModel.getChildData().setPropertyMap(childPropertyMap);
//                prepareParentChildModel(parentChildModel);
                parentChildModel.getRelationshipId();
                relationshipsAmongParentChild.putIfAbsent(parentChildModel.getRelationshipId(), parentChildModel);
                relationshipWithSelect.close(context);
            } catch (MatrixException ex) {
                EXPAND_LOGGER.error(ex);
            }
        });

        HashMap<String, List<ParentChildModel>> mapOfParentChildModel = mapOfParentChildModel(relationshipsAmongParentChild);
        if (mapOfParentChildModel.isEmpty()) {
            rootItemInfo.setLevel(0);
            rootItemInfo.setNetQuantity(0);
            mapOfParentChildModel.put("standAloneItem", List.of(rootItemInfo));
        } 

        return mapOfParentChildModel;
    }

    //    private ParentChildModel getRootItemInfo(SelectList selectBusinessAttributeList, HashMap<String, List<ParentChildModel>> childParentHashMap, ExpansionWithSelect expandSelect) {
//        ParentChildModel rootItemInfo = getRootItemInfo(selectBusinessAttributeList, expandSelect.getRootWithSelect());
////        List<ParentChildModel> rootItemList = new ArrayList<>();
////        rootItemList.add(rootItemInfo);
////        childParentHashMap.put("rootItem", rootItemList);
//        return rootItemInfo;
//    }
    //    protected void addChildModel(BusinessObjectWithSelect busSelect, ParentChildModel parentChildModel, HashMap<String, List<ParentChildModel>> childParentHashMap) {
//        if (childParentHashMap.containsKey(busSelect.getSelectData("name"))) {
//            childParentHashMap.get(busSelect.getSelectData("name")).add(parentChildModel);
//        } else {
//            List<ParentChildModel> childItemList = new ArrayList<>();
//            childItemList.add(parentChildModel);
//            childParentHashMap.put(busSelect.getSelectData("name"), childItemList);
//        }
//    }
//    protected void prepareParentChildModel(ParentChildModel parentChildModel) {
//        parentChildModel.getRelationshipId();
//        relationshipsAmongParentChild.putIfAbsent(parentChildModel.getRelationshipId(), parentChildModel);
//    }

protected HashMap<String, List<ParentChildModel>> mapOfParentChildModel(HashMap<String, ParentChildModel> relationshipsAmongParentChild) {
        HashMap<String, List<ParentChildModel>> parentChildMap = new HashMap<>();
        relationshipsAmongParentChild.forEach((String, parentChildModel) -> {
            ChildData parentData = parentChildModel.getParentData();
            String parentName = parentData.getTnr().getName();

//            ChildData parentInfo = listMapOfAllItems.get(parentData.getId());
//            parentData.setOwner(parentInfo.getOwner());
//            parentData.setAttributeMap(parentInfo.getAttributeMap());

            if (parentChildMap.containsKey(parentName)) {
                List<ParentChildModel> parentChildModelList = parentChildMap.get(parentName);
                ParentChildModel parentChildDataForQuantityUpdate = parentChildModelList.stream().filter(childData -> childData.getChildData().getTnr().getName().equalsIgnoreCase(parentChildModel.getChildData().getTnr().getName())).findFirst().orElseGet(() -> {
                    parentChildModelList.add(parentChildModel);
                    parentChildModel.setNetQuantity(0);
                    return parentChildModel;
                });

                parentChildDataForQuantityUpdate.setNetQuantity(parentChildDataForQuantityUpdate.getNetQuantity() + 1);
            } else {
                List<ParentChildModel> parentChildModelList = new ArrayList<>();
                parentChildMap.put(parentName, parentChildModelList);
                parentChildModelList.add(parentChildModel);
                parentChildModel.setNetQuantity(1);
            }
        });

        return parentChildMap;
    }

    private HashMap<String, String> getChildRelationshipAttributes(SelectList selectRelationAttributeList, RelationshipWithSelect relationshipWithSelect) {
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

        HashMap<String, String> childRelAttributeMap = new HashMap<>();
        selectRelationAttributeList.stream()/*.parallel()*/.forEach(selectData -> {
                    String attributeName = businessObjectOperations.getAttributeName(selectData);
                    childRelAttributeMap.put(attributeName, relationshipWithSelect.getSelectData(selectData));
                });
        return childRelAttributeMap;
    }

    private ParentChildModel prepareParentChildModel(Context context, BusinessObjectWithSelect childAttributes, BusinessObject parentBusinessObject, String relationshipId) throws MatrixException {
        ParentChildModel parentChildModel = new ParentChildModel();

        /**
         * Need to upgrade from.getAttributeValues(context,
         * "MBOM_MBOMPDM.MBOM_PDM_Revision")
         */
        childAttributes.open(context);
        prepareChildData(childAttributes, parentChildModel);
        childAttributes.close(context);
        prepareParentData(context, parentBusinessObject, parentChildModel);

        parentChildModel.setRelationshipId(relationshipId);
        return parentChildModel;
    }

    private void prepareChildData(BusinessObjectWithSelect childAttributes, ParentChildModel parentChildModel) {
        String typeName = PDMTypes.valueOf(childAttributes.getSelectData("type")).getValue();

        if (typeName.equalsIgnoreCase(PropertyReader.getProperty("mastership.change.enovia.pdm.integration.commercial.items"))) {
            String type =  childAttributes.getSelectData("type");
            String componentType = childAttributes.getSelectData("attribute[VAL_VALComponent.VAL_CommercialItemFamily]");
            
            String materialType = childAttributes.getSelectData("attribute[VAL_VALComponentMaterial.VAL_CommercialItemFamily]");
            if(type.equalsIgnoreCase("VAL_VALComponentMaterial"))
                typeName = childAttributes.getSelectData("attribute[VAL_VALComponentMaterial.VAL_CommercialItemFamily]");
            else if(type.equalsIgnoreCase("VAL_VALComponent"))
                typeName = childAttributes.getSelectData("attribute[VAL_VALComponent.VAL_CommercialItemFamily]");
//            typeName = Optional.ofNullable(componentType).filter(type -> !type.isEmpty()).orElse(materialType);
//            return;
        }

        ChildData childData = Optional.ofNullable(parentChildModel.getChildData()).orElse(new ChildData());
        parentChildModel.setChildData(childData);
        String objectId = childAttributes.getSelectData("id");
        childData.setId(objectId);
        String name = childAttributes.getSelectData("name");
        String revision = childAttributes.getSelectData("MBOM_MBOMPDM.MBOM_PDM_Revision");
        childData.setTnr(new TNR(typeName, name, revision));
    }

    private void prepareParentData(Context context, BusinessObject parentBusinessObject, ParentChildModel parentChildModel) throws MatrixException {
        ChildData parentData = Optional.ofNullable(parentChildModel.getParentData()).orElse(new ChildData());
        parentChildModel.setParentData(parentData);

        String parentBusinessObjectId = parentBusinessObject.getObjectId();
        HashMap<String, String> parentAttributeMap = itemAttributeMap.get(parentBusinessObjectId);

        if (NullOrEmptyChecker.isNullOrEmpty(parentAttributeMap.get("type"))) {
            parentBusinessObject.open(context);
            parentAttributeMap.replace("type", parentBusinessObject.getTypeName());
            parentAttributeMap.replace("name", parentBusinessObject.getName());
            parentAttributeMap.replace("revision", parentBusinessObject.getRevision());
        }

        String parentTypeName = PDMTypes.valueOf(parentAttributeMap.get("type")).getValue();
        String name = parentAttributeMap.get("name");
        String revision = parentAttributeMap.get("MBOM_MBOMPDM.MBOM_PDM_Revision");
        parentData.setTnr(new TNR(parentTypeName, name, revision));
        parentData.setId(parentBusinessObjectId);
    }
    
    private ExpansionWithSelect expandRootItem(List<String> busPatternList, List<String> relPatternList, BusinessObject rootBusinessObject, Context context, SelectList selectBusinessAttributeList, SelectList selectRelationAttributeList, Short expandLevel) throws MatrixException {
        try {
            String patternDelimiter = ",";
            Pattern typePattern = (busPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelimiter, busPatternList));
            Pattern relPattern = (relPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelimiter, relPatternList));
            ExpansionWithSelect expandSelect = rootBusinessObject.expandSelect(context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusinessAttributeList, selectRelationAttributeList, false, true, expandLevel);
            return expandSelect;
        } catch (MatrixException exp) {
            exp.addMessage("(Invalid Object)");
            EXPAND_LOGGER.error(exp);
            throw exp;
        }
    }

    private ParentChildModel getRootItemInfo(Context context, BusinessObject parentObject,SelectList selectBusinessAttributeList, BusinessObjectWithSelect rootWithSelect, String rootObjectID) {
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

        ParentChildModel rootItemInfo = new ParentChildModel();
        ChildData parentData = new ChildData();
        rootItemInfo.setParentData(parentData);
        String typeName = rootWithSelect.getSelectData("type");
        String name = rootWithSelect.getSelectData("name");
        String revision = rootWithSelect.getSelectData("attribute[MBOM_MBOMPDM.MBOM_PDM_Revision]");

        rootItemInfo.getParentData().setTnr(new TNR(PDMTypes.valueOf(typeName).getValue(), name, revision));

        HashMap<String, String> rootItemAttribute = new HashMap<>();
        HashMap<String, String> rootItemProperties = new HashMap<>();
        HashMap<String, String> rootAttribute = new HashMap<>();
        rootAttribute.put("type", typeName);
        rootAttribute.put("name", name);
        rootAttribute.put("revision", revision);

        itemAttributeMap.put(rootWithSelect.getSelectData("id"), rootAttribute);

        selectBusinessAttributeList.stream().forEach((String attributeName) -> {
            try {
                String attribute = businessObjectOperations.getAttributeName(attributeName);
                String selectData = rootWithSelect.getSelectData(attributeName);
                

                if (attribute.equalsIgnoreCase(attributeName)) {
                    rootItemProperties.put(attributeName, selectData);
                } else if (attribute.equalsIgnoreCase("MBOM_MBOMPDM.MBOM_PDM_Owner_Group")) {
                    if (NullOrEmptyChecker.isNullOrEmpty(selectData)) {
                    selectData = businessObjectOperations.getPreviousBOAttributesListOfRevisedItem(context,parentObject).get("MBOM_MBOMPDM.MBOM_PDM_Owner_Group");
                    businessObjectOperations.updateObject(context,rootObjectID,"MBOM_MBOMPDM.MBOM_PDM_Owner_Group",selectData);
                }
                    rootItemInfo.getParentData().setOwner(selectData);
                    rootAttribute.put("MBOM_MBOMPDM.MBOM_PDM_Owner_Group", selectData);
                } else if (attribute.equalsIgnoreCase("MBOM_MBOMPDM.MBOM_PDM_Revision")) {
                    return;
                } else {
                    rootItemAttribute.put(attributeName, selectData);
                    rootAttribute.put(attributeName, selectData);
                }
            } catch (Exception exp) {
                EXPAND_LOGGER.error(exp);
            }
        });

        rootItemInfo.getParentData().setAttributeMap(rootItemAttribute);
        rootItemInfo.getParentData().setPropertyMap(rootItemProperties);
        return rootItemInfo;
    }

    private HashMap<String, String> getChildAttributes(SelectList selectBusinessAttributeList, BusinessObjectWithSelect busSelect, ParentChildModel parentChildModel) {
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

        HashMap<String, String> childObjectAttributeMap = new HashMap<>();

        HashMap<String, String> childAttribute;

        String childId = busSelect.getSelectData("id");
        if (!itemAttributeMap.containsKey(childId)) {
            childAttribute = new HashMap<>();
            itemAttributeMap.put(childId, childAttribute);
        } else {
            childAttribute = itemAttributeMap.get(childId);
        }

        selectBusinessAttributeList.stream()/*.parallel()*/.forEach(selectData -> {
                    final String attributeName = businessObjectOperations.getAttributeName(selectData);
                    String attributeValue = busSelect.getSelectData("attribute[" + attributeName + "]");
                    if (Optional.ofNullable(attributeValue)/*.filter(value -> !value.isEmpty())*/.isPresent()) {

                        if (attributeName.equalsIgnoreCase("MBOM_MBOMPDM.MBOM_PDM_Owner_Group")) {
                            parentChildModel.getChildData().setOwner(attributeValue);
                            childAttribute.putIfAbsent(attributeName, attributeValue);
                        } else if (attributeName.equalsIgnoreCase("MBOM_MBOMPDM.MBOM_PDM_Revision")) {
                            parentChildModel.getChildData().getTnr().setRevision(attributeValue);
                            childAttribute.putIfAbsent(attributeName, attributeValue);
                        } else {
                            childObjectAttributeMap.put(attributeName, attributeValue);
                            childAttribute.putIfAbsent(attributeName, attributeValue);
                        }
                    } else {
                        if (attributeName.equalsIgnoreCase("type") || attributeName.equalsIgnoreCase("name") || attributeName.equalsIgnoreCase("revision")) {
                            childAttribute.put(attributeName, busSelect.getSelectData(attributeName));
                        }
                    }
                });
        return childObjectAttributeMap;
    }

    private HashMap<String, String> getItemProperties(BusinessObjectWithSelect busSelect) throws MatrixException {
        HashMap<String, String> childPropertyMap = new HashMap<>();

        childPropertyMap.put("description", busSelect.getSelectData("description"));
        childPropertyMap.put("organization", busSelect.getSelectData("organization"));
        childPropertyMap.put("owner", busSelect.getSelectData("owner"));
        childPropertyMap.put("project", busSelect.getSelectData("project"));
        childPropertyMap.put("current", busSelect.getSelectData("current"));

        return childPropertyMap;
    }

//    @Override
//    public HashMap<String, ChildData> getListMapOfAllItems() {
//        return listMapOfAllItems;
//    }

    @Override
    public ParentChildModel getRootItemInfo() {
        return this.rootItemInfo;
    }
}
