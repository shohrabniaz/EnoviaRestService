/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.manager.GTS;

import com.bjit.common.rest.app.service.utilities.PropertiesFileUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * To load translation related configurations
 *
 * @author Arifur Rahman
 */
public class TranslationConfig {

    private static final Logger LOGGER = Logger.getLogger(TranslationConfig.class);
    private static final String GTS_BUNDLEID_QUERY_REQUEST_COUNT_KEY = "enovia.query.search.bundleid.request.count";
    private static final String GTS_ENOVIA_TITLE_BUNDLEID_QUERY_REQUEST_COUNT = "200";
    private static String INITIAL_SCHEDULER_TIMESTAMP = null;
    private static String SCHEDULER_PROPERTIES_FILE_NAME = null;
    private static String GTS_TITLE_LASTUPDATE_TIMESTAMP_KEY = null;
    private static String SCHEDULER_PROPERTIES_FILE_LOCATION= null;

    /**
     * To initialize scheduler property file Following properties are their: 1.
     * initialized with last scheduler timestamp. 2. enovia bulk bundle id
     * request count
     *
     * @throws java.lang.Exception
     */
    static {

        try {
            LOGGER.info("Initializing translation config properties.");
            PropertyReader.loadEnvironment();
            SCHEDULER_PROPERTIES_FILE_LOCATION= PropertyReader.getProperty("app.integration.scheduler.information");
            INITIAL_SCHEDULER_TIMESTAMP = PropertyReader.getProperty("gts.initial.scheduler.timestamp");
            SCHEDULER_PROPERTIES_FILE_NAME = PropertyReader.getProperty("gts.scheduler.properties.filename");
            GTS_TITLE_LASTUPDATE_TIMESTAMP_KEY = PropertyReader.getProperty("gts.title.lastupdate.timestamp.key");
            Map<String, String> propertiesKeyValuePair = new LinkedHashMap<String, String>();
            propertiesKeyValuePair.put(GTS_TITLE_LASTUPDATE_TIMESTAMP_KEY, INITIAL_SCHEDULER_TIMESTAMP);
            propertiesKeyValuePair.put(GTS_BUNDLEID_QUERY_REQUEST_COUNT_KEY, GTS_ENOVIA_TITLE_BUNDLEID_QUERY_REQUEST_COUNT);

            PropertiesFileUtil.initializePropertiesFile(SCHEDULER_PROPERTIES_FILE_NAME, propertiesKeyValuePair, SCHEDULER_PROPERTIES_FILE_LOCATION);
        } catch (Exception ex) {
            LOGGER.error("#####################################################");
            LOGGER.error("Exception occured! cause:" + ex.getMessage());
            LOGGER.error("#####################################################");
        }
    }

    /**
     * Modification Start Date value fetching
     *
     * @return Last update timestamp
     * @throws Exception
     */
    public String getLastUpdateTimestamp() throws Exception {
        return PropertiesFileUtil.readProperty(SCHEDULER_PROPERTIES_FILE_NAME, GTS_TITLE_LASTUPDATE_TIMESTAMP_KEY,SCHEDULER_PROPERTIES_FILE_LOCATION);
    }

    /**
     * Get Number of Bundle Id in Search Query
     *
     * @return bundle id query request count
     * @throws Exception
     */
    public String getBundleIdQueryRequestCount() throws Exception {
        return PropertiesFileUtil.readProperty(SCHEDULER_PROPERTIES_FILE_NAME, GTS_BUNDLEID_QUERY_REQUEST_COUNT_KEY,SCHEDULER_PROPERTIES_FILE_LOCATION);
    }

    /**
     * To update scheduler timestamp
     *
     * @param modificationEndDate
     * @throws java.lang.Exception
     */
    public void updateSchedulerTimestamp(String modificationEndDate) throws Exception {
        PropertiesFileUtil.updateProperty(SCHEDULER_PROPERTIES_FILE_NAME, GTS_TITLE_LASTUPDATE_TIMESTAMP_KEY, modificationEndDate,SCHEDULER_PROPERTIES_FILE_LOCATION);
    }

}
