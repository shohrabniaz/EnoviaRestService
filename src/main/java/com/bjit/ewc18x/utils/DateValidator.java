/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import com.bjit.common.rest.app.service.export.product.ProductExportServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Suvonkar Kundu
 */
public class DateValidator {

    String finalJson;
    ObjectMapper mapper = new ObjectMapper();
    EwcUtilities ewcUtilities = new EwcUtilities();
    private static final Logger LOGGER = Logger.getLogger(DateValidator.class);
    private final String dateValidationMessage = "Invalid Date Format. Used these Date Format: (mm/dd/yyyy HH:mm:ss a) or (mm/dd/yyyy)";
    private final String objectKey = "Results";

    public boolean isDateTimeStampValid(String inputString) {
        final List<String> dateFormats = Arrays.asList("mm/dd/yyyy HH:mm:ss a", "mm/dd/yyyy");

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.parse(inputString);
                return true;
            } catch (ParseException ex) {
                LOGGER.error("Exception: " + ex);
            }
        }
        return false;
    }

    public String validationResponse() {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        JSONObject finalObject = new JSONObject();
        JSONArray mainJsonArray = new JSONArray();
        mainJsonArray.put(dateValidationMessage);
        finalObject.put(objectKey, ewcUtilities.jsonArrayToString(mainJsonArray));
        finalJson = ewcUtilities.jsonObjectToString(finalObject);
        String finalObjectStr = ewcUtilities.replaceLast(finalJson, "\"", "");
        finalObjectStr = finalObjectStr.substring(0, 11) + finalObjectStr.substring(12);
        finalJson = finalObjectStr.replaceAll("\":null", "\":\"\"");
        Object jsonValue = null;
        try {
            jsonValue = mapper.readValue(finalJson, Object.class);
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonValue);
        } catch (IOException ex) {
            LOGGER.error("Exception: " + ex);
        }
        return json;
    }

    public boolean isValid(String date) {
        if ((date == null || date == "")) {
            return true;
        } else {
            return isDateTimeStampValid(date);
        }
    }

    public boolean isValid(String startDate, String endDate) {
        if (isValid(startDate) && isValid(endDate)) {
            return true;
        } else {
            return false;
        }
    }

}
