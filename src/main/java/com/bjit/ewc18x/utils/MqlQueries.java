/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.model.UserContextForm;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.db.Query;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kayum-603
 */
public class MqlQueries {

    UserContextForm contextForm = new UserContextForm();
    private static final Logger LOGGER = Logger.getLogger(MqlQueries.class);
    private HashMap<String, String> resultMap;

    public String getQueryResults(Context context, String query) throws CustomException {
        LOGGER.debug("At getQueryResults method");
        String queryResult = null;
        try {
            if (context.checkContext()) {
                BusinessObject businessObject = new BusinessObject("");

                LOGGER.debug("Context created: " + context.checkContext());
                LOGGER.debug("Provided query: " + query);
                MQLCommand objMQL = new MQLCommand();
                objMQL.open(context);
                String sMQLStatement = query;
                queryResult = MqlUtil.mqlCommand(context, objMQL, sMQLStatement);
                objMQL.close(context);
            } else {
                LOGGER.debug("Context not found");

            }

        } catch (Exception ex) {
            LOGGER.error("Exception occured: " + ex);
            throw new CustomException(ex.getMessage());
        }
        return queryResult;
    }

    public String getObjectId(Context context, String type, String name, String rev) throws MatrixException {
        String objectId = "";
        try {
            Query quuery = new Query("");
            quuery.setBusinessObjectName(name);
            quuery.setBusinessObjectType(type);
            quuery.setBusinessObjectRevision(NullOrEmptyChecker.isNullOrEmpty(rev) ? "*" : rev);
            BusinessObjectList list = quuery.evaluate(context);
            if (list.size() > 0) {
                objectId = list.get(list.size() - 1).getObjectId();
            }

        } catch (MatrixException ex) {
            throw ex;
        }
        return objectId;
    }

    public String getPhysicalIdFromProcessImpl(Context context, String processImplementCnxID) throws FrameworkException {
        String physicalID_VPMReference = "";
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus ").append(processImplementCnxID).append(" select paths.path dump |");
        String selectPathsQuery = queryBuilder.toString();
        String queryResult = MqlUtil.mqlCommand(context, selectPathsQuery);
        if (NullOrEmptyChecker.isNullOrEmpty(queryResult) || !queryResult.contains("VPMReference,")) {
            return "";
        }
        physicalID_VPMReference = queryResult.substring(queryResult.indexOf("VPMReference,")).split("\\,")[1];

        return physicalID_VPMReference;
    }

    public String getPhysicalIdFromDocumentrocessImpl(Context context, String processImplementCnxID, String docType) throws FrameworkException {
        String physicalID_VPMReference = "";
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus ").append(processImplementCnxID).append(" select paths.path dump |");
        String selectPathsQuery = queryBuilder.toString();
        String queryResult = MqlUtil.mqlCommand(context, selectPathsQuery);
        if (NullOrEmptyChecker.isNullOrEmpty(queryResult) || (!queryResult.contains("Document,") && queryResult.contains("PLMDMTDocument,"))) {
            return "";
        } else if (queryResult.contains("Document,")) {
            physicalID_VPMReference = queryResult.substring(queryResult.indexOf("Document,")).split("\\,")[1];
        } else if (queryResult.contains("PLMDMTDocument,")) {
            String physicalID_Document = queryResult.substring(queryResult.indexOf("PLMDMTDocument,")).split("\\,")[1];
            physicalID_VPMReference = getDistributionListFromDrawing(context, physicalID_Document, docType);
        }

        return physicalID_VPMReference;
    }

    public String getDistributionListFromDrawing(Context context, String physicalId, String docType) throws FrameworkException {
        String docDistribution = "";
        String phyid = "";
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("print bus ").append(physicalId).append(" select attribute[DOC_DocDistributionList.DOC_DocDistributionList] dump |");
        String selectPathsQuery = queryBuilder.toString();
        docDistribution = MqlUtil.mqlCommand(context, selectPathsQuery);
        if (NullOrEmptyChecker.isNullOrEmpty(docDistribution)) {
            return phyid;
        } else {
            if (!NullOrEmptyChecker.isNullOrEmpty(docType)) {
                String[] attributeArr = docType.split(",");
                if (attributeArr.length == 1) {

                    if (docDistribution.equals(attributeArr[0])) {
                        phyid = physicalId;
                    }
                } else {
                    for (int i = 0; i < attributeArr.length; i++) {
                        if (docDistribution.equals(attributeArr[i])) {
                            phyid = physicalId;
                        }
                    }
                }

            }
            return phyid;
        }

    }

    public String getPhysicalIdFromTNR(Context context, String type, String name, String rev) throws FrameworkException, Exception {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("temp query bus ")
                .append(type)
                .append(" ")
                .append(name)
                .append(" ")
                .append(rev)
                .append(" select physicalId dump |");
        String queryResult = MqlUtil.mqlCommand(context, queryBuilder.toString());
        if (NullOrEmptyChecker.isNullOrEmpty(queryResult)) {
            return queryResult;
        }
        Pattern multilinePattern = Pattern.compile("\n");
        Matcher matcher = multilinePattern.matcher(queryResult);

        if (matcher.find()) {
            throw new Exception(PropertyReader.getProperty("maturity.change.multiple.object"));
        }
        return queryResult.split("\\|")[3];
    }

    public boolean doesExist(Context context, String physicalId) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("pri bus ")
                .append(physicalId)
                .append(" select name");
        try {
            String objectNameQuery = queryBuilder.toString();
            MqlUtil.mqlCommand(context, objectNameQuery);
        } catch (FrameworkException e) {
            return false;
        }
        return true;
    }

    public List<HashMap<String, String>> searchItem(Context context, TNR tnr, HashMap<String, String> whereClausesMap, List<String> selectDataList, Short limit) throws FrameworkException {
        String searchQuery = "temp query bus '" + Optional.ofNullable(tnr.getType()).orElse("*") + "' '" + Optional.ofNullable(tnr.getName()).orElse("*") + "' '" + Optional.ofNullable(tnr.getRevision()).orElse("*") + "' ";

        StringJoiner whereClauseStringJoiner = new StringJoiner(" && ");
        String whereClause = null;
        String limitData = null;

        if (Optional.ofNullable(whereClausesMap).isPresent()) {
            whereClausesMap.forEach((key, value) -> whereClauseStringJoiner.add(key + "=='" + value + "'"));
            whereClause = " where \"" + whereClauseStringJoiner.toString() + "\"";
        }
        if (Optional.ofNullable(limit).isPresent()) {
            limitData = MessageFormat.format(" limit {0}", limit.toString());
        }

        StringJoiner selectDataJoiner = new StringJoiner(" ");
        Optional.ofNullable(selectDataList).orElse(setDefaultSelectedList()).stream().forEach((selectData) -> selectDataJoiner.add("'" + selectData + "'"));

        searchQuery = searchQuery + Optional.ofNullable(whereClause).orElse("") + Optional.ofNullable(limitData).orElse("") + " select " + selectDataJoiner.toString();

        LOGGER.info("Search Query : " + searchQuery);

        try {
            String queryResult = MqlUtil.mqlCommand(context, searchQuery);
            LOGGER.info("Query Result : " + queryResult);

            //Map<String, String> resultMap = parseSearchQuery(queryResult);
            List<HashMap<String, String>> searchResults = parseResult(queryResult);

            if (searchResults.isEmpty()) {
                LOGGER.warn("Searched item not found");
                throw new NullPointerException("Searched item not found");
            }
            LOGGER.debug("Parsed successfully");
            return searchResults;
        } catch (FrameworkException exp) {
            LOGGER.error(exp);
            throw exp;
        } catch (NullPointerException exp) {
            LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error(exp);
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

    public List<HashMap<String, String>> parseResult(String searchResult) {
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
                if (key.startsWith("attribute[")) {
                    key = key.substring(10, key.length() - 1);
                }
                resultMap.put(key, valueLineList.get(1).trim());
            }
        });

        return resultMapList;
    }
}
