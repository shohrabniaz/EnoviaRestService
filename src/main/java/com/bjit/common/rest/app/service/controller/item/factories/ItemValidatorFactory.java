/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.factories;

import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemValidator;
import com.bjit.common.rest.app.service.controller.item.validators.AtonMVValidator;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemValidator;

/**
 *
 * @author BJIT
 */
public class ItemValidatorFactory {
    public IItemValidator getValidator(String validationType) {
        IItemValidator itemValidator;
        switch (validationType) {
            case ItemImportEnvironments.COMMON:
                itemValidator = new CommonItemValidator();
                break;
            case ItemImportEnvironments.ATON:
                itemValidator = new AtonMVValidator();
                break;
            default:
                itemValidator = new CommonItemValidator();
                break;
        }
        return itemValidator;
    }
}
