/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectCreationProcessor;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class VALComponentMaterialItemImport implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER = org.apache.log4j.Logger.getLogger(VALComponentItemImport.class);

    @Override
    public <T, K> K doImport(final Context context, final T createObjectBeanDataList) {

        List<CreateObjectBean> createObjectBeanList = (List<CreateObjectBean>) createObjectBeanDataList;
        List<ResponseMessageFormaterBean> tnrSuccessfullList = new ArrayList<>();
        List<ResponseMessageFormaterBean> tnrErrorList = new ArrayList<>();
        HashMap<String, List<ResponseMessageFormaterBean>> tnrListMap = new HashMap<>();
        createObjectBeanList.forEach((CreateObjectBean createObjectBean) -> {

            Date transactionStartTime = DateTimeUtils.getTime(new Date());
            String typeForTimeChecking = createObjectBean.getTnr().getType();
            String nameForTimeChecking = createObjectBean.getTnr().getName();
            String revisionForTimeChecking = createObjectBean.getTnr().getRevision();
            
            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

            try {
                /*---------------------------------------- ||| Start Transaction Clone Business Object ||| ----------------------------------------*/
                CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.debug("Starting Transaction");
                ContextUtil.startTransaction(context, true);

                try {
                    ResponseMessageFormaterBean tnrId = new ResponseMessageFormaterBean();
                    tnrId.setTnr((TNR) createObjectBean.getTnr().clone());

                    try {
                        String objectType = createObjectBean.getTnr().getType();

                        createObjectBean.setSource(createObjectBean.getSource().toLowerCase());
                        
                        
                        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
                        createObjectBean.getAttributes().put(commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision"), createObjectBean.getTnr().getRevision());

                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.debug("Object Type : " + objectType);
                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.debug("Source Environment : " + createObjectBean.getSource());

                        String envObjectType = createObjectBean.getSource() + "." + objectType;

                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.debug("Environment Object Type : " + envObjectType);

                        createObjectBean.getTnr().setType(ItemImportUtility.BUSINESS_OBJECT_TYPE_MAP.containsKey(envObjectType) ? ItemImportUtility.BUSINESS_OBJECT_TYPE_MAP.get(envObjectType) : objectType);

                        String mapsAbsoluteDirectory = ItemImportUtility.validateCreateObjectBean(createObjectBean, businessObjectOperations);

                        if (NullOrEmptyChecker.isNullOrEmpty(mapsAbsoluteDirectory)) {
                            //String errorMessage = "Mapping file may be not exist for the item type '" + createObjectBean.getTnr().getType() + "' or '" + objectType + "'";
                            String errorMessage = "System could not recognize the type : '" + createObjectBean.getTnr().getType() + "'";
                            CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error(errorMessage);
                            throw new NullPointerException(errorMessage);
                        }

                        /*---------------------------------------- ||| Process for cloning object ||| ----------------------------------------*/
                        //ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, BomImportMapping.class);
                        /**
                         * This process modifies the attribute map of
                         * CreateObjectBean class. As the attribute map is a
                         * reference type so when the map is manipulated on
                         * other places then the real object face the changes
                         */
                        AttributeBusinessLogic attributeBusinessLogic = new AttributeBusinessLogic();
                        ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, ItemImportMapping.class, createObjectBean, attributeBusinessLogic);

                        ObjectCreationProcessor objectCreationProcessor = new ObjectCreationProcessor();
                        String clonedObjectId = objectCreationProcessor.processCreateObjectOperation(context, createObjectBean, businessObjectOperations, Boolean.TRUE);

                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.info("Cloned ObjectId : " + clonedObjectId);
                        
                        if (createObjectBean.getAttributes().containsKey("MBOM_MBOMM_.MBOM_Inventory_Unit")) {
                            
                            attributeBusinessLogic.addInterfaceToValComponentMaterial(context, clonedObjectId, createObjectBean.getAttributes().get("MBOM_MBOMM_.MBOM_Inventory_Unit"));
                        }

                        tnrId.setObjectId(clonedObjectId);

                        tnrSuccessfullList.add(tnrId);
                    } catch (FrameworkException exp) {
                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error(exp);
                        tnrId.setErrorMessage(exp.getMessage());
                        tnrErrorList.add(tnrId);
                        throw new RuntimeException(exp);
                    } catch (Exception exp) {
                        CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error(exp);
                        tnrId.setErrorMessage(exp.getMessage());
                        tnrErrorList.add(tnrId);
                        throw new RuntimeException(exp);
                    }
                } catch (CloneNotSupportedException exp) {
                    CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error(exp);
                    throw new RuntimeException(exp);
                }

                /*---------------------------------------- ||| Commit Transaction Clone Business Object ||| ----------------------------------------*/
                CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.info("Committing for '" + createObjectBean.getTnr().getType() + "' '" + createObjectBean.getTnr().getName() + "'");
                ContextUtil.commitTransaction(context);
            } catch (FrameworkException exp) {
                CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error(exp);
                CREATE_VAL_COMPONENT_MATERIAL_ITEM_LOGGER.error("Aborting for '" + createObjectBean.getTnr().getType() + "' '" + createObjectBean.getTnr().getName() + "'");
                ContextUtil.abortTransaction(context);
            }
        });

        tnrListMap.put("successFullList", tnrSuccessfullList);
        tnrListMap.put("errorList", tnrErrorList);

        return (K) tnrListMap;
    }
}
