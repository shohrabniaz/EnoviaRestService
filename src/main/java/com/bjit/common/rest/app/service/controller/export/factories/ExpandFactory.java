/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.factories;

import com.bjit.common.rest.app.service.controller.export.contracts.IExpand;

/**
 *
 * @author BJIT
 */
public class ExpandFactory {
    public static synchronized IExpand getExpansionObject(String expandType) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
        try {
            String className = "com.bjit.common.rest.app.service.controller.export.expansions." + expandType + "Expand";
            Class<?> expandObjectClass = Class.forName(className);
            IExpand expandObject = (IExpand) expandObjectClass.newInstance();
            return expandObject;
        } catch (ClassNotFoundException exp) {
            throw exp;
        }
    }
}
