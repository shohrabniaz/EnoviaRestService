package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IItem;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import matrix.db.BusinessObject;
import matrix.db.Context;

public class Item implements IItem {
    @Override
    public Item prepareItem(Context context, BusinessObject businessObject) throws Exception{
        return null;
    }

    @Override
    public Item prepareItem(Context context, String objectId) throws Exception {
        return null;
    }

    @Override
    public Item prepareItem(Context context, TNR tnr) throws Exception {
        return null;
    }
}
