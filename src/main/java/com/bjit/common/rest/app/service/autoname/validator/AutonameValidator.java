/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.autoname.validator;

import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class is used to validate the supported type for Auto Name generation.
 * It also generate the validation error message.
 * @created 2021-04-06
 * @author Sudeepta
 */
@Component
public class AutonameValidator {

    /* Logger variable */
    private static final Logger LOGGER = Logger.getLogger(AutonameValidator.class);
    
    /**
     * Validate the object type
     * 
     * @param type  object type
     * @return true if valid else false
     */
    public boolean isValidType(String type) {
        String catiaSupportedTypes = PropertyReader.getProperty("generate.auto_name.type.support.catia");
        String otherSupportedTypes = PropertyReader.getProperty("generate.auto_name.type.support.other");
        List<String> catiaSupportedTypeList = Arrays.asList(catiaSupportedTypes.split(","));
        List<String> otherSupportedTypeList = Arrays.asList(otherSupportedTypes.split(","));
        List<String> supportedTypeList = new ArrayList<>();
        supportedTypeList.addAll(catiaSupportedTypeList);
        supportedTypeList.addAll(otherSupportedTypeList);
        if (supportedTypeList.contains(type)) {
            return true;
        }
        return false;
    }

    /**
     * Validate the object count
     * @param objectCount
     * @return true if valid else false
     */
    public boolean isValidObjectCount(int objectCount) {
        int maxValue = Integer.parseInt(PropertyReader.getProperty("generate.auto_name.limit.max"));
        if (objectCount > 0 && objectCount <= maxValue) {
            return true;
        }
        return false;
    }

    /**
     * Validate object type and object count
     * 
     * @param type object type
     * @param objectCount
     * @return true if valid else false
     */
    public boolean isValid(String type, int objectCount) {
        return isValidType(type) && isValidObjectCount(objectCount);
    }

    /**
     * return the error message if validation error
     * @param type object type
     * @param objectCount
     * @return error message. if the parameter is valid then return empty string
     */
    public String getErrorMessage(String type, int objectCount) {
        String errorMessage = "";
        if (!isValidType(type)) {
            errorMessage = "This type: " + type + " is not supported or wrong.";
        }
        if (!isValidObjectCount(objectCount)) {
            errorMessage += "ObjectCount must be greater then 0 and less than " + PropertyReader.getProperty("generate.auto_name.limit.max");
        }
        return errorMessage;
    }
}
