/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.notifier.utilities;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ObjectUtility {

    public static Boolean isNullOrEmpty(String value) {
        return isNull(value) || value.equalsIgnoreCase("") || value.isEmpty();
    }

    public static Boolean isNullOrEmpty(HashMap<String, String> checkMap) {
        return isNull(checkMap) || checkMap.isEmpty();
    }

    public static Boolean isNullOrEmpty(List<String> checkList) {
        return isNull(checkList) || checkList.isEmpty();
    }

    public static Boolean isNullOrEmpty(String[] checkArray) {
        return isNull(checkArray) || checkArray.length == 0;
    }

    public static Boolean isNull(Object checkObject) {
        return checkObject == null;
    }

    public static String capitalizeFirstLetter(String original) {
        if (!isNullOrEmpty(original)) {
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }
        return original;
    }
}
