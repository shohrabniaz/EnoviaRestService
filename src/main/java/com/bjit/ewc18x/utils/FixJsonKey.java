/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kayum-603
 */
public class FixJsonKey {
   public static void main(String[] args) {
    String testJSON = "{\"menu\": {\n" +
"  \"id no\": \"file\",\n" +
"  \"value type\": \"File\",\n" +
"  \"popup value\": {\n" +
"    \"menu item\": [\n" +
"      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
"      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
"      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
"    ]\n" +
"  }\n" +
"}}";
//    Map oldJSONObject = new Gson().fromJson(testJSON, Map.class);
//    JsonObject newJSONObject = iterateJSON(oldJSONObject);
//
//    Gson someGson = new Gson();
//    String outputJson = someGson.toJson(newJSONObject);
//    System.out.println(outputJson);
JSONObject json = new JSONObject(testJSON);
       System.out.println("Final output: "+replaceKeyWhiteSpace(json));

}

private static JsonObject iterateJSON(Map JSONData) {
    JsonObject newJSONObject = new JsonObject();
    Set jsonKeys = JSONData.keySet();
    Iterator<?> keys = jsonKeys.iterator();
    while(keys.hasNext()) {
        String currentKey = (String) keys.next();
        String newKey = currentKey.replaceAll(" ", "_");
        if (JSONData.get(currentKey) instanceof Map) {
            JsonObject currentValue = iterateJSON((Map) JSONData.get(currentKey));
            newJSONObject.add(currentKey, currentValue);
        } else {
            String currentValue = (String) JSONData.get(currentKey);
            newJSONObject.addProperty(newKey, currentValue);
        }
    }
    return newJSONObject;
}

    private static JSONObject replaceKeyWhiteSpace(Object json) {
        JSONObject jsonObject = null;
        if (json instanceof JSONObject) {
            System.out.println("Object");
            jsonObject = (JSONObject) json;
            List<String> keyList = new LinkedList<String>(jsonObject.keySet());
            for (String key : keyList) {
                if (!key.matches(".*[\\s\t\n]+.*")) { // key without any space
                    System.out.println("Word:" + key);
                    Object value = jsonObject.get(key);
                    replaceKeyWhiteSpace(value);
                    continue;
                }

                Object value = jsonObject.remove(key);
                String newKey = key.replaceAll("[\\s\t\n]", "_");

                replaceKeyWhiteSpace(value);

                jsonObject.accumulate(newKey, value);

            }
        } else if (json instanceof JSONArray) {
            System.out.println("Array");
            for (Object aJsonArray : (JSONArray) json) {
                replaceKeyWhiteSpace(aJsonArray);
            }
        }
        return jsonObject;
    }
}
