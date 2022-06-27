/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.consumer.ntlmAuth;

/**
 *
 * @author BJIT
 * @param <T>
 */
public interface IComosData<T> {
    String getComosData(T comosRequestData);
}
