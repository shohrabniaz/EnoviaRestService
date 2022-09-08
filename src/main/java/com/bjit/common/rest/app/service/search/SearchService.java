/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.search;



import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface SearchService {

    public Items getItems(Context context,CommonItemSearchBean commonItemSearchBean);
    public Map<String, String> search(Context context, String type, String name, String revision) throws FrameworkException;
    public HashMap<String, String> search(Context context, String type, String name, String revision, String matchAttributeOrProperty, String matchListAsString, String matchDelimeter) throws FrameworkException;
}
