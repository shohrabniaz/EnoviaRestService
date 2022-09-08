/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.processors;

import com.bjit.common.rest.app.service.mail.mapper.models.MailTemplateMapper;
import com.bjit.common.rest.app.service.utilities.FileUtilities;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.common.rest.app.service.mail.mapper.interfaces.IMailMapCacheManager;

/**
 *
 * @author BJIT
 */
public class MapperCacheMgr implements IMailMapCacheManager {
    MapperBuilder<MailTemplateMapper> mapperBuilder;

    public MapperCacheMgr() {
        mapperBuilder = new MapperBuilder<>();
    }
    

    @Override
    public MailTemplateMapper getMapper() throws Exception {
        return getMapper(PropertyReader.getProperty("mail.template.xml.map"), MailTemplateMapper.class);
    }

    @Override
    public MailTemplateMapper getMapper(String mapsAbsoluteDirectory, Class<MailTemplateMapper> classType) throws Exception {
        String fileNameFromPath = FileUtilities.getFileNameFromPath(mapsAbsoluteDirectory);

        MailTemplateMapper xmlMapper;

        if (Boolean.parseBoolean(PropertyReader.getProperty("mail.template.singleton.instance"))) {
            if (NullOrEmptyChecker.isNull(CacheKeeper.getMapper(fileNameFromPath))) {
                MailTemplateMapper mapper = (MailTemplateMapper) mapperBuilder.getData(mapsAbsoluteDirectory, MailTemplateMapper.class);
                CacheKeeper.setMapper(fileNameFromPath, mapper);
            }
            xmlMapper = CacheKeeper.getMapper(fileNameFromPath);
        } else {
            xmlMapper = (MailTemplateMapper) mapperBuilder.getData(mapsAbsoluteDirectory, MailTemplateMapper.class);
        }
        return xmlMapper;
    }
}
