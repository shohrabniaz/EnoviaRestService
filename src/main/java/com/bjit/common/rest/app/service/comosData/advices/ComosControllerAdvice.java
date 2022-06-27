/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.advices;

import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author BJIT
 */
@RestControllerAdvice
public class ComosControllerAdvice {
    
    @Autowired
    IResponse responseBuilder;

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exp) {
        Map<String, List<String>> errors = new HashMap<>();

        System.out.println(exp.getBindingResult().getFieldErrors().size());

        exp.getBindingResult().getFieldErrors().forEach(error ->{
            String errorField = error.getField();
            if(!errors.containsKey(errorField)){
                List<String> errorList = new ArrayList<>();
                errors.put(errorField, errorList);
            }
            errors.get(errorField).add(error.getDefaultMessage());
        });

        
        String errorResponse = responseBuilder.addErrorMessage(errors).setStatus(Status.FAILED).buildResponse();
        
        return errorResponse;
    }

}