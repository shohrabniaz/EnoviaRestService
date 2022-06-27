package com.bjit.common.rest.app.service.controller.itemhistory.service.pid;

import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;
import com.bjit.common.rest.app.service.controller.itemhistory.model.constraint.HistoryOrder;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import matrix.db.Context;
import org.apache.log4j.Logger;

import java.util.*;

public class ItemsBasicDetailsFetcherByTNR implements ItemsBasicDetailsFetcher {
    private static final Logger LOGGER = Logger.getLogger(ItemsBasicDetailsFetcherByTNR.class);
    private HistoryOrder historyOrder;

    public ItemsBasicDetailsFetcherByTNR() {
        this.historyOrder = HistoryOrder.DESCENDIN_ORDER;
    }

    public ItemsBasicDetailsFetcherByTNR(HistoryOrder historyOrder) {
        this.historyOrder = historyOrder;
    }

    @Override
    public List<Data> fetch(List<Data> dataList, Context context) throws Exception {
        LOGGER.debug("+++++++++++++++++ ItemsBasicDetailsFetcherByTNR:fetch() ++++++++++++++++++++++");
        CommonSearch commonSearch = new CommonSearch();
        List<Data> newDataList = new ArrayList<>();
        List<String> selectedKeys = Arrays.asList("physicalid", "type", "name", "revision", "history");

        for (Data data : dataList) {
            // we only search by TNR while item id is not given
            if (data.getItemId() == null || data.getItemId().isEmpty()) {
                Data newTempData = (Data) data.clone();
                try {
                    newTempData.setTNR(this.getQueriableTNR(newTempData.getTNR()));
                    List<HashMap<String, String>> items = commonSearch.searchItem(context, newTempData.getTNR(), selectedKeys);
                    LOGGER.info(items);
                    newDataList.addAll(this.createDataNodes(items, newTempData));
                } catch (NullPointerException exception) {
                    newTempData.setExists(false);
                    newDataList.add(newTempData);
                }
            }
        }

        return newDataList;
    }

    private TNR getQueriableTNR(TNR tnr) throws CloneNotSupportedException {
        if (NullOrEmptyChecker.isNullOrEmpty(tnr.getType())) {
            tnr.setType("*");
        }

        if (NullOrEmptyChecker.isNullOrEmpty(tnr.getName())) {
            tnr.setName("*");
        }

        if (NullOrEmptyChecker.isNullOrEmpty(tnr.getRevision())) {
            tnr.setRevision("*");
        }
        return tnr;
    }

    private List<Data> createDataNodes(List<HashMap<String, String>> items, Data data) {
        List<Data> strongDataList = new ArrayList<>();

        // n items found with same type/name/rev
        for (HashMap<String, String> mapOfAitem : items) {
            TNR tnr = new TNR(mapOfAitem.get("type"), mapOfAitem.get("name"), mapOfAitem.getOrDefault("revision", ""));

            Data d = new Data();
            d.setItemId(mapOfAitem.get("physicalid"));
            d.setTNR(tnr);
            d.setDescId(data.getDescId());
            d.setExists(true);
            List<String> histories = this.getHistory(mapOfAitem.get("history"));
            if (this.historyOrder.equals(HistoryOrder.DESCENDIN_ORDER)) {
                Collections.reverse(histories);
            }
            d.setHistories(histories);
            strongDataList.add(d);
        }

        return strongDataList;
    }

    public static List<String> getHistory(String historyStr) {
        String[] histories = historyStr.split("\\,");
        LOGGER.debug("histories by TNR length:" + histories.length);
        return Arrays.asList(histories);
    }
}
