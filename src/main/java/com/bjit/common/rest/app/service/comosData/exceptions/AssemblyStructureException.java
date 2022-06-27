package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.AssemblyRequestData;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("AssemblyStructureException")

@Getter
@Setter
public class AssemblyStructureException extends RuntimeException {

    private String exceptionMassage;
    AssemblyRequestData requestData;

    public AssemblyStructureException() {
        super();
    }

    public AssemblyStructureException(String message) {
        super(message);
    }

    public AssemblyStructureException(String message, AssemblyRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
