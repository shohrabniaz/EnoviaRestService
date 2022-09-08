/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

import com.bjit.common.rest.app.service.enovia_pdm.service.IMasterShipChange;

/**
 *
 * @author BJIT
 */
public interface IParameterizedResponseHandler<T, K, L> {
    T doHandle(K mapOfPDMResponses, IMasterShipChange masterShipChangeService) throws Exception;
    T doHandle(K pdmResponse, L parameter) throws Exception;
}
