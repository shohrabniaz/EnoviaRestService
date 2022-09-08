package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("SubtasksStructureException")
@Getter
@Setter
public class SubtasksStructureException extends RuntimeException {

    private String exceptionMassage;
    SubtasksRequestData requestData;

    public SubtasksStructureException() {
        super();
    }

    public SubtasksStructureException(String message) {
        super(message);
        exceptionMassage=message;
    }

    public SubtasksStructureException(String message, SubtasksRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
