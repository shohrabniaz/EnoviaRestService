/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer.Util;

import com.bjit.common.rest.app.service.controller.lntransfer.LNTransferServiceImpl;
import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ex.integration.transfer.actions.LNTransferAction;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ex.integration.model.webservice.Item;
import com.bjit.ex.integration.transfer.actions.factory.TransferActionType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public class LNResponseServiceImpl  {

    private static final org.apache.log4j.Logger LN_RESPONSE_SERVICE_IMPL = org.apache.log4j.Logger.getLogger(LNResponseServiceImpl.class);
    public static final String SUCCESSFUL_ITEM_LIST = "SuccessfulItemList";
    public static final String FAILED_ITEM_LIST = "FailedItemList";
    LNRequestUtil lNRequestUtil = new LNRequestUtil();
    public boolean isService = true;
    public boolean hasBOM = true;
    LNResponseMessageUtil lNResponseMessageUtil = new LNResponseMessageUtil();
//



    public Map<LNResponseMessageFormater, String> finalResponseListMap(Map<String, LNResponseMessageFormater> transferResultMap) {

        Map<LNResponseMessageFormater, String> listTransferResultMap = new HashMap<>();
        LNResponseMessageFormater errorItemList = new LNResponseMessageFormater();
        LNResponseMessageFormater successItemList = new LNResponseMessageFormater();
        successItemList = transferResultMap.get(LNResponseServiceImpl.SUCCESSFUL_ITEM_LIST);
        errorItemList = transferResultMap.get(LNResponseServiceImpl.FAILED_ITEM_LIST);
        if (!NullOrEmptyChecker.isNull(successItemList)) {
            listTransferResultMap.put(successItemList, SUCCESSFUL_ITEM_LIST);
            transferResultMap.remove(successItemList);
        }

        if (!NullOrEmptyChecker.isNull(errorItemList)) {
            listTransferResultMap.put(errorItemList, FAILED_ITEM_LIST);
            transferResultMap.remove(errorItemList);
        }
        
        
       
        

        return listTransferResultMap;
    }

}