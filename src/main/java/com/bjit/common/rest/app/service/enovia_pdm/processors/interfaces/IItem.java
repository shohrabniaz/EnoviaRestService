package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;


import com.bjit.common.rest.app.service.enovia_pdm.processors.Item;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import matrix.db.BusinessObject;
import matrix.db.Context;

public interface IItem {
    Item prepareItem(Context context, BusinessObject businessObject) throws Exception;
    Item prepareItem(Context context, String objectId) throws Exception;
    Item prepareItem(Context context, TNR tnr) throws Exception;
}
