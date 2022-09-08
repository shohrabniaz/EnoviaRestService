/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public interface IJSON {

    String addAProperty(Object object, String propertyName, String propertyValue);

    /**
     *
     * @param jsonSerializedString json object string
     * @param classType te be converted into the class
     * @param <T> generic type
     * @return converted object
     */
    <T> T deserialize(String jsonSerializedString, Class<T> classType);

    /**
     *
     * @param stringBusinessData a json string
     * @param key kew of the key value pair of the string
     * @return only the value of the key
     * @throws JsonSyntaxException
     */
    String getValueFromJsonKey(String stringBusinessData, String key) throws JsonSyntaxException;

    /**
     *
     * @param object single object
     * @param <T> generic type
     * @return a json string
     */
    <T> String serialize(T object);

    /**
     *
     * @param object single object
     * @param <T> generic type
     * @param newProperties
     * @return a json string
     */
    /*public <T> String serialize(T object, HashMap<String, String> newProperties) {
    try {
    //return gson.toJson(object);
    JsonElement toJsonTree = gson.toJsonTree(object);
    newProperties.forEach((key, value) -> {
    toJsonTree.getAsJsonObject().addProperty(key, value);
    });
    return gson.toJson(toJsonTree);
    } catch (Exception exp) {
    exp.printStackTrace(System.out);
    throw exp;
    }
    }*/
    /**
     *
     * @param object single object
     * @param <T> generic type
     * @param newProperties
     * @return a json string
     */
    <T> String serialize(T object, HashMap<String, Object> newProperties);

    /**
     *
     * @param object list of objects
     * @param <T> generic list
     * @return json string
     */
    <T> String serialize(List<T> object);

    <T> String serializeObjectList(T object, String propertyName, List<Object> newProperties);
    
}
