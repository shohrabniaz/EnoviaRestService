/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Administrator
 */
@Component
public class PropertyReader {

    private static final Logger PROPERTY_READER_LOGGER = Logger.getLogger(PropertyReader.class);
    private static Properties prop = new Properties();
    private static InputStream aPInputStream;
    private static String resourceName = "application.properties";
    private static String environmentName = "Default";
    public static Boolean isEnvironmentFileLoaded = Boolean.FALSE;

    @Autowired
    private static Environment springEnvironment;
//	private static InputStream xmlInputStream;

    public PropertyReader() {
    }

    static {
        loadPropertyFile();
    }

    private static void loadPropertyFile() {
        aPInputStream = PropertyReader.class.getClassLoader().getResourceAsStream(resourceName);
        try {
            prop.load(aPInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized String getProperty(String key) {
        PROPERTY_READER_LOGGER.debug("Strated reading property file");
        String filePath = "";
        filePath = prop.getProperty(key);
        PROPERTY_READER_LOGGER.debug("Done reading property file");
        return filePath;
    }

    public static void loadEnvironment() {
        try {
            if (environmentName.equalsIgnoreCase("Default")) {
                prop.load(aPInputStream);
                String environment = prop.getProperty("spring.profiles.active");

                if (environment != null && !environment.equalsIgnoreCase("")) {
                    environmentName = environment;
                    resourceName = "application-" + environmentName + ".properties";
                    loadPropertyFile();
                }
                PropertyReader.isEnvironmentFileLoaded = Boolean.TRUE;
            }
        } catch (IOException ex) {
            PROPERTY_READER_LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    public static String getEnvironmentName() {
        return environmentName;
    }

    public static InputStream getXMLInputStream() {
        return PropertyReader.class.getClassLoader().getResourceAsStream("input.xml");
    }

    public static synchronized HashMap<String, String> getProperties(String keyPattern, Boolean removePattern) {
        PROPERTY_READER_LOGGER.debug("Key Pattern : " + keyPattern);
        PROPERTY_READER_LOGGER.debug("Key set : " + prop.keySet());
        try {
            HashMap<String, String> propertyMap = getPropertyMap(keyPattern, removePattern, prop.keySet());
            PROPERTY_READER_LOGGER.debug("Patterned property map : " + propertyMap);
            return propertyMap;
        } catch (Exception exp) {
            throw exp;
        }
    }

    public static synchronized HashMap<String, String> getProperties(HashMap<String, String> propertiesMap, String keyPattern, Boolean removePattern) {
        try {
            if (propertiesMap == null || propertiesMap.isEmpty()) {
                return getProperties(keyPattern, removePattern);
            }
            return propertiesMap;
        } catch (Exception exp) {
            throw exp;
        }
    }

    private static synchronized HashMap<String, String> getPropertyMap(String keyPattern, Boolean removePattern, Set<Object> keySet) {
        HashMap<String, String> keyValueMap = new HashMap<>();
        keySet.forEach(action -> {
            String key = action.toString();

            if (key.startsWith(keyPattern)) {
                if (removePattern) {
                    keyValueMap.put(key.replace(keyPattern + ".", ""), getProperty(key));
                } else {
                    keyValueMap.put(key, getProperty(key));
                }
            }
        });

        return keyValueMap;
    }

    /*
    Started readPropertyFile
    Added by: Kayum
    > Reads properties text file, match key and returm key's value
     */
    public String readPropertyTextFile(File file, String key) {
        //File file = new File(location);
//        logger.debug("File: " + file + ", key : " + key);
        String strValue = null;

        try {
            boolean flag = false;
            List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
            for (String str : fileContent) {
                if (!str.substring(str.lastIndexOf("=") + 1).equals("")) {
                    String[] keyValue = str.split("=");
                    for (int i = 0; i < keyValue.length; i++) {
                        if (keyValue[i].equalsIgnoreCase(key)) {
                            PROPERTY_READER_LOGGER.debug("key matched: " + key);
                            strValue = keyValue[++i];
                            flag = true;
                            break;
                        }
                    }

                }
            }
        } catch (IOException ex) {
            PROPERTY_READER_LOGGER.error("Exception Stacktrace ::" + ex);
        }
        return strValue;
    }

    /*
    Done readPropertyFile
     */

 /*
    
    Started readPropertyFileKeyValue
    Added by: Kayum
    > Reads properties file like: messages.properties, match key and returm key's value
     */
    public String readPropertyFileKeyValue(String fileName, String key) {
        Properties prop = new Properties();
        String value = null;
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream(fileName));
            value = prop.getProperty(key);
        } catch (IOException ex) {
            PROPERTY_READER_LOGGER.error("Exception stacktrace :: " + ex.getMessage());
        }
        return value;
    }

    /*
    Done readPropertyFile
     */
    /**
     * Used to retrieve configuration based on given file and configuration name
     *
     * @param filename
     * @param configuration
     * @return String - configuration value
     * @throws com.bjit.ewc.utils.CustomException
     *
     */
    public String readXMLConfFile(String filename, String configuration) throws CustomException {
//        logger.debug("in readXMLConfFile method.");
        try {
            File file = new File(getClass().getResource("/services/" + filename + ".xml").getFile());
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element element = doc.getDocumentElement();
            String confValue = element.getElementsByTagName(configuration).item(0).getTextContent();
//            logger.debug("confValue : " + confValue);
            return confValue;

        } catch (FileNotFoundException | ParserConfigurationException ex) {
            PROPERTY_READER_LOGGER.error(ex.getMessage());
            throw new CustomException("Could not read configuration.");
        } catch (SAXException | IOException ex) {
            PROPERTY_READER_LOGGER.error(ex.getMessage());
            throw new CustomException("Could not read configuration.");
        } catch (NullPointerException ex) {
            PROPERTY_READER_LOGGER.error("Could not read configuration." + ex.getMessage());
            return "";
        }
    }

    public static String getSpringProperty(String property) {
        return springEnvironment.getProperty(property);
    }

    public static void getSpringPropertyMap(String pattern, Boolean removePattern) {
//        System.out.println("##################################################Spring boot properties#############################################");
        String[] activeProfiles = springEnvironment.getActiveProfiles();
//        System.out.println("Spring boot properties " + Arrays.toString(activeProfiles));

//        AbstractEnvironment abstractProperties = (AbstractEnvironment) springEnvironment;
//        PropertySource source
//                = abstractProperties.getPropertySources().get("application.properties");
//        Properties props = (Properties) source.getSource();
//        Set<Object> keySet = props.keySet();
//        System.out.println("Spring boot properties " + keySet);
    }
}
