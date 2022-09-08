/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.itemHistoryDetatils.validator;

import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemHistoryReqModel;
import com.bjit.common.rest.app.service.model.itemHistoryLine.ItemsInfo;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ItemHistoryValidator {

    private static final String historyOrderAscending = PropertyReader.getProperty("item.history.order.ascending");

    public static boolean validateRequest(ItemHistoryReqModel reqModel) {

        List<ItemsInfo> itemList = reqModel.getData();

        if (NullOrEmptyChecker.isNullOrEmpty(itemList)) {
            return false;
        } else {
            for (ItemsInfo item : itemList) {
                if (NullOrEmptyChecker.isNullOrEmpty(item.getDescId())) {
                    return false;
                }
                if (NullOrEmptyChecker.isNullOrEmpty(item.getItemId())) {
                    return false;
                }
            }
        }

        if (NullOrEmptyChecker.isNullOrEmpty(reqModel.getConstraint().getHistoryOrder())) {
            reqModel.getConstraint().setHistoryOrder(historyOrderAscending);
        }

        return true;
    }

}
