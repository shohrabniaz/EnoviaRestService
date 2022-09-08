/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.itemHistoryLines.utilities;

import com.bjit.common.rest.app.service.model.itemHistoryLine.HistoryLineDetatils;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import matrix.db.Context;

/**
 *
 * @author Fazley Rabbi-11372 Date: 23-08-2022
 */

public class FetchHistoryData {

    private HashMap<String, String> resultMap;
    private StringBuilder queryWhereClause;
    private List<String> event;
    private List<String> user;
    private List<String> time;
    private List<String> state;
    private List<String> description;
    private int count;

    private static final org.apache.log4j.Logger FETCH_HISTORY_DATA_LOGGER = org.apache.log4j.Logger.getLogger(FetchHistoryData.class);

    public List<HistoryLineDetatils> searchItem(Context context, TNR tnr, List<String> selectDataList) throws FrameworkException, Exception {
        return searchItem(context, tnr, null, selectDataList, null);
    }

    public List<HistoryLineDetatils> searchItem(Context context, TNR tnr, HashMap<String, String> whereClausesMap, List<String> selectDataList, Short limit) throws FrameworkException, Exception {
        StringJoiner whereClauseStringJoiner = new StringJoiner(" && ");
        String whereClause = null;

        if (Optional.ofNullable(whereClausesMap).isPresent()) {
            whereClausesMap.forEach((key, value) -> whereClauseStringJoiner.add(key + "=='" + value + "'"));
            whereClause = " where \"" + whereClauseStringJoiner.toString() + "\"";
        }

        return searchItemInDB(context, tnr, whereClause, selectDataList, limit);
    }

    private List<HistoryLineDetatils> searchItemInDB(Context context, TNR tnr, String whereClause, List<String> selectDataList, Short limit) throws Exception {
        String limitData = null;
        if (Optional.ofNullable(limit).isPresent()) {
            limitData = MessageFormat.format(" limit {0}", limit.toString());
        }
        String searchQuery = "temp query bus '" + Optional.ofNullable(tnr.getType()).orElse("*") + "' '" + Optional.ofNullable(tnr.getName()).orElse("*") + "' '" + Optional.ofNullable(tnr.getRevision()).orElse("*") + "' ";
        StringJoiner selectDataJoiner = new StringJoiner(" ");
        Optional.ofNullable(selectDataList).orElse(setDefaultSelectedList()).stream().forEach((selectData) -> selectDataJoiner.add("'" + selectData + "'"));
        searchQuery = searchQuery + Optional.ofNullable(whereClause).orElse("") + Optional.ofNullable(limitData).orElse("") + " select " + selectDataJoiner.toString();
        FETCH_HISTORY_DATA_LOGGER.info("Search Query : " + searchQuery);
        try {
            String queryResult = MqlUtil.mqlCommand(context, searchQuery);
            FETCH_HISTORY_DATA_LOGGER.info("Query Result : " + queryResult);
            List<HistoryLineDetatils> searchResults = parseResult(queryResult);
            if (NullOrEmptyChecker.isNullOrEmpty(searchResults)) {
                FETCH_HISTORY_DATA_LOGGER.warn(tnr.toString() + " item not exists in the system");
                throw new NullPointerException(tnr.toString() + " item not exists in the system");
            }
            return searchResults;
        } catch (FrameworkException exp) {
            FETCH_HISTORY_DATA_LOGGER.error(exp);
            throw exp;
        } catch (NullPointerException exp) {
            FETCH_HISTORY_DATA_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            FETCH_HISTORY_DATA_LOGGER.error(exp);
            throw exp;
        }
    }

    private List<String> setDefaultSelectedList() {
        List<String> selectDataList = new ArrayList<>();
        selectDataList.add("type");
        selectDataList.add("name");
        selectDataList.add("revision");
        selectDataList.add("id");
        return selectDataList;
    }

    private List<HistoryLineDetatils> parseResult(String searchResult) {
        event = new ArrayList<>();
        user = new ArrayList<>();
        time = new ArrayList<>();
        state = new ArrayList<>();
        description = new ArrayList<>();

        if (NullOrEmptyChecker.isNullOrEmpty(searchResult)) {
            return null;
        }

        try {
            List<String> resultStringLineList = Arrays.asList(searchResult.split("history."));
            List<HashMap<String, String>> resultMapList = new ArrayList<>();

            for (String resultStringLine : resultStringLineList) {
                List<String> valueLineList = Arrays.asList(resultStringLine.split("="));

                if (valueLineList.size() != 2) {
                    resultMap = new HashMap<>();
                    resultMapList.add(resultMap);
                }
                if (valueLineList.size() == 2) {
                    String key = valueLineList.get(0).trim();
                    String value = valueLineList.get(1).trim();

                    if (key.equalsIgnoreCase("event")) {
                        event.add(value);
                    }
                    if (key.equalsIgnoreCase("description")) {
                        description.add(value);
                    }
                    if (key.equalsIgnoreCase("time")) {
                        String[] timeDate = value.split(":", 2);
                        time.add(timeDate[1].trim());
                    }
                    if (key.equalsIgnoreCase("user")) {
                        String[] userName = value.split(":");
                        user.add(userName[1].trim());
                    }
                    if (key.equalsIgnoreCase("state")) {
                        String[] stateName = value.split(":");
                        state.add(stateName[1].trim());
                    }
                    if (key.equalsIgnoreCase("count")) {
                        count = (Integer.parseInt(value));
                    }
                }
            }
            return prepareHistoryResult();
            
        } catch (Exception ex) {
            FETCH_HISTORY_DATA_LOGGER.error(ex.getMessage());
            throw ex;
        }
    }

    private List<HistoryLineDetatils> prepareHistoryResult() {
        List<HistoryLineDetatils> historyLines = new ArrayList<>();
        HistoryLineDetatils historyLineDetatils;
        try {
            for (int i = 0; i < this.count; i++) {
                historyLineDetatils = new HistoryLineDetatils();
                historyLineDetatils.setEvent(this.event.get(i));
                historyLineDetatils.setUser(this.user.get(i));
                historyLineDetatils.setTime(this.time.get(i));
                historyLineDetatils.setState(this.state.get(i));
                historyLineDetatils.setDescription(this.description.get(i));

                historyLines.add(historyLineDetatils);
            }
        } catch (Exception ex) {
            FETCH_HISTORY_DATA_LOGGER.error(ex.getMessage());
        }
        return historyLines;
    }
}
