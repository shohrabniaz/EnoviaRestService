package com.bjit.common.rest.pdm_enovia.bom.comparison.utility;

import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Ashikur Rahman / BJIT
 */
public class PropertyUtil implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private static Properties properties = new Properties();
    private static Map<String, String> propertyMap = new LinkedHashMap<String, String>();

    private static InputStream aPInputStream;
    private static String resourceName = "application.properties";
    private static String environmentName = "Default";

    /**
     * @param propertiesFilePath
     * @throws IOException
     */
    public void loadPropertiesFile(URI propertiesFilePath) throws IOException {
        InputStream file = new FileInputStream(new File(propertiesFilePath));
        properties.load(file);
    }

//    public static void loadEnvironment() {
//        try {
//            if (environmentName.equalsIgnoreCase("Default")) {
//                properties.load(aPInputStream);
//                String environment = properties.getProperty("spring.profiles.active");
//
//                if (environment != null && !environment.equalsIgnoreCase("")) {
//                    environmentName = environment;
//                    resourceName = "application-" + environmentName + ".properties";
//                    loadPropertyFile();
//                }
//                PropertyReader.isEnvironmentFileLoaded = Boolean.TRUE;
//            }
//        } catch (IOException ex) {
//
//            ex.printStackTrace();
//        }
//    }
//
//    private static void loadPropertyFile() {
//        aPInputStream = PropertyReader.class.getClassLoader().getResourceAsStream(resourceName);
//        try {
//            properties.load(aPInputStream);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    /**
     * @param propertyKey
     * @return
     */
    public static String getPropertyValue(String propertyKey) {
        propertyKey = propertyKey.trim();
        if (propertyKey != null && !propertyKey.isEmpty()) {
            return properties.getProperty(propertyKey);
        }
        return null;
    }

    public static Map<String, String> getProperties() {
        for (String propertyKey : properties.stringPropertyNames()) {
            propertyMap.put(propertyKey.trim(), properties.getProperty(propertyKey).trim());
        }
        return propertyMap;
    }
}
