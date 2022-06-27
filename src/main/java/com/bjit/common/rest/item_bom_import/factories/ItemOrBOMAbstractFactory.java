package com.bjit.common.rest.item_bom_import.factories;

import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;

public abstract class ItemOrBOMAbstractFactory<T, K> {

    public static final String IMPORT_TYPE_ITEM = "ITEM";
    public static final String IMPORT_TYPE_COMMON_ITEM = "COMMON ITEM";
    public static final String IMPORT_TYPE_BOM = "BOM";
    public static final String IMPORT_MOPAZ_TYPE_BOM = "MOPAZ BOM";
    public static final String IMPORT_TYPE_STRUCTURE = "STRUCTURE";
    public static final String IMPORT_TYPE_PRODUCT = "PRODUCT";
    public static final String IMPORT_TYPE_HARDWARE_PRODUCT = "HARDWAREPRODUCT";
    public static final String IMPORT_TYPE_MODEL_VERSION = "MODELVERSION";
    public static final String IMPORT_TYPE_SOFTWARE_PRODUCT = "SOFTWAREPRODUCT";
    public static final String IMPORT_TYPE_SERVICE_PRODUCT = "SERVICEPRODUCT";
    public static final String IMPORT_TYPE_MEDICAL_DEVICE_PRODUCT = "MEDICALDEVICEPRODUCT";

    public abstract ItemOrBOMImport getImportType(String importType);
}
