/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.lntransfer.Util;

import com.bjit.common.rest.app.service.lntransfer.LNResponseMessageFormater;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.ex.integration.model.webservice.Item;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sarowar-221
 */
public class LNResponseMessageUtil {

    public static final String SUCCESSFUL_ITEM_LIST = "SuccessfulItemList";
    public static final String FAILED_ITEM_LIST = "FailedItemList";

    public static final String SUCCESSFUL_BOM_LIST = "SuccessfulBomList";
    public static final String FAILED_BOM_LIST = "FailedBomList";

    public Map<String, ResponseMessageFormaterBean> getResponseMessageFormatter(Item item, Map<String, String> itemResultMap, String type) {
        ResponseMessageFormaterBean messageFormatterBean = new ResponseMessageFormaterBean();
        Map<String, ResponseMessageFormaterBean> transferResultMap = new HashMap<>();
        ResponseMessageFormaterBean errorItemList = new ResponseMessageFormaterBean();
        ResponseMessageFormaterBean successItemList = new ResponseMessageFormaterBean();
        LNResponseMessageFormater lnResponseMessageFormater = new LNResponseMessageFormater();

        if (itemResultMap.size() > 0) {
            if (itemResultMap.get(item.getTnr().getName().toUpperCase()).equalsIgnoreCase("RESULT OK")) {
                if (item.getTnr() != null) {
                    messageFormatterBean.setTnr(new TNR(
                            item.getTnr().getType(),
                            item.getTnr().getName(),
                            item.getTnr().getRevision()));
                }
                successItemList = messageFormatterBean;
                if (type.equalsIgnoreCase("item")) {
                    transferResultMap.put(SUCCESSFUL_ITEM_LIST, successItemList);

                }
                if (type.equalsIgnoreCase("bom")) {
                    transferResultMap.put(SUCCESSFUL_BOM_LIST, successItemList);

                }

            } else {
                if (item.getTnr() != null) {
                    messageFormatterBean.setTnr(new TNR(
                            item.getTnr().getType(),
                            item.getTnr().getName(),
                            item.getTnr().getRevision()));
                }
                errorItemList = messageFormatterBean;
                if (type.equalsIgnoreCase("item")) {
                    transferResultMap.put(FAILED_ITEM_LIST, errorItemList);

                }
                if (type.equalsIgnoreCase("bom")) {
                    transferResultMap.put(FAILED_BOM_LIST, errorItemList);

                }

            }
        } else {
            if (item.getTnr() != null) {
                messageFormatterBean.setTnr(new TNR(
                        item.getTnr().getType(),
                        item.getTnr().getName(),
                        item.getTnr().getRevision()));
            }
            errorItemList = messageFormatterBean;
            if (type.equalsIgnoreCase("item")) {
                transferResultMap.put(FAILED_ITEM_LIST, errorItemList);

            }
            if (type.equalsIgnoreCase("bom")) {
                transferResultMap.put(FAILED_BOM_LIST, errorItemList);

            }

        }

        return transferResultMap;
    }

    public Map<String, ResponseMessageFormaterBean> getResponseMessageFormatter(Item item, String type, Exception ex) {
        ResponseMessageFormaterBean messageFormatterBean = new ResponseMessageFormaterBean();
        Map<String, ResponseMessageFormaterBean> transferResultMap = new HashMap<>();
        ResponseMessageFormaterBean errorItemList = new ResponseMessageFormaterBean();
        Set<ResponseMessageFormaterBean> successItemList = new HashSet<>();
        LNResponseMessageFormater lnResponseMessageFormater = new LNResponseMessageFormater();
        if (item.getTnr() != null) {
            messageFormatterBean.setTnr(new TNR(
                    item.getTnr().getType(),
                    item.getTnr().getName(),
                    item.getTnr().getRevision()));
        }
        errorItemList = messageFormatterBean;
        if (type.equalsIgnoreCase("item")) {
            transferResultMap.put(FAILED_ITEM_LIST, errorItemList);

        }
        if (type.equalsIgnoreCase("bom")) {
            transferResultMap.put(FAILED_BOM_LIST, errorItemList);
        }

        return transferResultMap;
    }

    public static ResponseMessageFormaterBean getResponseMessageFormatter(Item item, String message) {
        ResponseMessageFormaterBean messageFormatterBean = new ResponseMessageFormaterBean();
        messageFormatterBean.setErrorMessage(message);
        // messageFormatterBean.setObjectId(item.getId());
        if (item.getTnr() != null) {
            messageFormatterBean.setTnr(new TNR(
                    item.getTnr().getType(),
                    item.getTnr().getName(),
                    item.getTnr().getRevision()));
        }
        return messageFormatterBean;
    }
}
