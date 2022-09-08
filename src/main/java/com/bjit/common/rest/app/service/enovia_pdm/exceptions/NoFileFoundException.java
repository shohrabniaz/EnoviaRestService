package com.bjit.common.rest.app.service.enovia_pdm.exceptions;

import lombok.*;

@Data
public class NoFileFoundException extends RuntimeException {

    String fileDirectory;
    String message;

    public NoFileFoundException() {
    }

    public NoFileFoundException(String fileDirectory, String message) {
        super(message);
        this.fileDirectory = fileDirectory;
        this.message = message;
    }

    public NoFileFoundException(String message) {
        super(message);
        this.message = message;
    }
}
