/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search;

import com.bjit.common.rest.app.service.model.common.ItemSearchDetailsResponseBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchParamBean;
import com.bjit.common.rest.app.service.model.common.ItemSearchResponseBean;
import java.util.List;

/**
 *
 * @author  BJIT
 */
public interface FtsSearchService {

    /**
     *
     * @param searchList
     * @return
     * @throws Exception
     */
 
    
    public List<ItemSearchDetailsResponseBean> searchDetails(List<ItemSearchParamBean> searchList) throws Exception;
}
