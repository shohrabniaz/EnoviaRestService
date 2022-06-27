package com.bjit.common.rest.app.service.controller.common.search.processor;

import com.bjit.common.rest.app.service.controller.common.search.validator.CommonSearchValidator;
import com.bjit.common.rest.app.service.model.common.ItemSearchErrorBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchRequestBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import com.bjit.common.rest.app.service.search.SearchService;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class PDMItemSearchProcessor {
    private static final Logger PDM_SEARCH_PROCESSOR = Logger.getLogger(PDMItemSearchProcessor.class);
    
    public List<ItemSearchResponseBean> searchItemsByName(Context context, ItemSearchRequestBean searchRequest, SearchService searchService, List<ItemSearchErrorBean> errors) throws Exception {
        try {
            CommonSearchValidator.validateExternalRequester(searchRequest);
            CommonSearchValidator.validateParams(searchRequest);

            String type = PropertyReader.getProperty("common.search.type.default");
            String revision = "*";
            List<ItemSearchResponseBean> results = new ArrayList<>();

            searchRequest.getParams().forEach(param -> {
                if(!NullOrEmptyChecker.isNullOrEmpty(param.getName())) {
                    try {
                        CommonSearchValidator.validateName(param.getName());
                        Map<String, String> searchResult = searchService.search(context, type, param.getName(), revision);
                        String found = searchResult.get("found");
                        ItemSearchResponseBean result = new ItemSearchResponseBean(param.getName(), found);
                        if(found.equals("true")) {
                            result.addMessage("mastership", searchResult.get("mastership"));
                        }
                        results.add(result);
                    } 
                    catch(FrameworkException | RuntimeException ex) {
                        PDM_SEARCH_PROCESSOR.error(param.getName() + " Search Error : " + ex.getMessage());
//                        errors.add(new ItemSearchErrorBean(param.getName(), ex.getMessage()));
                        results.add(new ItemSearchResponseBean(param.getName(), "Not Found"));
                    }
                }
            });
            
            return results;
        } catch(Exception e) {
            PDM_SEARCH_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }
    
    public List<ItemSearchResponseBean> searchByName(Context context, ItemSearchRequestBean searchRequest, SearchService searchService, List<ItemSearchErrorBean> errors) throws Exception {
        try {
            CommonSearchValidator.validateExternalRequester(searchRequest);
            CommonSearchValidator.validateParams(searchRequest);

            String type = PropertyReader.getProperty("common.search.type.default");
            String name = "*";
            String revision = "*";
            List<ItemSearchResponseBean> results = new ArrayList<>();
            
            CommonSearchValidator.validateNameMatchList(searchRequest, results);
            
            List<String> matchList = searchRequest.getParams().stream()
                    .map(ItemSearchParamBean::getName).collect(Collectors.toList());
            
            String matchListAsString = String.join(",", matchList);
            
            HashMap<String, String> searchResultMap = searchService.search(context, type, name, revision, "name", matchListAsString, ",");
            Set<String> searchResult = searchResultMap.keySet().stream().collect(Collectors.toSet());
            
            matchList.forEach(match -> {
                ItemSearchResponseBean itemResponseBean;
                if(searchResult.contains(match)) {
                    itemResponseBean = new ItemSearchResponseBean(match, "true");
                    itemResponseBean.addMessage("mastership", searchResultMap.get(match));
                }
                else {
                    itemResponseBean = new ItemSearchResponseBean(match, "false");
                }
                results.add(itemResponseBean);
            });
            
            return results;
        } catch(Exception e) {
            PDM_SEARCH_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }
}