/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.cache_manager;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportMapping;
import com.bjit.ex.integration.transfer.util.NullOrEmptyChecker;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class CacheKeeper {

    private static HashMap<String, ItemImportMapping> itemsMapCacheKeeper;

    static {
        if (NullOrEmptyChecker.isNullOrEmpty(itemsMapCacheKeeper)) {
            synchronized (CacheKeeper.class) {
                itemsMapCacheKeeper = new HashMap<>();
            }
        }
    }
    
    public static void setMapper(String fileNameFromPath, ItemImportMapping mapper){
        itemsMapCacheKeeper.put(fileNameFromPath, mapper);
    }
    
    public static ItemImportMapping getMapper(String fileNameFromPath){
        return itemsMapCacheKeeper.get(fileNameFromPath);
    }
}
