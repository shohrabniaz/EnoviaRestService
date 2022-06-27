/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.maturity;

import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author BJIT
 */
public interface MaturityChangeService {

    public Map<String, String> getLogin();

    public Map<String, String> getCSRFToken(Map<String, String> cas, String username, String password);

    public ResponseEntity getRestResponse(String uri, StringBuilder requestJsonBuilder, String securityContext, HttpServletRequest httpRequest, Map<String, String> cas);
}
