/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.factories;

import com.bjit.common.rest.app.service.comos.creator.ComosItemCloner;
import com.bjit.common.rest.app.service.comos.creator.ComosItemCreator;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.item.creators.CommonItemCreator;
import com.bjit.common.rest.app.service.controller.item.creators.HPItemCreator;
import com.bjit.common.rest.app.service.controller.item.creators.MDItemCreator;
import com.bjit.common.rest.app.service.controller.item.creators.MVItemCreator;
import com.bjit.common.rest.app.service.controller.item.creators.SPItemCreator;
import com.bjit.common.rest.app.service.controller.item.creators.SWItemCreator;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemCreationProcessor;
import com.bjit.ewc18x.utils.PropertyReader;

/**
 *
 * @author BJIT
 */
public class ItemCreationProcessorFactory {
    public IItemCreationProcessor getItemCreationProcessor(String creationProcessor) {
        IItemCreationProcessor itemCreationProcessor;
        switch (creationProcessor) {
            case ItemImportEnvironments.COMMON:
                itemCreationProcessor = new CommonItemCreator();
                break;
            case ItemImportEnvironments.MASS_MV_IMPORT:
                itemCreationProcessor = new MVItemCreator();
                break;
            case ItemImportEnvironments.MASS_HW_IMPORT:
                itemCreationProcessor = new HPItemCreator();
                break;
            case ItemImportEnvironments.COMOS:
                itemCreationProcessor = Boolean.parseBoolean(PropertyReader.getProperty("comos.item.cloner")) ? new ComosItemCloner() : new ComosItemCreator();
            case ItemImportEnvironments.MASS_SWP_IMPORT:
                itemCreationProcessor = new SWItemCreator();
                break;
            case ItemImportEnvironments.MASS_SP_IMPORT:
                itemCreationProcessor = new SPItemCreator();
                break;
            case ItemImportEnvironments.MASS_MDP_IMPORT:
                itemCreationProcessor = new MDItemCreator();
                break;
            default:
                itemCreationProcessor = new CommonItemCreator();
                break;
        }
        return itemCreationProcessor;
    }
}