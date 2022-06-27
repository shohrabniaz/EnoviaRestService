package com.bjit.common.rest.pdm_enovia.importer;

import java.util.List;
import java.util.Map;

import org.apache.http.util.Args;

/**
 * @author Tohidul-571
 *
 */
public class SimpleDSLCItemServiceResponseParserForItemImport implements DSLCItemServiceResponseParser {
    private final int nItemCreation = 1;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T parse(Map<String, List<String>> responseMap) throws Exception {
        Args.notNull(responseMap, "responseMap can not be null");

        if (responseMap.isEmpty()) {
            throw new Exception("responseMap can not be empty");
        }

        if (responseMap.size() > nItemCreation) {
            System.out.println("SimpleDSLCItemServiceResponseParserForItemImport is not the right response parser.");
            System.out.println("It is designed to parse response which contains only single item response.");
        }

        Map.Entry<String, List<String>> entry = responseMap.entrySet().iterator().next();
        String key = entry.getKey();
        List<String> values = entry.getValue();

        
        if (values.isEmpty()) {
            throw new Exception("responseMap values can not be empty");
        }
        return (T) values.get(0);
    }

}
