/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;

/**
 *
 * @author BJIT
 */
public interface IMastershipAttributeBusinessLogic {
    String getItemAttribute(IMapperElementMemento mapperElementMemento, String attributeName, String value);
    String getRelationshipAttribute(IMapperElementMemento mapperElementMemento, String attributeName, String value);
}
