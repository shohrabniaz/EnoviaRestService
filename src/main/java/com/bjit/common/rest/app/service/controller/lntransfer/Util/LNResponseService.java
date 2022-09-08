/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer.Util;

import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ex.integration.model.webservice.Item;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface LNResponseService {

    public Map<String, ResponseMessageFormaterBean> ResponseList(Context context, Item item, Map<String, String> itemResultMap, Iterator<Item> iterator, List<Item> expandedBOM);

    public Map<LNResponseMessageFormater, String> finalResponseListMap(Map<String, LNResponseMessageFormater> transferResultMap);

}
