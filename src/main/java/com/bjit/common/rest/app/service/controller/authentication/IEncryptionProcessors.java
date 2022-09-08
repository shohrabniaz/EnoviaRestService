/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication;

/**
 *
 * @author BJIT
 */
public interface IEncryptionProcessors {
    String encrypt(String plainData) throws Exception;
    String decrypt(String encryptedData) throws Exception;
    void setSalt() throws Exception;
    void setSalt(String salt) throws Exception;
    void setSalt(boolean isFixed) throws Exception;
}
