package com.bjit.common.rest.app.service.controller.itemhistory.service.pid;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;
import com.bjit.common.rest.app.service.controller.itemhistory.model.constraint.HistoryOrder;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class ItemsBasicDetailsFetcherByPID implements ItemsBasicDetailsFetcher {
    private static final Logger LOGGER = Logger.getLogger(ItemsBasicDetailsFetcherByPID.class);
    private static final String queryTemplate = "print bus {0} select {1} dump |";
    private List<String> selectedKeys = Arrays.asList("physicalid", "type", "name", "revision", "history");
    private Map<String, List<String>> memset = new HashMap<>();
    private HistoryOrder historyOrder;

    public ItemsBasicDetailsFetcherByPID() {
        this.historyOrder = HistoryOrder.DESCENDIN_ORDER;
    }

    public ItemsBasicDetailsFetcherByPID(HistoryOrder historyOrder) {
        this.historyOrder = historyOrder;
    }

    @Override
    public List<Data> fetch(List<Data> dataList, Context context) throws Exception {
        List<Data> newDataList = new ArrayList<>();
        String select = String.join(" ", selectedKeys);

        for (Data data : dataList) {
            if (data.getItemId() != null && !data.getItemId().isEmpty()) {
                Data newTempData = (Data) data.clone();
                String query = MessageFormat.format(queryTemplate, data.getItemId(), select);
                try {
                    HashMap<String, String> item = this._doGetItemDetails(query, context);
                    LOGGER.info(item);
                    newDataList.add(this.createDataNode(item, newTempData));
                } catch (NullPointerException exception) {
                    newTempData.setExists(false);
                    newDataList.add(newTempData);
                }
            }
        }

        return newDataList;
    }

    private HashMap<String, String> _doGetItemDetails(String query, Context context) throws Exception {
        HashMap<String, String> keyValueMap = new HashMap<>();

        String queryResults = getQueryResult(query, context);
        String[] dumpedResults = queryResults.split("\\|");

        for (int i = 0; i < selectedKeys.size() - 1; i++) {
            keyValueMap.put(selectedKeys.get(i), dumpedResults[i]);
        }

        List<String> histories = new ArrayList<>();
        for (int i = selectedKeys.size() - 1; i < dumpedResults.length; i++) {
            histories.add(dumpedResults[i]);
        }
        if (this.historyOrder.equals(HistoryOrder.DESCENDIN_ORDER)) {
            Collections.reverse(histories);
        }

        memset.put("history", histories);

        LOGGER.info("fetch item details by pid:" + keyValueMap.toString());
        return keyValueMap;
    }

    private Data createDataNode(HashMap<String, String> mapOfAitem, Data newTempData) {
        TNR tnr = new TNR(mapOfAitem.get("type"), mapOfAitem.get("name"), mapOfAitem.getOrDefault("revision", ""));

        Data d = new Data();
        d.setItemId(mapOfAitem.get("physicalid"));
        d.setTNR(tnr);
        d.setDescId(newTempData.getDescId());
        d.setExists(true);
        d.setHistories(memset.get("history"));

        return d;
    }

    private static String getQueryResult(String query, Context context) throws Exception {
        MQLCommand objMQL = new MQLCommand();
        try {
            objMQL.open(context);
            String result = MqlUtil.mqlCommand(context, objMQL, query);
            LOGGER.info("Query: " + query);
            LOGGER.info("Result: " + result);
            // objMQL.close(context);
            return result;
        } catch (MatrixException e) {
            LOGGER.error("Matrix Exception occured at during query execution: " + e.getMessage());
            LOGGER.error("Query String : " + query);
            throw e;
        }
    }
}
