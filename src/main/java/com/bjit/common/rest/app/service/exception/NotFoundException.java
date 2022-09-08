/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.exception;

/**
 *
 * @author BJIT
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
