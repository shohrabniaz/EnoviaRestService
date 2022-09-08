/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author BJIT
 */
public class ComosReflectionUtilities {

    public <T, K> HashMap<String, String> getHashMapFromAnnotaionAndValue(T instantiatedObject) {
        HashMap<String, String> annotationValueMap = new HashMap<>();

        Field[] declaredFields = instantiatedObject.getClass().getDeclaredFields();
        Arrays.stream(declaredFields).forEach(eachField -> {
            eachField.setAccessible(true);
            JsonProperty annotation = eachField.getAnnotation(JsonProperty.class);
            try {
                annotationValueMap.put(annotation.value(), Optional.ofNullable(eachField.get(instantiatedObject)).orElse("").toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.out);
            }
        });

        return annotationValueMap;
    }
}
