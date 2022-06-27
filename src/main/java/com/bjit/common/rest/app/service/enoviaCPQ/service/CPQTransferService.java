/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.service;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author BJIT
 */
public interface CPQTransferService {

    Map<String, ResponseEntity> bomExportjson(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse);

    String cpqTransfer(ResponseEntity bomExportjson);
}
