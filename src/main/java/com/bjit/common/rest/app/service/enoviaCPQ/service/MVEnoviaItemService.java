/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.service;

import com.bjit.common.rest.app.service.model.mvEnoviaItem.ItemInfo;
import com.bjit.ex.integration.model.webservice.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author BJIT
 */
public interface MVEnoviaItemService {

    public Map<String, List<String>> getModelByItem(Context context, ItemInfo item, String match);

    public Map<String, List<String>> getModelVersionByItem(Context context, ItemInfo item, String match);

    public Map<String, List<String>> getModelByParentPhysicalId(Context context, String parentPhysicalId);

}
