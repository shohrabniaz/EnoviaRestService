/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.processors;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.controller.export.model.ExportModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.ExpandBusinessObject;
import com.bjit.mapper.mapproject.jsonOutput.Results;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT
 */
public class ExportProcessor {

    private ExportModel exportModel;
    private Context context;
    private static final org.apache.log4j.Logger EXPORT_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(ExportProcessor.class);

    public ExportProcessor(ExportModel exportModel, Context context) {
        this.setExportModel(exportModel);
        this.setContext(context);
    }
    
    public Results getExpandedData() throws Exception {
        try {
            ExpandBusinessObject expandBusinessObject = new ExpandBusinessObject(PropertyReader.getProperty("export.mapper.xml.file.location") + this.getExportModel().getExportType() + ".xml");
            return expandBusinessObject.getExportResultFromByExpandingBusinessObject(this.getContext(), getObject());
        } catch (Exception exp) {
            EXPORT_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private BusinessObject getObject() throws MatrixException {
        try {
            TNR tnr = this.getExportModel().getTnr();
            EXPORT_PROCESSOR_LOGGER.debug("Object Type : " + tnr.getType());
            EXPORT_PROCESSOR_LOGGER.debug("Object Name : " + tnr.getName());

            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
            BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
            
            String objectId = businessObjectUtility.searchByTypeName(this.getContext(), tnr.getType(), tnr.getName());
            EXPORT_PROCESSOR_LOGGER.debug("Object Id : " + objectId);

            if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                EXPORT_PROCESSOR_LOGGER.error("'" + tnr.getName() + "' is not exist");
                throw new NullPointerException("'" + tnr.getName() + "' is not exist");
            }

            
            return businessObjectOperations.getObject(objectId);

        } catch (MatrixException exp) {
            EXPORT_PROCESSOR_LOGGER.error(exp);
            throw exp;
        }
    }

    private void validateExportModel(ExportModel exportModel) {

    }

    private ExportModel getExportModel() {
        return exportModel;
    }

    private void setExportModel(ExportModel exportModel) {
        this.exportModel = exportModel;
    }

    private Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
