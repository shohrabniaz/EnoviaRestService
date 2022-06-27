/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.utility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author BJIT
 */
public class CommonItemImportUtility {
    private static final org.apache.log4j.Logger COMMON_ITEM_IMPORT_UTILITY_LOGGER = org.apache.log4j.Logger.getLogger(CommonItemImportUtility.class);

    public String replaceResponseMessage(String responseMessage, HashMap<String, String> destinationSourceMap) {
        try {
            Set<String> destinationMapKeySet = destinationSourceMap.keySet();
            Iterator<String> destinationKeyIterator = destinationMapKeySet.iterator();

            while (destinationKeyIterator.hasNext()) {
                String sourceKey = destinationKeyIterator.next();
                if (responseMessage.contains(sourceKey)) {
                    return responseMessage.replace(sourceKey, destinationSourceMap.get(sourceKey));
                }
            }

            return responseMessage;
        } catch (Exception exp) {
            COMMON_ITEM_IMPORT_UTILITY_LOGGER.debug(exp);
            return responseMessage;
        }
    }

    public String checkErrorCodeInErrorMessage(String errorMessage) {
        String errorCode = "Error: #1900068: ";
        String errorCode2 = "Error: #1900016: ";
        String errorCode3 = "modify business object failed.";
        String errorPattern = errorCode + "|" + errorCode2 + "|" + errorCode3;

        Pattern pattern = Pattern.compile(errorPattern);
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            errorMessage = errorMessage.replace(errorCode, "");
            errorMessage = errorMessage.replace(errorCode2, "");
            errorMessage = errorMessage.replace("\n", ".");
            errorMessage = errorMessage.replace(errorCode3, "");
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1);
            return errorMessage;
        }

        return errorMessage;
    }
}
