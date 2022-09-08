package com.bjit.common.rest.app.service.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Md. Omour Faruq Sumon
 */
@Component
@Scope(value = "prototype")
public class JSON implements IJSON {
    private GsonBuilder gsonBuilder;
    private Gson gson;

    public JSON() {
        this(true);

    }

    public JSON(Boolean serializeNull) {

        gsonBuilder = new GsonBuilder();
        if (serializeNull) {
            gsonBuilder.serializeNulls();
        }
        gson = gsonBuilder.create();
    }

    public JSON(Boolean serializeNull, Boolean disableHtmlEscaping) {

        gsonBuilder = new GsonBuilder();

        if (serializeNull && disableHtmlEscaping) {
            gsonBuilder.serializeNulls();
            gsonBuilder.disableHtmlEscaping();
        } else if (serializeNull) {
            gsonBuilder.serializeNulls();
        } else if (disableHtmlEscaping) {
            gsonBuilder.disableHtmlEscaping();
        }
        gson = gsonBuilder.create();
    }

    /**
     *
     * @param object single object
     * @param <T> generic type
     * @return a json string
     */
    @Override
    public <T> String serialize(T object) {
        try {
            return gson.toJson(object);

        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

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
    @Override
    public <T> String serialize(T object, HashMap<String, Object> newProperties) {
        try {
            //return gson.toJson(object);
            JsonElement toJsonTree = gson.toJsonTree(object);
            newProperties.forEach((String key, Object value) -> {
                JsonElement objectElement = gson.toJsonTree(value);
                toJsonTree.getAsJsonObject().add(key, objectElement);
            });
            return gson.toJson(toJsonTree);

        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    @Override
    public <T> String serializeObjectList(T object, String propertyName, List<Object> newProperties) {
        /*try {
            //return gson.toJson(object);
            JsonElement toJsonTree = gson.toJsonTree(object);
            newProperties.forEach((String key, Object value) -> {
                JsonElement objectElement = gson.toJsonTree(value);
                toJsonTree.getAsJsonObject().add(key, objectElement);
            });
            return gson.toJson(toJsonTree);

        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }*/
        return null;
    }

    /**
     *
     * @param object list of objects
     * @param <T> generic list
     * @return json string
     */
    @Override
    public <T> String serialize(List<T> object) {
        try {
            return gson.toJson(object);

        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    /**
     *
     * @param jsonSerializedString json object string
     * @param classType te be converted into the class
     * @param <T> generic type
     * @return converted object
     */
    @Override
    public <T> T deserialize(String jsonSerializedString, Class<T> classType) {
        try {
            T object = gson.fromJson(jsonSerializedString, classType);
            return object;
        } catch (JsonSyntaxException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    /**
     *
     * @param stringBusinessData a json string
     * @param key kew of the key value pair of the string
     * @return only the value of the key
     * @throws JsonSyntaxException
     */
    @Override
    public String getValueFromJsonKey(String stringBusinessData, String key) throws JsonSyntaxException {
        try {
            Map jsonJavaRootObject = new Gson().fromJson(stringBusinessData, Map.class);
            return jsonJavaRootObject.get(key).toString();
        } catch (JsonSyntaxException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    public Boolean FreeUpMemories() {
        try {
            gsonBuilder = null;
            gson = null;
            return true;
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    @Override
    public String addAProperty(Object object, String propertyName, String propertyValue) {
        JsonElement toJsonTree = gson.toJsonTree(object);
        toJsonTree.getAsJsonObject().addProperty(propertyName, propertyValue);
        return gson.toJson(toJsonTree);
    }
}
