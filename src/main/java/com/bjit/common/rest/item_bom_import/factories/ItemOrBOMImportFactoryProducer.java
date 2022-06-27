package com.bjit.common.rest.item_bom_import.factories;

import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;

public class ItemOrBOMImportFactoryProducer {

    public static final String ITEM_TYPE_ODI = "ODI"; // CreateAssembly
    public static final String ITEM_TYPE_MOPAZ = "mopaz"; // CreateAssembly
    public static final String ITEM_TYPE_CREATEASSEMBLY_PROCESS_CONT_CREATE_MAT = "CreateAssemblyORProcessContinuousCreateMaterial"; // CreateAssembly
    public static final String IMPORT_ITEM_TYPE_VAL_COMPONENT = "VAL_COMPONENT";
    public static final String IMPORT_ITEM_TYPE_VAL_COMPONENT_MATERIAL = "VAL_COMPONENT_MATERIAL";
    public static final String ITEM_IMPORT = "ITEM_IMPORT"; // CreateAssembly


    /**
     * Getting BOM Abstract Factory
     *
     * @param importItemType Import Item Type
     * @return
     */
    public static ItemOrBOMAbstractFactory getFactory(String importItemType) {
        switch (importItemType) {
            case ITEM_TYPE_ODI:
                return new MANItemImportFactory();
            case ITEM_TYPE_CREATEASSEMBLY_PROCESS_CONT_CREATE_MAT:
                return new MANItemImportFactory();
            case IMPORT_ITEM_TYPE_VAL_COMPONENT:
                return new VALComponentItemImportFactory();
            case ITEM_IMPORT:
                return new ItemImportFactory();
            case ItemImportEnvironments.MASS_HW_IMPORT:
                return new MANItemImportFactory();
            case ItemImportEnvironments.MASS_MV_IMPORT:
                return new MANItemImportFactory();
            case ItemImportEnvironments.MASS_SWP_IMPORT:
                return new MANItemImportFactory();
            case ItemImportEnvironments.MASS_SP_IMPORT:
                return new MANItemImportFactory();
            case ItemImportEnvironments.MASS_MDP_IMPORT:
                return new MANItemImportFactory();
            case ItemImportEnvironments.MOPAZ:
                return new ItemImportFactory();
            case ItemImportEnvironments.COMOS:
                return new ItemImportFactory();
            default:
                break;
        }
        return null;
    }

}
