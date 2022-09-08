package com.bjit.common.rest.item_bom_import.factories;

import com.bjit.common.rest.app.service.controller.item.promises.CommonItemImportPromise;
import com.bjit.common.rest.app.service.controller.item.promises.ModelVersionItemImportPromise;
import com.bjit.common.rest.item_bom_import.bom_import.MANItemBOMImport;
import com.bjit.common.rest.item_bom_import.bom_import.StructureImport;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.item_import.MANItemImport;

public class MANItemImportFactory extends ItemOrBOMAbstractFactory {

    /**
     * Getting Item Or BOM Import Promise
     *
     * @param importType Import Type
     * @return
     */
    @Override
    public ItemOrBOMImport getImportType(String importType) {
        if (importType.equals(IMPORT_TYPE_ITEM)) {
            return new MANItemImport();
        } else if (importType.equals(IMPORT_TYPE_COMMON_ITEM)) {
            return new CommonItemImportPromise();
        }  else if (importType.equals(IMPORT_TYPE_BOM)) {
            return new MANItemBOMImport();
        } else if (importType.equals(IMPORT_TYPE_STRUCTURE)) {
            return new StructureImport();
        } else if (importType.equals(IMPORT_TYPE_HARDWARE_PRODUCT)) {
            return new ModelVersionItemImportPromise();
        } else if (importType.equals(IMPORT_TYPE_MODEL_VERSION)) {
            return new ModelVersionItemImportPromise();
        } else if (importType.equals(IMPORT_TYPE_SOFTWARE_PRODUCT)) {
            return new ModelVersionItemImportPromise();
        } else if (importType.equals(IMPORT_TYPE_SERVICE_PRODUCT)) {
            return new ModelVersionItemImportPromise();
        } else if (importType.equals(IMPORT_TYPE_MEDICAL_DEVICE_PRODUCT)) {
            return new ModelVersionItemImportPromise();
        }
        return null;
    }

}
