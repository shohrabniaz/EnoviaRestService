/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.facades;

import com.bjit.common.rest.app.service.controller.bom.promises.CommonBOMImportPromise;
import com.bjit.common.rest.app.service.constants.RelationshipMaps;
import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.app.service.controller.bom.promises.BomValidatorPromise;
import com.bjit.common.rest.app.service.model.createBOM.BOMStructure;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_BOM_mapper_model.Relationships;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.builder.MapperBuilder;
import com.matrixone.apps.domain.util.ContextUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
public class CommonBOMImportFacade implements ItemOrBOMImport {

    private static final org.apache.log4j.Logger COMMON_BOM_IMPORT_LOGGER = org.apache.log4j.Logger.getLogger(CommonBOMImportFacade.class);

    @Override
    public <T, K> K doImport(Context context, T bomStructure) {
        BOMStructure bomStructureModel = (BOMStructure) bomStructure;
        List<CreateBOMBean> createBOMBeanList = bomStructureModel.getCreateBomBeanList();
        String source = bomStructureModel.getSource();

        CommonBOMImportParams commonBomImportParams = new CommonBOMImportParams();
        commonBomImportParams.relationshipList = null;
        commonBomImportParams.source = source;
        prepareCommonParameters(commonBomImportParams);

        try {

            String mappingFilePath = getMappingFilePath(source);

            Relationships relationshipMapping;
            if (Boolean.parseBoolean(PropertyReader.getProperty(("bom.map.singleton.instance")))) {
                if (NullOrEmptyChecker.isNull(RelationshipMaps.commonRelationships)) {
                    RelationshipMaps.commonRelationships = (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath);
                }
                relationshipMapping = RelationshipMaps.commonRelationships;
            } else {
                relationshipMapping = (Relationships) new MapperBuilder().getMapper(MapperBuilder.XML, Relationships.class, mappingFilePath);
            }

            commonBomImportParams.relationshipList = relationshipMapping.getRelationshipList();
        } catch (Exception exp) {
            COMMON_BOM_IMPORT_LOGGER.error(exp);
        }

        commonBomImportParams.relName = "";
        commonBomImportParams.interfaceName = "";
        String responseStatus = "";

        try {
            commonBomImportParams.isBOMCreatedSuccessfully = true;
            List<HashMap<String, ArrayList<String>>> existingChildInfoRelMapList = new ArrayList<>();

            commonBomImportParams.total_Child_from_PDM = 0;
            int total_line_from_PDM = createBOMBeanList.size();

            BomValidatorPromise bomValidatorPromise = new BomValidatorPromise();
            bomValidatorPromise.validatorPromise(source, createBOMBeanList, commonBomImportParams, context, commonBomImportParams.bomStatisticalTimeForParentMap, commonBomImportParams.parentChildInfoMapList, existingChildInfoRelMapList);

            COMMON_BOM_IMPORT_LOGGER.info(" | Data | Total Requested BOM | " + total_line_from_PDM);
            COMMON_BOM_IMPORT_LOGGER.info(" | Data | Total Requested Child | " + commonBomImportParams.total_Child_from_PDM);
            COMMON_BOM_IMPORT_LOGGER.info(" | Process Time | Total Validation | " + commonBomImportParams.bomStatisticalTimeForParentMap);

            CommonUtilities commonUtility = new CommonUtilities();
            commonUtility.doStartTransaction(context);

            BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
            
            List<String> exisintConnectionList = new ArrayList<>();

            CommonBOMImportPromise commonBomImportPromise = new CommonBOMImportPromise();
            commonBomImportPromise.bomImportProcess(context, commonBomImportParams);

            existingChildInfoRelMapList.stream().forEach(connectionIdsMap -> {
                Set<String> connectionIdsKeyset = connectionIdsMap.keySet();
                connectionIdsKeyset.stream().forEach(connectionIdsKey -> {
                    exisintConnectionList.addAll(connectionIdsMap.get(connectionIdsKey));
                });
            });

            exisintConnectionList.removeAll(commonBomImportParams.processedConnections);
            exisintConnectionList.stream().parallel().forEach(connectionId -> {
                try {
                    businessObjectUtil.disconnectRelationship(context, connectionId);
                    COMMON_BOM_IMPORT_LOGGER.info("Disconencted : " + connectionId);
                } catch (MatrixException ex) {
                    COMMON_BOM_IMPORT_LOGGER.error(ex);
                    throw new RuntimeException(ex);
                }
            });

            setStatisticalLogs(total_line_from_PDM, commonBomImportParams, commonBomImportParams.bomStatisticalTimeForParentMap);

            if (!commonBomImportParams.errorInStructure) {
                COMMON_BOM_IMPORT_LOGGER.debug("Committing Transaction");
                ContextUtil.commitTransaction(context);
            }

            if (commonBomImportParams.errorParentInfoList.isEmpty()) {
                commonBomImportParams.responseMsgMap.put("Successful", commonBomImportParams.successfulParentInfoList);
            }
        } catch (MatrixException | NumberFormatException exp) {
            COMMON_BOM_IMPORT_LOGGER.debug("Aborting Transaction");
            COMMON_BOM_IMPORT_LOGGER.error(exp.getMessage());
            COMMON_BOM_IMPORT_LOGGER.error(exp);
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(exp);
        } catch (Exception exp) {
            COMMON_BOM_IMPORT_LOGGER.debug("Aborting Transaction");
            COMMON_BOM_IMPORT_LOGGER.error(exp.getMessage());
            COMMON_BOM_IMPORT_LOGGER.error(exp);
            ContextUtil.abortTransaction(context);
            throw new RuntimeException(exp);
        }
        COMMON_BOM_IMPORT_LOGGER.debug("responseMsgMap :: " + commonBomImportParams.responseMsgMap);
        return (K) commonBomImportParams.responseMsgMap;
    }

    protected String getMappingFilePath(String source) {
        String mappingFilePath = PropertyReader.getProperty(source + ".bom.import.mapping.xml.directory");
        return mappingFilePath;
    }

    protected void prepareCommonParameters(CommonBOMImportParams commonBomImportParams) {
        commonBomImportParams.successfulParentInfoList = new ArrayList<>();
        commonBomImportParams.errorParentInfoList = new ArrayList<>();
        commonBomImportParams.parentChildInfoMapList = new ArrayList<>();
        commonBomImportParams.responseMsgMap = new HashMap<>();
        commonBomImportParams.bomStatisticalTimeForParentMap = new HashMap<>();
        commonBomImportParams.processedConnections = new ArrayList<>();
    }

    protected void setStatisticalLogs(int total_line_from_PDM, CommonBOMImportParams commonBomImportVariables, HashMap<String, Long> validationForParentMap) {
        COMMON_BOM_IMPORT_LOGGER.debug("Total BOM/line requested from PDM | " + total_line_from_PDM);
        COMMON_BOM_IMPORT_LOGGER.debug("Total number of child requested from PDM | " + commonBomImportVariables.total_Child_from_PDM);
        commonBomImportVariables.total_connection_processed = commonBomImportVariables.new_connection + commonBomImportVariables.modified_connection + commonBomImportVariables.deleted_connection;
        COMMON_BOM_IMPORT_LOGGER.debug("New connection | " + commonBomImportVariables.new_connection);
        COMMON_BOM_IMPORT_LOGGER.debug("Modified connection | " + commonBomImportVariables.modified_connection);
        COMMON_BOM_IMPORT_LOGGER.debug("Deleted connection | " + commonBomImportVariables.deleted_connection);
        COMMON_BOM_IMPORT_LOGGER.debug("Total connection processed | " + commonBomImportVariables.total_connection_processed);
        COMMON_BOM_IMPORT_LOGGER.info(" | Data | Total Child ENOVIA | " + commonBomImportVariables.total_child_in_ENOVIA);
        COMMON_BOM_IMPORT_LOGGER.info(" | Process Time | Total BOM VAlidation and Process | " + validationForParentMap);
    }
}
