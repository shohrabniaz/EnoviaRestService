/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.config;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author BJIT
 */
@Configuration
public class AppConfig {
//    @Bean
//    IResponse responseBuilder() {
//        return new CustomResponseBuilder();
//    }

    @Bean
    @Qualifier("responseMessageFormatterBean")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ResponseMessageFormaterBean responseMessageFormatterBean() {
        return new ResponseMessageFormaterBean();
    }
}
