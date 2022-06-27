/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search;

import com.bjit.common.rest.app.service.controller.common.search.validator.CommonSearchValidator;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import com.bjit.common.rest.app.service.model.DSsearch.DsItemSearchResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchDetailsResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import com.bjit.common.rest.app.service.utilities.FtsSearchUtility;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
@Service
public class DsFtsSearchServiceImpl implements DsFtsSearchService {

    private static final Logger LOGGER = Logger.getLogger(DsFtsSearchServiceImpl.class);

    public DsServiceCall service;

    public Set<ItemSearchResponseBean> search(List<ItemSearchParamBean> searchList) throws Exception {

        Set<ItemSearchResponseBean> results = new HashSet<>();
        Map<String, String> itemCheck = new HashMap<>();

        FtsSearchUtility ftsSearchUtility = new FtsSearchUtility();
        String searchType = "itemUnique";
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
            //System.out.println("Auth error: "+ e);
            this.service = new DsServiceCall();
        }

        try {
            String url = PropertyReader.getProperty("ds.federated.service.fts.url");
            int resultNumber = Integer.parseInt((PropertyReader.getProperty("fts.search.service.max.item")));

            String params = "";
            //  for (ItemSearchParamBean item : searchList) {

            JSONObject jsonobject = ftsSearchUtility.ftsSearchBody(searchList, resultNumber, searchType);
            String resp = service.callPostService(url, jsonobject.toString());
            JSONObject object = new JSONObject(resp);
            JSONObject info = object.getJSONObject("infos");
            String nresult = info.get("nresults").toString();
            LOGGER.info("Total Result : " + nresult);
            if (Integer.parseInt(nresult) == 0) {
                results.add(new ItemSearchResponseBean(null, "false"));

            } else {

                JSONArray result = object.getJSONArray("results");

                result.forEach(itemValue -> {
                    JSONObject attr = (JSONObject) itemValue;
                    JSONArray attrValue = attr.getJSONArray("attributes");
                    // String resultName = "";
                    List<String> member = new ArrayList<>();
                    attrValue.forEach(attrItem -> {
                        JSONObject attrParseValue = (JSONObject) attrItem;
                        String resultName = "";

                        if (attrParseValue.getString("name").endsWith("identifier")) {
                            resultName = attrParseValue.getString("value");
                            //LOGGER.info("Total Result masterShip:22 " + masterShip);
                            if (!itemCheck.containsKey(resultName)) {
                                itemCheck.put(resultName, "true");
                                member.add(resultName);

                            }

                        }

                        if (attrParseValue.getString("name").endsWith("MBOM_MBOMPDM.MBOM_Mastership")) {

                            String masterShip = attrParseValue.get("value").toString();

                            ItemSearchResponseBean itemSearchResponseBean = new ItemSearchResponseBean();
                            itemCheck.remove(member.get(0));
                            itemCheck.put(member.get(0), "m_true");
                            itemSearchResponseBean.setName(member.get(0));
                            itemSearchResponseBean.setFound("true");
                            itemSearchResponseBean.addMessage("mastership", masterShip);
                            results.add(itemSearchResponseBean);

                        }

                    });

                });
//                List<String> values = searchList.getName().stream()
//                        //.distinct() // include this if there may be duplicate keys
//                        .filter(itemCheck::containsKey)
//                        .map(someHashMap::get)
//                        .collect(Collectors.toList());

                //      Map<String, String> collect = itemCheck.entrySet().stream().filter(x -> x.getValue() == "true").collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
                for (int i = 0; i < searchList.size(); i++) {
                    if (itemCheck.containsKey(searchList.get(i).getName())) {
                        if (itemCheck.get(searchList.get(i).getName()).equals("true")) {
                            ItemSearchResponseBean itemSearchResponseBean = new ItemSearchResponseBean();
                            itemSearchResponseBean.setName(searchList.get(i).getName());
                            itemSearchResponseBean.setFound("true");
                            itemSearchResponseBean.addMessage("mastership", "");
                            results.add(itemSearchResponseBean);
                        }
                    } else {

                        results.add(new ItemSearchResponseBean(searchList.get(i).getName(), "false"));
                    }

                }

//                for (String itm : collect.keySet()) {
//                  
//                }
            }

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

    public List<ItemSearchDetailsResponseBean> searchDetails(List<ItemSearchParamBean> searchList) throws Exception {

        List<ItemSearchDetailsResponseBean> results = new ArrayList<>();

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
            //System.out.println("Auth error: "+ e);
            this.service = new DsServiceCall();
        }

        try {
            String url = PropertyReader.getProperty("ds.service.base.url.3dspace") + PropertyReader.getProperty("ds.service.search.url");

            String params = "";
            for (ItemSearchParamBean item : searchList) {
                if (!NullOrEmptyChecker.isNullOrEmpty(item.getName())) {
                    CommonSearchValidator.validateName(item.getName());
                    if (NullOrEmptyChecker.isNullOrEmpty(params)) {
                        params = item.getName();
                    } else {
                        params = params + " OR " + item.getName();
                    }
                }
            }

            String resp = service.callService(url, params);

            ObjectMapper mapper = new ObjectMapper();
            DsItemSearchResponseBean respBean = mapper.readValue(resp, DsItemSearchResponseBean.class);
            List<HashMap<String, String>> member = respBean.getMember();

            Set<String> nameSet = new HashSet<String>();
            Map<String, List<String>> nameRevMap = new HashMap<String, List<String>>();
            Map<String, HashMap<String, String>> detailsMap = new HashMap<String, HashMap<String, String>>();

            member.forEach((item) -> {
                nameSet.add(item.get("name"));
                List<String> revList = new ArrayList<String>();
                if (nameRevMap.containsKey(item.get("name"))) {
                    revList = nameRevMap.get(item.get("name"));
                    revList.add(item.get("revision"));
                } else {
                    revList.add(item.get("revision"));
                }
                nameRevMap.put(item.get("name"), revList);

                HashMap<String, String> itemDetails = new HashMap<String, String>();
                itemDetails.put("type", item.get("type"));
                itemDetails.put("name", item.get("name"));
                itemDetails.put("revision", item.get("revision"));
                itemDetails.put("state", item.get("state"));
                itemDetails.put("title", item.get("title"));
                itemDetails.put("collabspace", item.get("collabspace"));
                itemDetails.put("organization", item.get("organization"));
                detailsMap.put(item.get("name") + "##" + item.get("revision"), itemDetails);
            });
            //System.out.println(nameRevMap);

            searchList.forEach((searchStr) -> {
                List<HashMap<String, String>> itemDetailsList = new ArrayList<HashMap<String, String>>();
                if (nameSet.contains(searchStr.getName())) {
                    if (nameRevMap.containsKey(searchStr.getName())) {
                        List<String> revList = nameRevMap.get(searchStr.getName());
                        revList.forEach((rev) -> {
                            if (detailsMap.containsKey(searchStr.getName() + "##" + rev)) {
                                HashMap<String, String> detMap = detailsMap.get(searchStr.getName() + "##" + rev);
                                itemDetailsList.add(detMap);
                            }
                        });
                        results.add(new ItemSearchDetailsResponseBean(searchStr.getName(), "true", itemDetailsList));
                    }

                } else {
                    results.add(new ItemSearchDetailsResponseBean(searchStr.getName(), "false", null));
                }
            });

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
