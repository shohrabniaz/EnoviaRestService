/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.mapper.mapproject.util.CommonUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import matrix.db.BusinessObjectWithSelect;
import matrix.util.MatrixException;

/**
 *
 * @author Tahmid
 */
public class Drawing {

    private boolean isMbomReport;
    private boolean isAllDrawingDataRequired;
    private BusinessObjectWithSelect drawingObjWithSelect;
    private int level;
    private String drawingId;
    private String docId;
    private String drawingRev;
    private String drawingStatus;
    private String drawingTitle;
    private String termId;
    private String primaryLang;
    private String secondaryLang;
    private List<String> attributeList;
    private boolean isAttributeListEmpty;
    private Map<String, String> drawingData;
    private HashMap<String, String> typeShortNameMap;
    Map<String, String> typeSelectablesWithOutputName;
    private HashMap<String, String> languageMap;
    private Map<String, String> gtsTitleMap;

    private void setShortTypeName() {
        if (addData("Type")) {
            drawingData.put("Type", typeShortNameMap.get(drawingData.get("Type").toLowerCase()));
        }
    }

    public Drawing(BusinessObjectWithSelect drawingObjWithSelect, int level, boolean isMbomReport, boolean isAttributeListEmpty, List<String> attributeList, HashMap<String, String> typeShortNameMap, boolean isAllDrawingDataRequired, String primaryLang, String secondaryLang, HashMap<String, String> languageMap, Map<String, String> typeSelectablesWithOutputName, Map<String, String> gtsTitleMap) throws MatrixException {
        this.isMbomReport = isMbomReport;
        this.drawingObjWithSelect = drawingObjWithSelect;
        this.level = level;
        this.drawingData = new LinkedHashMap<>();
        this.isAttributeListEmpty = isAttributeListEmpty;
        this.attributeList = attributeList;
        this.typeShortNameMap = typeShortNameMap;
        this.isAllDrawingDataRequired = isAllDrawingDataRequired;
        this.primaryLang = primaryLang;
        this.secondaryLang = secondaryLang;
        this.languageMap = languageMap;
        this.typeSelectablesWithOutputName = typeSelectablesWithOutputName;
        this.gtsTitleMap = gtsTitleMap;
        initializeDrawingData();
    }

    private void initializeDrawingData() throws MatrixException {
        typeSelectablesWithOutputName.forEach((CommonUtil.ThrowingConsumer<String, String>) (key, value) -> {
            String str = drawingObjWithSelect.getSelectData(key);
         //   if (addData(value)) {
                drawingData.put(value, NullOrEmptyChecker.isNullOrEmpty(str) ? "" : str);
          //  }
        });
        setDrawingId();
        setDrawingRev();
        if (isAllDrawingDataRequired) {
            setShortTypeName();
            setLevel();
            setDrawingTitle();
        }
    }

    public int getLevel() {
        return level;
    }

    private void setLevel() {
        if (addData("Level") && !isMbomReport) {
            level++;
            level = level == 0 ? ++level : level;
            drawingData.put("Level", level + "");
        }
    }

    public String getDrawingId() {
        return drawingId;
    }
//

    private void setDrawingId() throws MatrixException {

        if (!NullOrEmptyChecker.isNullOrEmpty(drawingData.get("DocId"))) {
            drawingId = drawingData.get("DocId") + "_" + drawingData.get("revision");
        } else {

            drawingId = drawingData.get("name") + "_" + drawingData.get("revision");
        }
        drawingData.put("Drawing Number", drawingId);
    }

//    public String getDocId() {
//        return docId;
//    }
//
//    public String setDocId() {
//
//        docId = (!NullOrEmptyChecker.isNullOrEmpty(drawingData.get("DocId"))) ? drawingData.get("DocId") : drawingId;
//
//        return drawingId;
//    }
    public String getDrawingRev() {
        return drawingRev;
    }

    public void setDrawingRev() {
        drawingRev = drawingData.get("revision");
    }

    public String getDrawingStatus() {
        return drawingStatus;
    }

    public String getDrawingTitle() {
        return drawingTitle;
    }

    private void setDrawingTitle() throws MatrixException {
        if (addData("Title")) {
            setTermId();
            drawingTitle = "EN: " + drawingData.get("Title");
            drawingData.put("Title", drawingTitle);
        }
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId() throws MatrixException {
        termId = drawingData.get("Term_ID");
    }

    public BusinessObjectWithSelect getDrawingObj() {
        return drawingObjWithSelect;
    }

    public void setDrawingObj(BusinessObjectWithSelect drawingObjWithSelect) {
        this.drawingObjWithSelect = drawingObjWithSelect;
    }

    public Map<String, String> getDrawingData() {
        return drawingData;
    }

    public Map<String, String> getGtsTitleMap() {
        return gtsTitleMap;
    }

    private boolean addData(String attrName) {
        if (isAttributeListEmpty || attributeList.contains(attrName)) {
            return true;
        }
        return false;
    }
}
