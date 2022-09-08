package com.bjit.common.rest.app.service.comosData.xmlPreparation.controller.equipment;

public class ComosItemNotFoundException extends RuntimeException {
    public ComosItemNotFoundException(Exception exp) {
        super(exp);
    }
    public ComosItemNotFoundException(String exp) {
        super(exp);
    }
}
