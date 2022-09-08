/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.interfaces;

import com.bjit.common.rest.app.service.controller.bom.processor.CommonBOMImportParams;
import com.bjit.common.rest.app.service.model.createBOM.CreateBOMBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.bomResponseMessageFormatter.BOMDataCollector;
import java.io.IOException;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
public interface IBomValidator {
    BOMDataCollector bomValidationAndDataCollection(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateBOMBean createBOMBean, CommonBOMImportParams commonBomImportVariables) throws IOException, MatrixException, Exception;
}
