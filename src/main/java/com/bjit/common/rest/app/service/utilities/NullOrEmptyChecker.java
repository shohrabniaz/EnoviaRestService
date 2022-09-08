/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import java.util.List;
import java.util.Map;

/**
 *
 * @author BJIT
 */
public class NullOrEmptyChecker {

    public static synchronized Boolean isNull(Object checkObject) {
        return checkObject == null;
    }

    public static synchronized Boolean isNullOrEmpty(String checkString) {
        return isNull(checkString) || checkString.equalsIgnoreCase("");
    }

    public static synchronized Boolean isNullOrEmpty(Integer checkNumber) {
        return isNull(checkNumber) || checkNumber < 1;
    }

    public static synchronized Boolean isNullOrEmpty(Map checkMap) {
        return isNull(checkMap) || checkMap.isEmpty();
    }

    public static synchronized Boolean isNullOrEmpty(List checkList) {
        return isNull(checkList) || checkList.isEmpty();
    }

    public static synchronized Boolean isNullOrEmpty(Object[] checkArray) {
        return isNull(checkArray) || checkArray.length == 0;
    }

    public static synchronized String capitalizeFirstLetter(String original) {
        if (!isNullOrEmpty(original)) {
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }
        return original;
    }

    public static synchronized <T> void clear(T object) {
        if (!isNull(object)) {
            object = null;
        }
    }

    public static synchronized <T> void clear(List object) {
        if (!isNullOrEmpty(object)) {
            object.clear();
            object = null;
        }
    }

    public static synchronized <T> void clear(Map object) {
        if (!isNullOrEmpty(object)) {
            object.clear();
            object = null;
        }
    }

    public static synchronized <T> void clear(Object[] object) {
        if (!isNullOrEmpty(object)) {
            object = null;
        }
    }
}
