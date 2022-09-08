package com.bjit.common.rest.item_bom_import.import_interfaces;

import matrix.db.Context;

public interface ItemOrBOMImport {
    public <T, K> K doImport(final Context context, final T t);
}
