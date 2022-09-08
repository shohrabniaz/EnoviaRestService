/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

import com.bjit.common.rest.app.service.webServiceConsumer.RequestModel;

/**
 *
 * @author BJIT
 */
public interface IRequestHandler {
    public RequestModel prepareRequest(String requestData) throws Exception;
    public String sendRequest(RequestModel serviceRequesterModel) throws Exception;
    public String sendRequest(RequestModel serviceRequesterModel, Boolean isMockResponse) throws Exception;
}
