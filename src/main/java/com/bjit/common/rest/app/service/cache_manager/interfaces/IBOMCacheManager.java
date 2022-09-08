/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.cache_manager.interfaces;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportMapping;

/**
 *
 * @author BJIT
 */
public interface IBOMCacheManager {
//    void setDetails(String environment, String configurationName);
    ItemImportMapping getMapper(String typeName) throws Exception;
    ItemImportMapping getMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType) throws Exception;
}
