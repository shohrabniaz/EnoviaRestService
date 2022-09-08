/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.contracts;

import com.bjit.mapper.mapproject.builder.MapperBuilder;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;

/**
 *
 * @author BJIT
 */
public interface IExpand {
    public IExpand __init__(String mapFileType, MapperBuilder mapperBuilder, String mapFileDirectory) throws Exception;
    public ExpansionWithSelect getExpandedData(Context context, BusinessObject businessObject) throws Exception;
}
