/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class ItemImportUtility {

    public static HashMap<String, String> MAP_DIRECTORY;
    public static HashMap<String, String> BUSINESS_OBJECT_TYPE_MAP;
    private static final org.apache.log4j.Logger ITEM_IMPORT_UTILITY_LOGGER = org.apache.log4j.Logger.getLogger(ItemImportUtility.class);

    static {
        BUSINESS_OBJECT_TYPE_MAP = PropertyReader.getProperties(BUSINESS_OBJECT_TYPE_MAP, "import.type.map.Enovia", Boolean.TRUE);
    }

    public static synchronized String validateCreateObjectBean(CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) {
        String errorMessage;
        if (NullOrEmptyChecker.isNull(createObjectBean)) {
            errorMessage = "No data found in the bean object";
            ITEM_IMPORT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(MAP_DIRECTORY)) {
            MAP_DIRECTORY = PropertyReader.getProperties("import.object.erp.map", true);
            ITEM_IMPORT_UTILITY_LOGGER.debug("Directory Map " + MAP_DIRECTORY);
        }

        businessObjectOperations.validateTNR(createObjectBean.getTnr(), !createObjectBean.getIsAutoName(), !createObjectBean.getIsAutoName());

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            errorMessage = "There is no attribute presents in the request";
            ITEM_IMPORT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        } else if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getSource())) {
            return MAP_DIRECTORY.get("common");
        } else {
            return MAP_DIRECTORY.get(createObjectBean.getSource() + "." + createObjectBean.getTnr().getType());
        }
    }

    public CreateObjectBean updateNameAndRevision(CreateObjectBean createObjectBean, HashMap<String, String> destinedMap) {
        if (!createObjectBean.getIsAutoName()) {
            destinedMap.put("revision", createObjectBean.getTnr().getRevision());
            destinedMap.put("name", createObjectBean.getTnr().getName());
        }

        createObjectBean.setIsAutoName(true);

        return createObjectBean;
    }
}
