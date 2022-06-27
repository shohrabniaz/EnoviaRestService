package com.bjit.common.rest.app.service.controller.export.report.single_level.provider;

import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import matrix.db.BusinessObject;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *  Business Object provider class for Report Generation
 * @author BJIT
 */
public class BusinessObjectProvider {
    private static final Logger BUSINESS_OBJECT_PROCESSOR_LOGGER = Logger.getLogger(BusinessObjectProvider.class);
    
    /**
     * Provide business object based on Object Id or TNR
     * @param objectId
     * @param type
     * @param name
     * @param revision
     * @return
     * @throws MatrixException
     * @throws NullPointerException 
     */
    public BusinessObject provideBusinessObject(String objectId, String type, String name, String revision) throws MatrixException, NullPointerException {
        BusinessObject businessObject = null;
        if(NullOrEmptyChecker.isNullOrEmpty(objectId)) {
            String vault = "";
            try {
                vault = PropertyReader.getProperty("report.single.level.object.vault");
            } catch (NullPointerException e) {
                BUSINESS_OBJECT_PROCESSOR_LOGGER.error(PropertyReader.getProperty("report.single.level.object.vault.error"));
            }
            businessObject = new BusinessObject(type, name, revision, vault);
        }
        else if(NullOrEmptyChecker.isNullOrEmpty(name)) {
            businessObject = new BusinessObject(objectId);
        }
        return businessObject;
    }
    
    public void provideBusinessObject(ReportBusinessModel businessModel) throws MatrixException, NullPointerException {
        BusinessObject businessObject = BusinessObjectProvider.this.provideBusinessObject(businessModel.getParameter().getObjectId(), 
                businessModel.getParameter().getType(),
                businessModel.getParameter().getName(), 
                businessModel.getParameter().getRev());
        businessModel.setBusinessObject(businessObject);
    }
}
