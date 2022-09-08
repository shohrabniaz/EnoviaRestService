/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.processors;

import com.bjit.common.rest.app.service.mail.mapper.models.MailTemplateMapper;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;

import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class CacheKeeper {

    private static HashMap<String, MailTemplateMapper> mailsMapCacheKeeper;

    static {
        if (NullOrEmptyChecker.isNullOrEmpty(mailsMapCacheKeeper)) {
            synchronized (CacheKeeper.class) {
                mailsMapCacheKeeper = new HashMap<>();
            }
        }
    }
    
    public static void setMapper(String fileNameFromPath, MailTemplateMapper mapper){
        mailsMapCacheKeeper.put(fileNameFromPath, mapper);
    }
    
    public static MailTemplateMapper getMapper(String fileNameFromPath){
        return mailsMapCacheKeeper.get(fileNameFromPath);
    }
}
