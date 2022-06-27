package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.EquipmentRequestData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("EquipmentStructureException")

@Getter
@Setter
public class EquipmentStructureException extends RuntimeException {

    private String exceptionMassage;
    EquipmentRequestData requestData;

    public EquipmentStructureException() {
        super();
    }

    public EquipmentStructureException(String message) {
        super(message);
    }

    public EquipmentStructureException(String message, EquipmentRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
