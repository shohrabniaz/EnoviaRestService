/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 *
 * @author Omour Faruq
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ServiceURLFilter> filterRegistrationBean() {
        FilterRegistrationBean<ServiceURLFilter> registrationBean = new FilterRegistrationBean();
        ServiceURLFilter urlFilter = new ServiceURLFilter();

        registrationBean.setFilter(urlFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); //set precedence
        return registrationBean;
    }
}
