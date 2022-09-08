package com.bjit.common.rest.pdm_enovia.bom.comparison.constant;

/**
 * @author Ashikur Rahman / BJIT
 */
public final class Constant {

    public static final String DEFAULT_LEVEL = "99";
    public static final String DEFAULT_RELATIONSHIP = "BOM";
    public static final String DEFAULT_REVISION = "00";
    public static final String DEFAULT_ENOVIA_REVISION = "1.1";
    public static final String DEFAULT_TYPE = "Create Assembly";

    public static final String BOM_ATTRIBUTE_DRAWING_NUMBER = "Drawing number";
    public static final String BOM_ATTRIBUTE_CUSTOMER_BOM_TYPE = "Customer BOM type";
    public static final String BOM_ATTRIBUTE_TITLE = "Title in English";

    public static final String RELATIONSHIP_ATTR_WIDTH = "Width";
    public static final String RELATIONSHIP_ATTR_LENGTH = "Length";
    public static final String RELATIONSHIP_ATTR_POSITION = "Position";
    public static final String RELATIONSHIP_ATTR_LEVEL = "Level";
    public static final String RELATIONSHIP_ATTR_QUANTITY = "Net quantity";

    public static final String PDM_ATTR_NODE = "attr";
    public static final String PDM_OBJECT_TREE_NODE = "object-tree";
    public static final String PDM_ATTR_DRAWING_NUMBER = "Drawing number";
    public static final String PDM_ATTR_TRANSFERRED_TO_ERP = "Transferred to ERPs";
    public static final String PDM_ATTR_TITLE = "Title in English";
    public static final String ENDPOINT_EXCEPTION = "Endpoint URL cannot be null or empty.";
    public static final String STRING_EXCEPTION = "Source input is expected to be string format JSON data.";
    public static final String ENDPOINT_EXCEPTION_MESSAGE = "Provide sourceInput Endpoint URL, or provide an URL in the properties file";
    public static final String ITEM_CANNOT_BE_NULL_EXCEPTION = "Item list cannot be null or empty.";
    public static final String ITEM_NAME_BE_NULL_EXCEPTION = "Item name cannot be null or empty.";

    public static final String BOM_EXPORT_URL = "bom.export.url";
    public static final String BOM_COMPARISON_PROPERTIES = "pdm-bom-comparison.properties";
}
