package com.bjit.common.rest.app.service.controller.itemhistory.model;

import com.bjit.common.rest.app.service.controller.itemhistory.model.constraint.Condition;
import com.bjit.common.rest.app.service.controller.itemhistory.model.constraint.HistoryOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constraint {
    private List<String> selects = new ArrayList<>(Arrays.asList("history"));
    private HistoryOrder historyOrder = HistoryOrder.DESCENDIN_ORDER;
    private List<Condition> conditions = new ArrayList<>();

    public List<String> getSelects() {
        return selects;
    }

    public void setSelects(List<String> selects) {
        this.selects = selects;
    }

    public HistoryOrder getHistoryOrder() {
        return historyOrder;
    }

    public void setHistoryOrder(HistoryOrder historyOrder) {
        this.historyOrder = historyOrder;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
