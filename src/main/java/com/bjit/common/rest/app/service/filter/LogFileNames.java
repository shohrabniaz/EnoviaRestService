/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.filter;

import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author BJIT
 */
public class LogFileNames {

    private static HashMap<String, String> logMap;

    public static synchronized void updateLogFileName(String resourcePath) {
        new LogFileNames().updateLog4jConfiguration(resourcePath);
    }

    private String getLogFileName(String logFile) {
        String[] logFileDirectories = logFile.split("/");
        String logFileName = logFileDirectories[logFileDirectories.length - 1];
        return getMappedLogFileName(logFileName);
    }

    private String getMappedLogFileName(String logFileName) {

        try {
            if (NullOrEmptyChecker.isNullOrEmpty(logMap)) {
                CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
                HashMap<String, String> propertyValue = commonPropertyReader.getPropertyValue("log4j.log.file.name", Boolean.TRUE);
                logMap = propertyValue;
            }
        } catch (IOException ex) {
            Logger.getLogger(LogFileNames.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(logMap)) {
            throw new NullPointerException("Map file not loaded");
        }

        logFileName = logMap.get(logFileName);

        if (!NullOrEmptyChecker.isNullOrEmpty(logFileName)) {
            return logFileName;
        }

        return logMap.get("default");
    }

    private void updateLog4jConfiguration(String logFile) {
        Properties props = new Properties();
        try {
            InputStream configStream = getClass().getResourceAsStream("/log4j.properties");
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot load configuration file ");
        }
        String todaysDate = getTodaysDate();
        String logsDirectory = System.getProperty("catalina.base") + "/logs/EnoviaRestService/" + todaysDate;
        String directoryName = logsDirectory + "/" + getLogFileName(logFile) + ".log";
        props.setProperty("log4j.appender.file.File", directoryName);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    private String getTodaysDate() {
        String logFileDateFormat;
        try{
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            logFileDateFormat = commonPropertyReader.getPropertyValue("log4j.log.file.date.format");
        }
        catch(IOException exp){
            logFileDateFormat = "dd_MMM_yyyy";
            exp.printStackTrace(out);
        }
        
        
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(logFileDateFormat);
        String formattedDate = dateFormat.format(today);
        return formattedDate;
    }
}
