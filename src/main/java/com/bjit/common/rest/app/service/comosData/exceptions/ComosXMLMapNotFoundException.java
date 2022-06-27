/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.exceptions;

/**
 *
 * @author Omour Faruq
 */
public class ComosXMLMapNotFoundException extends NullPointerException {

    public ComosXMLMapNotFoundException() {
        super();
    }

    public ComosXMLMapNotFoundException(String message) {
        super(message);
    }

    public ComosXMLMapNotFoundException(NullPointerException exp) {
        super(exp.getMessage());
    }
}
