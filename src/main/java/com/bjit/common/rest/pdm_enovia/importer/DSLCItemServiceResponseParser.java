package com.bjit.common.rest.pdm_enovia.importer;

import java.util.List;
import java.util.Map;

public interface DSLCItemServiceResponseParser {
    public abstract <T extends Object> T parse(Map<String, List<String>> responseMap) throws Exception;
}
