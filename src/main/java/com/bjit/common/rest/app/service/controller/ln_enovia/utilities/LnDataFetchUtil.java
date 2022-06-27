package com.bjit.common.rest.app.service.controller.ln_enovia.utilities;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Bjit
 */
public class LnDataFetchUtil {
    
    private static final Logger LN_DATA_FETCH_UTIL_LOGGER = Logger.getLogger(LnDataFetchUtil.class);
    private static final SimpleDateFormat DATE_FORMATTER;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    static {
        DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);
    }
    
    public static synchronized void validateCostBeforeDateTime(String dateTime) throws ParseException {
        if(!NullOrEmptyChecker.isNullOrEmpty(dateTime)) {
            try {
                Date fromDate = DATE_FORMATTER.parse(dateTime);
            } catch (ParseException e) {
                String errorMessage = "Invalid dateTime '" + dateTime + "'. Valid format: " + DATE_PATTERN;
                LN_DATA_FETCH_UTIL_LOGGER.error(errorMessage);
                throw new ParseException(errorMessage, e.getErrorOffset());
            }
        }
    }
}