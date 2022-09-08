package com.bjit.mapper.mapproject.util;

import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tareq Sefati
 *
 */
public final class Constants {

    public static final String PDF = "pdf";
    public static final String XLS = "xls";
    public static final String JSON = "json";
    public static final String ENGLISH = "En";
    public static final String FINNISH = "Fn";
    public static final String REPORT_PREFIX = "\\Report_";
    public static final String CONTEXT_EXCEPTION = "Context is not connected.";
    public static final String AUTHENTICATION_EXCEPTION = "Authentication Failed. Please give valid user credentials.";
    public static final String ATTRIBUTION_EXCEPTION = "Please check the request URL, service name, mandatory parameter name or attrs, and mandatory attrs fields Name, Title. Please fix it and try again.";
    public static final String OBJECT_EXCEPTION = "Please specify object name and type correctly. Supported ones are: "
            + " \n type: CreateAssembly and name: mass-EPS1-00000771 or mass-EPS1-00003478 "
            + " \n type: VPMReference   and name: prd-EPS1-04621194  or prd-EPS1-01227875";
    public static final String TYPE_NAME_BE_NULL_EXCEPTION = "Provided 'Type' and 'Name' is invalid.";
    public static final String TYPE_NAME_REVISION_BE_NULL_EXCEPTION = "Provided 'Type', 'Name' and 'Revision' is invalid.";
    public static final String LATEST_REVISION_BE_NULL_EXCEPTION = "Revision can not be empty when isLatest value false";
    public static final String REPORT_GENERATION_MESSAGE = "Report generation is finished.";
    public static final String INVALID_ATTRIBUTE_MESSAGE = "Provided Attribute and Value is invalid";
    public static final ArrayList<String> LANG = new ArrayList<>(Arrays.asList("en", "fn", "sv", "zh", "bn", "jp", "fr", "de"));
    public static final ArrayList<String> ALLOWED_FORMATS = new ArrayList<>(Arrays.asList(PDF, XLS, JSON));
    public static final Map<String, String> LANG_MAP;
    public static final String SEE_OTHER = "303";
    public static final String SUCCESS = "200";
    public static final String FAILED = "500";
    public static final String FALSE = "false";

    static {
        LANG_MAP = new HashMap<>();
        LANG_MAP.put("en", "English");
        LANG_MAP.put("fn", "Finnish");
        LANG_MAP.put("sv", "Swedish");
        LANG_MAP.put("zh", "Chinese");
        LANG_MAP.put("fr", "French");
        LANG_MAP.put("de", "German");
    }
    public static final Map<String, String> PRODUCT_TYPE_SOURCE_MAP;

    static {
        PRODUCT_TYPE_SOURCE_MAP = new HashMap<>();
        PRODUCT_TYPE_SOURCE_MAP.put("modelVersion", ItemImportEnvironments.MASS_MV_IMPORT);
        PRODUCT_TYPE_SOURCE_MAP.put("hardwareProduct", ItemImportEnvironments.MASS_HW_IMPORT);
        PRODUCT_TYPE_SOURCE_MAP.put("softwareProduct", ItemImportEnvironments.MASS_SWP_IMPORT);
        PRODUCT_TYPE_SOURCE_MAP.put("serviceProduct", ItemImportEnvironments.MASS_SP_IMPORT);
        PRODUCT_TYPE_SOURCE_MAP.put("medicalDeviceProduct", ItemImportEnvironments.MASS_MDP_IMPORT);
    }
    public static final String MODEL_VERSION = "Products";
    public static final String HARDWARE_PRODUCT = "Hardware Product";
    public static final String SOFTWARE_PRODUCT = "Software Product";
    public static final String SERVICE_PRODUCT = "Service Product";
    public static final String MEDICAL_DEVICE_PRODUCT = "Medical Device Product";

}
