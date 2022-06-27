/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.factories;

import static com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory.IMPORT_TYPE_ITEM;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.item_bom_import.item_import.VALComponentMaterialItemImport;

/**
 *
 * @author BJIT
 */
public class VALComponentMaterialItemImportFactory extends ItemOrBOMAbstractFactory {

    @Override
    public ItemOrBOMImport getImportType(String importType) {
        if (importType.equals(IMPORT_TYPE_ITEM)) {
            return new VALComponentMaterialItemImport();
        }
        return null;
    }

}
