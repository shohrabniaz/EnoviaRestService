package com.bjit.common.rest.app.service.comosData.exceptions;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ProjectStructureRequestData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ProjectStructureException")

@Getter
@Setter
public class ProjectStructureException extends RuntimeException {

    private String exceptionMassage;
    ProjectStructureRequestData requestData;

    public ProjectStructureException() {
        super();
    }

    public ProjectStructureException(String message) {
        super(message);
        exceptionMassage=message;
    }

    public ProjectStructureException(String message, ProjectStructureRequestData requestData) {
        super(message);
        exceptionMassage = message;
        this.requestData = requestData;
    }

}
