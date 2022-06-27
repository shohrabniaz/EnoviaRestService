package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j
public class PropertyReader {
    static Properties properties;
    public static void loadPropertyFile(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        properties = new Properties();
        greetings();
        try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyKey){
        return properties.getProperty(propertyKey);
    }

    private static void greetings() {
        log.info("######################################");
        log.info("#         Loading Properties         #");
        log.info("######################################");
    }
}
