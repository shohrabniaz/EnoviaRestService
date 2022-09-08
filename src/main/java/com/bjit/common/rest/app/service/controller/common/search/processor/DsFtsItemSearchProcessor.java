package com.bjit.common.rest.app.service.controller.common.search.processor;

import com.bjit.common.rest.app.service.controller.common.search.validator.CommonSearchValidator;
import com.bjit.common.rest.app.service.model.common.ItemSearchDetailsResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchErrorBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchRequestBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import com.bjit.common.rest.app.service.search.DsFtsSearchService;
import com.bjit.common.rest.app.service.search.FtsSearchService;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import matrix.db.Context;
import org.apache.log4j.Logger;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class DsFtsItemSearchProcessor {
    private static final Logger DS_FTS_SEARCH_PROCESSOR = Logger.getLogger(DsFtsItemSearchProcessor.class);
    
    public List<ItemSearchResponseBean> searchItemsByName(Context context, ItemSearchRequestBean searchRequest, DsFtsSearchService searchService, List<ItemSearchErrorBean> errors) throws Exception {
        try {
            CommonSearchValidator.validateExternalRequester(searchRequest);
            CommonSearchValidator.validateParams(searchRequest);

            List<ItemSearchResponseBean> results = new ArrayList<>();
            try {
                Set<ItemSearchResponseBean> searchResult = searchService.search(searchRequest.getParams());
                results.addAll(searchResult);
            } 
            catch(Exception ex) {
                DS_FTS_SEARCH_PROCESSOR.error(" Search Error: " + ex.getMessage());
                throw ex;
            }
            return results;
        } catch(Exception e) {
            DS_FTS_SEARCH_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }
    
    public List<ItemSearchDetailsResponseBean> getItemDetailsByName(Context context, ItemSearchRequestBean searchRequest, DsFtsSearchService searchService, List<ItemSearchErrorBean> errors) throws Exception {
        try {
            CommonSearchValidator.validateExternalRequester(searchRequest);
            CommonSearchValidator.validateParams(searchRequest);

            List<ItemSearchDetailsResponseBean> results = new ArrayList<>();
            try {
                List<ItemSearchDetailsResponseBean> searchResult = searchService.searchDetails(searchRequest.getParams());
                results.addAll(searchResult);
            } 
            catch(Exception ex) {
                DS_FTS_SEARCH_PROCESSOR.error(" Search Error: " + ex.getMessage());
                throw ex;
            }
            return results;
        } catch(Exception e) {
            DS_FTS_SEARCH_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }
    
     public List<ItemSearchDetailsResponseBean> getFTSItemDetailsByName(Context context, ItemSearchRequestBean searchRequest, FtsSearchService searchService, List<ItemSearchErrorBean> errors) throws Exception {
        try {
            CommonSearchValidator.validateExternalRequester(searchRequest);
            CommonSearchValidator.validateParams(searchRequest);

            List<ItemSearchDetailsResponseBean> results = new ArrayList<>();
            try {
                List<ItemSearchDetailsResponseBean> searchResult = searchService.searchDetails(searchRequest.getParams());
                results.addAll(searchResult);
            } 
            catch(Exception ex) {
                DS_FTS_SEARCH_PROCESSOR.error(" Search Error: " + ex.getMessage());
                throw ex;
            }
            return results;
        } catch(Exception e) {
            DS_FTS_SEARCH_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }
    
}