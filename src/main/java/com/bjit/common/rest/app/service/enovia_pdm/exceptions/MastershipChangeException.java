/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.exceptions;

/**
 *
 * @author BJIT
 */
public class MastershipChangeException extends RuntimeException {

    private Object errorException;
    
    public MastershipChangeException(Exception exp) {
        super(exp);
    }

    public MastershipChangeException(String errorMessage) {
        super(errorMessage);
    }

    public Object getErrorException() {
        return errorException;
    }

    public void setErrorException(Object errorException) {
        this.errorException = errorException;
    }
    
    
}
