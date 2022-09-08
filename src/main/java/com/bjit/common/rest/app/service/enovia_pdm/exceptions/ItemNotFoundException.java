package com.bjit.common.rest.app.service.enovia_pdm.exceptions;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ItemNotFoundException extends RuntimeException {
    String message;

    public ItemNotFoundException() {

    }

    public ItemNotFoundException(String message) {
        super(message);
        this.message = message;

    }

    public ItemNotFoundException(Exception ex) {
        super(ex);

    }
}
