/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.itemHistoryLines;

import com.bjit.common.rest.app.service.itemHistoryLines.utilities.FetchHistoryData;
import com.bjit.common.rest.app.service.model.itemHistoryLine.HistoryLineDetatils;
import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemHistoryBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import matrix.db.Context;

/**
 *
 * @author Fazley Rabbi-11372 Date: 27-06-2022
 */
public class HistoryLinesServiceImpl implements HistoryLinesService {

    private static final org.apache.log4j.Logger HISTORY_LINES_SERVICE_IMPL_LOGGER = org.apache.log4j.Logger.getLogger(HistoryLinesServiceImpl.class);
    private String historyOrder;
    private final String historyOrderDescending = PropertyReader.getProperty("item.history.order.descending");
    private Context context;

    public HistoryLinesServiceImpl() {
    }

    public HistoryLinesServiceImpl(String historyOrder, Context context) {
        this.historyOrder = historyOrder;
        this.context = context;
    }

    @Override
    public ItemHistoryBean getItemHistoryLines(String objId) throws Exception {
        ItemHistoryBean itemHistoryBean = new ItemHistoryBean();
        List<HistoryLineDetatils> historyDataList = new ArrayList<>();

        try {
            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
            TNR tnr = null;
            try {
                tnr = businessObjectOperations.getObjectTNR(this.context, objId);

                List<String> selectDataList = new ArrayList<String>();
                selectDataList.add("history.event");
                selectDataList.add("history.user");
                selectDataList.add("history.time");
                selectDataList.add("history.state");
                selectDataList.add("history.description");
                selectDataList.add("history.count");
                //selectDataList.add("history.between");

                FetchHistoryData fetch = new FetchHistoryData();

                //get history data list
                historyDataList = fetch.searchItem(this.context, tnr, selectDataList);
                HISTORY_LINES_SERVICE_IMPL_LOGGER.info("History Data: " + historyDataList);
                
            } catch (Exception ex) {
                HISTORY_LINES_SERVICE_IMPL_LOGGER.error("Error Raised: " + ex.getMessage());
                itemHistoryBean.setError("History not found");
                return itemHistoryBean;
            }

            //checking history order
            if (this.historyOrder.equalsIgnoreCase(this.historyOrderDescending)) {
                Collections.reverse(historyDataList);
            }

            itemHistoryBean = new ItemHistoryBean();

            itemHistoryBean.setTnr(tnr);
            itemHistoryBean.setItemId(objId);
            itemHistoryBean.setHistoryLines(historyDataList);

        } catch (Exception ex) {
            HISTORY_LINES_SERVICE_IMPL_LOGGER.error("Error Raised: " + ex.getMessage());
        }
        return itemHistoryBean;
    }
}
