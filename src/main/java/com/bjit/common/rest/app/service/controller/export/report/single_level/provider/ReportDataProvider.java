package com.bjit.common.rest.app.service.controller.export.report.single_level.provider;

import com.bjit.common.rest.app.service.controller.export.ReportUtilities;
import com.bjit.common.rest.app.service.controller.export.report.single_level.biz_logic.SLReportBusinessLogic;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportDataModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.util.HashMap;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ReportDataProvider {
    private static final Logger REPORT_DATA_PROVIDER = Logger.getLogger(ReportDataProvider.class);
    
    /**
     * Provides required data for report generation. 
     * 
     * @param businessModel the business model class which is required for data processing
     * @return an Object of ReportDataModel class
     * @throws MatrixException
     * @throws IOException
     * @throws Exception 
     * @see ReportDataModel
     */
    public ReportDataModel provideReportData(ReportBusinessModel businessModel) throws MatrixException, IOException, Exception  {
        ReportUtilities.enableGraphicsSupport();
        getMapAbsoluteDirectoryByType(businessModel);
        
        ReportAttributeProvider attributeProvider = new ReportAttributeProvider();
        attributeProvider.provideAttributeData(businessModel);
        
        BusinessObjectProvider businessObjectProvider = new BusinessObjectProvider();
        businessObjectProvider.provideBusinessObject(businessModel);
        
        try {
            businessModel.getBusinessObject().open(businessModel.getParameter().getContext());
        } catch (MatrixException e) {
            REPORT_DATA_PROVIDER.error(PropertyReader.getProperty("report.single.level.root.object.open.error"));
            throw e;
        }
        
        SLReportBusinessLogic reportBusinessLogic = new SLReportBusinessLogic(businessModel.getMapAbsoluteDirectory());
        ReportDataModel reportDataModel = reportBusinessLogic.prepareReportData(businessModel);
        
        try {
            businessModel.getBusinessObject().close(businessModel.getParameter().getContext());
        } catch (MatrixException e) {
            REPORT_DATA_PROVIDER.error(PropertyReader.getProperty("report.single.level.root.object.close.error"));
        }
        
        return reportDataModel;
    }
    
    /**
     * Provide absolute directory path based on report's root object type which will be used to fetch XML map
     * @param type
     * @throws Exception 
     */
    private void getMapAbsoluteDirectoryByType(ReportBusinessModel businessModel) throws Exception {
        HashMap<String, String> reportAbsoluteDirectoryMap = new HashMap<>();
        try {
            reportAbsoluteDirectoryMap = PropertyReader.getProperties("reporting.printing.map.directory", true);
        } catch (Exception e) {
            String error = PropertyReader.getProperty("report.single.level.xml.map.directory.error");
            REPORT_DATA_PROVIDER.error(error + e);
            throw new Exception(error + e);
        }
        if(NullOrEmptyChecker.isNullOrEmpty(reportAbsoluteDirectoryMap)) {
            throw new Exception(PropertyReader.getProperty("report.single.level.xml.map.directory.error"));
        }
        String mapAbsoluteDirectory = reportAbsoluteDirectoryMap.get(businessModel.getParameter().getType());
        businessModel.setMapAbsoluteDirectory(mapAbsoluteDirectory);
    }
}