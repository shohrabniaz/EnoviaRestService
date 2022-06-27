/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.interfaces;

import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ChildInfo;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.ParentInfo;
import java.util.HashMap;
import java.util.concurrent.Callable;
import matrix.db.Context;

/**
 *
 * @author BJIT
 */
public interface IBOMCreationProcessor extends Callable{
    void initilize(Context context, HashMap<ParentInfo, HashMap<String, ChildInfo>> parentChildInfoMap, CommonBOMImportParams commonBomImportVariables);
}
