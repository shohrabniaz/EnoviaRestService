/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.configurations;

import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author BJIT
 */
public class AppConfig {

    @Bean
    public BusinessObjectOperations businessObjectOperations() {
        return new BusinessObjectOperations();
    }

    @Bean
    public IResponse responseBuilder() {
        return new CustomResponseBuilder();
    }
}
