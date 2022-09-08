package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectSearchRequestData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("MilEquipmentIdSearchException")
@Getter
@Setter
public class MilEquipmentIdSearchException extends RuntimeException {

    private String exceptionMassage;
    ProjectSearchRequestData requestData;

    public MilEquipmentIdSearchException() {
        super();
    }

    public MilEquipmentIdSearchException(String message) {
        super(message);
        exceptionMassage=message;
    }

    public MilEquipmentIdSearchException(String message, ProjectSearchRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
