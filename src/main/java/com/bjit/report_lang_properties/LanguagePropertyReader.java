/**
 *
 */
package com.bjit.report_lang_properties;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.util.Constants;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author TAREQ SEFATI
 *
 */
public class LanguagePropertyReader {

    private final Logger logger = Logger.getLogger(LanguagePropertyReader.class);
    private Map<String, String> TYPE_MAPPING;

    public Map<Object, Object> getLabel(String lang) {
        Properties properties = new Properties();
        Map<Object, Object> results = new HashMap<>();
        try {
            lang = lang.toLowerCase();
            if (Constants.LANG.contains(lang)) {
                logger.debug("Found properties file. Language: " + lang);
            } else {
                logger.debug("Not found properties file. Language: " + lang);
                logger.debug("Initiating default language: English");
                lang = "en";
            }
            //InputStream inputStream = LanguagePropertyReader.class.getClassLoader().getResourceAsStream("localization/reportLabel_" + lang + ".properties");
            //properties.load(LanguagePropertyReader.class.getClassLoader().getResourceAsStream("localization/reportLabel_" + lang + ".properties"));
            properties.load(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("localization/reportLabel_" + lang + ".properties"), "UTF-8"));

            for (Entry<Object, Object> entry : properties.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
            logger.debug("Report Label Map: " + results);
        } catch (IOException e) {
            logger.error("ERROR: Could not get properties file. Language: " + lang);
            logger.error(e.getMessage());
        }
        return results;
    }

    public Map<Object, Object> getLabel(String lang, String type) {
        Properties properties = new Properties();
        Map<Object, Object> results = new HashMap<>();
        try {
            lang = lang.toLowerCase();
            if (Constants.LANG.contains(lang)) {
                logger.debug("Found properties file. Language: " + lang);
            } else {
                logger.debug("Not found properties file. Language: " + lang);
                logger.debug("Initiating default language: English");
                lang = "en";
            }
            //InputStream inputStream = LanguagePropertyReader.class.getClassLoader().getResourceAsStream("localization/reportLabel_" + lang + ".properties");
            properties.load(LanguagePropertyReader.class.getClassLoader().getResourceAsStream("localization/reportLabel_" + lang + ".properties"));

            for (Entry<Object, Object> entry : properties.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
            if (results.containsKey("lbl_eng_bom")){
                results.replace("lbl_eng_bom", results.get("lbl_report_title_"+type));         
            }
            logger.debug("Report Label Map: " + results);
        } catch (IOException e) {
            logger.error("ERROR: Could not get properties file. Language: " + lang);
            logger.error(e.getMessage());
        }
        return results;
    }
}
