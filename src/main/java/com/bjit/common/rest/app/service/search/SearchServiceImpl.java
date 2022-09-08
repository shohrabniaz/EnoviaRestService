/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search;

import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.common.rest.app.service.utilities.CommonSearchUtil;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author BJIT
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = Logger.getLogger(SearchServiceImpl.class);

    @Override
    public Items getItems(Context context, CommonItemSearchBean commonItemSearchBean) {

        Items items = new Items();
        List<Map<String, String>> responseFromTNR = new ArrayList<>();
        List<Map<String, String>> response = new ArrayList<>();
        CommonSearchUtil commonSearchUtil = new CommonSearchUtil();

        responseFromTNR = commonSearchUtil.getResponseFromTNR(context, commonItemSearchBean, "");

        if (responseFromTNR.size() > 0) {
            for (int i = 0; i < responseFromTNR.size(); i++) {
                Map<String, String> responseResult = new HashMap<>();
                responseResult.put("type", commonItemSearchBean.getType());
                responseResult.put("name", commonItemSearchBean.getName());
                responseFromTNR.get(i).forEach((key, value) -> {
                    responseResult.put(key, value);
                });
                response.add(responseResult);
            }
            items.setItems(response);
            return items;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String> search(Context context, String type, String name, String revision) throws FrameworkException {
        Map<String, String> result = new HashMap<>();
        result.put("found", "false");

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("temp query bus ")
                .append(" '").append(type).append("' ")
                .append(" '").append(name).append("' ")
                .append(" '").append(revision).append("' ")
                .append("where \"(vault==").append(PropertyReader.getProperty("common.search.item.vault")).append(") ")
                .append(PropertyReader.getProperty("common.search.item.check.mastership"))
                .append("\"")
                .append(" select attribute[MBOM_MBOMPDM.MBOM_Mastership].value dump |");
        String mqlQuery = queryBuilder.toString();
        String mqlResult = MqlUtil.mqlCommand(context, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            if (mqlResult.contains(name)) {
                result.put("found", "true");
                String[] splittedRows = mqlResult.split("\n");
                String singleRow = splittedRows[0];
                String[] splittedResult = singleRow.split(Pattern.quote("|"), -1);
                result.put("mastership", splittedResult[3]);
            }
        }
        return result;
    }

    @Override
    public HashMap<String, String> search(Context context, String type, String name, String revision, String matchAttributeOrProperty, String matchListAsString, String matchDelimeter) throws FrameworkException {
        HashMap<String, String> results = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("temp query bus ")
                .append(" '").append(type).append("' ")
                .append(" '").append(name).append("' ")
                .append(" '").append(revision).append("' ")
                .append("where ").append("\" ")
                .append("(").append(matchAttributeOrProperty).append(" matchlist '").append(matchListAsString).append("' '").append(matchDelimeter).append("') ")
                .append("AND ")
                .append("(vault=='").append(PropertyReader.getProperty("common.search.item.vault")).append("') ")
                .append(PropertyReader.getProperty("common.search.item.check.mastership"))
                .append(" \" ")
                .append(" select attribute[MBOM_MBOMPDM.MBOM_Mastership].value dump |");

        String mqlQuery = queryBuilder.toString();
        String mqlResult = MqlUtil.mqlCommand(context, mqlQuery);
        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            String[] splittedResult = mqlResult.split("\n");
            for (String result : splittedResult) {
                String[] splittedObjectInfo = result.split(Pattern.quote("|"), -1);
                results.put(splittedObjectInfo[1], splittedObjectInfo[3]);
            }
        }
        return results;
    }
}
