/**
 *
 */
package com.bjit.mapper.mapproject.jsonOutput;

import com.bjit.common.code.utility.conversion.unit.InventoryUnitConverter;
import com.bjit.common.rest.app.service.GTS.GTSDataServiceUtil;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FecthItemAutCycle;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FetchEvolution;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonUtilities;
import com.bjit.common.rest.app.service.utilities.DrawingUtil;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.model.Drawing;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.actions.utilities.BusinessObjectUtils;
import com.bjit.mapper.mapproject.expand.ExpandObject;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import com.bjit.mapper.mapproject.util.CommonUtil.ThrowingConsumer;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author TAREQ SEFATI
 *
 */
public class JsonOutput {

    protected ExpandObject expandObject;
    protected DrawingUtil drawingUtil;
    protected ObjectTypesAndRelations typesAndRelations;
    protected Map<String, String> jsonObjectMap = new HashMap<>();
    protected Map<String, String> typeSelectablesWithOutputName = new LinkedHashMap<>();
    protected Map<String, String> relationSelectablesWithOutputName = new LinkedHashMap<>();
    protected List<String> typeSelectables;
    protected List<String> relationSelectables;
    protected boolean isMBOMReport;
    protected List<Map<String, Object>> reportResultsMap;
    protected List<Object> multilevelBOMList;
    protected List<Object> responseBOMList;
    protected HashMap<String, Object> bomLineMap;
    protected HashMap<String, Object> singleObjResult;
    protected HashMap<String, Object> mergedQtyRow;
    protected boolean isSummaryRequired;
    protected HashMap<String, HashMap<String, String>> summaryMap;
    protected HashMap<String, HashMap<String, String>> singleLevelSummaryMap;
    protected List<HashMap<String, String>> summaryResultList;
    protected List<HashMap<String, String>> summarySingleResultList;
    protected Set<String> summaryTemp;
    protected boolean isDrawingDataRequired;
    protected HashMap<String, List<Map<String, String>>> drawingDataHolderMap;
    protected HashMap<String, String> drawingNumberMap;
    protected HashMap<String, Integer> bomTracker;
    protected HashMap<String, String> tnrTrackerMap;
    protected HashMap<String, String> duplicateRelTrackerMap;
    protected HashMap<String, String[]> lengthWidthMap;
    protected Map<String, Object> singleResultCpx;
    protected List<Map<String, Object>> resultsMapCpx;
    protected Map<String, Object> singleResultBrochureItem;
    protected List<Map<String, Object>> resultsMapBrochureItem;
    protected Map<String, Object> singleResultBrochure;
    protected List<Map<String, Object>> resultsMapBrochure;
    protected HashMap<String, ArrayList<String>> alternativeAttributeMap;
    protected HashMap<String, Object> rootItemParams;
    protected HashMap<String, String> typeShortNameMap;
    protected Map<String, String> gtsTitleList;
    protected HashMap<String, String> positionAttributeNameMap;
    protected HashMap<String, String> bomMarkerMap;
    protected String requestLang;
    protected String primaryLang;
    protected String secondaryLang;
    protected String rootObjDrawing;
    protected boolean foundData = false;
    protected static final Logger JSON_OUTPUT_LOGGER = Logger.getLogger(JsonOutput.class);
    protected boolean isDrawingRevRequired;
    protected HashSet<String> termIdList;
    protected List<String> attributeList;
    protected String requestId;
    protected boolean mailReportLinkToUser;
    protected CommonPropertyReader commonProperty;
    protected HashMap<String, String> standardShortNameMap;
    protected HashMap<String, String> languageMap;
    protected QuantityCalculation quantityCalculation;
    protected String requester;
    protected List<String> usageCoeffTypesForQty;
    protected List<String> unitForLengthCount;
    protected List<String> unitForAreaCount;
    protected List<String> unitForConversion;
    protected DecimalFormat qtyPrecisionFormat;
    protected List<String> processCnxTypeListForDrawing;
    protected HashMap<String, String> bomDataList;
    protected List<String> stdTypes;

    public JsonOutput() throws IOException {
        expandObject = new ExpandObject();
        singleObjResult = new LinkedHashMap<>();
        mergedQtyRow = new LinkedHashMap<>();
        singleResultCpx = new LinkedHashMap<>();
        bomLineMap = new HashMap<>();
        bomTracker = new HashMap<>();
        tnrTrackerMap = new HashMap<>();
        duplicateRelTrackerMap = new HashMap<>();
        lengthWidthMap = new HashMap<>();
        singleResultBrochureItem = new LinkedHashMap<>();
        singleResultBrochure = new LinkedHashMap<>();
        positionAttributeNameMap = new HashMap<>();
        multilevelBOMList = new ArrayList<>();
        responseBOMList = new ArrayList<>();
        reportResultsMap = new ArrayList<>();
        resultsMapCpx = new ArrayList<>();
        resultsMapBrochureItem = new ArrayList<>();
        resultsMapBrochure = new ArrayList<>();
        summaryMap = new LinkedHashMap<String, HashMap<String, String>>();
        singleLevelSummaryMap = new LinkedHashMap<String, HashMap<String, String>>();
        summaryResultList = new ArrayList<HashMap<String, String>>();
        summarySingleResultList = new ArrayList<HashMap<String, String>>();
        summaryTemp = new HashSet<String>();
        typesAndRelations = new ObjectTypesAndRelations();
        typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        typeShortNameMap = typesAndRelations.getTypeShortNameMap();
        gtsTitleList = new LinkedHashMap<>();
        rootItemParams = new LinkedHashMap<>();
        isMBOMReport = false;
        rootObjDrawing = null;
        bomMarkerMap = new HashMap<>();
        alternativeAttributeMap = new HashMap<>();
        attributeList = new ArrayList<>();
        requestId = "";
        termIdList = new HashSet();
        drawingNumberMap = new HashMap<>();
        drawingDataHolderMap = new HashMap<String, List<Map<String, String>>>();
        isDrawingDataRequired = false;
        isSummaryRequired = false;
        commonProperty = new CommonPropertyReader();
        standardShortNameMap = commonProperty.getPropertyValue("enovia.standard.type", Boolean.TRUE);
        languageMap = commonProperty.getPropertyValue("gts.table.language", true);
        usageCoeffTypesForQty = Arrays.asList(commonProperty.getPropertyValue("qty.types.for.usage.coefficient").split("\\|"));
        unitForLengthCount = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
        unitForAreaCount = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
        unitForConversion = Arrays.asList(PropertyReader.getProperty("unit.conversion").split("\\|"));
        quantityCalculation = new QuantityCalculation();
        qtyPrecisionFormat = new DecimalFormat(PropertyReader.getProperty("bom.export.quantity.precision.rounding.pattern"));
        processCnxTypeListForDrawing = Arrays.asList(PropertyReader.getProperty("item.types.list.for.process.cnx").split(","));
        stdTypes = Arrays.asList(commonProperty.getPropertyValue("std.types").split("\\|"));
        mailReportLinkToUser = false;
        bomDataList = new HashMap<>();
    }

    public JsonOutput(String mapsAbsoluteDirectory) throws IOException {
        expandObject = new ExpandObject();
        singleObjResult = new LinkedHashMap<>();
        mergedQtyRow = new LinkedHashMap<>();
        singleResultCpx = new LinkedHashMap<>();
        bomLineMap = new HashMap<>();
        tnrTrackerMap = new HashMap<>();
        duplicateRelTrackerMap = new HashMap<>();
        lengthWidthMap = new HashMap<>();
        bomTracker = new HashMap<>();
        singleResultBrochureItem = new LinkedHashMap<>();
        singleResultBrochure = new LinkedHashMap<>();
        positionAttributeNameMap = new HashMap<>();
        multilevelBOMList = new ArrayList<>();
        responseBOMList = new ArrayList<>();
        termIdList = new HashSet();
        rootItemParams = new LinkedHashMap<>();
        reportResultsMap = new ArrayList<>();
        resultsMapCpx = new ArrayList<>();
        resultsMapBrochureItem = new ArrayList<>();
        resultsMapBrochure = new ArrayList<>();
        summaryMap = new LinkedHashMap<String, HashMap<String, String>>();
        singleLevelSummaryMap = new LinkedHashMap<String, HashMap<String, String>>();
        summaryTemp = new HashSet<String>();
        summaryResultList = new ArrayList<HashMap<String, String>>();
        summarySingleResultList = new ArrayList<HashMap<String, String>>();
        typesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory);
        typeSelectablesWithOutputName = typesAndRelations.getTypeSelectablesWithOutputName();
        relationSelectablesWithOutputName = typesAndRelations.getRelationSelectablesWithOutputName();
        typeShortNameMap = typesAndRelations.getTypeShortNameMap();
        bomMarkerMap = new HashMap<>();
        gtsTitleList = new LinkedHashMap<>();
        alternativeAttributeMap = new HashMap<>();
        attributeList = new ArrayList<>();
        requestId = "";
        isMBOMReport = false;
        drawingNumberMap = new HashMap<>();
        drawingDataHolderMap = new HashMap<String, List<Map<String, String>>>();
        isDrawingDataRequired = false;
        isSummaryRequired = false;
        commonProperty = new CommonPropertyReader();
        standardShortNameMap = commonProperty.getPropertyValue("enovia.standard.type", true);
        languageMap = commonProperty.getPropertyValue("gts.table.language", true);
        usageCoeffTypesForQty = Arrays.asList(commonProperty.getPropertyValue("qty.types.for.usage.coefficient").split("\\|"));
        unitForLengthCount = Arrays.asList(PropertyReader.getProperty("length.units").split("\\|"));
        unitForAreaCount = Arrays.asList(PropertyReader.getProperty("area.units").split("\\|"));
        unitForConversion = Arrays.asList(PropertyReader.getProperty("unit.conversion").split("\\|"));
        quantityCalculation = new QuantityCalculation();
        qtyPrecisionFormat = new DecimalFormat(PropertyReader.getProperty("bom.export.quantity.precision.rounding.pattern"));
        stdTypes = Arrays.asList(commonProperty.getPropertyValue("std.types").split("\\|"));
        processCnxTypeListForDrawing = Arrays.asList(PropertyReader.getProperty("item.types.list.for.process.cnx").split(","));
        mailReportLinkToUser = false;
        bomDataList = new HashMap<>();
    }

    public BomExportResults prepareJsonForBomExport(Context context, BusinessObject businessObject, HashMap<String, String> urlParams, String docType) throws MatrixException, Exception {
        if (attributeList.isEmpty() || attributeList.contains("Title")) {
            attributeList.add("Term_ID");
        }
        attributeList.add("Document Links");

        boolean isChildPropertiesRequired = true;
        // Prevention of 99 level expansion if level 0 is requested.
        String expandLevel = urlParams.get("expandLevel");
        if (Integer.parseInt(expandLevel) == 0) {
            expandLevel = "1";
            isChildPropertiesRequired = false;
        }
        JSON_OUTPUT_LOGGER.info("Going to expand ROOT Object: Type: "
                + businessObject.getTypeName() + " Name: " + businessObject.getName() + " Rev: " + businessObject.getRevision()
                + " till level : " + expandLevel);
        long startTime = System.currentTimeMillis();
        ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, expandLevel, this.typesAndRelations);

        long endTime = System.currentTimeMillis();
        JSON_OUTPUT_LOGGER.info("Time taken for expansion: " + (endTime - startTime) + " milliseconds.");
        BusinessObjectWithSelect root = expandResult.getRootWithSelect();
        if (root.getSelectData("type").equals("CreateAssembly")
                || root.getSelectData("type").equals("ProcessContinuousCreateMaterial")) {
            isMBOMReport = true;
        }
        populateBomExportResultsMap(root, context, new RelationshipWithSelect(), typeSelectablesWithOutputName, relationSelectablesWithOutputName, true, docType);

        if (isChildPropertiesRequired) {
            RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            while (relationshipWithSelectItr.next()) {
                RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
                BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();
                populateBomExportResultsMap(businessObjectWithSelect, context, relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName, false, docType);
                businessObjectWithSelect = null;
                relationshipWithSelect = null;
            }
        }
        multilevelBOMList.forEach(itemData -> {
            HashMap<String, Object> itemDataMap = (HashMap) itemData;
            TreeMap<String, Object> positionedBomLineMap = (TreeMap) itemDataMap.get("bomLines");
            removeExtraBOMExportAttributes(itemDataMap);
            String itemName = (String) itemDataMap.get("name");
            if (positionedBomLineMap.size() > 0 || itemName.equals(root.getSelectData("name"))) {
                Collection<Object> positionedList = positionedBomLineMap.values();
                positionedList.forEach(obj -> removeExtraBOMExportAttributes((HashMap) obj));
                ArrayList<Object> sortedBomList = new ArrayList<Object>(positionedList);
                itemDataMap.put("bomLines", sortedBomList);
                responseBOMList.add(itemData);
            }
        });
        modifyResultForBOMExport();
        BomExportResults results = new BomExportResults();
        if (!NullOrEmptyChecker.isNullOrEmpty(getRequester())) {
            results.setRequester(getRequester());
        }
        results.setBomExportResults(responseBOMList);
        return results;
    }

    public void modifyResultForBOMExport() throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        HashMap<String, String> termIdTempTitleMap = new HashMap<>();
        if (attributeList.isEmpty() || attributeList.contains("Title")) {
            long startGTSTime = System.currentTimeMillis();
            termIdTempTitleMap = GTSDataServiceUtil.getTermIdTitleMap(termIdList, primaryLang, secondaryLang, languageMap);
            long endGTSTime = System.currentTimeMillis();
            JSON_OUTPUT_LOGGER.info("Total time taken for GTS Service call and Title data population in the structure : " + (endGTSTime - startGTSTime));
        }
        HashMap<String, String> termIdTitleMap = termIdTempTitleMap;
        responseBOMList.forEach(itemData -> {
            HashMap<String, Object> itemDataMap = (HashMap) itemData;
            if (attributeList.isEmpty() || attributeList.contains("Title")) {
                String termId = (String) itemDataMap.get("Term_ID");
                itemDataMap.remove("Term_ID");
                if (!NullOrEmptyChecker.isNullOrEmpty(termId)) {
                    String title = termIdTitleMap.get(termId);
                    if (!NullOrEmptyChecker.isNullOrEmpty(title)) {
                        itemDataMap.put("Title", title);
                    }
                }
            }
            if (attributeList.isEmpty() || attributeList.contains("Qty")) {

                itemDataMap.put("Qty", trimQtyToPrecisionCeiling(itemDataMap.get("Qty").toString()));

            }

            ArrayList<HashMap<String, Object>> childrenList = (ArrayList<HashMap<String, Object>>) itemDataMap.get("bomLines");
            childrenList.forEach(child -> {
                if (attributeList.isEmpty() || attributeList.contains("Title")) {
                    String childTermId = (String) child.get("Term_ID");
                    child.remove("Term_ID");
                    if (!NullOrEmptyChecker.isNullOrEmpty(childTermId)) {
                        String childTitle = termIdTitleMap.get(childTermId);
                        if (!NullOrEmptyChecker.isNullOrEmpty(childTitle)) {
                            child.put("Title", childTitle);
                        }
                    }
                }
                if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                    child.put("Qty", trimQtyToPrecisionCeiling(child.get("Qty").toString()));
                }
            });
        });
    }

    //Normal format json output
    public ReportResults prepareJsonFromExpandObject(Context context, BusinessObject businessObject, String expandLevel, String docType) throws MatrixException, Exception {
        boolean isTitleRequired = attributeList.isEmpty() || attributeList.contains("Title");
        // attributeList.add("Technical Designation");
        // attributeList.add("Revision Comment");
//        attributeList.add("Reference");
//        attributeList.add("item common text");
//        attributeList.add("bom common text");
        if (isTitleRequired) {
            attributeList.add("Term_ID");
        }
        attributeList.add("PCS");
        boolean isChildPropertiesRequired = true;
        // Prevention of 99 level expansion if level 0 is requested.
        if (Integer.parseInt(expandLevel) == 0) {
            expandLevel = "1";
            isChildPropertiesRequired = false;
        }
        JSON_OUTPUT_LOGGER.info("Going to expand ROOT Object: Type: "
                + businessObject.getTypeName() + " Name: " + businessObject.getName() + " Rev: " + businessObject.getRevision()
                + " till level : " + expandLevel);
        long startTime = System.currentTimeMillis();
        ExpansionWithSelect expandResult = expandObject.expand(context, businessObject, expandLevel, this.typesAndRelations);
        long endTime = System.currentTimeMillis();
        JSON_OUTPUT_LOGGER.info("Time taken for expansion: " + (endTime - startTime) + " milliseconds.");
        BusinessObjectWithSelect root = expandResult.getRootWithSelect();
        if (root.getSelectData("type").equals("CreateAssembly")
                || root.getSelectData("type").equals("ProcessContinuousCreateMaterial")) {
            isMBOMReport = true;
        }

        // populateWidthAndLength(context, root, expandLevel);
        populateResultsMap(root, context, new RelationshipWithSelect(), typeSelectablesWithOutputName, relationSelectablesWithOutputName, true, docType);
        if (isChildPropertiesRequired) {
            RelationshipWithSelectList relationshipList = expandResult.getRelationships();
            JSON_OUTPUT_LOGGER.info("Total number of connections found: " + relationshipList.size());
            //int objLimitForMail = Integer.parseInt(commonProperty.getPropertyValue("mail.report.link.item.minimum.size"));
            int objLimitForMail = Integer.MAX_VALUE;
            if (!NullOrEmptyChecker.isNullOrEmpty(requestId)
                    && relationshipList.size() > objLimitForMail) {
                mailReportLinkToUser = true;
            }
            RelationshipWithSelectItr relationshipWithSelectItr = new RelationshipWithSelectItr(/*positionSort(*/relationshipList/*)*/);
            while (relationshipWithSelectItr.next()) {
                RelationshipWithSelect relationshipWithSelect = relationshipWithSelectItr.obj();
                BusinessObjectWithSelect businessObjectWithSelect = relationshipWithSelect.getTarget();
                populateResultsMap(businessObjectWithSelect, context, relationshipWithSelect, typeSelectablesWithOutputName, relationSelectablesWithOutputName, false, docType);
                businessObjectWithSelect = null;
                relationshipWithSelect = null;
            }
        }

        multilevelBOMList.forEach(itemData -> {
            HashMap<String, Object> itemDataMap = (HashMap) itemData;
            TreeMap<String, Object> positionedBomLineMap = (TreeMap) itemDataMap.get("bomLines");
            String itemName = (String) itemDataMap.get("name");
            if (positionedBomLineMap.size() > 0 || itemName.equals(root.getSelectData("name"))) {
                Collection<Object> positionedList = positionedBomLineMap.values();
                ArrayList<Object> sortedBomList = new ArrayList<Object>(positionedList);
                itemDataMap.put("bomLines", sortedBomList);
                responseBOMList.add(itemData);
                bomLineMap.put((String) itemDataMap.get("physicalid"), itemData);
            }
        });

        flattenStructure(responseBOMList.get(0), 0, docType);
        addGTSTitleForAllItems(isTitleRequired);
        ReportResults results = new ReportResults();
        results.setResults(reportResultsMap);
        return results;
    }

    protected void flattenStructure(Object itemData, int level, String docType) {
        HashMap<String, Object> itemDataMap = (HashMap) itemData;
        ArrayList<Object> childrenData = (ArrayList<Object>) itemDataMap.get("bomLines");
        int childLevel = level + 1;
        if (level == 0) {
            reportResultsMap.add(itemDataMap);
            updateSummaryReport(level, null, itemDataMap, "");
            addDrawingToResponse((HashMap) responseBOMList.get(0), docType);
            removeSingleExtraAttributes(itemDataMap);
        }
        childrenData.forEach(childData -> {
            HashMap<String, Object> childDataMap = new HashMap<>();
            childDataMap.putAll((HashMap<String, Object>) childData);
            String childPhysicalId = (String) childDataMap.get("physicalid");
            if (attributeList.isEmpty() || attributeList.contains("Level")) {
                childDataMap.put("Level", "" + childLevel);
            }
            if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                childDataMap.put("Qty", trimQtyToPrecisionCeiling(childDataMap.get("Qty").toString()));
            }
            reportResultsMap.add(childDataMap);
            addDrawingToResponse(childDataMap, docType);
            updateSummaryReport(childLevel, itemDataMap, childDataMap, "");
            removeSingleExtraAttributes(childDataMap);
            if (bomLineMap.containsKey(childPhysicalId)) {
                flattenStructure(bomLineMap.get(childPhysicalId), childLevel, docType);
            }
        });
    }

    /**
     * To response add drawing info
     *
     * @param itemDataMap
     */
    protected void addDrawingToResponse(HashMap<String, Object> itemDataMap, String docType) {
        if (attributeList.isEmpty() || !attributeList.contains("Drawing Number")) {
            return;
        }
        String physicalId = (String) itemDataMap.get("physicalid");
        if (drawingDataHolderMap.containsKey(physicalId)) {
            List<Map<String, String>> drawingList = drawingDataHolderMap.get(physicalId);
            String[] attributeArr = docType.split(",");

            if (((drawingList.size() > 0 && attributeArr.length == 1) && (attributeArr[0].equalsIgnoreCase("ProductionAndCustomer") || attributeArr[0].equalsIgnoreCase("Customer")))) {
                drawingList.remove(0);
            }
            List<Map<String, Object>> drawingDataList = new ArrayList<>();
            drawingDataHolderMap.get(physicalId).forEach(drawingData -> {
                Map<String, Object> drawingDataObj = new HashMap<>();
                drawingData.forEach((k, v) -> drawingDataObj.put(k, v));
                // drawingDataObj.put("Type", "Drawing");
                // drawingDataList.add(drawingDataObj);
                String type = "Drawing";
                updateSummaryReport(0, null, drawingDataObj, type);
                removeSingleExtraAttributes((HashMap) drawingDataObj);
                /*
                    VSIX-5137 For Multiple Drawing, in first row, only drawing Number will show,
                    and 2nd row and onwards – Name column will be empty,
                    drawing Number should be in drawing number column.
                 */
                JSON_OUTPUT_LOGGER.info("Setting empty name for drawing, drawing name : " + drawingDataObj.get("name"));
                drawingDataObj.put("name", "");
                drawingDataList.add(drawingDataObj);
            });
            reportResultsMap.addAll(drawingDataList);
        }
    }

    public void addGTSTitleForAllItems(boolean isTitleRequired) throws IOException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        long startGTSTime = System.currentTimeMillis();
        HashMap<String, String> termIdTitleMap = GTSDataServiceUtil.getTermIdTitleMap(termIdList, primaryLang, secondaryLang, languageMap);
        long endGTSTime = System.currentTimeMillis();
        JSON_OUTPUT_LOGGER.info("Total time taken for GTS Service call and Title data population in the structure : " + (endGTSTime - startGTSTime));

        if (!isTitleRequired) {
            reportResultsMap.forEach(itemData -> {
                addAttributeWithoutTitle((HashMap) itemData);
            });
        }
        reportResultsMap.forEach(itemData -> {
            addAttributeWithTitle((HashMap) itemData, termIdTitleMap);
        });

        if (isSummaryRequired) {
            summaryMap.forEach((summaryKey, summaryData) -> {
                addTitleInSummaryreport(summaryData, termIdTitleMap);
            });
        }
    }

    public void addAttributeWithoutTitle(HashMap<String, Object> itemDataMap) {
        String revisionComment = "";
        // String reference = "";
        String itemCommonText = "";
        String bomCommonText = "";
        String titleDescription = "";
        String technicalDesignation = "";
        if (attributeList.isEmpty() || attributeList.contains("Technical Designation")) {
            technicalDesignation = (String) itemDataMap.get("Technical Designation");
        }
        if (attributeList.isEmpty() || attributeList.contains("Revision Comment")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) itemDataMap.get("Revision Comment"))) {
                revisionComment = (String) itemDataMap.get("Revision Comment");
            }
        }
        /*  if (attributeList.isEmpty() || attributeList.contains("Reference")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) itemDataMap.get("Reference"))) {
                reference = (String) itemDataMap.get("Reference");
            }
        } */
        if (attributeList.isEmpty() || attributeList.contains("item common text")) {
            if (!NullOrEmptyChecker.isNullOrEmpty(((String) itemDataMap.get("item common text")))) {
                itemCommonText = (String) itemDataMap.get("item common text");
            }
        }
        if (attributeList.isEmpty() || attributeList.contains("bom common text")) {
            if (!NullOrEmptyChecker.isNullOrEmpty(((String) itemDataMap.get("bom common text")))) {
                bomCommonText = (String) itemDataMap.get("bom common text");
            }
        }

        titleDescription = title(technicalDesignation, "", "", revisionComment, itemCommonText, bomCommonText);
        if (!NullOrEmptyChecker.isNullOrEmpty(titleDescription)) {
            itemDataMap.put("Title", titleDescription);

        }

//        itemDataMap.remove("Technical Designation");
//        itemDataMap.remove("Revision Comment");
//        itemDataMap.remove("Reference");
//        itemDataMap.remove("Term_ID");
//        itemDataMap.remove("item common text");
//        itemDataMap.remove("bom common text");
    }

    public void addAttributeWithTitle(HashMap<String, Object> itemDataMap, HashMap<String, String> termIdTitleMap) {
        String revisionComment = "";
        String itemCommonText = "";
        String bomCommonText = "";
        String termId = itemDataMap.get("Term_ID").toString();
        String titleDescription = "";
        String technicalDesignation = "";
        if (attributeList.isEmpty() || attributeList.contains("Technical Designation")) {
            technicalDesignation = (String) itemDataMap.get("Technical Designation");
        }
        if (attributeList.isEmpty() || attributeList.contains("Revision Comment")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) itemDataMap.get("Revision Comment"))) {
                revisionComment = (String) itemDataMap.get("Revision Comment");
            }
        }
        if (attributeList.isEmpty() || attributeList.contains("item common text")) {
            if (!NullOrEmptyChecker.isNullOrEmpty(((String) itemDataMap.get("item common text")))) {
                itemCommonText = (String) itemDataMap.get("item common text");
            }
        }
        if (attributeList.isEmpty() || attributeList.contains("bom common text")) {
            if (!NullOrEmptyChecker.isNullOrEmpty(((String) itemDataMap.get("bom common text")))) {
                bomCommonText = (String) itemDataMap.get("bom common text");
            }
        }
        String preTitle = (String) itemDataMap.get("Title");
        String title = "";

        if (!NullOrEmptyChecker.isNullOrEmpty(termId)) {
            title = termIdTitleMap.get(termId);
        }
//            if (preTitle.contains(technicalDesignation) || preTitle.contains(revisionComment) || preTitle.contains(reference) || preTitle.contains(itemCommonText) || preTitle.contains(bomCommonText)) {
//                titleDescription = title;
//            } else {
        titleDescription = title(technicalDesignation, title, preTitle, revisionComment, itemCommonText, bomCommonText);
        // }
        if (!NullOrEmptyChecker.isNullOrEmpty(titleDescription)) {
            itemDataMap.put("Title", titleDescription);

        }

        itemDataMap.remove("Technical Designation");
        itemDataMap.remove("Revision Comment");
        itemDataMap.remove("Term_ID");
        itemDataMap.remove("item common text");
        itemDataMap.remove("bom common text");
    }

    public void addTitleInSummaryreport(HashMap<String, String> summaryData, HashMap<String, String> termIdTitleMap) {
        String termId = summaryData.get("Term_ID");
        String revisionComment = "";
        String itemCommonText = "";
        String bomCommonText = "";
        String titleDescription = "";
        String technicalDesignation = "";

        if (!NullOrEmptyChecker.isNullOrEmpty((String) summaryData.get("Technical Designation"))) {
            technicalDesignation = (String) summaryData.get("Technical Designation");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty((String) summaryData.get("Revision Comment"))) {
            revisionComment = (String) summaryData.get("Revision Comment");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(((String) summaryData.get("item common text")))) {
            itemCommonText = (String) summaryData.get("item common text");
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(((String) summaryData.get("bom common text")))) {
            bomCommonText = (String) summaryData.get("bom common text");
        }

        String preTitle = (String) summaryData.get("Title");
        String title = "";

        if (!NullOrEmptyChecker.isNullOrEmpty(termId)) {
            title = termIdTitleMap.get(termId);
        }
//        if (preTitle.contains(technicalDesignation) || preTitle.contains(revisionComment) || preTitle.contains(reference) || preTitle.contains(itemCommonText) || preTitle.contains(bomCommonText)) {
//            titleDescription = preTitle;
//        } else {
        titleDescription = title(technicalDesignation, title, preTitle, revisionComment, itemCommonText, bomCommonText);
        // }

        if (!NullOrEmptyChecker.isNullOrEmpty(titleDescription)) {
            summaryData.put("Title", titleDescription);
            summaryData.remove("Term_ID");
            summaryData.remove("Technical Designation");
            summaryData.remove("Revision Comment");
            summaryData.remove("item common text");
            summaryData.remove("bom common text");
        }

    }

    protected void modifyValuesForBomExport(Context context, BusinessObjectWithSelect busWithSelect, boolean isAttributeListEmpty, boolean isRoot, String docType) throws MatrixException, IOException {
        String type = (String) singleObjResult.get("Type");
        if (isAttributeListEmpty || attributeList.contains("Title")) {
            singleObjResult.put("Title", "EN: " + ((String) singleObjResult.get("Title")));
            String termID = (String) singleObjResult.get("Term_ID");
            if (!NullOrEmptyChecker.isNullOrEmpty(termID)) {
                termIdList.add(termID);
            }
        }

        if (isAttributeListEmpty || attributeList.contains("Length")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("Length"))) {
                Float ln = Float.parseFloat((String) singleObjResult.get("Length")) * 1000;
                singleObjResult.put("Length", String.format("%.1f", ln));
            } else {
                singleObjResult.put("Length", "");
            }
        }

        if (attributeList.contains("Work Quantity")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("Work Quantity"))) {
                singleObjResult.put("Work Quantity", singleObjResult.get("Work Quantity"));
            } else {
                singleObjResult.remove("Work Quantity");
            }
        }

        if (attributeList.contains("ItemCode") && (singleObjResult.get("Project").toString().equalsIgnoreCase("AUTOMATION_INTERNAL"))) {
            if (!singleObjResult.get("AUT_Purpose").toString().equalsIgnoreCase("Phantom")) {
                if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("ItemCode"))) {

                    singleObjResult.put("ItemCode", singleObjResult.get("ItemCode"));

                } else {
                    FetchEvolution fetchEvolution = new FetchEvolution();
                    FecthItemAutCycle fecthItemAutCycle = new FecthItemAutCycle();
                    String getParentPhysicalId = "";
                    String relationshipIdByItem = fecthItemAutCycle.getRelationshipIdByItem(context, singleObjResult.get("Type").toString(), singleObjResult.get("name").toString(), singleObjResult.get("revision").toString());

                    List<String> typeRelationshipToParentId = fecthItemAutCycle.getTypeRelationshipToChildId(context, relationshipIdByItem);
                    if (typeRelationshipToParentId.size() > 0) {
                        getParentPhysicalId = fecthItemAutCycle.getRelationshipIdByItem(context, typeRelationshipToParentId.get(3), typeRelationshipToParentId.get(4), typeRelationshipToParentId.get(5));
                    }
                    try {
                        // boolean hasEvolution = fetchEvolution.hasEvolution(getParentPhysicalId, context);

                        boolean hasEvolution = fetchEvolution.hasConfigContext(getParentPhysicalId, context);

                        if (hasEvolution) {
                            List<String> mvRelid = fetchEvolution.fetchConfigContext(getParentPhysicalId, context);

//                            Map.Entry<String, List<String>> next = mvRelid.entrySet().iterator().next();
//                            List<String> mvrel = mvRelid.get(next.getKey());
                            List<String> mvitemInfo = fecthItemAutCycle.getTypeRelationshipId(context, mvRelid.get(0));
//                           
                            singleObjResult.put("ItemCode", mvitemInfo.get(1));
                            JSON_OUTPUT_LOGGER.info("Item code value " + mvitemInfo.get(1));

                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }

        if (isAttributeListEmpty || attributeList.contains("AUT_Purpose")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("AUT_Purpose"))) {
                singleObjResult.put("AUT_Purpose", singleObjResult.get("AUT_Purpose"));
                if (singleObjResult.get("AUT_Purpose").toString().equalsIgnoreCase("Phantom")) {
                    singleObjResult.put("ItemCode", singleObjResult.get("name"));

                }
            } else {
                singleObjResult.remove("AUT_Purpose");
            }
        }

        if (isAttributeListEmpty || attributeList.contains("Width")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("Width"))) {
                Float ln = Float.parseFloat((String) singleObjResult.get("Width")) * 1000;
                singleObjResult.put("Width", String.format("%.1f", ln));
            } else {
                singleObjResult.put("Width", "");
            }
        }
        if (isAttributeListEmpty || attributeList.contains("ERP Item Type")) {
            String value = (String) singleObjResult.get("ERP Item Type");
            if (value.equals("0") || value.equals("1")) {
                singleObjResult.put("ERP Item Type", "manufacture");
            } else {
                singleObjResult.put("ERP Item Type", "purchase");
            }
        }

        String[] attributeArr = docType.split(",");
        attributeArr = getPriorityDrawingTypeList(attributeArr);

        if (isAttributeListEmpty || attributeList.contains("DistributionList")) {
            drawingUtil = new DrawingUtil();
            HashMap<String, String> distributionList = drawingUtil.fetchDrawingNumber(context, busWithSelect, docType, attributeArr, typesAndRelations);
            singleObjResult.put("DistributionList", distributionList.get("DistributionList"));
        }
        if (isAttributeListEmpty || attributeList.contains("Drawing Number")) {

            if (singleObjResult.containsKey("Drawing Number")) {
                singleObjResult.remove("Drawing Number");
            }
            if (singleObjResult.containsKey("Document Links")) {
                singleObjResult.remove("Document Links");
            }
            drawingUtil = new DrawingUtil();

            HashMap<String, String> drawingNo = drawingUtil.fetchDrawingNumber(context, busWithSelect, docType, attributeArr, typesAndRelations);
            singleObjResult.put("Drawing Number", drawingNo.get("Drawing Number"));
            singleObjResult.put("Document Links", drawingNo.get("Document Links"));

            ArrayList<HashMap<String, String>> drawingInfo = drawingUtil.fetchDrawingInfo(context, busWithSelect, docType, typesAndRelations);
            singleObjResult.put("Drawing Info", drawingInfo);
            if (!isAttributeListEmpty && !attributeList.contains("Drawing Info")) {
                attributeList.add("Drawing Info");
            }
        }
        if (isAttributeListEmpty || attributeList.contains("BOM Marking")) {
            String objId = busWithSelect.getSelectData("id");
            if (bomMarkerMap.containsKey(objId)) {
                singleObjResult.put("BOM Marking", bomMarkerMap.get(objId));
            } else {
                BusinessObject busObj = new BusinessObject(objId);
                ExpansionWithSelect singleLevelExpansionResult = expandObject.expand(context, busObj, "1", this.typesAndRelations);
                int size = singleLevelExpansionResult.getRelationships().size();
                if (size > 0) {
                    singleObjResult.put("BOM Marking", "*");
                    bomMarkerMap.put(objId, "*");
                } else {
                    singleObjResult.put("BOM Marking", "");
                    bomMarkerMap.put(objId, "");
                }
            }
        }
        if (isAttributeListEmpty || attributeList.contains("Unit")) {
            if (NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get("ContinuousUnit"))) {
                singleObjResult.put("Unit", "pcs");
            } else {
                singleObjResult.put("Unit", singleObjResult.get("ContinuousUnit"));
            }
        }

        if (isAttributeListEmpty || attributeList.contains("Type")) {
            singleObjResult.put("T", this.getTypeShortNameMap().get(type));
        }
        if (isAttributeListEmpty || attributeList.contains("Unique Key")) {
            singleObjResult.put("Unique Key", "");
        }

        if (isAttributeListEmpty || attributeList.contains("Standard")) {
            Object standard = singleObjResult.get("Standard");
            if (!NullOrEmptyChecker.isNull(standard)) {
                String standardRealValue = ((String) standard).replace(" ", "_");
                singleObjResult.put("Standard", standardShortNameMap.get(standardRealValue));
            }
        }

        InventoryUnitConverter converter = new InventoryUnitConverter();
        if ((isAttributeListEmpty || attributeList.contains("Qty"))
                && !isRoot) {
            if (NullOrEmptyChecker.isNullOrEmpty(type)) {
                type = busWithSelect.getSelectData("type");
            }

            if ((usageCoeffTypesForQty.contains(type))) {
                String netQuantity = singleObjResult.get("Qty").toString();
                String inventoryUnit = singleObjResult.get("ContinuousUnit").toString();
                try {
                    double convertedNetQuantity = converter.convertFromBaseUnit(netQuantity, inventoryUnit);
                    singleObjResult.put("Qty", trimQtyToPrecisionCeiling(String.valueOf(convertedNetQuantity)));
                } catch (Exception ex) {

                }

            } else {

                singleObjResult.put("Qty", "1.0");
            }

            if (!NullOrEmptyChecker.isNull(singleObjResult.get("By Product"))) {
                String byProduct = (String) singleObjResult.get("By Product");
                if (Boolean.parseBoolean(byProduct)) {
                    singleObjResult.put("Qty", "-" + singleObjResult.get("Qty").toString());
                }
            }
        }
        resolveBomExportAlternatives();
    }

    protected void modifyValuesForReportUI(Context context, BusinessObjectWithSelect busWithSelect, RelationshipWithSelect relationshipWithSelect, List<String> attributeList, boolean isAttributeListEmpty, boolean isRoot, String docType) throws MatrixException, IOException {
        if (isAttributeListEmpty || attributeList.contains("Length")) {
            if (!NullOrEmptyChecker.isNullOrEmpty((String) mergedQtyRow.get("Length"))) {
                Float ln = Float.parseFloat((String) mergedQtyRow.get("Length")) * 1000;
                mergedQtyRow.put("Length", String.format("%.1f", ln));
            } else {
                mergedQtyRow.put("Length", "");
            }

        }
        if (isAttributeListEmpty || attributeList.contains("Width")) {

            if (!NullOrEmptyChecker.isNullOrEmpty((String) mergedQtyRow.get("Width"))) {
                Float ln = Float.parseFloat((String) mergedQtyRow.get("Width")) * 1000;
                mergedQtyRow.put("Width", String.format("%.1f", ln));
            } else {
                mergedQtyRow.put("Width", "");
            }

        }

        // List out the unique Term/Bundle Ids to be sent to GTS to fetch Title later
        if (isAttributeListEmpty || attributeList.contains("Title")) {
            mergedQtyRow.put("Title", "EN: " + ((String) mergedQtyRow.get("Title")));
            String termID = (String) mergedQtyRow.get("Term_ID");
            if (!NullOrEmptyChecker.isNullOrEmpty(termID)) {
                termIdList.add(termID);
            }
        }

        if (isDrawingDataRequired) {
            String drawingNo = fetchConnectedDrawing(context, busWithSelect, relationshipWithSelect, attributeList, isAttributeListEmpty, docType);
            if (isRoot) {
                rootObjDrawing = drawingNo;
            }
            JSON_OUTPUT_LOGGER.info("##############################################################");
            JSON_OUTPUT_LOGGER.info("Document Type: " + docType + ", Drawing Number: " + drawingNo);
            JSON_OUTPUT_LOGGER.info("##############################################################");
            mergedQtyRow.put("Drawing Number", drawingNo);
            if (!isAttributeListEmpty && !attributeList.contains("Drawing Number")) {
                attributeList.add("Drawing Number");
            }
        }
        if (isAttributeListEmpty || attributeList.contains("BOM Marking")) {
//            String objId = busWithSelect.getSelectData("id");
//            if (bomMarkerMap.containsKey(objId)) {
//                mergedQtyRow.put("BOM Marking", bomMarkerMap.get(objId));
//            } else {
//                BusinessObject busObj = new BusinessObject(objId);
//                ExpansionWithSelect singleLevelExpansionResult = expandObject.expand(context, busObj, "1", this.typesAndRelations);
//                int size = singleLevelExpansionResult.getRelationships().size();
//                if (size > 0) {
//                    mergedQtyRow.put("BOM Marking", "*");
//                    bomMarkerMap.put(objId, "*");
//                } else {
            mergedQtyRow.put("BOM Marking", "");
//                    bomMarkerMap.put(objId, "");
//                }
//            }
        }
        if (isAttributeListEmpty || attributeList.contains("Inventory Unit")) {
            if (NullOrEmptyChecker.isNullOrEmpty((String) mergedQtyRow.get("ContinuousUnit"))) {
                mergedQtyRow.put("Inventory Unit", "pcs");
            } else {
                mergedQtyRow.put("Inventory Unit", mergedQtyRow.get("ContinuousUnit"));
            }
        }

        String type = (String) mergedQtyRow.get("ItemType");
        /*  if (isAttributeListEmpty || attributeList.contains("Type")) {
            mergedQtyRow.put("Type", this.getTypeShortNameMap().get(type));
        }*/
        String standard = "";
        if ((stdTypes.contains(type)) && (isAttributeListEmpty || attributeList.contains("Standard"))) {
            standard = (String) mergedQtyRow.get("Standard");
            if (NullOrEmptyChecker.isNullOrEmpty(standard)) {
                standard = (String) mergedQtyRow.get("MaterialStandard");

            }
            mergedQtyRow.put("Standard", standard);
        }
        if ((isAttributeListEmpty || attributeList.contains("PCS"))) {
            if ((usageCoeffTypesForQty.contains(type)) && (unitForLengthCount.contains(mergedQtyRow.get("ContinuousUnit").toString()) || unitForAreaCount.contains(mergedQtyRow.get("ContinuousUnit").toString()))) {
                mergedQtyRow.put("PCS", "1");
            } else {
                mergedQtyRow.put("PCS", "");
            }
        }

        InventoryUnitConverter converter = new InventoryUnitConverter();
        if ((isAttributeListEmpty || attributeList.contains("Qty"))
                && !isRoot) {

            if ((usageCoeffTypesForQty.contains(type))) {

                String netQuantity = mergedQtyRow.get("Qty").toString();
                String inventoryUnit = mergedQtyRow.get("ContinuousUnit").toString();
                try {
                    double convertedNetQuantity = converter.convertFromBaseUnit(netQuantity, inventoryUnit);
                    mergedQtyRow.put("Qty", trimQtyToPrecisionCeiling(String.valueOf(convertedNetQuantity)));
                } catch (Exception ex) {

                }

            } else {

                mergedQtyRow.put("Qty", "1.0");
            }

            String byProduct = (String) mergedQtyRow.get("By Product");
            if (!NullOrEmptyChecker.isNullOrEmpty(byProduct) && Boolean.parseBoolean(byProduct)) {
                mergedQtyRow.put("Qty", "-" + mergedQtyRow.get("Qty").toString());
            }
        }
        resolveAlternativeAttributeValues();
    }

    protected void resolveAlternativeAttributeValues() {
        alternativeAttributeMap.forEach((mainAttribute, alternativeList) -> {
            if (mergedQtyRow.containsKey(mainAttribute)) {
                alternativeList.forEach(alternativeAttribute -> {
                    if (!NullOrEmptyChecker.isNullOrEmpty((String) mergedQtyRow.get(alternativeAttribute))) {
                        mergedQtyRow.put(mainAttribute, mergedQtyRow.get(alternativeAttribute));
                    }
                    mergedQtyRow.remove(alternativeAttribute);
                });
            }
        });
    }

    protected void resolveBomExportAlternatives() {
        alternativeAttributeMap.forEach((mainAttribute, alternativeList) -> {
            if (singleObjResult.containsKey(mainAttribute)) {
                alternativeList.forEach(alternativeAttribute -> {
                    if (!NullOrEmptyChecker.isNullOrEmpty((String) singleObjResult.get(alternativeAttribute))) {
                        singleObjResult.put(mainAttribute, singleObjResult.get(alternativeAttribute));
                    }
                    singleObjResult.remove(alternativeAttribute);
                });
            }
        });
    }

    protected void populateBomExportResultsMap(BusinessObjectWithSelect businessObjectWithSelect, Context context,
                                               RelationshipWithSelect relationshipWithSelect, Map<String, String> typeSelectablesWithOutputName,
                                               Map<String, String> relationSelectablesWithOutputName, boolean isRoot, String docType) throws Exception {
        String objectType = businessObjectWithSelect.getSelectData("type");
        boolean attributeListEmpty = attributeList.isEmpty();
        singleObjResult = new LinkedHashMap<>();
        HashMap<String, Integer> position = new HashMap<>();
        position.put("Position", 0);
        typeSelectablesWithOutputName.forEach((ThrowingConsumer<String, String>) (key, value) -> {
            String str = businessObjectWithSelect.getSelectData(key);
            singleObjResult.put(value, NullOrEmptyChecker.isNullOrEmpty(str) ? "" : str);
        });
        relationSelectablesWithOutputName.forEach((key, value) -> {
            //  String str = "";
            if (isRoot) {
                position.put("Position", 0);
                singleObjResult.put(value, "");
            } else {

                String str = relationshipWithSelect.getSelectData(key);

                if (value.equals("Position") && !NullOrEmptyChecker.isNullOrEmpty(str)) {
                    position.put("Position", Integer.parseInt(str));
                }
                if (NullOrEmptyChecker.isNullOrEmpty(str)) {
                    singleObjResult.put(value, "");
                } else {
                    if (value.equals("Name")) {
                        singleObjResult.put("Rel " + value, str);
                    } else {
                        singleObjResult.put(value, str);
                    }
                }
            }
        });
        modifyValuesForBomExport(context, businessObjectWithSelect, attributeListEmpty, isRoot, docType);

        prepareMultilevelList(isRoot, objectType, businessObjectWithSelect, relationshipWithSelect, position.get("Position"));

    }

    protected void prepareMultilevelList(boolean isRoot, String objectType, BusinessObjectWithSelect businessObjectWithSelect, RelationshipWithSelect relationshipWithSelect, int position) throws NumberFormatException {
        if (isRoot) {
            String bomTrackerKey = objectType + "_" + businessObjectWithSelect.getSelectData("name") + "_" + businessObjectWithSelect.getSelectData("revision");
            singleObjResult.put("bomLines", new TreeMap<>());
            multilevelBOMList.add(singleObjResult);
            bomTracker.put(bomTrackerKey, 0);
            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), bomTrackerKey);
        } else {
            String relId = relationshipWithSelect.getSelectData("id");

            BusinessObject parent = relationshipWithSelect.getFrom();
            String childKey = objectType + "_" + businessObjectWithSelect.getName() + "_" + businessObjectWithSelect.getRevision();
            String parentId = parent.getName();
            String relTrackerKey = parentId + "_" + childKey + "_" + relId;
            if (duplicateRelTrackerMap.containsKey(relId)) {
                return;
            }

            duplicateRelTrackerMap.put(relId, relTrackerKey);
            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), childKey);
            String parentTrackerKey = tnrTrackerMap.get(parentId);
            int indexOfParent = bomTracker.get(parentTrackerKey);
            HashMap<String, Object> parentDataMap = (HashMap) multilevelBOMList.get(indexOfParent);
            TreeMap<String, Object> parentBomLines = (TreeMap) parentDataMap.get("bomLines");

            // Quantity addition based on position begins
            /*
             * Padded position to allow position 2 before 11, position 3 before 20 etc.
             * Normally in alphabetical sorting 11 appears before 2
             * But, 000000002 will appear before 000000011 because of the leading zeros.
             * Variable paddedPosition ensures that.
             */
            String paddedPosition = String.format("%09d", position);
            if (!parentBomLines.containsKey(paddedPosition + "_" + singleObjResult.get("name"))) {
                parentBomLines.put(paddedPosition + "_" + singleObjResult.get("name"), singleObjResult);
            } else {
                if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
                    BigDecimal previousQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(existingMap.get("Qty").toString()) ? "1.0" : existingMap.get("Qty").toString());
                    BigDecimal currentQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(singleObjResult.get("Qty").toString()) ? "1.0" : singleObjResult.get("Qty").toString());
                    BigDecimal updatedQty = currentQty.add(previousQty);
                    existingMap.put("Qty", updatedQty + "");
                }

            }
            if (attributeList.isEmpty() || attributeList.contains("Level")) {
                if (bomDataList.isEmpty()) {
                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
                    existingMap.put("Level", "" + 1);
                    bomDataList.put(singleObjResult.get("name").toString(), "" + 1);
                }
                if (bomDataList.containsKey(parentDataMap.get("name"))) {
                    int lvl = Integer.parseInt(bomDataList.get(parentDataMap.get("name")).toString()) + 1;
                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
                    existingMap.put("Level", "" + lvl);
                    bomDataList.put(singleObjResult.get("name").toString(), "" + lvl);
                }
                if (!bomDataList.containsKey(parentDataMap.get("name"))) {
                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
                    existingMap.put("Level", "" + 1);
                    bomDataList.put(singleObjResult.get("name").toString(), "" + 1);
                }

            }

            if (attributeList.isEmpty() || attributeList.contains("Unique Key")) {
                HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
                if (NullOrEmptyChecker.isNullOrEmpty(existingMap.get("Unique Key").toString())) {
                    existingMap.put("Unique Key", relTrackerKey);

                } else if (!NullOrEmptyChecker.isNullOrEmpty(existingMap.get("Unique Key").toString()) && existingMap.get("Unique Key").toString().equalsIgnoreCase(relTrackerKey)) {
                    existingMap.put("Unique Key", relTrackerKey);

                }

            }
            // Quantity addition based on position ends
            if (!bomTracker.containsKey(childKey)) {
                bomTracker.put(childKey, multilevelBOMList.size());
                HashMap<String, Object> multilevelBomNode = new HashMap<>();
                multilevelBomNode.putAll(singleObjResult);
                multilevelBomNode.put("bomLines", new TreeMap<>());
                multilevelBOMList.add(multilevelBomNode);
            }
        }
    }
//    protected void prepareMultilevelList(boolean isRoot, String objectType, BusinessObjectWithSelect businessObjectWithSelect, RelationshipWithSelect relationshipWithSelect, int position) throws NumberFormatException {
//        if (isRoot) {
//            String bomTrackerKey = objectType + "_" + businessObjectWithSelect.getSelectData("name") + "_" + businessObjectWithSelect.getSelectData("revision");
//            singleObjResult.put("bomLines", new TreeMap<>());
//            multilevelBOMList.add(singleObjResult);
//            bomTracker.put(bomTrackerKey, 0);
//            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), bomTrackerKey);
//        } else {
//            String relId = relationshipWithSelect.getSelectData("id");
//            BusinessObject parent = relationshipWithSelect.getFrom();
//            String childKey = objectType + "_" + businessObjectWithSelect.getName() + "_" + businessObjectWithSelect.getRevision();
//            String parentId = parent.getName();
//            String relTrackerKey = parentId + "_" + childKey + "_" + relId;
//            if (duplicateRelTrackerMap.containsKey(relId)) {
//                return;
//            }
//
//            duplicateRelTrackerMap.put(relId, relTrackerKey);
//            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), childKey);
//            String parentTrackerKey = tnrTrackerMap.get(parentId);
//            int indexOfParent = bomTracker.get(parentTrackerKey);
//            HashMap<String, Object> parentDataMap = (HashMap) multilevelBOMList.get(indexOfParent);
//            TreeMap<String, Object> parentBomLines = (TreeMap) parentDataMap.get("bomLines");
//
//            // Quantity addition based on position begins
//            /* 
//             * Padded position to allow position 2 before 11, position 3 before 20 etc. 
//             * Normally in alphabetical sorting 11 appears before 2
//             * But, 000000002 will appear before 000000011 because of the leading zeros. 
//             * Variable paddedPosition ensures that.
//             */
//            String paddedPosition = String.format("%09d", position);
//            if (!parentBomLines.containsKey(paddedPosition + "_" + singleObjResult.get("name"))) {
//                parentBomLines.put(paddedPosition + "_" + singleObjResult.get("name"), singleObjResult);
//            } else {
//                if (attributeList.isEmpty() || attributeList.contains("Qty")) {
//                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + singleObjResult.get("name"));
//                    BigDecimal previousQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(existingMap.get("Qty").toString()) ? "1.0" : existingMap.get("Qty").toString());
//                    BigDecimal currentQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(singleObjResult.get("Qty").toString()) ? "1.0" : singleObjResult.get("Qty").toString());
//                    BigDecimal updatedQty = currentQty.add(previousQty);
//                    existingMap.put("Qty", updatedQty + "");
//                }
//            }
//            // Quantity addition based on position ends
//
//            if (!bomTracker.containsKey(childKey)) {
//                bomTracker.put(childKey, multilevelBOMList.size());
//                HashMap<String, Object> multilevelBomNode = new HashMap<>();
//                multilevelBomNode.putAll(singleObjResult);
//                multilevelBomNode.put("bomLines", new TreeMap<>());
//                multilevelBOMList.add(multilevelBomNode);
//            }
//        }
//    }

    protected void prepareMultilevelRnPList(boolean isRoot, BusinessObjectWithSelect businessObjectWithSelect, RelationshipWithSelect relationshipWithSelect, int position) throws NumberFormatException {
        if (isRoot) {
            String bomTrackerKey = businessObjectWithSelect.getSelectData("type") + "_" + businessObjectWithSelect.getSelectData("name") + "_" + businessObjectWithSelect.getSelectData("revision");
            mergedQtyRow.put("bomLines", new TreeMap<>());
            multilevelBOMList.add(mergedQtyRow);
            bomTracker.put(bomTrackerKey, 0);
            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), bomTrackerKey);
        } else {
            String relId = relationshipWithSelect.getSelectData("id");
            BusinessObject parent = relationshipWithSelect.getFrom();
            String childKey = businessObjectWithSelect.getSelectData("type") + "_" + businessObjectWithSelect.getName() + "_" + businessObjectWithSelect.getRevision();
            String parentId = parent.getName();

            String relTrackerKey = parentId + "_" + childKey + "_" + relId;
            if (duplicateRelTrackerMap.containsKey(relId)) {
                return;
            }
            duplicateRelTrackerMap.put(relId, relTrackerKey);
            tnrTrackerMap.put(businessObjectWithSelect.getSelectData("id"), childKey);

            String parentTrackerKey = tnrTrackerMap.get(parentId);
            int indexOfParent = bomTracker.get(parentTrackerKey);
            HashMap<String, Object> parentDataMap = (HashMap) multilevelBOMList.get(indexOfParent);
            TreeMap<String, Object> parentBomLines = (TreeMap) parentDataMap.get("bomLines");

            // Quantity addition based on position begins
            /*
             * Padded position to allow position 2 before 11, position 3 before 20 etc.
             * Normally in alphabetical sorting 11 appears before 2
             * But, 000000002 will appear before 000000011 because of the leading zeros.
             * Variable paddedPosition ensures that.
             */
            String paddedPosition = String.format("%09d", position);
            if (!parentBomLines.containsKey(paddedPosition + "_" + mergedQtyRow.get("name"))) {
                parentBomLines.put(paddedPosition + "_" + mergedQtyRow.get("name"), mergedQtyRow);
            } else {
                if (attributeList.isEmpty() || attributeList.contains("Qty")) {
                    HashMap<String, Object> existingMap = (HashMap) parentBomLines.get(paddedPosition + "_" + mergedQtyRow.get("name"));
                    BigDecimal previousQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(existingMap.get("Qty").toString()) ? "1.0" : existingMap.get("Qty").toString());
                    BigDecimal currentQty = new BigDecimal(NullOrEmptyChecker.isNullOrEmpty(mergedQtyRow.get("Qty").toString()) ? "1.0" : mergedQtyRow.get("Qty").toString());

                    // Pcs addition based on position and Qty begins
                    if (!NullOrEmptyChecker.isNullOrEmpty((existingMap.get("PCS").toString()))) {
                        int curPCS = Integer.valueOf(existingMap.get("PCS").toString());
                        int upPCS = curPCS + 1;
                        existingMap.put("PCS", String.valueOf(upPCS));
                    }
                    BigDecimal updatedQty = currentQty.add(previousQty);
                    existingMap.put("Qty", updatedQty);
                }
            }
            // Quantity addition based on position ends

            if (!bomTracker.containsKey(childKey)) {
                bomTracker.put(childKey, multilevelBOMList.size());
                HashMap<String, Object> multilevelBomNode = new HashMap<>();
                multilevelBomNode.putAll(mergedQtyRow);
                multilevelBomNode.put("bomLines", new TreeMap<>());
                multilevelBOMList.add(multilevelBomNode);
            }
        }
    }

    protected void populateResultsMap(BusinessObjectWithSelect businessObjectWithSelect, Context context,
                                      RelationshipWithSelect relationshipWithSelect, Map<String, String> typeSelectablesWithOutputName,
                                      Map<String, String> relationSelectablesWithOutputName, boolean isRoot, String docType) throws Exception {
        boolean attributeListEmpty = attributeList.isEmpty();
        mergedQtyRow = new HashMap<>();

        HashMap<String, Integer> position = new HashMap<>();
        position.put("Position", 0);

        typeSelectablesWithOutputName.forEach((ThrowingConsumer<String, String>) (key, value) -> {
            String str = businessObjectWithSelect.getSelectData(key);
            mergedQtyRow.put(value, NullOrEmptyChecker.isNullOrEmpty(str) ? "" : str);
        });
        relationSelectablesWithOutputName.forEach((key, value) -> {
            if (isRoot) {
                position.put("Position", 0);
                mergedQtyRow.put(value, "");
            } else {
                String str = relationshipWithSelect.getSelectData(key);
                if (value.equals("Position") && !NullOrEmptyChecker.isNullOrEmpty(str)) {
                    position.put("Position", Integer.parseInt(str));
                }
                if (NullOrEmptyChecker.isNullOrEmpty(str)) {
                    mergedQtyRow.put(value, "");
                } else {
                    if (value.equals("Name")) {
                        mergedQtyRow.put("Rel " + value, str);
                    } else {
                        mergedQtyRow.put(value, str);
                    }
                }
            }
        });
        modifyValuesForReportUI(context, businessObjectWithSelect, relationshipWithSelect, attributeList, attributeListEmpty, isRoot, docType);
        addRootItemParams(isRoot, context, businessObjectWithSelect);
        prepareMultilevelRnPList(isRoot, businessObjectWithSelect, relationshipWithSelect, position.get("Position"));
    }

    /**
     * Update summary report,
     *
     * @linkplain VSIX-5137 : For multiple drawing number no need to show
     * drawing name in name column
     *
     * @param level
     * @param parentData
     * @param childData
     */
    protected void updateSummaryReport(int level, Map<String, Object> parentData, Map<String, Object> childData, String type) {
        if (isSummaryRequired) {
            StringBuilder summaryMapKeyBuilder = new StringBuilder();
            StringBuilder childMapKeyBuilder = new StringBuilder();
            String parentLevel = "0";
            if (!NullOrEmptyChecker.isNullOrEmpty(childData.get("Level").toString())) {
                parentLevel = String.valueOf(Integer.parseInt(childData.get("Level").toString()) - 1);
            }
            if (!isMBOMReport) {
                if (parentData != null) {
                    if (!NullOrEmptyChecker.isNullOrEmpty(parentData.get("ItemType").toString())) {
                        String itemType = parentData.get("ItemType").toString();
                        if (itemType.equals("CreateAssembly") || itemType.equals("ProcessContinuousCreateMaterial")) {
                            isMBOMReport = true;
                        }
                    }
                } else {
                    if (!NullOrEmptyChecker.isNullOrEmpty(childData.get("ItemType").toString())) {
                        String itemType = childData.get("ItemType").toString();
                        if (itemType.equals("CreateAssembly") || itemType.equals("ProcessContinuousCreateMaterial")) {
                            isMBOMReport = true;
                        }
                    }
                }
            }

            summaryMapKeyBuilder.append(childData.get("type"))
                    .append("|")
                    .append(childData.get("name"))
                    .append("|")
                    .append(childData.get("revision"))
                    .append("|")
                    .append(childData.get("Width"))
                    .append("|")
                    .append(childData.get("Length"));

            childMapKeyBuilder.append(childData.get("type"))
                    .append("|")
                    .append(childData.get("name"))
                    .append("|")
                    .append(childData.get("revision"))
                    .append("|")
                    .append(childData.get("Width"))
                    .append("|")
                    .append(childData.get("Length"))
                    .append("|")
                    .append(parentLevel);

            String summaryMapKey = summaryMapKeyBuilder.toString();
            String childMapKey = childMapKeyBuilder.toString();
            String childlevel = childData.get("Level").toString();
            //      JSON_OUTPUT_LOGGER.info("Type: " + type);

            if (summaryMap.containsKey(summaryMapKey)) {
                Map<String, String> summaryData = summaryMap.get(summaryMapKey);
                if (attributeList.contains("Qty")) {
                    String trimQtyToPrecisionCeiling = trimQtyToPrecisionCeiling(quantityCalculation.getCummulativeNetQty(level, parentData, childData, summaryMapKey, childMapKey));
                    summaryData.put("SummaryQty", trimQtyToPrecisionCeiling);
                    if (isMBOMReport) {
                        if (!NullOrEmptyChecker.isNullOrEmpty(trimQtyToPrecisionCeiling)) {
                            if (!NullOrEmptyChecker.isNullOrEmpty(childData.get("Width").toString()) && !NullOrEmptyChecker.isNullOrEmpty(childData.get("Length").toString())) {
                                String unit = childData.get("Inventory Unit").toString();
                                String pcsFromQtyCalculation = trimQtyToPrecisionCeiling(pcsFromQtyCalculation(trimQtyToPrecisionCeiling, childData.get("Width").toString(), childData.get("Length").toString(), unit));
                                summaryData.put("TotalPCS", pcsFromQtyCalculation);
                            } else {
                                summaryData.put("TotalPCS", "");
                            }
                        } else {
                            summaryData.put("TotalPCS", "");
                        }
                    }
                }
                /*
                    VSIX-5137 For Multiple Drawing, in first row, only drawing Number will show,
                    and 2nd row and onwards – Name column will be empty,
                    drawing Number should be in drawing number column.
                 */
                if (NullOrEmptyChecker.isNullOrEmpty(childlevel) && type.equalsIgnoreCase("Drawing")) {
                    JSON_OUTPUT_LOGGER.info("Going to set name empty for : " + summaryData.get("name"));
                    summaryData.put("name", "");
                }
                String summarySingleMap = summaryMapKey + "_" + childData.get("Drawing Number");
                singleLevelSummaryMap.put(summarySingleMap, (HashMap) childData);
                summaryMap.put(summaryMapKey, (HashMap) summaryData);
            } else {
                Map<String, String> summaryData = new HashMap<>();
                childData.forEach((k, v) -> {
                    if (attributeList.contains(k)) {
                        summaryData.put(k, (String) v);
                    }
                });
                if (attributeList.contains("Qty")) {
                    String trimQtyToPrecisionCeiling = trimQtyToPrecisionCeiling(quantityCalculation.getCummulativeNetQty(level, parentData, childData, summaryMapKey, childMapKey));
                    summaryData.put("SummaryQty", trimQtyToPrecisionCeiling);
                    if (isMBOMReport) {
                        if (!NullOrEmptyChecker.isNullOrEmpty(trimQtyToPrecisionCeiling)) {
                            if (!NullOrEmptyChecker.isNullOrEmpty(childData.get("Width").toString()) && !NullOrEmptyChecker.isNullOrEmpty(childData.get("Length").toString())) {
                                String unit = childData.get("Inventory Unit").toString();
                                String pcsFromQtyCalculation = trimQtyToPrecisionCeiling(pcsFromQtyCalculation(trimQtyToPrecisionCeiling, childData.get("Width").toString(), childData.get("Length").toString(), unit));
                                summaryData.put("TotalPCS", pcsFromQtyCalculation);
                            } else {
                                summaryData.put("TotalPCS", "");
                            }
                        } else {
                            summaryData.put("TotalPCS", "");
                        }
                    }
                }
                /*
                    VSIX-5137 For Multiple Drawing, in first row, only drawing Number will show,
                    and 2nd row and onwards – Name column will be empty,
                    drawing Number should be in drawing number column.
                 */
                if (NullOrEmptyChecker.isNullOrEmpty(childlevel) && type.equalsIgnoreCase("Drawing")) {
                    JSON_OUTPUT_LOGGER.info("Going to set name empty for : " + summaryData.get("name"));
                    summaryData.put("name", "");
                }
                String summarySingleMap = summaryMapKey + "_" + childData.get("Drawing Number");
                singleLevelSummaryMap.put(summarySingleMap, (HashMap) childData);
                summaryMap.put(summaryMapKey, (HashMap) summaryData);

            }
        }
    }

    protected String trimQtyToPrecisionCeiling(String qty) {
        if (!NullOrEmptyChecker.isNullOrEmpty(qty)) {
            String numberD = String.valueOf(qty);
            numberD = numberD.substring(numberD.indexOf("."));

            double parse = Double.parseDouble(numberD);
            double ngtValue = 1.0 - parse;
            if (ngtValue <= 0.1) {
                qtyPrecisionFormat.setRoundingMode(RoundingMode.CEILING);
                return !NullOrEmptyChecker.isNullOrEmpty(qty) ? qtyPrecisionFormat.format(Double.parseDouble(qty)) : qty;
            } else {
                qtyPrecisionFormat.setRoundingMode(RoundingMode.HALF_UP);
                return !NullOrEmptyChecker.isNullOrEmpty(qty) ? qtyPrecisionFormat.format(Double.parseDouble(qty)) : qty;
            }
        } else {
            qtyPrecisionFormat.setRoundingMode(RoundingMode.HALF_UP);
            return !NullOrEmptyChecker.isNullOrEmpty(qty) ? qtyPrecisionFormat.format(Double.parseDouble(qty)) : qty;

        }
    }

    protected void removeSingleExtraAttributes(HashMap<String, Object> dataMap) {
        if (!attributeList.isEmpty()) {
            dataMap.forEach((k, v) -> {
                if (!attributeList.contains(k)
                        && !k.equalsIgnoreCase("physicalId")) {
                    dataMap.put(k, "");
                }
            });
        }
    }

    protected String pcsFromQtyCalculation(String trimQtyToPrecisionCeiling, String width, String length, String unit) {
        String totalPc = "";
        String unitReConvertor = "";

        if (unitForConversion.contains(unit)) {
            unitReConvertor = unitReConvertor(trimQtyToPrecisionCeiling, unit);
        } else {
            unitReConvertor = trimQtyToPrecisionCeiling;
        }
        float widthLength = ((Float.parseFloat(width) * Float.parseFloat(length)) / 1000000);
        if (widthLength > 0) {

            float totalPcs = Float.parseFloat(unitReConvertor) / widthLength;

            String numberD = String.valueOf(totalPcs);
            numberD = numberD.substring(numberD.indexOf("."));
            double parse = Double.parseDouble(numberD);
            double ngtValue = 1.0 - parse;

            if (ngtValue <= 0.1) {
                BigDecimal totalPcss = new BigDecimal(unitReConvertor).divide(new BigDecimal(widthLength), RoundingMode.HALF_UP);
                totalPc = String.valueOf(totalPcss);
            } else {
                totalPc = String.valueOf(totalPcs);

            }

        }
        return totalPc;
    }

    protected String unitReConvertor(String value, String unit) {
        String widthLength = "";
        if (unit.equals("in")) {
            widthLength = String.valueOf(Double.parseDouble(value) / 39.37);
        }
        if (unit.equals("in2")) {
            widthLength = String.valueOf(Double.parseDouble(value) / 1550.0031);
        }
        if (unit.equals("ft")) {
            widthLength = String.valueOf(Double.parseDouble(value) / 3.281);
        }
        if (unit.equals("ft2")) {
            widthLength = String.valueOf(Double.parseDouble(value) / 10.76391);
        }
        return widthLength;
    }

    protected void removeExtraBOMExportAttributes(HashMap<String, Object> dataMap) {
        if (!attributeList.isEmpty()) {
            dataMap.entrySet().removeIf(entry -> !attributeList.contains(entry.getKey()));
        }
    }

    protected void addRootItemParams(boolean isRoot, Context context, BusinessObjectWithSelect busWithSel) {
        if (isRoot) {
            rootItemParams.clear();
            rootItemParams.put("rootObjectType", busWithSel.getSelectData("type"));
            rootItemParams.put("rootObjectName", busWithSel.getSelectData("name"));
            rootItemParams.put("rootObjectRev", busWithSel.getSelectData("revision"));
            rootItemParams.put("rootObjectDes", busWithSel.getSelectData("attribute[PLMEntity.V_description]"));
            rootItemParams.put("rootObjectState", busWithSel.getSelectData("current"));
            addRootItemReleaseInfo(busWithSel, context, "rootObjectReleasedBy", "rootObjectReleasedDate");
            addRootItemDrawingNumber(isRoot);
        }
    }

    protected void addRootItemDrawingNumber(boolean isRoot) {
        if (isRoot) {
            if (isMBOMReport) {
                rootItemParams.put("rootObjectDrNum", NullOrEmptyChecker.isNullOrEmpty(rootObjDrawing) ? "" : rootObjDrawing);
            } else {

                List<Map<String, String>> drawingDataList = new ArrayList<>();
                drawingDataHolderMap.forEach((key, drawingData) -> {

                    if (drawingData.size() > 1) {
                        if (!NullOrEmptyChecker.isNullOrEmpty(drawingData.get(1))) {
                            drawingDataList.add(drawingData.get(1));
                        }
                    }

                });
                if (drawingDataList.size() > 0) {

                    rootObjDrawing = drawingDataList.get(0).get("Drawing Number");
                    rootItemParams.put("rootObjectDrNum", NullOrEmptyChecker.isNullOrEmpty(rootObjDrawing) ? "" : rootObjDrawing);
                }

            }

        }
    }

    protected void addRootItemReleaseInfo(BusinessObjectWithSelect busWithSelect, Context context, String rootReleasedBy, String rootReleasedDate) {
        ArrayList<BusinessObject> changeActionReviewersFromItem = new ArrayList<>();

        rootItemParams.put(rootReleasedBy, "");
        rootItemParams.put(rootReleasedDate, "");

        if (busWithSelect.getSelectData("current").equals("RELEASED")) {

            BusinessObjectUtils bou = new BusinessObjectUtils();
            try {
                changeActionReviewersFromItem = bou.getChangeActionReviewersFromItem(context, busWithSelect);
            } catch (Exception ex) {
                JSON_OUTPUT_LOGGER.error("Could not get Reviewer item :" + ex.getMessage());
            }
            try {
                if (changeActionReviewersFromItem.size() > 0) {
                    changeActionReviewersFromItem.forEach(state -> {

                        SimpleDateFormat matrixDateFormat = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");
                        SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            rootItemParams.put(rootReleasedBy, state.getName());
                            rootItemParams.put(rootReleasedDate, reportDateFormat.format(matrixDateFormat.parse(getReleasedDate(busWithSelect, context))));

                        } catch (Exception pe) {
                            JSON_OUTPUT_LOGGER.error("Could not parse released date of root item :" + busWithSelect.getSelectData("name") + ". Reason: " + pe.getMessage());
                        }

                    });
                }
            } catch (Exception me) {
                JSON_OUTPUT_LOGGER.error("Matrix exception occurred. Reason: " + me.getMessage());
            }
        }

    }

    protected String getReleasedDate(BusinessObjectWithSelect busWithSelect, Context context) throws MatrixException {
        String releasedDate = "";

        BusinessObject bo = new BusinessObject(busWithSelect.getSelectData("type"),
                busWithSelect.getSelectData("name"),
                busWithSelect.getSelectData("revision"),
                null);
        List<String> finalDate = new ArrayList<>();
        bo.getStates(context).forEach(state -> {
            state.getBranches().forEach(branch -> {
                try {
                    String rootReleasedDate = state.getActualDate();
                    finalDate.add(rootReleasedDate);

                } catch (Exception pe) {
                    JSON_OUTPUT_LOGGER.error("Could not parse released date of root item :" + busWithSelect.getSelectData("name") + ". Reason: " + pe.getMessage());
                }

            });
        });
        if (finalDate.size() > 0) {
            releasedDate = finalDate.get(0);
        }
        return releasedDate;
    }

    //    protected String getSignerName(BusinessObject bo, Context context, String fromState, String toState) {
//        SignatureList signatures;
//        String statusUpdater = "";
//        try {
//            signatures = bo.getSignatures(context, fromState, toState);
//            Signature statusUpdateSignature = null;
//            if (signatures != null && signatures.size() > 0) {
//                statusUpdateSignature = signatures.get(signatures.size() - 1);
//            }
//            statusUpdater = statusUpdateSignature.getSigner();
//        } catch (MatrixException ex) {
//            JSON_OUTPUT_LOGGER.debug("Exception while fetching signer name. Reason: " + ex.getMessage());
//        }
//        return statusUpdater;
//    }
    public HashMap<String, Object> getRootItemParams() {
        return rootItemParams;
    }

    public String fetchConnectedDrawing(Context context, BusinessObjectWithSelect busWithSel, RelationshipWithSelect relWithSel, List<String> attributeList, boolean isAttributeListEmpty, String docType) throws MatrixException {
        JSON_OUTPUT_LOGGER.info("+++ fetchConnectedDrawing +++");
        if (!isDrawingDataRequired) {
            return "";
        }
        List<Map<String, String>> drawingDataList = new ArrayList<>();
        String objectType = busWithSel.getSelectData("type");
        String drawingNumber = "";
        drawingUtil = new DrawingUtil();

        String drawingNumberForPDM = busWithSel.getSelectData(PropertyReader.getProperty("PDM.drawing.attribute"));
        if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumberForPDM)) {
            drawingNumber = drawingNumberForPDM;
        }
        if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
            String physicalId = busWithSel.getSelectData("physicalid");
            String drawingId = "";
            String drawingRev = "";
            Map<String, String> drawingData = new LinkedHashMap<String, String>();
            if (isDrawingDataRequired && drawingNumberMap.containsKey(physicalId)) {
                drawingDataList = drawingDataHolderMap.get(physicalId);
                if (isMBOMReport) {
                    return drawingNumberMap.get(physicalId);
                }
                return "";
            }
            String physicalID_VPMReference = "";
            if (objectType.equals("VPMReference")) {
                physicalID_VPMReference = physicalId;
            } else {
                BusinessObjectWithSelectList processImplementCnx = expandObject.getChildObjectFromExpansion(context, new BusinessObject(busWithSel.getObjectId()), processCnxTypeListForDrawing, PropertyReader.getProperty("rel.item.to.process.cnx"), typesAndRelations);
                if (NullOrEmptyChecker.isNull(processImplementCnx)) {
                    drawingNumber = drawingUtil.fetchLegacyDrawingNumber(busWithSel);
                    return drawingNumber;
                }

                for (BusinessObjectWithSelect drawingBusWithSelect : processImplementCnx) {
                    if (drawingBusWithSelect.getSelectData("type").equals("DELFmiProcessImplementCnx")) {
                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
                        MqlQueries mqlQuery = new MqlQueries();
                        physicalID_VPMReference = mqlQuery.getPhysicalIdFromProcessImpl(context, processImplementCnxID);
                    }
                }
            }
            if (NullOrEmptyChecker.isNullOrEmpty(physicalID_VPMReference)) {
                drawingNumber = drawingUtil.fetchLegacyDrawingNumber(busWithSel);
                return drawingNumber;
            }
            BusinessObject VPMReferenceBO = new BusinessObject(physicalID_VPMReference);
            if (NullOrEmptyChecker.isNull(VPMReferenceBO)) {
                drawingNumber = drawingUtil.fetchLegacyDrawingNumber(busWithSel);
                return drawingNumber;
            }
            BusinessObjectWithSelectList drawingList = expandObject.getDrawingObjectsFromExpansion(context, VPMReferenceBO, PropertyReader.getProperty("item.type.drawing"), PropertyReader.getProperty("rel.vpmreference.to.drawing"), typesAndRelations);
            if (drawingList.isEmpty() || NullOrEmptyChecker.isNullOrEmpty(docType)) {

                drawingNumber = drawingUtil.fetchLegacyDrawingNumber(busWithSel);
                return drawingNumber;
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(docType) && docType.split(",").length >= 1) {
                /* VSIX-5160 : docType is multiselect dropdown from UI , 
                sample docType: 'Production', 'Production,ProductionAndCustomer'
                 */

                String[] docTypes = docType.split(",");
                docTypes = getPriorityDrawingTypeList(docTypes);

                boolean drawingNumberFound = false;
                for (int i = 0; i < drawingList.size(); i++) {
                    BusinessObjectWithSelect busWithSelect = drawingList.get(i);
                    for (String docTypeStr : docTypes) {
                        if (busWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals(docTypeStr)) {
                            Drawing drawing = new Drawing(busWithSelect, relWithSel.getLevel(), isMBOMReport, isAttributeListEmpty, attributeList, typeShortNameMap, isDrawingDataRequired, getPrimaryLang(), getSecondaryLang(), languageMap, typeSelectablesWithOutputName, gtsTitleList);
                            gtsTitleList = drawing.getGtsTitleMap();
                            if (NullOrEmptyChecker.isNullOrEmpty(drawingId) && NullOrEmptyChecker.isNullOrEmpty(drawingRev) && !drawingNumberFound) {
                                drawingId = drawing.getDrawingId();
                                //  drawingRev = drawing.getDrawingRev();
                                JSON_OUTPUT_LOGGER.info("DrawingNumber: " + drawingId + "_" + drawingRev + ", DocType:" + docTypeStr);
                                drawingNumberFound = true;
                            }
                            drawingData = drawing.getDrawingData();
                            if (!isAttributeListEmpty && !attributeList.contains("revision")) {
                                drawingData.put("revision", "");
                            }
                            if (i > 0 || !isMBOMReport) {
                                JSON_OUTPUT_LOGGER.info("Added to drawing data list...");
                                JSON_OUTPUT_LOGGER.info("drawingData: " + drawingData);
                                drawingDataList.add(drawingData);
                            }
                        }
                    }

                }

            }

            List<Map<String, String>> drawingDataListInit = new ArrayList<>();
            drawingDataListInit.addAll(drawingDataList);
            drawingDataHolderMap.put(physicalId, drawingDataListInit);
            if (!NullOrEmptyChecker.isNullOrEmpty(drawingId)) {
                drawingNumber = isMBOMReport ? drawingId : "";
            }

            if (NullOrEmptyChecker.isNull(drawingNumber)) {
                drawingNumber = drawingUtil.fetchLegacyDrawingNumber(busWithSel);
            }

            drawingNumberMap.put(physicalId, drawingNumber);
        }
        JSON_OUTPUT_LOGGER.info("--- fetchConnectedDrawing ---");
        return drawingNumber;

    }

    /**
     * Get Predefined Priority Drawing Type List
     *
     * @param drawingTypes Drawing types list is generated according to drawing
     * types
     * @return
     */
    public String[] getPriorityDrawingTypeList(String[] drawingTypes) {
        Set<String> drawingTypeSet = CommonUtilities.convertArrayToSet(drawingTypes);
        String priorityOrderStr = isMBOMReport ? PropertyReader.getProperty("bom.reporting.mbom.type.drawinginfo.priority.order")
                : PropertyReader.getProperty("bom.reporting.ebom.type.drawinginfo.priority.order");
        String[] priorityOrders = priorityOrderStr.split(",");

        List<String> priorityList = new ArrayList<>();
        for (String priorityOrder : priorityOrders) {
            priorityList.add(priorityOrder);
        }
        Iterator itr = priorityList.iterator();

        while (itr.hasNext()) {
            String drawingType = (String) itr.next();
            if (!drawingTypeSet.contains(drawingType)) {
                JSON_OUTPUT_LOGGER.info("Removing from priority order: " + drawingType);
                itr.remove();
            }
        }
        return priorityList.toArray(new String[priorityList.size()]);
    }

    public String title(String technicalDesignation, String enTitle, String preTitle, String revisionComment, String itemCommonText, String bomCommonText) {

        StringBuilder titleDescription = new StringBuilder();

        if (NullOrEmptyChecker.isNullOrEmpty(enTitle)) {
            enTitle = preTitle;
        }
        titleDescription = titleDescription.append(enTitle);
        Set<String> descriptionValue = new LinkedHashSet<>();

        descriptionValue.add(technicalDesignation);
        descriptionValue.add(revisionComment);
        descriptionValue.add(itemCommonText);
        descriptionValue.add(bomCommonText);

        for (String value : descriptionValue) {
            if (!NullOrEmptyChecker.isNullOrEmpty(value)) {
                titleDescription.append("\n" + value);
            }
        }
        return titleDescription.toString();
    }

    public String getRequestLang() {
        return requestLang;
    }

    public void setRequestLang(String requestLang) {
        this.requestLang = requestLang;
    }

    public String getPrimaryLang() {
        return primaryLang;
    }

    public void setPrimaryLang(String primaryLang) {
        if (!NullOrEmptyChecker.isNullOrEmpty(primaryLang)) {
            this.primaryLang = primaryLang.toLowerCase();
        } else {
            this.primaryLang = "en";
        }
    }

    public void setSecondaryLang(String secondaryLang) {
        if (!NullOrEmptyChecker.isNullOrEmpty(secondaryLang)) {
            this.secondaryLang = secondaryLang.toLowerCase();
        } else {
            this.secondaryLang = null;
        }
    }

    public String getSecondaryLang() {
        return secondaryLang;
    }

    public boolean getDrawingRevRequired() {
        return isDrawingRevRequired;
    }

    public void setDrawingRevRequired(boolean isDrawingRevRequired) {
        this.isDrawingRevRequired = isDrawingRevRequired;
    }

    public void setDrawingDataRequired(boolean isDrawingDataRequired) {
        this.isDrawingDataRequired = isDrawingDataRequired;
    }

    public void setPositionAttributeNameMap(HashMap<String, String> positionAttrNameMap) {
        positionAttributeNameMap = positionAttrNameMap;
    }

    public HashMap<String, ArrayList<String>> getAlternativeAttributeMap() {
        return alternativeAttributeMap;
    }

    public void setAlternativeAttributeMap(HashMap<String, ArrayList<String>> updatedAlternativeAttributeMap) {
        alternativeAttributeMap = updatedAlternativeAttributeMap;
    }

    public void setAttributNames(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public List<String> getAttributeNames() {
        return attributeList;
    }

    public HashMap<String, String> getTypeShortNameMap() {
        return typeShortNameMap;
    }

    public void setTypeShortNameMap(HashMap<String, String> typeShortNameMap) {
        this.typeShortNameMap = typeShortNameMap;
    }

    public List<HashMap<String, String>> getSingleSummaryResultList() {
        singleLevelSummaryMap.forEach((summaryMapkey, summaryData) -> {
            HashMap<String, String> tempDraw = singleLevelSummaryMap.get(summaryMapkey);
            String drawNumber = tempDraw.get("Drawing Number");
            if (!summaryTemp.contains(drawNumber)) {

                summaryTemp.add(drawNumber);
                summarySingleResultList.add(summaryData);
            }

        });
        return summarySingleResultList;
    }

    public List<HashMap<String, String>> getSummaryResultList() {
        summaryMap.forEach((summaryMapkey, summaryData) -> {
            summaryResultList.add(summaryData);

        });
        return summaryResultList;
    }

    public void setIsSummaryRequired(boolean isSummaryRequired) {
        this.isSummaryRequired = isSummaryRequired;
    }

    public boolean getIsSummaryRequired() {
        return isSummaryRequired;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean getMailReportLinkToUser() {
        return mailReportLinkToUser;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequester() {
        return requester;
    }

    public void clear() {
        NullOrEmptyChecker.clear(expandObject);
        NullOrEmptyChecker.clear(typesAndRelations);
        NullOrEmptyChecker.clear(jsonObjectMap);
        NullOrEmptyChecker.clear(typeSelectablesWithOutputName);
        NullOrEmptyChecker.clear(relationSelectablesWithOutputName);
        NullOrEmptyChecker.clear(typeSelectables);
        NullOrEmptyChecker.clear(relationSelectables);
        NullOrEmptyChecker.clear(singleResultCpx);
        NullOrEmptyChecker.clear(resultsMapCpx);
        NullOrEmptyChecker.clear(singleResultBrochureItem);
        NullOrEmptyChecker.clear(resultsMapBrochureItem);
        NullOrEmptyChecker.clear(singleResultBrochure);
        NullOrEmptyChecker.clear(resultsMapBrochure);
        NullOrEmptyChecker.clear(rootItemParams);
        NullOrEmptyChecker.clear(positionAttributeNameMap);
        NullOrEmptyChecker.clear(typeShortNameMap);
        NullOrEmptyChecker.clear(requester);
        NullOrEmptyChecker.clear(mailReportLinkToUser);
        NullOrEmptyChecker.clear(bomTracker);
        NullOrEmptyChecker.clear(tnrTrackerMap);
        NullOrEmptyChecker.clear(multilevelBOMList);
        NullOrEmptyChecker.clear(responseBOMList);
    }
}
