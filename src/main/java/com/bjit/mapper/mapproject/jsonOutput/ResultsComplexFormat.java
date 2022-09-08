/**
 *
 */
package com.bjit.mapper.mapproject.jsonOutput;

import java.util.List;
import java.util.Map;

/**
 * @author TAREQ SEFATI
 *
 */
public class ResultsComplexFormat {

    private List<Map<String, Object>> results;

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }
}
