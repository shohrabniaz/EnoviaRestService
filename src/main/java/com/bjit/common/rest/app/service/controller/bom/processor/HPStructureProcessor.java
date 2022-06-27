/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.processor;

import com.bjit.common.rest.app.service.controller.bom.expand.CommonBOMExpand;
import com.bjit.common.rest.app.service.controller.bom.serviceInterfaces.IProductStructureService;
import com.bjit.common.rest.app.service.controller.bom.model.ChildItem;
import com.bjit.common.rest.app.service.controller.bom.model.RootItem;
import com.bjit.common.rest.app.service.controller.bom.model.StructureModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentChildRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.model.ParentRelationshipModel;
import com.bjit.common.rest.app.service.controller.bom.utilities.CommonBOMUtilities;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.model.structure.ItemStructure;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//@RequestScope
public class HPStructureProcessor {

    private static final org.apache.log4j.Logger IMPORT_PC_STRUCTURE_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(HPStructureProcessor.class);

    @Autowired
    CommonBOMUtilities commonBOMUtilities;
    
    @Autowired
    CommonBOMExpand commonBOMExpand;

    public void importStructure(Context context, ParentChildRelationshipModel parentChildRelationshipModel, CreateBOMBean structure) throws MatrixException, Exception {
        //public void importStructure(Context context, HashMap<String, List<ImportBOMController.ParentRelation>> childParentRelationship, CreateBOMBean structure) throws MatrixException, Exception {
        TNR rootItem = structure.getItem();
        HashMap<String, List<ParentRelationshipModel>> childParentRelationship = parentChildRelationshipModel.getParentChildRelationshipModel();

        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();

        ArrayList<BusinessObject> searchRootItem = businessObjectUtil.findBO(context, rootItem);

        if (!searchRootItem.isEmpty()) {
            BusinessObject rootBusinessObject = searchRootItem.get(0);
            String rootId = rootBusinessObject.getObjectId();

            List<HashMap<String, String>> childStructure = structure.getLines();

            childStructure.forEach((HashMap<String, String> lineItem) -> {
                TNR lineTnr = new TNR(lineItem.get("type"), lineItem.get("component"), lineItem.get("revision"));
                //ParentRelation parentChildRelation = null;
                List<ParentRelationshipModel> parentRelationList = null;
                if (childParentRelationship.containsKey(lineTnr.getType())) {

                    parentRelationList = childParentRelationship.get(lineTnr.getType());

                    if (!parentRelationList.stream().anyMatch(parentRelation -> parentRelation.getParentType().equalsIgnoreCase(rootItem.getType()))) {
                        throw new RuntimeException("Making relationship between '" + rootItem.getType() + "' and '" + lineTnr.getType() + "' is not possible");
                    }
                }

                try {
                    ArrayList<BusinessObject> searchLineItem = businessObjectUtil.findBO(context, lineTnr);
                    if (!searchLineItem.isEmpty()) {

                        StringJoiner relationshipJoiner = new StringJoiner(",");
                        parentRelationList.stream().forEach((ParentRelationshipModel parentRelation) -> {
                            relationshipJoiner.add(parentRelation.relationName);
                        });

                        List<TNR> expandedList = commonBOMExpand.expand(context, rootId, relationshipJoiner.toString(), lineTnr.getType(), (short) 1);
                        boolean anyMatch = expandedList.stream().anyMatch(expandedItem -> expandedItem.getType().equals(lineTnr.getType()) && expandedItem.getName().equals(lineTnr.getName()) && expandedItem.getRevision().equals(lineTnr.getRevision()));
                        if (!anyMatch) {
                            parentRelationList.stream().parallel().forEach((ParentRelationshipModel parentChildRelation) -> {
                                try {
                                    String newConnectionId = commonBOMUtilities.createNewConnection(context, rootId, searchLineItem.get(0).getObjectId(), parentChildRelation.getRelationName());
                                    commonBOMUtilities.updateProjectAndOrganization(rootId, context, newConnectionId);
                                } catch (MatrixException exp) {
                                    IMPORT_PC_STRUCTURE_PROCESSOR_LOGGER.error(exp);
                                    throw new RuntimeException(exp);
                                } catch (InterruptedException exp) {
                                    IMPORT_PC_STRUCTURE_PROCESSOR_LOGGER.error(exp);
                                    throw new RuntimeException(exp);
                                }
                            });

                        }
                    }
                } catch (MatrixException exp) {
                    IMPORT_PC_STRUCTURE_PROCESSOR_LOGGER.error(exp);
                    throw new RuntimeException(exp);
                } catch (Exception exp) {
                    IMPORT_PC_STRUCTURE_PROCESSOR_LOGGER.error(exp);
                    throw new RuntimeException(exp);
                }
            });
        }
    }

    /**
     * Prepares only the relationship map where only the relationship names of
     * the product configuration exists
     * @return 
     */
    public ParentChildRelationshipModel prepareHardwareProductRelationshipMap() {

        HashMap<String, List<ParentRelationshipModel>> childParent = new HashMap<>();
        ParentChildRelationshipModel parentChildRelationshipModel = new ParentChildRelationshipModel();
        List<ParentRelationshipModel> configurationFeatures = new ArrayList<>();
        configurationFeatures.add(new ParentRelationshipModel("Hardware Product", "Configuration Features"));
        childParent.put("Configuration Feature", configurationFeatures);
        List<ParentRelationshipModel> configurationOptions = new ArrayList<>();
        configurationOptions.add(new ParentRelationshipModel("Configuration Feature", "Configuration Options"));
        childParent.put("Configuration Option", configurationOptions);
        List<ParentRelationshipModel> productConfiguration = new ArrayList<>();
        productConfiguration.add(new ParentRelationshipModel("Hardware Product", "Product Configuration"));
        productConfiguration.add(new ParentRelationshipModel("Hardware Product", "Feature Product Configuration"));
        childParent.put("Product Configuration", productConfiguration);
        parentChildRelationshipModel.setParentChildRelationshipModel(childParent);
        return parentChildRelationshipModel;
    }

    public StructureModel convertItemStructureToStructureModel(ItemStructure itemStructure) {
        StructureModel structureModel = new StructureModel();
        List<CreateBOMBean> strucuteList = itemStructure.getStrucuteList();

        List<RootItem> rootItemList = new ArrayList<>();
        strucuteList.stream().forEach((CreateBOMBean createBomBean) -> {

            TNR item = createBomBean.getItem();

            RootItem rootItem = new RootItem();
            TNR parentTnr = new TNR();
            parentTnr.setType(item.getType());
            parentTnr.setName(item.getName());
            parentTnr.setRevision(item.getRevision());
            rootItem.setTnr(parentTnr);

            List<ChildItem> childItemList = new ArrayList<>();

            List<HashMap<String, String>> lines = createBomBean.getLines();

            lines.forEach((HashMap<String, String> lineData) -> {
                ChildItem childItem = new ChildItem();
                TNR childTnr = new TNR(lineData.get("type"), lineData.get("component"), lineData.get("revision"));
                lineData.remove("type");
                lineData.remove("component");
                lineData.remove("revision");

                childItem.setTnr(childTnr);

                HashMap<String, String> childAttributes = new HashMap<>();
                lineData.entrySet().stream().forEach(entry -> childAttributes.put(entry.getKey(), entry.getValue()));

                childItem.setAttributes(childAttributes);
                childItemList.add(childItem);
            });

            rootItem.setLines(childItemList);
            rootItemList.add(rootItem);
        });

        structureModel.setItem(rootItemList);
        structureModel.setSource(itemStructure.getSource());
        return structureModel;
    }
}
