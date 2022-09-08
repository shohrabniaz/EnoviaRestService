package com.bjit.common.rest.app.service.controller.lntransfer.Util;

import com.bjit.common.rest.app.service.model.itemTransfer.LNTransferRequestModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.model.webservice.Item;
import com.bjit.ex.integration.model.webservice.TNR;
import com.bjit.ex.integration.transfer.actions.utilities.TransferObjectUtils;
import com.bjit.mapper.mapproject.expand.ExpandObject;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.bjit.mapper.mapproject.util.CommonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.RelationshipWithSelectList;

/**
 *
 * @author BJIT
 */
public class LNRequestUtil {

    ExpandObject expandObject = new ExpandObject();
    Map<String, String> positionMap = new HashMap<>();
    List<Item> singleObjResult = new ArrayList<>();
    List<Item> singleObjBOMResult = new ArrayList<>();
    String parentItem = "";
    String itemName = "";
    //  List<Item> singleObjResultList = new ArrayList<>();

    public static void validateRequest(LNTransferRequestModel itemTransferModel) {
        if (itemTransferModel == null) {
            throw new IllegalArgumentException("Please provide a valid request body");
        } else if (itemTransferModel.getItems() == null || itemTransferModel.getItems().size() == 0) {
            throw new IllegalArgumentException("Item(s) can't be null or empty");
        }
    }

    public static ResponseMessageFormaterBean validateRequestedItem(Context context, Item item) {
        if (NullOrEmptyChecker.isNullOrEmpty(item.getId())) {
            TNR tnr = item.getTnr();
            if (tnr == null) {
                return LNResponseMessageUtil.getResponseMessageFormatter(item, "Either item or TNR must be provided");
            } else if (NullOrEmptyChecker.isNullOrEmpty(tnr.getType()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getName())) {
                return LNResponseMessageUtil.getResponseMessageFormatter(item, "Either item or TNR must be provided");
            }
        } else if (!NullOrEmptyChecker.isNullOrEmpty(item.getId())) {
            BusinessObject businessObject = null;
            if (item.getTnr() == null || NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getName()) || NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getType())) {
                try {
                    businessObject = new BusinessObject(item.getId());
                    businessObject.open(context);
                    item.setTnr(new TNR(businessObject.getTypeName(), businessObject.getName(), businessObject.getRevision()));
                } catch (MatrixException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        businessObject.close(context);
                    } catch (MatrixException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

//    
    public Map<String, List<Item>> getExpandedItem(Item item, Context context, String type, String level) throws Exception {
        Map<String, List<Item>> populateBomExportResultMap = new HashMap<>();
        TransferObjectUtils tansferObjectUtils = new TransferObjectUtils();
        tansferObjectUtils.__init__(context);
        BusinessObject businessObject = null;
        String objectId = "";
        Item rootItem = new Item();
        String position = "";
        String allowTransferedValue = "";

        try {
            HashMap<String, String> properties = PropertyReader.getProperties("bom.export.type.map.directory", true);
            String directoryMap = properties.get("CreateAssembly");
            //    protected ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations();

            ObjectTypesAndRelations typesAndRelations = new ObjectTypesAndRelations(directoryMap);
            Map<String, String> typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
            if (NullOrEmptyChecker.isNullOrEmpty(item.getId())) {
                MqlQueries mqlQuery = new MqlQueries();
                objectId = mqlQuery.getObjectId(context, item.getTnr().getType(), item.getTnr().getName(), item.getTnr().getRevision());
                TNR tnr = new TNR();
                Map<String, String> attrs = new HashMap<>();
                tnr.setName(item.getTnr().getName());
                tnr.setType(item.getTnr().getType());
                tnr.setRevision(item.getTnr().getRevision());
                rootItem.setTnr(tnr);
                rootItem.setAttributes(attrs);
            } else {
                Map<String, String> attrs = new HashMap<>();
                objectId = item.getId();
                rootItem.setId(objectId);
                rootItem.setAttributes(attrs);
            }

            businessObject = new BusinessObject(objectId);
            businessObject.open(context);

            ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, level, typesAndRelations);
            itemName = expandResult.getRootSelectData("name");
            String bundleId = expandResult.getRootSelectData("attribute[TRS_TermID.TRS_TermID]");
            String releasePurpose = expandResult.getRootSelectData("attribute[MBOM_MBOMERP.MBOM_Release_Purpose]");
            if (releasePurpose.equalsIgnoreCase("Production")) {
                parentItem = itemName;
            }

            RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            while (relationshipWithSelectItr.next()) {
                RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
                BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();
                populateBomExportResultsMap(businessObjectWithSelect, context, relationshipWithSelect, typeSelectablesWithOutputName, type);
                businessObjectWithSelect = null;
                relationshipWithSelect = null;
                RelationshipWithSelect relationWithSelect = expandResult.getRelationships().getElement(0);
                position = relationWithSelect.getSelectData("attribute[MBOM_MBOMERPInstance.MBOM_Position]");
                allowTransferedValue = relationWithSelect.getSelectData("attribute[MBOM_MBOMERPInstance.MBOM_AllowStructureTransferToERP]");
            }

            List<Item> singleObjResultList = singleObjResult.stream().collect(Collectors.toList());
            Collections.reverse(singleObjResultList);
            String refItem = expandResult.getRootSelectData("attribute[MBOM_MBOMERP.MBOM_ReferenceItem]");
            String masterShip = tansferObjectUtils.mastershipCheck(context, businessObject);

            String trasnferToErp = tansferObjectUtils.isTransfferedToErp(context, businessObject);
            if (!NullOrEmptyChecker.isNullOrEmpty(refItem) && !NullOrEmptyChecker.isNullOrEmpty(bundleId)) {

                singleObjResultList.add(rootItem);

            }
            populateBomExportResultMap.put("ITEM", singleObjResultList);

            //BOM Item Reverse  
            if (type.equalsIgnoreCase("bom")) {
                List<Item> singleObjBOMResultList = singleObjBOMResult.stream().collect(Collectors.toList());
                Collections.reverse(singleObjBOMResultList);
                if ((masterShip.equalsIgnoreCase("PDM") && allowTransferedValue.equalsIgnoreCase("TRUE")) && !trasnferToErp.equalsIgnoreCase("TRUE")) {

                } else if (!NullOrEmptyChecker.isNullOrEmpty(position)) {
                    if (Integer.parseInt(position) < 1) {
                    } else {
                        if (!singleObjResult.isEmpty()) {
                            singleObjBOMResultList.add(rootItem);
                        }
                    }
                }
                populateBomExportResultMap.put("BOM", singleObjBOMResultList);
            }

            singleObjResult.removeAll(singleObjResult);
            singleObjBOMResult.removeAll(singleObjResult);
            return populateBomExportResultMap;
        } catch (Exception e) {
            return populateBomExportResultMap;
        } finally {
            businessObject.close(context);
        }
    }

    private void populateBomExportResultsMap(BusinessObjectWithSelect businessObjectWithSelect, Context context,
            RelationshipWithSelect relationshipWithSelect, Map<String, String> typeSelectablesWithOutputName, String type) throws Exception {
        String objectType = businessObjectWithSelect.getSelectData("type");
        TransferObjectUtils tansferObjectUtils = new TransferObjectUtils();
        tansferObjectUtils.__init__(context);
        Item item = new Item();
        TNR tnr = new TNR();
        Map<String, String> attrs = new HashMap<>();

        typeSelectablesWithOutputName.forEach((CommonUtil.ThrowingConsumer<String, String>) (key, value) -> {

            boolean hasChild = false;
            BusinessObject businessObject = null;
            String position = "";
            String allowTransferedValue = "";
            String refItem = businessObjectWithSelect.getSelectData("attribute[MBOM_MBOMERP.MBOM_ReferenceItem]");
            String bundleId = businessObjectWithSelect.getSelectData("attribute[TRS_TermID.TRS_TermID]");
            String id = businessObjectWithSelect.getSelectData("id");
            String keyValue = itemName + id;
            businessObject = new BusinessObject(id);
            businessObject.open(context);

            if (NullOrEmptyChecker.isNullOrEmpty(refItem)) {

            } else if (NullOrEmptyChecker.isNullOrEmpty(bundleId)) {
            } else {

                if (key.equalsIgnoreCase("type")) {
                    String itemType = businessObjectWithSelect.getSelectData("type");

                    tnr.setType(itemType);

                }
                if (key.equalsIgnoreCase("name")) {
                    String name = businessObjectWithSelect.getSelectData("name");
                    //   uniqueKey = uniqueKey + name;
                    tnr.setName(name);

                }

                if (key.equalsIgnoreCase("revision")) {
                    String rev = businessObjectWithSelect.getSelectData("revision");

                    tnr.setRevision(rev);
                    item.setTnr(tnr);
                    String releasePurpose = "";
                    if (type.equalsIgnoreCase("bom")) {
                        // String objectId = businessObjectWithSelect.getSelectData("id");
                        releasePurpose = businessObjectWithSelect.getSelectData("attribute[MBOM_MBOMERP.MBOM_Release_Purpose]");
                        //  businessObject = new BusinessObject(id);

                        RelationshipWithSelectItr relationIterator = tansferObjectUtils.expandBusinessObject(context, businessObject);

                        while (relationIterator.next()) {
                            itemName = businessObjectWithSelect.getSelectData("name");
                            hasChild = true;
                            RelationshipWithSelect relationWithSelect = relationIterator.obj();
                            //  BusinessObjectWithSelect businessObWithSelect = relationshipWithSelect.();
                            position = relationWithSelect.getSelectData("attribute[MBOM_MBOMERPInstance.MBOM_Position]");
                            allowTransferedValue = relationWithSelect.getSelectData("attribute[MBOM_MBOMERPInstance.MBOM_AllowStructureTransferToERP]");
                        }
                    }

                    if (type.equalsIgnoreCase("bom") && hasChild) {
                        String masterShip = tansferObjectUtils.mastershipCheck(context, businessObject);

                        String trasnferToErp = tansferObjectUtils.isTransfferedToErp(context, businessObject);

                        if ((masterShip.equalsIgnoreCase("PDM") && allowTransferedValue.equalsIgnoreCase("TRUE")) && !trasnferToErp.equalsIgnoreCase("TRUE")) {

                        } else if (!NullOrEmptyChecker.isNullOrEmpty(position)) {
                            if (Integer.parseInt(position) > 0) {
                                if (positionMap.containsValue(position)) {

                                    String oldValue = positionMap.get(keyValue);
                                    if (oldValue.equalsIgnoreCase(position)) {
                                        positionMap.remove(keyValue);
                                        // 
                                    } else {
                                        positionMap.put(keyValue, position);
                                    }

                                } else {
                                    positionMap.put(keyValue, position);
                                }
                                if (!parentItem.equals("")) {

                                    if (allowTransferedValue.equalsIgnoreCase("TRUE")) {
                                        if (positionMap.containsKey(keyValue)) {
                                            singleObjBOMResult.add(item);
                                        }
                                    }

                                } else {
                                    if (positionMap.containsKey(keyValue)) {
                                        singleObjBOMResult.add(item);
                                    }

                                }

                            }
                        }
                        if (releasePurpose.equalsIgnoreCase("Production")) {
                            parentItem = itemName;
                        } else {
                            parentItem = "";
                        }
                    }
                    singleObjResult.add(item);

                }
            }
            businessObject.close(context);
        });
    }
}