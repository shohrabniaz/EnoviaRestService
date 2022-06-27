package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Qualifier("CommonSearch")
public class CommonSearch {

    private HashMap<String, String> resultMap;
    private StringBuilder queryWhereClause;

    private static final org.apache.log4j.Logger COMMON_SEARCH_LOGGER = org.apache.log4j.Logger.getLogger(CommonSearch.class);

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr) throws FrameworkException, Exception {
        return searchItem(context, tnr, null, setDefaultSelectedList(), null);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, HashMap<String, String> whereClausesMap) throws FrameworkException, Exception {
        return searchItem(context, tnr, whereClausesMap, setDefaultSelectedList(), null);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, short limit) throws FrameworkException, Exception {
        return searchItem(context, tnr, null, setDefaultSelectedList(), limit);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, List<String> selectDataList) throws FrameworkException, Exception {
        return searchItem(context, tnr, null, selectDataList, null);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, List<String> selectDataList, short limit) throws FrameworkException, Exception {
        return searchItem(context, tnr, null, selectDataList, limit);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, HashMap<String, String> whereClausesMap, List<String> selectDataList) throws FrameworkException, Exception {
        return searchItem(context, tnr, whereClausesMap, selectDataList, null);
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, HashMap<String, String> whereClausesMap, List<String> selectDataList, Short limit) throws FrameworkException, Exception {
        StringJoiner whereClauseStringJoiner = new StringJoiner(" && ");
        String whereClause = null;

        if (Optional.ofNullable(whereClausesMap).isPresent()) {
            whereClausesMap.forEach((key, value) -> whereClauseStringJoiner.add(key + "=='" + value + "'"));
            whereClause = " where \"" + whereClauseStringJoiner.toString() + "\"";
        }

        return searchItemInDB(context, tnr, whereClause, selectDataList, limit);
    }

    private List<HashMap<String, String>> searchItemInDB(Context context, TNR tnr, String whereClause, List<String> selectDataList, Short limit) throws Exception {
        String limitData = null;
        if (Optional.ofNullable(limit).isPresent()) {
            limitData = MessageFormat.format(" limit {0}", limit.toString());
        }
        String searchQuery = "temp query bus '" + Optional.ofNullable(tnr.getType()).orElse("*") + "' '" + Optional.ofNullable(tnr.getName()).orElse("*") + "' '" + Optional.ofNullable(tnr.getRevision()).orElse("*") + "' ";
        StringJoiner selectDataJoiner = new StringJoiner(" ");
        Optional.ofNullable(selectDataList).orElse(setDefaultSelectedList()).stream().forEach((selectData) -> selectDataJoiner.add("'" + selectData + "'"));
        searchQuery = searchQuery + Optional.ofNullable(whereClause).orElse("") + Optional.ofNullable(limitData).orElse("") + " select " + selectDataJoiner.toString();
        COMMON_SEARCH_LOGGER.info("Search Query : " + searchQuery);
        try {
            String queryResult = MqlUtil.mqlCommand(context, searchQuery);
            COMMON_SEARCH_LOGGER.info("Query Result : " + queryResult);
            //Map<String, String> resultMap = parseSearchQuery(queryResult);
            List<HashMap<String, String>> searchResults = parseResult(queryResult);
            if (NullOrEmptyChecker.isNullOrEmpty(searchResults)) {
                COMMON_SEARCH_LOGGER.warn(tnr.toString() + " item not exists in the system");
                throw new NullPointerException(tnr.toString() + " item not exists in the system");
            }
            return searchResults;
        } catch (FrameworkException exp) {
            COMMON_SEARCH_LOGGER.error(exp);
            throw exp;
        } catch (NullPointerException exp) {
            COMMON_SEARCH_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            COMMON_SEARCH_LOGGER.error(exp);
            throw exp;
        }
    }

    public List<HashMap<String, String>> searchPreparedItem(Context context, TNR tnr, List<String> selectDataList, Short limit) throws Exception {
        return searchItemInDB(context, tnr, NullOrEmptyChecker.isNull(queryWhereClause) ? "" : "where \"" + queryWhereClause.toString() + "\"", selectDataList, limit);
    }

    public CommonSearch prepareWhere(String leftElement, Conditionals condition, String rightElement) {
        this.queryWhereClause = Optional.ofNullable(this.queryWhereClause).orElse(new StringBuilder());
        Optional.ofNullable(leftElement).orElseThrow(() -> new RuntimeException("Query conditional data can't be null"));
        Optional.ofNullable(condition).orElseThrow(() -> new RuntimeException("Query conditional data can't be null"));
        Optional.ofNullable(rightElement).orElseThrow(() -> new RuntimeException("Query conditional data can't be null"));

        this.queryWhereClause.append("'").append(leftElement).append("'").append(condition.condition).append("'").append(rightElement).append("'");

        return this;
    }

    public CommonSearch and() {
        Optional.ofNullable(this.queryWhereClause).orElseThrow(() -> new RuntimeException("Where clause can't be null"));
        this.queryWhereClause.append(Conditionals.AND.condition);
        return this;
    }

    public CommonSearch or() {
        Optional.ofNullable(this.queryWhereClause).orElseThrow(() -> new RuntimeException("Where clause can't be null"));
        this.queryWhereClause.append(Conditionals.OR.condition);
        return this;
    }

    public CommonSearch nested() {
        this.queryWhereClause = Optional.ofNullable(this.queryWhereClause).orElse(new StringBuilder());
        this.queryWhereClause.append("(");
        return this;
    }

    public CommonSearch done() {
        Optional.ofNullable(this.queryWhereClause).orElseThrow(() -> new RuntimeException("Where clause can't be null"));
        this.queryWhereClause.append(")");
        return this;
    }

    private List<String> setDefaultSelectedList() {
        List<String> selectDataList = new ArrayList<>();
        selectDataList.add("type");
        selectDataList.add("name");
        selectDataList.add("revision");
        selectDataList.add("id");
        return selectDataList;
    }

    private Map<String, String> parseSearchQuery(String searchQueryResult) {
        return Arrays.asList(searchQueryResult.split("\n"))
                .stream()
                .map(arrayData -> arrayData.split("="))
                .filter(splitByEqualSign -> splitByEqualSign.length == 2)
                .collect(Collectors.toMap(resultData -> resultData[0].trim(), resultData -> resultData[1].trim()));
    }

    private List<HashMap<String, String>> parseResult(String searchResult) {
        if (NullOrEmptyChecker.isNullOrEmpty(searchResult)) {
            return null;
        }

        List<String> resultStringLineList = Arrays.asList(searchResult.split("\n"));
        List<HashMap<String, String>> resultMapList = new ArrayList<>();
        resultStringLineList.stream().forEach((String resultStringLine) -> {

            List<String> valueLineList = Arrays.asList(resultStringLine.split("="));

            if (valueLineList.size() != 2) {
                resultMap = new HashMap<>();
                resultMapList.add(resultMap);
            }
            if (valueLineList.size() == 2) {
                String key = valueLineList.get(0).trim();
                String value = valueLineList.get(1).trim();

                resultMap.put(key, resultMap.containsKey(key) ? (resultMap.get(key) + ", " + value) : value);

            }
        });

        return resultMapList;
    }
}
