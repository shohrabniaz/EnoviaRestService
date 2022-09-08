package com.bjit.common.rest.app.service.comosData.advices;

import com.bjit.common.rest.app.service.comosData.exceptions.ProjectStructureException;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.ResponseModel;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ProjectStructureControllerExceptionAdvice {

    @Autowired
    IResponse responseBuilder;

    @Autowired
    BeanFactory beanFactory;

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ProjectStructureException.class)
    public String AssemblyStructureExceptionHandle(ProjectStructureException exp) {
        ResponseModel responseModel = beanFactory.getBean(ResponseModel.class);

        List<String> errors = new ArrayList<>();
        System.out.println(exp.getExceptionMassage());
        errors.add(exp.getExceptionMassage());

        responseModel.setRequestData(exp.getRequestData());
        responseModel.setErrorMassage(exp.getExceptionMassage());
        String errorResponse = responseBuilder.setData(responseModel).setStatus(Status.FAILED).buildResponse();

        return errorResponse;
    }

}
