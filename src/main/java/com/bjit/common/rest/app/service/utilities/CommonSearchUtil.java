/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class CommonSearchUtil {

    private static final Logger COMMON_SEARCH_LOGGER = Logger.getLogger(CommonSearchUtil.class);

    public List<Map<String, String>> getResponseFromTNR(Context context, CommonItemSearchBean commonItemSearchBean, String type) {
        List<Map<String, String>> resultFromQuery = new ArrayList<>();
        String searchQuery = "";
        if (NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getName()) && NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getType()) && NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getRevision())) {

            searchQuery = getMultipleNameSearchQuery(commonItemSearchBean);
        } else if (!NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getAttr())) {
            searchQuery = getSearchQueryWithAttributes(commonItemSearchBean);
        } 
        else if(type.equalsIgnoreCase("costUpdate")) {

            searchQuery = getSearchQuery(commonItemSearchBean,type);
        }
        else  {

            searchQuery = getSearchQuery(commonItemSearchBean);
        }

        String queryResult = null;
        try {
            queryResult = MqlUtil.mqlCommand(context, searchQuery);
        } catch (FrameworkException ex) {
            COMMON_SEARCH_LOGGER.error(ex.getMessage());
        }
        if (!NullOrEmptyChecker.isNullOrEmpty(queryResult) && !NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getAttrs())) {
            resultFromQuery = getResultFromQuery(queryResult, commonItemSearchBean.getAttrs());
        } else if (!NullOrEmptyChecker.isNullOrEmpty(queryResult) && NullOrEmptyChecker.isNullOrEmpty(commonItemSearchBean.getAttrs())) {
            String[] attrs = {"objectid", "status", "isLatest"};
            resultFromQuery = getResultFromQuery(queryResult, attrs);
        }
        return resultFromQuery;
    }

    public List<Map<String, String>> getResultFromQuery(String queryResult, String[] attrs) {
        List<Map<String, String>> resultArray = new ArrayList<>();

        String[] tempList = queryResult.split("[\\r\\n]+");
        for (int i = 0; i < tempList.length; i++) {
            Map<String, String> objectId = new HashMap<>();
            String[] tempSingleList = tempList[i].split("\\|");
            objectId.put("type", tempSingleList[0]);
            objectId.put("name", tempSingleList[1]);
            objectId.put("revision", tempSingleList[2]);
            for (int k = 3; k < tempSingleList.length; k++) {
                String key = attrs[k - 3];
                String value = tempSingleList[k];
                objectId.put(key, value);
            }

            resultArray.add(objectId);
        }
        return resultArray;
    }

    public String getSearchQueryWithAttributes(CommonItemSearchBean commonItemSearchBean) {
        StringBuilder qureyBuilder = new StringBuilder();
        String type = commonItemSearchBean.getType();
        String name = commonItemSearchBean.getName();
        String rev = commonItemSearchBean.getRevision();
        String vault = PropertyReader.getProperty("bom.export.search.vault");
        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(name)) {
            name = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(rev)) {
            rev = "*";
        }

        qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").
                append(rev).append(" where \" vault == ").append(vault).append(" && ");

        int i = 1;

        for (String attrName : commonItemSearchBean.getAttr().get(0).keySet()) {

            if (i < commonItemSearchBean.getAttr().get(0).size()) {

                if (attrName.contains(".") || attrName.contains("_")) {
                    qureyBuilder = qureyBuilder.append("attribute[").append(attrName).append("]").append(" == ").append(commonItemSearchBean.getAttr().get(0).get(attrName)).append(" && ");
                } else {
                    qureyBuilder = qureyBuilder.append(attrName).append(" == ").append(attrName).append(" && ");
                }
            } else {
                if (attrName.contains(".") || attrName.contains("_")) {
                    qureyBuilder = qureyBuilder.append("attribute[").append(attrName).append("]").append(" == ").append(commonItemSearchBean.getAttr().get(0).get(attrName));
                } else {
                    qureyBuilder = qureyBuilder.append(attrName).append(" == ").append((commonItemSearchBean.getAttr().get(0).get(attrName)));
                }
            }
            i++;

        }
        qureyBuilder = qureyBuilder.append(" \" select id current attribute[V_isLastVersion] dump |");
        String searchQuery = qureyBuilder.toString();
        return searchQuery;
    }

    public String getSearchQuery(CommonItemSearchBean commonItemSearchBean) {
        StringBuilder qureyBuilder = new StringBuilder();
        String type = commonItemSearchBean.getType();
        String name = commonItemSearchBean.getName();
        String rev = commonItemSearchBean.getRevision();
        String vault = PropertyReader.getProperty("bom.export.search.vault");
        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(name)) {
            name = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(rev)) {
            rev = "*";
        }
        String[] attrs = commonItemSearchBean.getAttrs();
        if (NullOrEmptyChecker.isNullOrEmpty(attrs)) {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" where \" vault == ").append(vault).append(" \" select id current attribute[V_isLastVersion] dump |");
        } else {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" where \" vault == ").append(vault).append(" \" select ");
            for (int k = 0; k < attrs.length; k++) {
                if (attrs[k].contains("_") || attrs[k].contains(".")) {
                    attrs[k] = ("attribute[") + attrs[k] + ("]");
                }
                qureyBuilder = qureyBuilder.append(attrs[k]).append(" ");
            }
            qureyBuilder = qureyBuilder.append(" dump |");
        }
        String searchQuery = qureyBuilder.toString();
        COMMON_SEARCH_LOGGER.info("Item Search Query : " + searchQuery);
        return searchQuery;
    }

    public String getSearchQuery(CommonItemSearchBean commonItemSearchBean, String searchType) {
        StringBuilder qureyBuilder = new StringBuilder();
        String type = PropertyReader.getProperty("cost.data.update.type");
        String name = commonItemSearchBean.getName();
        String rev = commonItemSearchBean.getRevision();
        ;
        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(name)) {
            name = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(rev)) {
            rev = "*";
        }
        String[] attrs = commonItemSearchBean.getAttrs();
        if (NullOrEmptyChecker.isNullOrEmpty(attrs)) {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" select id current attribute[V_isLastVersion] dump |");
        } else {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" select ");
            for (int k = 0; k < attrs.length; k++) {
                if (attrs[k].contains("_") || attrs[k].contains(".")) {
                    attrs[k] = ("attribute[") + attrs[k] + ("]");
                }
                qureyBuilder = qureyBuilder.append(attrs[k]).append(" ");
            }
            qureyBuilder = qureyBuilder.append(" dump |");
        }
        String searchQuery = qureyBuilder.toString();
        COMMON_SEARCH_LOGGER.info("Item Search Query : " + searchQuery);
        return searchQuery;
    }

    public String getMultipleNameSearchQuery(CommonItemSearchBean commonItemSearchBean) {
        StringBuilder qureyBuilder = new StringBuilder();
        String type = commonItemSearchBean.getType();
        String name = "";
        String rev = commonItemSearchBean.getRevision();
        String vault = PropertyReader.getProperty("bom.export.search.vault");
        StringBuilder nameBuilder = new StringBuilder();
        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            type = "*";
        }
        if (NullOrEmptyChecker.isNullOrEmpty(rev)) {
            rev = "*";
        }

        for (int n = 0; n < commonItemSearchBean.getAttr().size(); n++) {
            if (commonItemSearchBean.getAttr().size() == 1) {
                nameBuilder = nameBuilder.append(commonItemSearchBean.getAttr().get(n).get("name"));

            } else {
                if (commonItemSearchBean.getAttr().size() == n + 1) {
                    nameBuilder = nameBuilder.append(commonItemSearchBean.getAttr().get(n).get("name"));

                } else {
                    nameBuilder = nameBuilder.append(commonItemSearchBean.getAttr().get(n).get("name")).append(",");
                }
            }
        }

        name = nameBuilder.toString();

        String[] attrs = commonItemSearchBean.getAttrs();
        if (NullOrEmptyChecker.isNullOrEmpty(attrs)) {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" where \" vault == ").append(vault).append(" \" select id current attribute[V_isLastVersion] dump |");
        } else {
            qureyBuilder = qureyBuilder.append("temp query bus ").append(type).append(" ").append(name).append(" ").append(rev).append(" where \" vault == ").append(vault).append(" \" select ");
            for (int k = 0; k < attrs.length; k++) {
                if (attrs[k].contains("_") || attrs[k].contains(".")) {
                    attrs[k] = ("attribute[") + attrs[k] + ("]");
                }
                qureyBuilder = qureyBuilder.append(attrs[k]).append(" ");
            }
            qureyBuilder = qureyBuilder.append(" dump |");
        }
        String searchQuery = qureyBuilder.toString();

        COMMON_SEARCH_LOGGER.info(searchQuery);
        return searchQuery;
    }
}
