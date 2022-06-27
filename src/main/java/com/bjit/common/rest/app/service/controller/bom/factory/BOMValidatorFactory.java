/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.factory;

import com.bjit.common.rest.app.service.comos.validators.ComosBOMValidator;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.bom.interfaces.IBomValidator;
import com.bjit.common.rest.app.service.controller.bom.validators.CommonBOMValidator;
import com.bjit.common.rest.app.service.controller.bom.validators.HagForsBOMValidator;

/**
 *
 * @author BJIT
 */
public class BOMValidatorFactory {

    private static final org.apache.log4j.Logger BOM_VALIDATOR_FACTORY_LOGGER = org.apache.log4j.Logger.getLogger(BOMValidatorFactory.class);

    public IBomValidator getValidation(String validationType) {
        switch (validationType) {
            case ItemImportEnvironments.COMMON:
                return new CommonBOMValidator();
            case ItemImportEnvironments.MOPAZ:
                return new HagForsBOMValidator();
            case ItemImportEnvironments.COMOS:
                return new ComosBOMValidator();
        }

        String error = "Source environment '" + validationType + "' not has not been allowed for BOM validation";
        BOM_VALIDATOR_FACTORY_LOGGER.info(error);
        throw new NullPointerException(error);
    }
}
