package com.bjit.common.rest.item_bom_import.factories;

import com.bjit.common.rest.app.service.comos.promise.ComosItemImportPromise;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.bom.facades.CommonBOMImportFacade;
import com.bjit.common.rest.app.service.controller.item.promises.CommonItemImportPromise;
import com.bjit.common.rest.app.service.controller.item.promises.ProductItemImportPromise;
import static com.bjit.common.rest.item_bom_import.factories.ItemOrBOMAbstractFactory.IMPORT_TYPE_PRODUCT;
import com.bjit.common.rest.item_bom_import.item_import.ItemImport;
import com.bjit.common.rest.item_bom_import.import_interfaces.ItemOrBOMImport;
import com.bjit.common.rest.app.service.comos.facade.ComosBOMImportFacade;

public class ItemImportFactory extends ItemOrBOMAbstractFactory {

    @Override
    public ItemOrBOMImport getImportType(String importType) {
        switch (importType) {
            case IMPORT_TYPE_ITEM:
                return new ItemImport();
            case IMPORT_TYPE_PRODUCT:
                return new ProductItemImportPromise();
            case ItemImportEnvironments.MOPAZ:
                return new CommonBOMImportFacade();
            case ItemImportEnvironments.COMMON:
                return new CommonItemImportPromise();
            case ItemImportEnvironments.COMOS:
                return new ComosItemImportPromise();
            case ItemImportEnvironments.COMOS_BOM:
                return new ComosBOMImportFacade();
            default:
                break;
        }
        return null;
    }
}
