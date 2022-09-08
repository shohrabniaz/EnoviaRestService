/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.cache_manager.interfaces.IBOMCacheManager;
import com.bjit.mapper.mapproject.builder.MapperBuilder;

import com.bjit.common.rest.app.service.cache_manager.CacheKeeper;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.enovia_pdm.models.xml_mapping_model.ItemImportMapping;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.CommonUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
@Scope(value = "prototype")
public class BOMMapperCacheMgr implements IBOMCacheManager {

    @Override
    public ItemImportMapping getMapper(String typeName) throws Exception {

        return getMapper("/mapper_files/EnoviaPDM/bom/PDM" + typeName + "Map.xml", ItemImportMapping.class);

    }

    @Override
    public ItemImportMapping getMapper(String mapsAbsoluteDirectory, Class<ItemImportMapping> classType) throws Exception {
        String fileNameFromPath = CommonUtil.getFileNameFromPath(mapsAbsoluteDirectory);

        ItemImportMapping bomMapper;
        if (Boolean.parseBoolean(PropertyReader.getProperty("odi.item.map.singleton.instance"))) {
            if (NullOrEmptyChecker.isNull(CacheKeeper.getMapper(fileNameFromPath))) {
                CacheKeeper.setMapper(fileNameFromPath, (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory));
            }
            bomMapper = CacheKeeper.getMapper(fileNameFromPath);
        } else {
            bomMapper = (ItemImportMapping) new MapperBuilder().getMapper(MapperBuilder.XML, classType, mapsAbsoluteDirectory);
        }
        return bomMapper;
    }
}
