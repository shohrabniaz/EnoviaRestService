/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search;

import com.bjit.common.rest.app.service.maturity.MaturityChangeService;
import com.bjit.common.rest.app.service.model.common.ItemSearchDetailsResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import com.bjit.common.rest.app.service.utilities.FtsSearchUtility;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
@Service
public class FtsSearchServiceImpl implements FtsSearchService {

    private static final Logger LOGGER = Logger.getLogger(FtsSearchServiceImpl.class);

    public DsServiceCall service;

    @Autowired
    private MaturityChangeService maturityChangeService;

//    public List<ItemSearchResponseBean> search(List<ItemSearchParamBean> searchList) throws Exception {
//
//        List<ItemSearchResponseBean> results = new ArrayList<>();
//
////        int max = Integer.parseInt(PropertyReader.getProperty("ds.service.search.item.max-count"));
////        if(searchList.size() > max){
////            throw new Exception("Search error: Search items limit exceeded. Max allowed: "+ max +" items");
////        }
//        try {
//            //check if service is already authenticated or not
//            if (this.service == null) {
//                this.service = new DsServiceCall();
//            }
//        } catch (Exception e) {
//            //System.out.println("Auth error: "+ e);
//            this.service = new DsServiceCall();
//        }
//
//        try {
//            String url = PropertyReader.getProperty("ds.federated.service.fts.base.url") + "federated/search";
//
//            String name = searchList.get(0).getName();
//
//            String reqBody = "{\r\n"
//                    + "      \"label\": \"3DSearch-coexusr1-1644554481828\",\r\n"
//                    + "      \"query\":" + name + "\r\n" + "    }";
//
////            for(ItemSearchParamBean item: searchList) {
////                if(!NullOrEmptyChecker.isNullOrEmpty(item.getName())) {
////                    CommonSearchValidator.validateName(item.getName());
////                    if(NullOrEmptyChecker.isNullOrEmpty(params)) {
////                        params = item.getName();
////                    } else {
////                        params = params + " OR " + item.getName();
////                    }
////                }
////            }
//            String resp = service.callPostService(url, reqBody);
//
//            ObjectMapper mapper = new ObjectMapper();
//            DsItemSearchResponseBean respBean = mapper.readValue(resp, DsItemSearchResponseBean.class);
//            List<HashMap<String, String>> member = respBean.getMember();
//
//            Set<String> nameSet = new HashSet<String>();
//
//            member.forEach((item) -> {
//                nameSet.add(item.get("name"));
//            });
//
//            searchList.forEach((searchStr) -> {
//                List<HashMap<String, String>> itemDetailsList = new ArrayList<HashMap<String, String>>();
//                if (nameSet.contains(searchStr.getName())) {
//                    results.add(new ItemSearchResponseBean(searchStr.getName(), "Found"));
//                } else {
//                    results.add(new ItemSearchResponseBean(searchStr.getName(), "Not Found"));
//                }
//            });
//            return results;
//        } catch (JsonParseException e) {
//            throw new Exception("Search error: Unexpected response");
//        } catch (RuntimeException e) {
//            throw new Exception("Search error: " + e);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
    public List<ItemSearchDetailsResponseBean> searchDetails(List<ItemSearchParamBean> searchList) throws Exception {

        List<ItemSearchDetailsResponseBean> results = new ArrayList<>();
        FtsSearchUtility ftsSearchUtility = new FtsSearchUtility();
        String searchType = "ftsSearch";
        int max = Integer.parseInt(PropertyReader.getProperty("ds.service.search.item.max-count"));
        if (searchList.size() > max) {
            throw new Exception("Search error: Search items limit exceeded. Max allowed: " + max + " items");
        }
        try {
            //check if service is already authenticated or not
            if (this.service == null) {
                this.service = new DsServiceCall();
            }
        } catch (Exception e) {

            this.service = new DsServiceCall();
        }

        try {

            String url = PropertyReader.getProperty("ds.federated.service.fts.url");

            int resultNumber = Integer.parseInt((PropertyReader.getProperty("fts.search.service.max.item")));
            ObjectMapper mapper = new ObjectMapper();
            Set<String> nameSet = new HashSet<String>();
            Set<String> titleSet = new HashSet<String>();
            Map<String, Set<String>> nameRevMap = new HashMap<String, Set<String>>();
            Map<String, HashMap<String, String>> detailsMap = new HashMap<String, HashMap<String, String>>();
            Set<String> itemDetailsListSet = new HashSet<>();

            JSONObject jsonobject = ftsSearchUtility.ftsSearchBody(searchList, resultNumber, searchType);
            LOGGER.info("Search Request Body : " + jsonobject.toString());
            String resp = service.callPostService(url, jsonobject.toString());

            LOGGER.info("Search Response " + resp);
            JSONObject object = new JSONObject(resp);
            JSONObject info = object.getJSONObject("infos");
            String nresult = info.get("nresults").toString();
            LOGGER.info("Total Result : " + nresult);
            if (Integer.parseInt(nresult) == 0) {
                for (ItemSearchParamBean searchStrng : searchList) {
                    results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), "Not Found"));
                }
            } else {

                JSONArray result = object.getJSONArray("results");
                List<HashMap<String, String>> member = new ArrayList<>();
                result.forEach(item -> {
                    JSONObject attr = (JSONObject) item;
                    JSONArray attrValue = attr.getJSONArray("attributes");
                    HashMap<String, String> attrMap = new HashMap<>();
                    attrValue.forEach(attrItem -> {
                        JSONObject attrParseValue = (JSONObject) attrItem;
                        if (attrParseValue.getString("name").endsWith("label")) {
                            attrMap.put("title", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("identifier")) {
                            attrMap.put("name", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("type")) {
                            attrMap.put("type", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("status")) {
                            String statevalue = attrParseValue.getString("value").split("\\.")[1];
                            attrMap.put("state", statevalue);
                        }
                        if (attrParseValue.getString("name").endsWith("organizationResponsible")) {
                            attrMap.put("organization", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("revision")) {
                            attrMap.put("revision", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("project")) {
                            attrMap.put("collabspace", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("TD_TechnicalDesignation.TD_TechnicalDesignation")) {
                            attrMap.put("technicalDesignation", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMPDM.MBOM_PDM_Drawing_Number")) {
                            attrMap.put("pdmDrawingNumber", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMPDM.MBOM_PDM_Owner_Group")) {
                            attrMap.put("pdmOwner", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMPDM.MBOM_PDM_State")) {
                            attrMap.put("pdmState", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMERP.MBOM_Release_Purpose")) {
                            attrMap.put("releasePurpose", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("DELFmiFunctionPPRCreateReference.V_EstimatedWeight")) {
                            attrMap.put("weight", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("TRS_TermID.TRS_TermID")) {
                            attrMap.put("trs_termId", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMERP.MBOM_TransferredtoERP")) {
                            attrMap.put("transferredToERP", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMReference.MBOM_Type")) {
                            attrMap.put("reference", attrParseValue.getString("value"));
                        }

                        if (attrParseValue.getString("name").endsWith("ITMTEXTS_ItemCommonText.ITMTEXTS_ItemCommonText")) {
                            attrMap.put("itemCommonText", attrParseValue.getString("value"));
                        }
                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMPDM.MBOM_Mastership")) {
                            attrMap.put("mastership", attrParseValue.getString("value"));
                        }
                        member.add(attrMap);
                    });

                });

                member.forEach((item) -> {
                    nameSet.add(item.get("name"));
                    titleSet.add(item.get("title"));
                    Set<String> revList = new HashSet<String>();
                    if (nameRevMap.containsKey(item.get("name"))) {
                        revList = nameRevMap.get(item.get("name"));
                        revList.add(item.get("name") + item.get("title") + item.get("revision"));
                    } else {
                        revList.add(item.get("name") + item.get("title") + item.get("revision"));
                        nameRevMap.put(item.get("name"), revList);

                    }
                    HashMap<String, String> itemDetails = new HashMap<String, String>();
                    itemDetails.put("type", item.get("type"));
                    itemDetails.put("name", item.get("name"));
                    itemDetails.put("revision", item.get("revision"));
                    itemDetails.put("state", item.get("state"));
                    itemDetails.put("title", item.get("title"));
                    itemDetails.put("collabspace", item.get("collabspace"));
                    itemDetails.put("organization", item.get("organization"));

                    itemDetails.put("technicalDesignation", item.get("technicalDesignation"));
                    itemDetails.put("pdmDrawingNumber", item.get("pdmDrawingNumber"));
                    itemDetails.put("pdmOwner", item.get("pdmOwner"));
                    itemDetails.put("pdmState", item.get("pdmState"));
                    itemDetails.put("releasePurpose", item.get("releasePurpose"));
                    itemDetails.put("weight", item.get("weight"));
                    itemDetails.put("transferredToERP", item.get("transferredToERP"));
                    itemDetails.put("reference", item.get("reference"));
                    itemDetails.put("itemCommonText", item.get("itemCommonText"));
                    itemDetails.put("trs_termId", item.get("trs_termId"));
                    itemDetails.put("mastership", item.get("mastership"));

                    if (detailsMap.containsKey(item.get("name") + "##" + item.get("name") + item.get("title") + item.get("revision"))) {

                    } else {
                        detailsMap.put(item.get("name") + "##" + item.get("name") + item.get("title") + item.get("revision"), itemDetails);
                    }

                });
                Set<HashMap<String, String>> itemDetailsList = new HashSet<HashMap<String, String>>();
                Boolean foundInName = false;
                for (ItemSearchParamBean searchStrng : searchList) {
                    // member.forEach((item) -> {
                    if (searchStrng.getSearchStr().contains("*")) {
                        if (searchStrng.getSearchStr().startsWith("*")) {
                            String searchStrPart = searchStrng.getSearchStr().replace("*", "").toUpperCase();
                            for (String name : nameSet) {
                                if (name.toUpperCase().endsWith(searchStrPart)) {
                                    for (Entry<String, Set<String>> map : nameRevMap.entrySet()) {
                                        if (map.getKey().toUpperCase().endsWith(searchStrPart)) {
                                            Set<String> revList = map.getValue();
                                            revList.forEach((rev) -> {
                                                for (Entry<String, HashMap<String, String>> map2 : detailsMap.entrySet()) {
                                                    if (map2.getKey().endsWith("##" + rev)) {
                                                        HashMap<String, String> detMap = map2.getValue();
                                                        itemDetailsList.add(detMap);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                        itemDetailsListSet.add(searchStrng.getSearchStr());
                                        List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                        results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                        itemDetailsList.clear();
                                        foundInName = true;
                                        break;
                                    }
                                }
                            }
                            if (!foundInName) {
                                for (String title : titleSet) {
                                    if (title.toUpperCase().endsWith(searchStrPart)) {
                                        detailsMap.forEach((key, val) -> {
                                            if (key.contains(searchStrPart)) {
                                                HashMap<String, String> detMap = val;
                                                itemDetailsList.add(detMap);
                                            }
                                        });

                                        if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                            itemDetailsListSet.add(searchStrng.getSearchStr());
                                            List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                            results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                            itemDetailsList.clear();
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (searchStrng.getSearchStr().endsWith("*")) {
                                String searchStrPart = searchStrng.getSearchStr().replace("*", "").toUpperCase();
                                for (String name : nameSet) {
                                    if (name.toUpperCase().startsWith(searchStrPart)) {
                                        for (Entry<String, Set<String>> map : nameRevMap.entrySet()) {
                                            if (map.getKey().toUpperCase().startsWith(searchStrPart)) {
                                                Set<String> revList = map.getValue();
                                                revList.forEach((rev) -> {
                                                    for (Entry<String, HashMap<String, String>> map2 : detailsMap.entrySet()) {
                                                        if (map2.getKey().toUpperCase().startsWith(searchStrPart)
                                                                && map2.getKey().endsWith("##" + rev)) {
                                                            HashMap<String, String> detMap = map2.getValue();
                                                            itemDetailsList.add(detMap);
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                            itemDetailsListSet.add(searchStrng.getSearchStr());
                                            List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                            results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                            itemDetailsList.clear();
                                            foundInName = true;
                                            break;
                                        }
                                    }
                                }
                                if (!foundInName) {
                                    for (String title : titleSet) {
                                        if (title.toUpperCase().startsWith(searchStrPart)) {
                                            detailsMap.forEach((key, val) -> {
                                                if (key.contains(searchStrPart)) {
                                                    HashMap<String, String> detMap = val;
                                                    itemDetailsList.add(detMap);
                                                }
                                            });

                                            if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                                itemDetailsListSet.add(searchStrng.getSearchStr());
                                                List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                                results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                                itemDetailsList.clear();
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                String[] splittedStr = searchStrng.getSearchStr().split("\\*");
                                String firstPart = "", lastPart = "";
                                if (splittedStr.length == 2) {
                                    firstPart = splittedStr[0].toUpperCase();
                                    lastPart = splittedStr[1].toUpperCase();
                                }
                                for (String name : nameSet) {
                                    if (name.toUpperCase().startsWith(firstPart) && name.toUpperCase().endsWith(lastPart)) {
                                        for (Entry<String, Set<String>> map : nameRevMap.entrySet()) {
                                            if (map.getKey().toUpperCase().startsWith(firstPart) && map.getKey().toUpperCase().endsWith(lastPart)) {
                                                Set<String> revList = map.getValue();
                                                for (String rev : revList) {
                                                    for (Entry<String, HashMap<String, String>> map2 : detailsMap.entrySet()) {
                                                        if (map2.getKey().toUpperCase().startsWith(firstPart)
                                                                && map2.getKey().endsWith("##" + rev)) {
                                                            HashMap<String, String> detMap = map2.getValue();
                                                            itemDetailsList.add(detMap);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                            itemDetailsListSet.add(searchStrng.getSearchStr());
                                            List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                            results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                            itemDetailsList.clear();
                                            foundInName = true;
                                            break;
                                        }
                                    }
                                }

                                if (!foundInName) {
                                    for (String title : titleSet) {
                                        if (title.toUpperCase().startsWith(firstPart) && title.toUpperCase().endsWith(lastPart)) {
                                            for (Entry<String, HashMap<String, String>> entry : detailsMap.entrySet()) {
                                                if (entry.getKey().contains(firstPart) && entry.getKey().contains(lastPart)) {
                                                    HashMap<String, String> detMap = entry.getValue();
                                                    itemDetailsList.add(detMap);
                                                }
                                            }

                                            if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                                itemDetailsListSet.add(searchStrng.getSearchStr());
                                                List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                                results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                                itemDetailsList.clear();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (itemDetailsListSet.isEmpty()) {
                            results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), "Not Found"));
                        }
                    } else {
                        if (nameSet.contains(searchStrng.getSearchStr())) {
                            if (nameRevMap.containsKey(searchStrng.getSearchStr())) {
                                Set<String> revList = nameRevMap.get(searchStrng.getSearchStr());
                                revList.forEach((rev) -> {
                                    if (detailsMap.containsKey(searchStrng.getSearchStr() + "##" + rev)) {
                                        HashMap<String, String> detMap = detailsMap.get(searchStrng.getSearchStr() + "##" + rev);
                                        itemDetailsList.add(detMap);
                                    }
                                });
                                if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                    itemDetailsListSet.add(searchStrng.getSearchStr());
                                    List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                    results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                    itemDetailsList.clear();
                                }
                            }
                        } else if (titleSet.contains(searchStrng.getSearchStr())) {
                            detailsMap.forEach((key, val) -> {
                                if (key.contains(searchStrng.getSearchStr())) {
                                    HashMap<String, String> detMap = val;
                                    itemDetailsList.add(detMap);
                                }
                            });
                            if (!itemDetailsListSet.contains(searchStrng.getSearchStr())) {
                                itemDetailsListSet.add(searchStrng.getSearchStr());
                                List<HashMap<String, String>> targetList = new ArrayList<>(itemDetailsList);
                                results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), targetList));
                                itemDetailsList.clear();
                            }
                            } else {
                                results.add(new ItemSearchDetailsResponseBean(searchStrng.getSearchStr(), "Not Found"));
                            }
                    }
                }
            }
            //   }

            return results;
        } catch (JsonParseException e) {
            throw new Exception("Search error: Unexpected response");
        } catch (RuntimeException e) {
            throw new Exception("Search error: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
