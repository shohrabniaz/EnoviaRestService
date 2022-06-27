/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.mapper.mapproject.util.CommonUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * To Manage Properties File
 *
 * @author Arifur Rahman
 */
public class PropertiesFileUtil {

    private static final Logger LOGGER = Logger.getLogger(PropertiesFileUtil.class);

    /**
     * Create if file not exists with initial properties
     *
     * @param propertiesFileName
     * @param propertiesKeyValuePair
     * @throws Exception
     */
    public static void initializePropertiesFile(String propertiesFileName, Map<String, String> propertiesKeyValuePair, String propertiesFileLocation) throws Exception {
        /* instructions :
            * 1. check if file exist
            * 2. if not exist then create
            * 3. else do noting
         */
        if (propertiesFileName == null || propertiesFileName.isEmpty()) {
            throw new Exception("Properties file name cannot be null!");
        }
        if (propertiesKeyValuePair == null) {
            throw new Exception("Properties key value pair map cannot be empty");
        }
//        ClassLoader classLoader = PropertiesFileUtil.class.getClassLoader();
//        String dir = classLoader.getResource("/").getFile();
        
        String fileLocation = propertiesFileLocation + propertiesFileName;
        File propertiesFile = new File(fileLocation);
        File dir = new File(propertiesFileLocation);
        if (!propertiesFile.exists()) {
            LOGGER.info("Going to Create '" + propertiesFileName + "' file!");
            LOGGER.info("File Location: " + propertiesFileLocation + propertiesFileName);
            try {
                dir.mkdirs();
                propertiesFile.createNewFile();
            } catch(IOException e) {
                throw new Exception("Exception occured during "+ propertiesFileName + " creation cause: " + e.getMessage());
            }
            
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(fileLocation);
                Set<String> propertyKeys = propertiesKeyValuePair.keySet();
                try ( PrintStream printStream = new PrintStream(outputStream)) {
                    for (String propertyKey : propertyKeys) {
                        printStream.println(propertyKey + " = " + propertiesKeyValuePair.get(propertyKey));
                    }
                    printStream.close();
                    LOGGER.info(propertiesFileName + " File Initialized!");
                }
            } catch (IOException ex) {
                LOGGER.error("IOException to '" + propertiesFileName + "' File, cause: " + ex.getMessage());
                throw new Exception("IOException to '" + propertiesFileName + "' file! cause: " + ex.getMessage());
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("Exception occured during closing output stream !, cause: " + e.getMessage());
                    throw new Exception("Exception occured during closing output stream !, cause: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Load properties of property file
     *
     * @param propertiesFileName
     * @return Properties of the requested file 
     * @throws Exception
     */
    private static synchronized Properties loadProperties(String propertiesFileName, String propertiesFileLocation) throws Exception {
        if (propertiesFileName == null || propertiesFileName.isEmpty()) {
            throw new Exception("Properties file name cannot be null!");
        }
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
//            ClassLoader classLoader = PropertiesFileUtil.class.getClassLoader();
//            String dir = classLoader.getResource("/").getFile();
            File initialFile = new File(propertiesFileLocation + propertiesFileName);
            if (initialFile.exists()) {
                inputStream = new FileInputStream(initialFile);
                properties.load(inputStream);
            } else {
                LOGGER.error("'" + propertiesFileName + "' Not found!");
                throw new Exception("'" + propertiesFileName + "' Not found!");
            }

            LOGGER.info("'" + propertiesFileName + "' loaded...");
            return properties;
        } catch (IOException ex) {
            LOGGER.error("#####################################################");
            LOGGER.error("Exception occured during read property! cause:" + ex.getMessage());
            LOGGER.error("#####################################################");
            throw new Exception(ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    LOGGER.info("Input stream closed.");
                }

            } catch (IOException ex) {
                LOGGER.error("'" + propertiesFileName + "' error to close file, cause: " + ex.getMessage());
                throw new Exception("'" + propertiesFileName + "' File Write Failed! cause: " + ex.getMessage());
            }
        }
    }

    /**
     * To get property value from scheduler
     *
     * @param propertiesFileName
     * @param propertyKey
     * @return Property value
     * @throws java.lang.Exception
     */
    public static synchronized String readProperty(String propertiesFileName, String propertyKey, String propertiesFileLocation) throws Exception {
        if (propertiesFileName == null || propertiesFileName.isEmpty()) {
            throw new Exception("Properties file name cannot be null!");
        }
        if (propertyKey == null || propertyKey.isEmpty()) {
            throw new Exception("Property Key cannot be null!");
        }
        String propertyValue = null;
        LOGGER.info("Going to load '" + propertiesFileName + "'");
        Properties properties = loadProperties(propertiesFileName,propertiesFileLocation);
        propertyValue = properties.getProperty(propertyKey);
//        if (propertyValue == null || propertyValue.isEmpty()) {
//            LOGGER.error("Value not found for '" + propertyKey + "' from '" + propertiesFileName + "' file!");
//            throw new Exception("Value not found for '" + propertyKey + "' from '" + propertiesFileName + "' file!");
//        }
        return propertyValue;
    }

    /**
     * To update scheduler timestamp
     *
     * @param propertiesFileName
     * @param propertyKey
     * @param propertyValue
     * @throws java.lang.Exception
     */
    public static synchronized void updateProperty(String propertiesFileName, String propertyKey, String propertyValue, String propertiesFileLocation) throws Exception {
        if (propertiesFileName == null || propertiesFileName.isEmpty()) {
            throw new Exception("Properties file name cannot be null!");
        }
        if (propertyKey == null || propertyKey.isEmpty()) {
            throw new Exception("Property Key cannot be null!");
        }
        if (propertyValue == null || propertyValue.isEmpty()) {
            throw new Exception("Value of '" + propertyKey + "' cannot be empty or null!");
        }
        // property key cannot hold extra spaces
        propertyKey = propertyKey.trim();
        BufferedWriter bufferedWriter = null;
        try {
//            ClassLoader classLoader = PropertiesFileUtil.class.getClassLoader();
//            String dir = classLoader.getResource("/").getFile();
            File file = new File(propertiesFileLocation  + propertiesFileName);
            LOGGER.info("'" + propertiesFileName + "' exists : " + file.exists());
            if (file.exists()) {
                String texts = CommonUtil.getStringFromTextFile(file);
                LOGGER.info("Properties Text: \n" + texts);
                boolean isPropertiesFound = false;
                if (!texts.trim().isEmpty()) {
                    String[] lines = texts.split(System.lineSeparator());
                    for (String line : lines) {
                        if (line.startsWith(propertyKey + " =")
                                || line.startsWith(propertyKey + "=")) {
                            String tempStr = propertyKey + " = " + propertyValue;
                            texts = texts.replaceFirst(line, tempStr);
                            isPropertiesFound = true;
                            break; // properties single line update
                        }
                    }
                }
                if (!isPropertiesFound) {
                    LOGGER.error("'" + propertyKey + "' not found from '" + propertiesFileName + "' file!");
                    throw new Exception("'" + propertyKey + "' not found from '" + propertiesFileName + "' file!");
                }

                bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(texts);
                LOGGER.info("'" + propertiesFileName + "' write success.");
            } else {
                LOGGER.error("'" + propertiesFileName + "' Not found!");
                throw new Exception("'" + propertiesFileName + "' Not found!");
            }
        } catch (IOException ex) {
            LOGGER.error("'" + propertiesFileName + "' write failed, cause: " + ex.getMessage());
            throw new Exception("'" + propertiesFileName + "' File Update Failed! cause: " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("'" + propertiesFileName + "' write failed, cause: " + ex.getMessage());
            throw new Exception("'" + propertiesFileName + "' write failed, cause: " + ex.getMessage());
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                    LOGGER.info("Buffered writer closed.");
                }

            } catch (IOException ex) {
                LOGGER.error("'" + propertiesFileName + "' error to close file, cause: " + ex.getMessage());
                throw new Exception("'" + propertiesFileName + "' File Write Failed! cause: " + ex.getMessage());
            }
        }
    }

}
