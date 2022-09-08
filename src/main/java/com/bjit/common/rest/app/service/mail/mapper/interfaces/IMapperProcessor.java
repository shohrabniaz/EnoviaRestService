/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mapper.interfaces;

import com.bjit.common.rest.app.service.mail.mapper.models.Templates;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public interface IMapperProcessor {
    HashMap<String, Templates> processAttributeXMLMapper() throws Exception;
//    IMapperElementMemento getMapperElementMemento();
}
