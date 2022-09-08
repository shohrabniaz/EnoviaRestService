/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author BJIT
 */
public class FtsSearchUtility {

    public JSONObject ftsSearchBody(List<ItemSearchParamBean> searchList, int resultNumber, String searchType) {

        JSONObject jsonobject = new JSONObject();
        JSONArray selectArray = new JSONArray();
        JSONArray selectSnippets = new JSONArray();
//
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder = queryBuilder.append("(");
        if (searchType.equalsIgnoreCase("ftsSearch")) {
            if (searchList.size() < 2) {
                queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(0).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(0).getSearchStr() + "))");
            } else {
                queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(0).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(0).getSearchStr() + "))");
                for (int i = 1; i < searchList.size(); i++) {
                    queryBuilder = queryBuilder.append(" OR ");
                    queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(i).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(i).getSearchStr() + "))");

                }
            }
        } else {
            if (searchList.size() < 2) {
                queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(0).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(0).getSearchStr() + "))");
            } else {
                queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(0).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(0).getSearchStr() + "))");
                for (int i = 1; i < searchList.size(); i++) {
                    queryBuilder = queryBuilder.append(" OR ");
                    queryBuilder = queryBuilder.append("([ds6w:identifier]:(" + searchList.get(i).getSearchStr() + ")" + " OR [ds6w:label]:(" + searchList.get(i).getSearchStr() + "))");

                }
            }
        }

        queryBuilder = queryBuilder.append(") AND ((NOT (flattenedtaxonomies:(\"interfaces/MBOM_MBOMPDM\"))) OR ((flattenedtaxonomies:(\"interfaces/MBOM_MBOMPDM\")) AND [ds6wg:MBOM_MBOMPDM.MBOM_Mastership]:\"3DX\"))");

        jsonobject.put("with_indexing_date", true);
        jsonobject.put("with_synthesis", false);
        jsonobject.put("with_nls", false);
        jsonobject.put("label", "3DSearch-coexusr1-1644554481828");
        jsonobject.put("query", queryBuilder.toString());
        jsonobject.put("nresults", resultNumber);

        jsonobject.put("order_by", "desc");
        jsonobject.put("order_field", "relevance");

        selectSnippets.put("ds6w:snippet");
        selectSnippets.put("ds6w:label:snippet");
        selectSnippets.put("ds6w:responsible:snippet");
        selectSnippets.put("ds6w:community:snippet");
        selectSnippets.put("swym:message_text:snippet");
        jsonobject.put("select_snippets", selectSnippets);
        selectArray.put("ds6w:label");
        selectArray.put("ds6w:identifier");
        selectArray.put("ds6wg:revision");
        selectArray.put("ds6w:project");
        selectArray.put("ds6wg:ITMTEXTS_ItemCommonText.ITMTEXTS_ItemCommonText");
        selectArray.put("ds6wg:TD_TechnicalDesignation.TD_TechnicalDesignation");
        selectArray.put("ds6wg:MBOM_MBOMPDM.MBOM_PDM_Drawing_Number");
        selectArray.put("ds6wg:MBOM_MBOMPDM.MBOM_PDM_Owner_Group");
        selectArray.put("ds6wg:MBOM_MBOMPDM.MBOM_PDM_State");
        selectArray.put("ds6wg:MBOM_MBOMERP.MBOM_TransferredtoERP");
        selectArray.put("ds6wg:MBOM_MBOMReference.MBOM_Type");
        selectArray.put("ds6wg:DELFmiFunctionPPRCreateReference.V_EstimatedWeight");
        selectArray.put("ds6wg:MBOM_MBOMERP.MBOM_Release_Purpose");
        selectArray.put("ds6wg:TRS_TermID.TRS_TermID");
        selectArray.put("ds6wg:MBOM_MBOMPDM.MBOM_Mastership");

        jsonobject.put("select_predicate", selectArray);

        JSONArray selectSource = new JSONArray();
        JSONArray selectfile = new JSONArray();
        selectSource.put("3dspace");
        selectfile.put("icon");
        selectfile.put("thumbnail_2d");
        jsonobject.put("source", selectSource);
        jsonobject.put("select_file", selectfile);
        jsonobject.put("start", "0");
        jsonobject.put("tenant", "OnPremise");
        jsonobject.put("with_synthesis_hierarchical", true);

        return jsonobject;

    }
}
