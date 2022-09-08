/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.factories;

import com.bjit.common.rest.app.service.comos.mapper_processor.ComosItemMapperProcessor;
import com.bjit.common.rest.app.service.constants.ItemImportEnvironments;
import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.mapper_processors.AtonMVMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.mapper_processors.CommonItemMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.mapper_processors.MVItemMapperProcessor;

/**
 *
 * @author BJIT
 */
public class XMLAttributeMapperFactory {

    /**
     * Getting XML mapper processor according to mapperType
     *
     * @param mapperType Mapper Type
     * @return
     */
    public IXmlMapperProcessor getTypeRelationMapperProcessor(String mapperType) {
        IXmlMapperProcessor xmlMapper;
        switch (mapperType) {
            case ItemImportEnvironments.COMMON:
                xmlMapper = new CommonItemMapperProcessor();
                break;
            case ItemImportEnvironments.MASS_HW_IMPORT:
                xmlMapper = new MVItemMapperProcessor();
                break;
            case ItemImportEnvironments.MASS_MV_IMPORT:
                xmlMapper = new MVItemMapperProcessor();
                break;
            case ItemImportEnvironments.MASS_SWP_IMPORT:
                xmlMapper = new MVItemMapperProcessor();
                break;
            case ItemImportEnvironments.MASS_SP_IMPORT:
                xmlMapper = new MVItemMapperProcessor();
                break;
            case ItemImportEnvironments.MASS_MDP_IMPORT:
                xmlMapper = new MVItemMapperProcessor();
                break;
            case ItemImportEnvironments.ATON:
                xmlMapper = new AtonMVMapperProcessor();
				break;
            case ItemImportEnvironments.COMOS:
                xmlMapper = new ComosItemMapperProcessor();
                break;
            default:
                xmlMapper = new CommonItemMapperProcessor();
                break;
        }
        return xmlMapper;
    }
}
