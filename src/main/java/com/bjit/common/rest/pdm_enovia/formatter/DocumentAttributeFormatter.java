package com.bjit.common.rest.pdm_enovia.formatter;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import java.util.HashMap;

/**
 *
 * @author Mashuk/BJIT
 */
public class DocumentAttributeFormatter extends AttributeFormatter {

    public DocumentAttributeFormatter(CreateObjectBean createObjectBean, HashMap<String, String> propertyMap) {
        super(createObjectBean,propertyMap);
    }

    @Override
    public CreateObjectBean getFormattedObjectBean(ResultUtil resultUtil, AttributeBusinessLogic attributeBusinessLogic) {
        String mapsAbsoluteDirectory = CommonUtil.populateMapDirectoryFromObject(createObjectBean);

        ObjectTypesAndRelations ObjectTypesAndRelations = new ObjectTypesAndRelations(mapsAbsoluteDirectory, ItemImportMapping.class, createObjectBean, attributeBusinessLogic);

        return createObjectBean;
    }
}
