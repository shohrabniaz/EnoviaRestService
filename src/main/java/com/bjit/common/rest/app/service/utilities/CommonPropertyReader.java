/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
public class CommonPropertyReader {

    private static final org.apache.log4j.Logger COMMON_PROPERTY_READER_LOGGER = org.apache.log4j.Logger.getLogger(CommonPropertyReader.class);
    private Properties properties;

    public CommonPropertyReader() throws IOException {
        this("common.properties");
    }

    public CommonPropertyReader(String propertyFileName) throws IOException {
        try (InputStream inputStream = CommonPropertyReader.class.getClassLoader().getResourceAsStream(propertyFileName)) {

            COMMON_PROPERTY_READER_LOGGER.debug("Common property file name : " + propertyFileName);

            properties = new Properties();

            if (inputStream == null) {
                COMMON_PROPERTY_READER_LOGGER.fatal("Sorry, unable to find " + propertyFileName);
                throw new RuntimeException("Sorry, unable to find " + propertyFileName);
            }

            properties.load(inputStream);
        } catch (RuntimeException | IOException exp) {
            COMMON_PROPERTY_READER_LOGGER.error(exp);
            throw exp;
        }
    }

    public String getPropertyValue(String propertyKey) {
        COMMON_PROPERTY_READER_LOGGER.debug("Property key : " + propertyKey);
        String propertyValue = properties.getProperty(propertyKey);
        COMMON_PROPERTY_READER_LOGGER.debug("Property value : " + propertyValue);
        return propertyValue;
    }

    public HashMap<String, String> getPropertyValue(String keyPattern, Boolean removePattern) {
        COMMON_PROPERTY_READER_LOGGER.debug("Key Pattern : " + keyPattern);
        COMMON_PROPERTY_READER_LOGGER.debug("Key set : " + properties.keySet());
        try {
            HashMap<String, String> propertyMap = getPropertyValue(keyPattern, removePattern, properties.keySet());
            COMMON_PROPERTY_READER_LOGGER.debug("Patterned property map : " + propertyMap);
            return propertyMap;
        } catch (Exception exp) {
            throw exp;
        }

    }

    public HashMap<String, String> getPropertyValue(String keyPattern, Boolean removePattern, Set<Object> keySet) {
        HashMap<String, String> keyValueMap = new HashMap<>();
        keySet.forEach(action -> {
            String key = action.toString();

            if (key.startsWith(keyPattern)) {
                if (removePattern) {
                    keyValueMap.put(key.replace(keyPattern + ".", ""), getPropertyValue(key));
                } else {
                    keyValueMap.put(key, getPropertyValue(key));
                }
            }
        });

        return keyValueMap;
    }
}
