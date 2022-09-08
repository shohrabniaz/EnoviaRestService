package com.bjit.common.rest.app.service.controller.export.report.single_level.biz_logic;

import com.bjit.common.rest.app.service.GTS.GTSDataServiceUtil;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportDataModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportDetailDataModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportSummaryDataModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.JsonOutput;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.RelationshipWithSelectList;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Container of Single Level Report Business Logic.<br>
 * Extended from JsonOutput class and added functions only for response model
 * generation.<br>
 * Internal Business Logic remained same.
 *
 * @author BJIT
 * @version 1.0
 * @since 1.0
 */
public class SLReportBusinessLogic extends JsonOutput {

    private static final Logger SL_REPORT_BUSINESS_LOGIC_LOGGER = Logger.getLogger(SLReportBusinessLogic.class);

    Set<String> duplicateDrawingCheck = new HashSet<>();

    /**
     * Constructor with absolute directory path of XML Map required for Report
     * Data
     *
     * @param mapAbsolutedirectory
     * @throws IOException
     */
    public SLReportBusinessLogic(String mapAbsolutedirectory) throws IOException {
        super(mapAbsolutedirectory);
    }

    /**
     * Default Constructor without absolute directory path of XML Map required
     * for Report Data
     *
     * @throws IOException
     */
    public SLReportBusinessLogic() throws IOException {
        super();
    }

    /**
     * Prepares Report Data based on Report Business Logic
     *
     * @param businessModel
     * @return
     * @throws Exception
     */
    public ReportDataModel prepareReportData(ReportBusinessModel businessModel) throws Exception {
        gatherDataFromRootObjectExpansion(businessModel);

        ReportDataModel reportDataModel = new ReportDataModel();

        List<ReportDetailDataModel> detailReportData = new ArrayList<>();
        detailReportData = prepareDetailReportData(this.responseBOMList.get(0), detailReportData, true);
        addGTSTitleForAllItems(businessModel.isIsTitleRequired(), detailReportData);
        reportDataModel.setDetailReportData(detailReportData);

        if (isSummaryRequired) {
            ReportSummaryDataModel summaryDataModel = Boolean.parseBoolean(businessModel.getParameter().getIsSummaryRequired()) ? prepareSummaryReportData() : new ReportSummaryDataModel();
            reportDataModel.setSummaryReportData(summaryDataModel);
        }
        return reportDataModel;
    }

    /**
     * Prepares Detail Report Data based on Report Business Logic
     *
     * @param rootItemData
     * @param detailReportData
     * @param isRoot
     * @return
     */
    private List<ReportDetailDataModel> prepareDetailReportData(Object rootItemData, List<ReportDetailDataModel> detailReportData, boolean isRoot) {
        HashMap<String, Object> rootItemDataMap = (HashMap) rootItemData;
        if (isRoot) {
            String itemPhysicalId = (String) rootItemDataMap.get("physicalid");
            String drawNumber = "";
            if (drawingDataHolderMap.containsKey(itemPhysicalId)) {
                List<Map<String, String>> drawingList = drawingDataHolderMap.get(itemPhysicalId);
                if(drawingList.size() > 0) {
                    Map<String, String> drawing = drawingList.get(0);
                    drawNumber = drawing.get("Drawing Number");
                }
            }
            ReportDetailDataModel singleLevelDetailBOM = getSingleLevelBOM(rootItemDataMap, drawNumber);
            detailReportData.add(singleLevelDetailBOM);

            detailReportData = checkSingleLevelBOMInChild(rootItemDataMap, detailReportData);
        } else {
            String itemPhysicalId = (String) rootItemDataMap.get("physicalid");
            if (bomLineMap.containsKey(itemPhysicalId)) {
                String drawNumber = "";
                if (drawingDataHolderMap.containsKey(itemPhysicalId)) {
                    List<Map<String, String>> drawingList = drawingDataHolderMap.get(itemPhysicalId);
                    if(drawingList.size() > 0) {
                        Map<String, String> drawing = drawingList.get(0);
                        drawNumber = drawing.get("Drawing Number");
                    }
                }
                ReportDetailDataModel singleLevelDetailBOM = getSingleLevelBOM((HashMap<String, Object>) bomLineMap.get(itemPhysicalId), drawNumber);
                detailReportData.add(singleLevelDetailBOM);
                detailReportData = checkSingleLevelBOMInChild(rootItemDataMap, detailReportData);
            }
        }
        return detailReportData;
    }

    /**
     * Prepares a Single Level BOM Data of Detail Report Data based on Report
     * Business Logic
     *
     * @param parentItemDataMap
     * @return
     */
    private ReportDetailDataModel getSingleLevelBOM(HashMap<String, Object> parentItemDataMap, String drawingNumber) {
        ReportDetailDataModel singleLevelDetailBOM = new ReportDetailDataModel();
        singleLevelDetailBOM = getRootObjectData(parentItemDataMap, singleLevelDetailBOM);

        List<HashMap<String, Object>> detailBOM = new ArrayList<>();
        String physicalId = (String) parentItemDataMap.get("physicalid");

        HashMap<String, Object> parentDetailBOMData = getBOMData(parentItemDataMap, true);
        detailBOM.add(parentDetailBOMData);

        addDrawingToSingleLevelBOM(physicalId, detailBOM, 1);

        ArrayList<Object> childrenList = (ArrayList<Object>) parentItemDataMap.get("bomLines");
        getChildBOMData(childrenList, detailBOM, drawingNumber);
        singleLevelDetailBOM.setDetailBOM(detailBOM);
        return singleLevelDetailBOM;
    }

    /**
     * Prepares Root Object Data for Summary report based on Report Business
     * Logic
     *
     * @param itemDataMap
     * @param singleLevelDetailBOM
     * @return
     */
    private ReportDetailDataModel getRootObjectData(HashMap<String, Object> itemDataMap, ReportDetailDataModel singleLevelDetailBOM) {
        singleLevelDetailBOM.setRootObjectName((String) Optional.ofNullable(itemDataMap.get("name")).orElse(""));
        singleLevelDetailBOM.setRootObjectRev((String) Optional.ofNullable(itemDataMap.get("revision")).orElse(""));
        singleLevelDetailBOM.setRootObjectState((String) Optional.ofNullable(itemDataMap.get("Status")).orElse(""));
        singleLevelDetailBOM.setRootObjectDes((String) Optional.ofNullable(itemDataMap.get("Description")).orElse(""));
        singleLevelDetailBOM.setRootObjectDrNum((String) Optional.ofNullable(itemDataMap.get("Drawing Number")).orElse(""));
        singleLevelDetailBOM.setRootObjectReleasedBy((String) Optional.ofNullable(rootItemParams.get("rootObjectReleasedBy")).orElse(""));
        singleLevelDetailBOM.setRootObjectReleasedDate((String) Optional.ofNullable(rootItemParams.get("rootObjectReleasedDate")).orElse(""));
        return singleLevelDetailBOM;
    }

    /**
     * Gathers BOM Data of children BOMLines
     *
     * @param childrenList
     * @param detailBOM
     * @return
     */
    private List<HashMap<String, Object>> getChildBOMData(ArrayList<Object> childrenList, List<HashMap<String, Object>> detailBOM, String drawingNumber) {
        if (!NullOrEmptyChecker.isNullOrEmpty(childrenList)) {
            childrenList.forEach((Object childData) -> {
                HashMap<String, Object> childDataMap = (HashMap) childData;
                String physicalId = (String) childDataMap.get("physicalid");
                HashMap<String, Object> childDetailBOMData = getBOMData(childDataMap, false);

                //childDetailBOMData.put("Drawing Number", drawingNumber);
                detailBOM.add(childDetailBOMData);
                addDrawingToSingleLevelBOM(physicalId, detailBOM, 1);

            });
        }
        return detailBOM;
    }

    /**
     * Gathers BOM Data based on physical id If Given item is a root object for
     * a Single Level BOM, then item's Quantity and Position value set as empty
     *
     * @param itemDataMap
     * @param isRoot
     * @return
     */
    private HashMap<String, Object> getBOMData(HashMap<String, Object> itemDataMap, boolean isRoot) {
        HashMap<String, Object> BOMDataMap = new HashMap<>();
        itemDataMap.forEach((String key, Object value) -> {
            if (!key.equals("bomLines") && !key.equals("physicalid")) {
                BOMDataMap.put(key, value);
            }
        });
        if (isRoot) {
            BOMDataMap.put("Qty", "");
            BOMDataMap.put("Position", "");
        } else {
            if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                BOMDataMap.put("Qty", trimQtyToPrecisionCeiling(BOMDataMap.get("Qty").toString()));
            }
        }
        removeSingleExtraAttributes(BOMDataMap);
        return BOMDataMap;
    }

    /**
     * Provides Single Level BOM data from Children BOMLines
     *
     * @param rootItemDataMap
     * @param detailReportData
     * @return
     */
    private List<ReportDetailDataModel> checkSingleLevelBOMInChild(HashMap<String, Object> rootItemDataMap, List<ReportDetailDataModel> detailReportData) {
        ArrayList<Object> childrenList = (ArrayList<Object>) rootItemDataMap.get("bomLines");
        if (!NullOrEmptyChecker.isNullOrEmpty(childrenList)) {
            childrenList.forEach((Object childData) -> {
                HashMap<String, Object> childDataMap = (HashMap) childData;
                String childPhysicalId = (String) childDataMap.get("physicalid");
                if (bomLineMap.containsKey(childPhysicalId)) {
                    prepareDetailReportData((HashMap<String, Object>) bomLineMap.get(childPhysicalId), detailReportData, false);
                }
            });
        }
        return detailReportData;
    }

    /**
     * Prepares Summary Report BOM data
     *
     * @return
     */
    private ReportSummaryDataModel prepareSummaryReportData() {
        ReportSummaryDataModel summaryReportData = new ReportSummaryDataModel();
        if (drawingDataHolderMap.size() > 0) {
            summaryReportData.setSummaryBOM(getSingleSummaryResultList());
        } else {
            summaryReportData.setSummaryBOM(getSummaryResultList());

        }
        summaryReportData.setRootObjectName(rootItemParams.get("rootObjectName").toString());
        summaryReportData.setRootObjectRev(rootItemParams.get("rootObjectRev").toString());
        summaryReportData.setRootObjectDrNum(summaryReportData.getSummaryBOM().get(0).get("Drawing Number"));
        summaryReportData.setRootObjectState(rootItemParams.get("rootObjectState").toString());
        summaryReportData.setRootObjectDes(rootItemParams.get("rootObjectDes").toString());
        summaryReportData.setRootObjectReleasedBy(rootItemParams.get("rootObjectReleasedBy").toString());
        summaryReportData.setRootObjectReleasedDate(rootItemParams.get("rootObjectReleasedDate").toString());
        return summaryReportData;
    }

    /**
     * Gather BOM Data by expanding Root Object and using Report Business Logic
     *
     * @param businessModel
     * @throws Exception
     */
    public void gatherDataFromRootObjectExpansion(ReportBusinessModel businessModel) throws Exception {
        this.setAlternativeAttributeMap(businessModel.getAlternativeAttributeMap());
        this.setAttributNames(businessModel.getAttributeList());
        this.setDrawingDataRequired(Boolean.parseBoolean(businessModel.getParameter().getIsDrawingInfoRequired()));
        this.setIsSummaryRequired(Boolean.parseBoolean(businessModel.getParameter().getIsSummaryRequired()));
        this.setRequestId(businessModel.getParameter().getRequestId());
        this.setRequestLang(businessModel.getParameter().getLang());
        this.setPositionAttributeNameMap(this.commonProperty.getPropertyValue("enovia.position.attribute", true));
        this.setPrimaryLang(businessModel.getParameter().getPrimaryLang());
        this.setSecondaryLang(businessModel.getParameter().getSecondaryLang());

        boolean isChildPropertiesRequired = true;
        if (Integer.parseInt(businessModel.getParameter().getExpandLevel()) == 0) {
            businessModel.getParameter().setExpandLevel("1");
            isChildPropertiesRequired = false;
        }

        SL_REPORT_BUSINESS_LOGIC_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.object.expand.start.info"),
                businessModel.getBusinessObject().getTypeName(),
                businessModel.getBusinessObject().getName(),
                businessModel.getBusinessObject().getRevision(),
                businessModel.getParameter().getExpandLevel()));
        long startTime = System.currentTimeMillis();
        ExpansionWithSelect expandResult = expandObject.expand(businessModel.getParameter().getContext(),
                businessModel.getBusinessObject(),
                businessModel.getParameter().getExpandLevel(),
                this.typesAndRelations);
        long endTime = System.currentTimeMillis();
        SL_REPORT_BUSINESS_LOGIC_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.object.expand.timer.info"), endTime - startTime));

        BusinessObjectWithSelect root = expandResult.getRootWithSelect();

        populateResultsMap(root, businessModel.getParameter().getContext(), new RelationshipWithSelect(),
                typeSelectablesWithOutputName,
                relationSelectablesWithOutputName, true, businessModel.getParameter().getDocType());

        if (isChildPropertiesRequired) {
            RelationshipWithSelectList relationshipList = expandResult.getRelationships();
            RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(relationshipList);
            while (relationshipWithSelectItr.next()) {
                RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
                BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();
                populateResultsMap(businessObjectWithSelect, businessModel.getParameter().getContext(), relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName, false, businessModel.getParameter().getDocType());
                businessObjectWithSelect = null;
                relationshipWithSelect = null;
            }
        }

        multilevelBOMList.forEach(itemData -> {
            HashMap<String, Object> itemDataMap = (HashMap) itemData;
            TreeMap<String, Object> positionedBomLineMap = (TreeMap) itemDataMap.get("bomLines");

            positionedBomLineMap.forEach((key, value) -> {

                HashMap<String, Object> childBom = (HashMap) positionedBomLineMap.get(key);

                String physicalId = childBom.get("physicalid").toString();

                if (drawingDataHolderMap.containsKey(physicalId)) {
                    List<Map<String, String>> drawingList = drawingDataHolderMap.get(physicalId);
                    if(drawingList.size() > 0) {
                        Map<String, String> drawing = drawingList.get(0);
                        childBom.put("Drawing Number", drawing.get("Drawing Number"));
                    }
                }

            });

            String itemName = (String) itemDataMap.get("name");
            if (positionedBomLineMap.size() > 0 || itemName.equals(root.getSelectData("name"))) {
                Collection<Object> positionedList = positionedBomLineMap.values();
                ArrayList<Object> sortedBomList = new ArrayList<>(positionedList);
                String physicalId = itemDataMap.get("physicalid").toString();
                if (drawingDataHolderMap.containsKey(physicalId)) {
                    List<Map<String, String>> drawingList = drawingDataHolderMap.get(physicalId);
                    if(drawingList.size() > 0) {
                        Map<String, String> drawing = drawingList.get(0);
                        itemDataMap.put("Drawing Number", drawing.get("Drawing Number"));
                    }
                }

                itemDataMap.put("bomLines", sortedBomList);
                responseBOMList.add(itemData);

                bomLineMap.put((String) itemDataMap.get("physicalid"), itemData);
            }
        });

        flattenStructure(responseBOMList.get(0), 0, businessModel.getParameter().getDocType());
    }

    /**
     * Process Data for Summary Report
     *
     * @param itemData
     * @param level
     */
    @Override
    protected void flattenStructure(Object itemData, int level, String docType) {
        HashMap<String, Object> itemDataMap = (HashMap) itemData;
        List<HashMap<String, Object>> rootLevelData = new ArrayList<>();
        ArrayList<Object> childrenData = (ArrayList<Object>) itemDataMap.get("bomLines");
        int childLevel = level + 1;
        if (level == 0) {
            reportResultsMap.add(itemDataMap);
            rootLevelData.add(itemDataMap);
            updateSummaryReport(level, null, itemDataMap, "");
            addDrawingToSingleLevelBOM((String) itemDataMap.get("physicalid"), rootLevelData, level);

        }
        childrenData.forEach(childData -> {
            HashMap<String, Object> childDataMap = new HashMap<>();
            List<HashMap<String, Object>> childLevelData = new ArrayList<>();
            childDataMap.putAll((HashMap<String, Object>) childData);
            String childPhysicalId = (String) childDataMap.get("physicalid");
            if (attributeList.isEmpty() || attributeList.contains("Level")) {
                childDataMap.put("Level", "" + childLevel);
            }
            if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                childDataMap.put("Qty", trimQtyToPrecisionCeiling(childDataMap.get("Qty").toString()));
            }
            if (drawingDataHolderMap.containsKey(childPhysicalId)) {
                List<Map<String, String>> drawingList = drawingDataHolderMap.get(childPhysicalId);
                if(drawingList.size() > 0) {
                    Map<String, String> drawing = drawingList.get(0);
                    childDataMap.put("Drawing Number", drawing.get("Drawing Number"));
                }
            }
            reportResultsMap.add(childDataMap);
            childLevelData.add(childDataMap);
            updateSummaryReport(childLevel, itemDataMap, childDataMap, "");
            addDrawingToSingleLevelBOM(childPhysicalId, childLevelData, 0);

            if (bomLineMap.containsKey(childPhysicalId)) {
                flattenStructure(bomLineMap.get(childPhysicalId), childLevel, docType);
            }
        });
    }

    /**
     * Gathers Data from GTS Service and Updates Title Data for Report
     *
     * @param isTitleRequired
     * @param detailReportData
     * @throws IOException
     * @throws MalformedURLException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public void addGTSTitleForAllItems(boolean isTitleRequired, List<ReportDetailDataModel> detailReportData) throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        long startGTSTime = System.currentTimeMillis();
        HashMap<String, String> termIdTitleMap = GTSDataServiceUtil.getTermIdTitleMap(termIdList, primaryLang, secondaryLang, languageMap);
        long endGTSTime = System.currentTimeMillis();
        SL_REPORT_BUSINESS_LOGIC_LOGGER.info("Total time taken for GTS Service call and Title data population in the structure : " + (endGTSTime - startGTSTime));

        if (!isTitleRequired) {
            detailReportData.forEach((ReportDetailDataModel singleLevelBOM) -> {
                if (!NullOrEmptyChecker.isNullOrEmpty(singleLevelBOM.getDetailBOM())) {
                    singleLevelBOM.getDetailBOM().forEach((HashMap<String, Object> itemData) -> {
                        addAttributeWithoutTitle(itemData);
                    });
                }
            });
        }

        detailReportData.forEach((ReportDetailDataModel singleLevelBOM) -> {
            if (!NullOrEmptyChecker.isNullOrEmpty(singleLevelBOM.getDetailBOM())) {
                singleLevelBOM.getDetailBOM().forEach((HashMap<String, Object> itemData) -> {
                    addAttributeWithTitle(itemData, termIdTitleMap);
                });
            }
        });

        if (isSummaryRequired) {
            summaryMap.forEach((summaryKey, summaryData) -> {
                addTitleInSummaryreport(summaryData, termIdTitleMap);
            });
        }
    }

    /**
     * Adds connecting Drawing Number for given Item in Detail Report Data<br>
     * Updates Summary Report Data for given Item
     *
     * @param physicalId
     * @param bomDataList
     */
    private void addDrawingToSingleLevelBOM(String physicalId, List<HashMap<String, Object>> bomDataList, int level) {
        if (attributeList.isEmpty() || !attributeList.contains("Drawing Number")) {
            return;
        }

        if (drawingDataHolderMap.containsKey(physicalId)) {

            List<Map<String, String>> drawingList = drawingDataHolderMap.get(physicalId);
            if (drawingList.size() >= 1 && level == 0) {

                drawingList.remove(0);

            }
            List<HashMap<String, Object>> drawingDataList = new ArrayList<>();

            drawingDataHolderMap.get(physicalId).forEach(drawingData -> {
                HashMap<String, Object> drawingDataObj = new HashMap<>();

                drawingData.forEach((k, v) -> drawingDataObj.put(k, v));
                String type = "Drawing";

                JSON_OUTPUT_LOGGER.info("Setting empty name for drawing, drawing name : " + drawingDataObj.get("name"));
                drawingDataObj.put("name", "");
                updateSummaryReport(0, null, drawingDataObj, type);
                drawingDataList.add(drawingDataObj);
                removeSingleExtraAttributes((HashMap) drawingDataObj);

            });

            bomDataList.addAll(drawingDataList);
        }

    }
}
