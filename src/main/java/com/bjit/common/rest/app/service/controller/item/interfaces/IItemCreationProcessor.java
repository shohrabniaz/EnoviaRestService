/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.interfaces;

import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.matrixone.apps.domain.util.FrameworkException;

/**
 *
 * @author BJIT
 */
public interface IItemCreationProcessor {
    String processCreateItemOperation(CommonItemParameters commonItemParameters) throws FrameworkException, Exception;
}
