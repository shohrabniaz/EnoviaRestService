/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapproject.adapters;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It receives an object as an argument and creates a map with key of all the
 * methods name (public or private or protected) and as a value it also assigns
 * the name of the methods.
 *
 * @author BJIT / Md.Omour Faruq
 */
public class ConvertAClassWithMethodsNameToAMap {

    static final Logger CONVERT_A_CLASS_WITH_METHOD_NAME_TO_A_MAP_LOGGER = Logger.getLogger(ConvertAClassWithMethodsNameToAMap.class.getName());

    public <T> HashMap<String, Class<?>[]> createMap(T object) {
        try {
            CONVERT_A_CLASS_WITH_METHOD_NAME_TO_A_MAP_LOGGER.log(Level.INFO, "Initializing Java Reflection API to get all the method names from the {0} class");
            Method[] methods = object.getClass().getDeclaredMethods();
            HashMap<String, Class<?>[]> objectsMethodsNames = new HashMap<>();

            for (Method method : methods) {
                objectsMethodsNames.put(method.getName(), method.getParameterTypes());
            }
            CONVERT_A_CLASS_WITH_METHOD_NAME_TO_A_MAP_LOGGER.log(Level.INFO, "By the method names of the given class a map has been created successfully");
            return objectsMethodsNames;
        } catch (SecurityException exp) {
            CONVERT_A_CLASS_WITH_METHOD_NAME_TO_A_MAP_LOGGER.log(Level.SEVERE, "By the method names of the given class a map creation process has been failed. {0}", exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
    }
}
