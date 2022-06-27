/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.facade;

import com.bjit.common.rest.app.service.controller.bom.facades.CommonBOMImportFacade;
import com.bjit.ewc18x.utils.PropertyReader;

/**
 *
 * @author BJIT
 */
public class ComosBOMImportFacade extends CommonBOMImportFacade {

    @Override
    protected String getMappingFilePath(String source) {
        String mappingFilePath = PropertyReader.getProperty(source + ".bom.import.mapping.xml.directory");
        return mappingFilePath;
    }
}
