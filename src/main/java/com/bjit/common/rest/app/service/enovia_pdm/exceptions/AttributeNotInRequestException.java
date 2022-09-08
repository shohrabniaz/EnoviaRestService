/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.enovia_pdm.exceptions;

/**
 *
 * @author Omour Faruq
 */
public class AttributeNotInRequestException extends RuntimeException {

    public AttributeNotInRequestException(Exception exp) {
        super(exp);
    }

    public AttributeNotInRequestException(String errorMessage) {
        super(errorMessage);
    }
}
