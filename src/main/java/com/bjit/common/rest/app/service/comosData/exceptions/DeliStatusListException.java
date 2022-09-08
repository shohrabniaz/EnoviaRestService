package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.SubtasksRequestData;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.deliStatusList.DeliStatusListRequestData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("SubtasksStructureException")
@Getter
@Setter
public class DeliStatusListException extends RuntimeException {

    private String exceptionMassage;
    DeliStatusListRequestData requestData;

    public DeliStatusListException() {
        super();
    }

    public DeliStatusListException(String message) {
        super(message);
        exceptionMassage=message;
    }

    public DeliStatusListException(String message, DeliStatusListRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
