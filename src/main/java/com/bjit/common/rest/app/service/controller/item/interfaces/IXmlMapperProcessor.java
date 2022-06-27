/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.interfaces;

import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public interface IXmlMapperProcessor {
    HashMap<String, String> processAttributeXMLMapper(CommonItemParameters commonItemParameters) throws Exception;
    HashMap<String, String> getDestinationSourceMap();
    List<String> getRunTimeInterfaceList();
}
