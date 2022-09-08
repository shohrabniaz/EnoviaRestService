package com.bjit.common.rest.pdm_enovia;

import com.bjit.common.rest.app.service.dsservice.consumers.ConsumerContainers;
import com.bjit.common.rest.app.service.dsservice.consumers.IConsumer;
import com.bjit.common.rest.app.service.dsservice.models.csrf.SecurityContextResponseModel;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.model.itemImport.Document;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.pdm_enovia.formatter.AttributeFormatter;
import com.bjit.common.rest.pdm_enovia.formatter.ItemAttributeFormatter;
import com.bjit.common.rest.pdm_enovia.importer.DocumentImporter;
import com.bjit.common.rest.pdm_enovia.importer.ItemImporter;
import com.bjit.common.rest.pdm_enovia.mapper.ItemMapper;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportValue;
import com.bjit.common.rest.pdm_enovia.mapper.xml_mapping_model.ItemImportXmlMapElementAttribute;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.result.xls.XLSFileUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.common.rest.pdm_enovia.utility.ValJsonParser;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class ValItemImportProcessor {

    private static final Logger VAL_ITEM_IMPORT_PROCESSOR_LOGGER = Logger.getLogger(ValItemImportProcessor.class);

    public void startValImportProcess(Context context, ObjectDataBean rootObject, ResultUtil resultUtil, AttributeBusinessLogic attributeBusinessLogic, ConsumerContainers consumerContainers) throws CloneNotSupportedException, Exception {

        /*---------------------------------------- ||| Start Val Item Import Process ||| ----------------------------------------*/
        VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info("\n\n\n=============== ||| VAL ITEM IMPORT Process Started ||| ===============");

        try {
            if (NullOrEmptyChecker.isNull(rootObject.geDataTree()) || rootObject.geDataTree().isEmpty()) {
                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info(">>>>>>>>> No object/s found to process!");
                throw new RuntimeException("No object/s found to process!");
            }

//            final int parallelism = Integer.parseInt(PropertyReader.getProperty("val.item.import.concurrent.total.thread.count"));
//            ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);
//
//            forkJoinPool.submit(() -> {

                rootObject.geDataTree().stream()/*.parallel()*/.forEach((DataTree itemTree) -> {

                    BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
                    BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

                    //for (DataTree itemTree : rootObject.geDataTree()) {
                    try {
                        /*---------------------------------------- ||| Start Val JSON Parser ||| ----------------------------------------*/
                        ValJsonParser valJsonParser = new ValJsonParser();
                        valJsonParser.parse(itemTree, rootObject.getSource(), resultUtil);

                        Map<String, CreateObjectBean> itemObjectMap = valJsonParser.getItemObjectMap();
                        Map<String, List<String>> substituteItemMap = valJsonParser.getSubstituteMap();
                        Map<String, List<Document>> documentTreeListMap = valJsonParser.getDocumentTreeListMap();
                        Map<String, String> itemNameIdMap = new HashMap<>();

                        for (Map.Entry<String, CreateObjectBean> entry : itemObjectMap.entrySet()) {
                            try {
                                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug(">>>>>>>>> Attribute Map of " + entry.getKey() + ": " + entry.getValue().getAttributes());
                                Map<String, String> attributeMap = entry.getValue().getAttributes();

                                /*---------------------------------------- ||| Check Inventory unit Attribute ||| ----------------------------------------*/
                                String inventoryUnitAttributeKey = PropertyReader.getProperty("pdm.attr.inventory.unit.key");
                                if (NullOrEmptyChecker.isNullOrEmpty(inventoryUnitAttributeKey)) {
                                    VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: Error occurred while getting pdm atribute 'Inventory Unit' key from properties");
                                    throw new Exception("Error occured while getting pdm atribute 'Inventory Unit' key from properties");
                                }

                                HashMap<String, String> propertyMap = new HashMap<>();
                                propertyMap.put("Name", entry.getKey());
                                propertyMap = getItemTypeByInventoryUnit(attributeMap, propertyMap, rootObject.getSource().toLowerCase());
                                attributeMap.put(inventoryUnitAttributeKey, propertyMap.get(inventoryUnitAttributeKey));

                                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info("Type: " + propertyMap.get("Type"));
                                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info("Type: " + propertyMap.get("Inventory unit"));

                                TNR itemTNR = (TNR) entry.getValue().getTnr().clone();
                                try {
                                    /*---------------------------------------- ||| Start Item Attribute Formatting Process ||| ----------------------------------------*/
                                    AttributeFormatter attributeFormatter = new ItemAttributeFormatter(entry.getValue(), propertyMap, substituteItemMap.get(entry.getKey()));
                                    VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info("Type: " + propertyMap.get("Type"));
                                    CreateObjectBean formattedItemBean = attributeFormatter.getFormattedObjectBean(resultUtil, attributeBusinessLogic);

                                    /*---------------------------------------- ||| Start Item Import Process ||| ----------------------------------------*/
                                    ItemImporter itemImporter = new ItemImporter();
                                    String objectId = itemImporter.doItemImport(context, formattedItemBean, businessObjectOperations, resultUtil, consumerContainers);

                                    if (!NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                                        itemNameIdMap.put(entry.getKey(), objectId);

                                        /*---------------------------------------- ||| Disconnect all the Document that are currently connected to Parent VAL Item ||| ----------------------------------------*/
                                        disconnectExistingDocumentFromItem(context, objectId);

                                        /*---------------------------------------- ||| Start Document Import Process ||| ----------------------------------------*/
                                        if (documentTreeListMap.containsKey(entry.getKey())) {
                                            if (!documentTreeListMap.get(entry.getKey()).isEmpty()) {
                                                DocumentImporter documentImporter = new DocumentImporter();
                                                documentImporter.documentImport(context, PropertyReader.getProperty("template.object.type.Document"),
                                                        documentTreeListMap.get(entry.getKey()), propertyMap, Boolean.FALSE, "", objectId, rootObject.getSource().toLowerCase(), resultUtil, attributeBusinessLogic, consumerContainers);
                                            }
                                        }
                                        }
                                } catch (Exception e) {
                                    VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + e.getMessage());
                                    resultUtil.addErrorResult(entry.getKey(), itemTNR, e.getMessage());
                                }
                            } catch (CloneNotSupportedException e) {
                                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + e.getMessage());
                                resultUtil.addErrorResult(entry.getKey(), entry.getValue().getTnr(), e.getMessage());
                            } catch (Exception ex) {
                                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + ex.getMessage());
                                resultUtil.addErrorResult(entry.getKey(), entry.getValue().getTnr(), ex.getMessage());
                            }
                        }

                        /*---------------------------------------- ||| Process Substitute Item Relation ||| ----------------------------------------*/
                        /*for (Map.Entry<String, List<String>> entry : substituteItemMap.entrySet()) {
                            String parentItemName = entry.getKey();
                            for (String substituteItemName : entry.getValue()) {
                            SubstituteItemRelator.relate(context, itemNameIdMap, parentItemName, substituteItemName);
                            }
                        }*/
                    } catch (CloneNotSupportedException exp) {
                        VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("\n\n");
                        VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + exp.getMessage());
                        throw new RuntimeException(exp);
                    } catch (Exception exp) {
                        VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("\n\n");
                        VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + exp.getMessage());
                        throw new RuntimeException(exp);
                    }
                });
//            }).get();

            /*---------------------------------------- ||| Process Scope Link Creation - VAL Item Write in XLS File ||| ----------------------------------------*/
            try {
                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("ScopeLink XLS Writing Started");
                Map<String, List<String>> writableItemListMap = new HashMap<>();
                writableItemListMap.put("created", resultUtil.successfulCreateList);
                writableItemListMap.put("updated", resultUtil.successfulUpdateList);
                new XLSFileUtil(writableItemListMap).writeFile();
                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("ScopeLink XLS Writing Ended");
            } catch (Exception e) {
                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error("XLS Writing Failed due to : " + e.getMessage());
            }

        } catch (RuntimeException e) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("\n\n");
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.debug("\n\n\n");
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info("=============== ||| VAL ITEM IMPORT Process Completed ||| ===============\n\n");
            /*---------------------------------------- ||| End Val Item Import Process ||| ----------------------------------------*/
        }
    }

    public void disconnectExistingDocumentFromItem(Context context, String baseItemID) {
        try {
            BusinessObject valItemBO = new BusinessObject(baseItemID);
            SelectList selectBusStmts = new SelectList();
            SelectList selectRelStmts = new SelectList();
            Pattern typePattern = new Pattern("PLMDocConnection");
            Pattern relPattern = new Pattern("VPLMrel/PLMConnection/V_Owner");
            String busWhereExpression = "";
            String relWhereExpression = "";
            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relItr = null;
            Short expandLevel = new Short("1");

            expandResult = valItemBO.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false, true,
                    expandLevel, busWhereExpression, relWhereExpression, false);
            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> boList = new ArrayList<>();
            while (relItr.next()) {
                String connectionID = relItr.value().getName();
                RelationshipWithSelect relSelect = relItr.obj();

                //BusinessObjectWithSelect  busSelect = relSelect.getTarget();
                BusinessObject childFileBO = relSelect.getTo();
                String docConnectionBOid = childFileBO.getObjectId(context);
                if (!boList.contains(docConnectionBOid)) {
                    String disconnectQuery = "disconnect connection " + connectionID;
                    VAL_ITEM_IMPORT_PROCESSOR_LOGGER.info(disconnectQuery);
                    MqlUtil.mqlCommand(context, disconnectQuery);
                    boList.add(docConnectionBOid);
                }
            }
        } catch (NumberFormatException | MatrixException ex) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(ex.getMessage());
        }
    }

    public HashMap<String, String> getItemTypeByInventoryUnit(Map<String, String> attributeMap, HashMap<String, String> propertyMap, String source) throws Exception {
        String inventoryUnitAttributeKey = PropertyReader.getProperty("pdm.attr.inventory.unit.key");
        if (NullOrEmptyChecker.isNullOrEmpty(inventoryUnitAttributeKey)) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: Error occured while getting pdm atribute 'Inventory Unit' key from properties");
            throw new Exception("Error occured while getting pdm atribute 'Inventory Unit' key from properties");
        }

        String valComponentTypeName = PropertyReader.getProperty("import.type.name.Enovia." + source + ".val.component");
        if (NullOrEmptyChecker.isNullOrEmpty(valComponentTypeName)) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: Error occured while getting Val Component Type's name from properties");
            throw new Exception("Error occured while getting 'Val Component Item' Type's name from properties");
        }

        String inventoryUnitFromPDM = attributeMap.get(inventoryUnitAttributeKey);

        if (NullOrEmptyChecker.isNullOrEmpty(inventoryUnitFromPDM)) {
            propertyMap.put("Type", valComponentTypeName);
            propertyMap.put(inventoryUnitAttributeKey, PropertyReader.getProperty(source + ".default.inventory.unit"));
            return propertyMap;
        }

        propertyMap = matchInventoryUnitForType(source, valComponentTypeName, propertyMap, inventoryUnitAttributeKey, inventoryUnitFromPDM);

        if (NullOrEmptyChecker.isNull(propertyMap.get("Type"))) {
            String valComponentMaterialTypeName = PropertyReader.getProperty("import.type.name.Enovia." + source + ".val.component.material");
            if (NullOrEmptyChecker.isNullOrEmpty(valComponentMaterialTypeName)) {
                VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: Error occured while getting Val Component Material Type's name from properties");
                throw new Exception("Error occured while getting 'Val Component Material' Item Type's name from properties");
            }

            propertyMap = matchInventoryUnitForType(source, valComponentMaterialTypeName, propertyMap, inventoryUnitAttributeKey, inventoryUnitFromPDM);
        }

        if (NullOrEmptyChecker.isNull(propertyMap.get("Type"))) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: System could not recognize the " + inventoryUnitAttributeKey + ": " + inventoryUnitFromPDM);
            throw new Exception("System could not recognize the " + inventoryUnitAttributeKey + ": " + inventoryUnitFromPDM);
        }

        return propertyMap;
    }

    public HashMap<String, String> matchInventoryUnitForType(String source, String type, HashMap<String, String> propertyMap, String inventoryUnitAttributeKey, String inventoryUnitFromPDM) throws Exception {
        try {
            String mpsAbsoluteDirectory = CommonUtil.populateMapDirectoryFromSourceAndType(source, type);
            ItemImportMapping mapper = null;
            mapper = new ItemMapper().getMapper(mpsAbsoluteDirectory, ItemImportMapping.class, mapper);

            List<ItemImportValue> valueList = null;
            List<ItemImportXmlMapElementAttribute> valComponentXmlAttributeList = mapper.getXmlMapElementObjects().getXmlMapElementObject().get(0).getXmlMapElementAttributes().getXmlMapElementAttribute();
            for (ItemImportXmlMapElementAttribute mapXmlAttribute : valComponentXmlAttributeList) {
                String sourceName = mapXmlAttribute.getSourceName();
                if (!NullOrEmptyChecker.isNullOrEmpty(sourceName) && sourceName.equals(inventoryUnitAttributeKey)) {
                    valueList = mapXmlAttribute.getValues().getValue();
                    break;
                }
            }

            if (!NullOrEmptyChecker.isNull(valueList)) {
                for (int i = 0; i < valueList.size(); i++) {
                    ItemImportValue value = valueList.get(i);
                    if (!NullOrEmptyChecker.isNull(value)) {
                        if (inventoryUnitFromPDM.equals(value.getValue())) {
                            propertyMap.put("Type", type);
                            propertyMap.put(inventoryUnitAttributeKey, inventoryUnitFromPDM);
                            return propertyMap;
                        }
                    }
                }
            }
        } catch (Exception e) {
            VAL_ITEM_IMPORT_PROCESSOR_LOGGER.error(">>>>>>>>> Error: Error occured while checking " + inventoryUnitAttributeKey + " map for type " + type + " : " + e.getMessage());
            throw new Exception("Error occured while checking " + inventoryUnitAttributeKey + " map for type " + type + " : " + e.getMessage());
        }
        return propertyMap;
    }
}
