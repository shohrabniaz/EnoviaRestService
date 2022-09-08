package com.bjit.common.rest.app.service.controller.export.himelli;

import org.apache.log4j.Logger;

/**
 * @author Ashikur / BJIT
 */
public class HimelliLogger {

    private static HimelliLogger instance;

    private HimelliLogger() {
    }

    public static HimelliLogger getInstance() {
        if (instance == null) {
            instance = new HimelliLogger();
        }
        return instance;
    }

    public void printLog(String logMessage, LogType logType) {
        Logger logger = Logger.getLogger(getClassInstance());
        if (logType == LogType.INFO) {
            logger.info(logMessage);
        } else if (logType == LogType.DEBUG) {
            logger.debug(logMessage);
        } else if (logType == LogType.ERROR) {
            logger.error(logMessage);
        }
    }

    private Class<?> getClassInstance() {
        return this.getClass();
    }
}
