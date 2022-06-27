package com.bjit.common.rest.app.service.controller.export.report.single_level.validator;

import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportParameterModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class ReportParameterValidator {
    private static final Logger RNP_PARAM_VALIDATOR_LOGGER = Logger.getLogger(ReportParameterValidator.class);
    
    public ReportParameterModel validateReportParameter(ReportParameterModel reportParameter) throws Exception {
        if (NullOrEmptyChecker.isNullOrEmpty(reportParameter.getFormat())) {
            String defaultFormat = PropertyReader.getProperty("report.single.level.default.format");
            reportParameter.setFormat(defaultFormat);
            RNP_PARAM_VALIDATOR_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.defaut.format.message"), defaultFormat));
        }
        
        if (NullOrEmptyChecker.isNullOrEmpty(reportParameter.getType())) {
            String defaultType = PropertyReader.getProperty("report.single.level.default.type");
            reportParameter.setType(defaultType);
            RNP_PARAM_VALIDATOR_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.defaut.type.message"), defaultType));
        }
        
        if (NullOrEmptyChecker.isNullOrEmpty(reportParameter.getLang())) {
            String defaultLang = PropertyReader.getProperty("report.single.level.default.lang");
            reportParameter.setLang(defaultLang);
            RNP_PARAM_VALIDATOR_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.defaut.lang.message"), defaultLang));
        }
        
        if (NullOrEmptyChecker.isNullOrEmpty(reportParameter.getExpandLevel())) {
            String defaultExpandLevel = PropertyReader.getProperty("report.single.level.default.expand.level");
            reportParameter.setExpandLevel(defaultExpandLevel);
            RNP_PARAM_VALIDATOR_LOGGER.info(MessageFormat.format(PropertyReader.getProperty("report.single.level.defaut.expand.level.message"), defaultExpandLevel));
        }
        
        if (NullOrEmptyChecker.isNullOrEmpty(reportParameter.getName()) && NullOrEmptyChecker.isNullOrEmpty(reportParameter.getObjectId())) {
            String error = PropertyReader.getProperty("report.single.level.param.object.id.name.missing");
            RNP_PARAM_VALIDATOR_LOGGER.error(error);
            throw new Exception(error);
        }
        
        if (!NullOrEmptyChecker.isNullOrEmpty(reportParameter.getFormat()) && 
                !Arrays.asList(PropertyReader.getProperty("report.single.level.allowed.format").split(",")).contains(reportParameter.getFormat().toLowerCase())) {
            String error = MessageFormat.format(PropertyReader.getProperty("report.single.level.allowed.format.message"), reportParameter.getFormat());
            RNP_PARAM_VALIDATOR_LOGGER.error(error);
            throw new Exception(error);
        }
        
        if (!NullOrEmptyChecker.isNullOrEmpty(reportParameter.getLang()) && 
                !PropertyReader.getProperties("report.single.level.allowed.lang", true).containsKey(reportParameter.getLang().toLowerCase())) {
            String error = MessageFormat.format(PropertyReader.getProperty("report.single.level.allowed.lang.message"), reportParameter.getLang());
            RNP_PARAM_VALIDATOR_LOGGER.error(error);
            throw new Exception(error);
        }
        
        if (reportParameter.getRequestId() == null) {
            reportParameter.setRequestId("");
        }
        
        return reportParameter;
    }
}