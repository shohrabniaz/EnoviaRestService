package com.bjit.common.rest.app.service.controller.export.report.single_level.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author BJIT
 */
public class ReportMapperModel {
    private boolean expandUp;
    private boolean expandDown;
    private int expandLevel;
    private int dataFetchLimit;
    private String busWhereClause;
    private String relWhereClause;
    private List<String> typeNames = new ArrayList<>();
    private List<String> relationNames = new ArrayList<>();
    private HashMap<String, String> typeShortNameMap = new HashMap<>();
    private Map<String, String> typeSelectablesWithOutputName = new HashMap<>();
    private Map<String, String> relationSelectablesWithOutputName = new HashMap<>();

    public boolean isExpandUp() {
        return expandUp;
    }

    public void setExpandUp(boolean expandUp) {
        this.expandUp = expandUp;
    }

    public boolean isExpandDown() {
        return expandDown;
    }

    public void setExpandDown(boolean expandDown) {
        this.expandDown = expandDown;
    }

    public int getExpandLevel() {
        return expandLevel;
    }

    public void setExpandLevel(int expandLevel) {
        this.expandLevel = expandLevel;
    }

    public int getDataFetchLimit() {
        return dataFetchLimit;
    }

    public void setDataFetchLimit(int dataFetchLimit) {
        this.dataFetchLimit = dataFetchLimit;
    }

    public String getBusWhereClause() {
        return busWhereClause;
    }

    public void setBusWhereClause(String busWhereClause) {
        this.busWhereClause = busWhereClause;
    }

    public String getRelWhereClause() {
        return relWhereClause;
    }

    public void setRelWhereClause(String relWhereClause) {
        this.relWhereClause = relWhereClause;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<String> getRelationNames() {
        return relationNames;
    }

    public void setRelationNames(List<String> relationNames) {
        this.relationNames = relationNames;
    }

    public HashMap<String, String> getTypeShortNameMap() {
        return typeShortNameMap;
    }

    public void setTypeShortNameMap(HashMap<String, String> typeShortNameMap) {
        this.typeShortNameMap = typeShortNameMap;
    }

    public Map<String, String> getTypeSelectablesWithOutputName() {
        return typeSelectablesWithOutputName;
    }

    public void setTypeSelectablesWithOutputName(Map<String, String> typeSelectablesWithOutputName) {
        this.typeSelectablesWithOutputName = typeSelectablesWithOutputName;
    }

    public Map<String, String> getRelationSelectablesWithOutputName() {
        return relationSelectablesWithOutputName;
    }

    public void setRelationSelectablesWithOutputName(Map<String, String> relationSelectablesWithOutputName) {
        this.relationSelectablesWithOutputName = relationSelectablesWithOutputName;
    }
}
