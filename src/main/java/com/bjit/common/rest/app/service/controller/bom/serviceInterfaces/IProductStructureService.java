/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.serviceInterfaces;

import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface IProductStructureService {
    <T, K> K importStructure(Context context, T structureModel, IResponse responseBuilder);
}
