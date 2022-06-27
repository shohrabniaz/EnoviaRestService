/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.refItems.utilities;

import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.ewc18x.utils.EwcUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author BJIT
 */
public class RefItemExportUtil {

    private static final org.apache.log4j.Logger REF_ITEM_EXPORT_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(RefItemExportUtil.class);
    JSONArray jsonArray = new JSONArray();
    EwcUtilities ewcUtilities = new EwcUtilities();

    /**
     * Executes GET request method service..
     *
     * @param code is reference item code
     * @param url is executes service URL
     * @throws IOException
     * @return String data
     */
    public String executeService(String code, String url) throws IOException{
        HttpURLConnection connection = null;
        BufferedReader input = null;
        try {
            URL refItemURL = new URL(url + code);
            connection = (HttpURLConnection) refItemURL.openConnection();
            connection.setRequestMethod("GET");
            input = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
        } catch (Exception ex) {
            REF_ITEM_EXPORT_UTIL_LOGGER.error(ex);
        }
        return input.readLine();
    }

    /**
     * prepare JSONArray.JSONArray contain filtered JsonObject
     *
     * @param data is JSONArray data .
     * @param refKeys filtering keys of JSON object.
     * @throws IOException
     * @return JSONArray
     */
    public JSONArray filterJsonobjectsOfJsonarray(JSONArray data, String[] refKeys) throws IOException {
        JSONObject finalJsonObject = new JSONObject();
        JSONArray finalJsonArray = new JSONArray();
        if (data.length() != 0) {
            for (int index = 0; index < data.length(); index++) {
                JSONObject jsonObject = data.getJSONObject(index);
                jsonObject.keySet().forEach(keyStr -> {
                    for (int j = 0; j < refKeys.length; j++) {
                        if (refKeys[j].equalsIgnoreCase(keyStr)) {
                            finalJsonObject.put(keyStr, jsonObject.get(keyStr));
                        }
                    }
                });
                finalJsonArray.put(finalJsonObject);
            }
        }
        return finalJsonArray;
    }

    /**
     * prepare JSONArray.JSONArray contain specific code status JsonObject.
     *
     * @param jsonArray is jsonArray data.JSONArray contain filtered JsonObject.
     * @param codestatus This parameter is reference item code status.
     * @throws JSONException
     * @return JSONArray
     */
    public JSONArray getJsonArrayByCodeStatus(JSONArray jsonArray, String codeStatus) throws JSONException {
        JSONArray jsonArrayByCs = new JSONArray();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if (jsonObject.getString("code_status").equals(codeStatus)) {
                jsonArrayByCs.put(jsonObject);
            }
        }
        return jsonArrayByCs;
    }

    /**
     * Build XML pattern response
     *
     * @param data contain final response data
     * @param message is response status
     * @return string data XML pattern
     */
    public String buildXMLResponse(String data, Status message) {
        JSONObject response = new JSONObject();
        JSONObject finalXMLData = new JSONObject();
        response.put("element", new JSONArray(data));
        finalXMLData.put("elements", response);
        finalXMLData.put("status", message);
        return finalXMLData.toString();
    }
}
