/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.cache;

import com.bjit.ex.integration.transfer.util.ApplicationProperties;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Sajjad
 */
@Controller
@RequestMapping(path = "/reload-cache")
public class ReloadCacheController {

    private static final org.apache.log4j.Logger RELOAD_CACHE_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(ReloadCacheController.class);
    
    @GetMapping("/LN/property-configuration")
    public String reloadLNPropertyConfigCache(HttpServletRequest request, Map<String, Object> model) {
        RELOAD_CACHE_CONTROLLER_LOGGER.info("LN Property Files Reload Controller!!");
        boolean isSuccessful = false;
        isSuccessful = ApplicationProperties.reloadProperty();
        if (isSuccessful) {
            RELOAD_CACHE_CONTROLLER_LOGGER.info("LN Property Files Reloaded!!");
            model.put("component", "LN");
            model.put("cacheType", "property-configuration");
        } else {
            RELOAD_CACHE_CONTROLLER_LOGGER.info("LN Property Files Reload Failed!!");
            model.put("component", "LN");
            model.put("cacheType", "False");
        }
        return "reloadCache";
    }   
}
