package com.bjit.common.rest.app.service.comosData.xmlPreparation.model;

import java.util.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ResponseMassage")
@Scope("prototype")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseModel {

    private Object requestData;
    private List<String> massage;
    private List<String> errorMessage;

    public void setResponseMassage(String massage) {
        this.massage = Optional.ofNullable(this.massage).orElse(new ArrayList<>());
        this.massage.add(massage);
    }

    public void setErrorMassage(String errorMessage) {
        this.errorMessage = Optional.ofNullable(this.errorMessage).orElse(new ArrayList<>());
        this.errorMessage.add(errorMessage);
    }
}
