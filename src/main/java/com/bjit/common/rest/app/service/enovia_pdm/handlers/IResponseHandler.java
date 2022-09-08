/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.handlers;

/**
 *
 * @author BJIT
 */
public interface IResponseHandler<T, K> {
    T handle(K pdmResponse) throws Exception;
}
