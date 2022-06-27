/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.comosData.xmlPreparation.model.validators;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Omour Faruq
 */
@Component
@RequestScope
@Qualifier("RFLVPMValidator")
public class RFLVPMValidator {


    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> concurrentHashMap = new ConcurrentHashMap<>();
        return t -> {
            Object apply = keyExtractor.apply(t);
            return concurrentHashMap.putIfAbsent(apply, Boolean.TRUE) == null;
        };
    }
    HashMap<String, HashMap<String, Long>> uniqueChildItemMap = new HashMap<>();

    public HashMap<String, HashMap<String, Long>> getUniqueChildItemMap() {
        return uniqueChildItemMap;
    }
}
