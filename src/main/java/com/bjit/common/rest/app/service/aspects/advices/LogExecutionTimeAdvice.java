/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.aspects.advices;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Aspect
@Component
public class LogExecutionTimeAdvice {

    private static final Logger LOG_EXECUTION_TIME_ADVICE_LOGGER = Logger.getLogger(LogExecutionTimeAdvice.class);

    @Around("@annotation(com.bjit.common.rest.app.service.aspects.annotations.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        LOG_EXECUTION_TIME_ADVICE_LOGGER.info(className + " : " + methodName + " Executed in " + executionTime + "ms");
        return proceed;
    }
}
