/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.factory;

import com.bjit.common.rest.app.service.comos.creator.ComosBOMCreationProcessor;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMCreationProcessor;
import com.bjit.common.rest.app.service.controller.bom.processor.HagForsBOMCreationProcessor;
import com.bjit.common.rest.app.service.controller.bom.interfaces.IBOMCreationProcessor;

/**
 *
 * @author BJIT
 */
public class BOMCreationProcessorFactory {

    public IBOMCreationProcessor getBomCreationProcess(String sourceEnvironment) {
        switch (sourceEnvironment) {
            case ItemImportEnvironments.COMMON:
                return new CommonBOMCreationProcessor();
            case ItemImportEnvironments.MOPAZ:
                return new HagForsBOMCreationProcessor();
            case ItemImportEnvironments.COMOS:
                return new ComosBOMCreationProcessor();

        }
        throw new RuntimeException("'" + sourceEnvironment + "' has not been allowed for BOM import");
    }
}
