/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.GTS.translation;

import com.bjit.common.rest.app.service.model.Translation.BundleAndText;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.bjit.ewc18x.utils.PropertyReader;

/**
 * Translation services are implemented here. 3dspace Item object title and
 * abbreviation is to be updated according to bundle information which came from GTS
 *
 * @author BJIT
 */
@Service
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOGGER = Logger.getLogger(TranslationServiceImpl.class);

    /**
     * Get Enovia item object physical id by gts bundle id
     *
     * @param context User login context
     * @param bundleIds Bundle id list
     * @return physical id and bundle id map
     */
    @Override
    public Map<String, String> getPhysicalIdFromBundleID(Context context, List<String> bundleIds) {
        Map<String, String> objectId = new HashMap<>();
        if (bundleIds.size() > 0) {
            String multipleBundleQuery = getSelectionQuery(context, bundleIds);
            long startQueryTime = System.currentTimeMillis();
            LOGGER.info("Select Query Execution Start ---- ");
            String queryResult = null;
            try {
                queryResult = MqlUtil.mqlCommand(context, multipleBundleQuery);
            } catch (FrameworkException ex) {
                LOGGER.error(ex.getMessage());
            }
            long endQueryTime = System.currentTimeMillis();
            LOGGER.info("Select Query Execution End ---");
            LOGGER.info("Select Query Execution Total Time (ms) : " + (endQueryTime - startQueryTime));
            if (!NullOrEmptyChecker.isNullOrEmpty(queryResult)) {
                String[] tempList = queryResult.split("[\\r\\n]+");
                for (int i = 0; i < tempList.length; i++) {
                    String[] tempSingleList = tempList[i].split("\\|");
                    String obId = tempSingleList[3];
                    String bnId = tempSingleList[4];
                    objectId.put(obId, bnId);
                }
                return objectId;
            }
            return objectId;
        } else {
            return objectId;
        }
    }

    /**
     * Update Enovia title compare to bundle from GTS
     *
     * @param context User login context
     * @param objectId Item objects object id
     * @param bundleAndText Bundle ids text and abbreviation
     * @return Result of update action
     */
    @Override
    public String updateTitleForBundles(Context context, String objectId, BundleAndText bundleAndText) {
        StringBuilder queryBuilder = new StringBuilder();
        String text = bundleAndText.getText();
        // to replace single quotation with escape characters
        text = text.replace("'", "\\\'");
        queryBuilder.append("escape modify bus ").append(objectId).append(" 'PLMEntity.V_Name' ").append("'").append(text).append("'");

        String abbreviation = bundleAndText.getAbbreviation();
        // to replace single quotation with escape characters
        abbreviation = abbreviation.replace("'", "\\\'");

        queryBuilder.append(" 'TRS_ShortEnglish.TRS_ShortNameEnglish' ").append("'").append(abbreviation).append("'");
        String mqlQuery = queryBuilder.toString();

        String queryResult = null;
        try {
            queryResult = MqlUtil.mqlCommand(context, mqlQuery);
            if (NullOrEmptyChecker.isNullOrEmpty(queryResult)) {
                return "Success";
            } else {
                return "Error";
            }
        } catch (FrameworkException ex) {
            LOGGER.error("Error generated query: " + mqlQuery);
            LOGGER.error(ex.getMessage());
            return "Error";
        }

    }

    /**
     * Getting 3dspace types which uses 'TRS_ShortEnglish' interface
     *
     * @param context User login context
     * @return Allowed types for english translation interface
     */
    private String getTypesByMQL(Context context) {
        String getTypeQuery = "print interface TRS_ShortEnglish select type dump";
        String queryResult = null;
        try {
            queryResult = MqlUtil.mqlCommand(context, getTypeQuery);
        } catch (FrameworkException ex) {
            LOGGER.error(ex.getMessage());
        }
        return queryResult;
    }

    /**
     * Prepare search mql query to fetch usage of bundle ids
     *
     * @param context User login context
     * @param bundleIds List of bundle ids
     * @return Prepared mql
     */
    private String getSelectionQuery(Context context, List<String> bundleIds) {
        StringBuilder qureyBuilder = new StringBuilder();
        String vault = PropertyReader.getProperty("bom.export.search.vault");
        String types = getTypesByMQL(context);
        StringBuilder printQuery = new StringBuilder();
        printQuery = printQuery.append(types).append(" * * where \" attribute[TRS_TermID.TRS_TermID].value matchlist ['");
        qureyBuilder = qureyBuilder.append("temp query bus ").append(types).append(" * * where \" attribute[TRS_TermID.TRS_TermID].value matchlist '");
        for (int k = 0; k < bundleIds.size(); k++) {
            if (k == bundleIds.size() - 1) {
                qureyBuilder = qureyBuilder.append(bundleIds.get(k));
                printQuery = printQuery.append(bundleIds.get(k));
            } else {
                qureyBuilder = qureyBuilder.append(bundleIds.get(k)).append(",");
                printQuery = printQuery.append(bundleIds.get(k)).append(",");
            }
        }
        printQuery = printQuery.append("'] ',' AND current != 'RELEASED' AND attribute[TRS_TermID.TRS_TermID].value != '' AND vault == ").append(vault).append(" \" select id attribute[TRS_TermID.TRS_TermID] dump |");
        qureyBuilder = qureyBuilder.append("' ',' AND current != 'RELEASED' AND attribute[TRS_TermID.TRS_TermID].value != '' AND vault == ").append(vault).append(" \" select id attribute[TRS_TermID.TRS_TermID] dump |");
        LOGGER.info("Item Search By Requested BundleID : " + printQuery.toString());
        String multipleBundleQuery = qureyBuilder.toString();
        return multipleBundleQuery;
    }
}
