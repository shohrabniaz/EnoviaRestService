/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.enovia_cpq.validator;

import com.bjit.common.rest.app.service.model.mvEnoviaItem.ItemInfo;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.Items;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class MVRequestValidator {

    public static boolean validateRequestData(Items items) {

        List<ItemInfo> itemInfoList = items.getItems();

        for (ItemInfo item : itemInfoList) {
            if (NullOrEmptyChecker.isNullOrEmpty(item.getPhysicalid())) {
                if (NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getType())
                        || NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getName()) || NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getRevision())) {

                    return false;
                }
            }
        }
        return true;
    }
}
