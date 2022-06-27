package com.bjit.common.rest.app.service.utilities.ln_enovia;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class RequestValidator {
    private static final Logger REQUEST_VALIDATOR_LOGGER = Logger.getLogger(RequestValidator.class);
    private static String datePattern;
    private static SimpleDateFormat dateFormatter;
    
    static {
        datePattern = PropertyReader.getProperty("ln.cost.service.date.pattern");
        dateFormatter = new SimpleDateFormat(datePattern);
    }
    
    public static void validateStartAtDate(String startAt, String datePattern) throws ParseException {
        if(!NullOrEmptyChecker.isNullOrEmpty(startAt)) {
                try {
                    Date validDate = dateFormatter.parse(startAt);
                } catch (ParseException e) {
                    throw new ParseException("Invalid value in 'startAt' header. Valid Format: " + datePattern, e.getErrorOffset());
                }
            }
            else {
                throw new NullPointerException("'startAt' header is required. Valid Format: " + datePattern);
            }
    }
}
