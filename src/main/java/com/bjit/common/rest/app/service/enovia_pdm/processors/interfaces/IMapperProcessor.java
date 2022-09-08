/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;


import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public interface IMapperProcessor {
    HashMap<String, HashMap<String, String>> processAttributeXMLMapper(CommonItemParameters commonItemParameters,String typeName) throws Exception;
    IMapperElementMemento getMapperElementMemento();
}
