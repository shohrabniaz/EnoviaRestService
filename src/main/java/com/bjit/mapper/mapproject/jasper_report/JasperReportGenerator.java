package com.bjit.mapper.mapproject.jasper_report;

import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.bjit.mapper.mapproject.report_mapping_model.ReportMappingData;
import com.bjit.mapper.mapproject.util.CommonUtil;
import com.bjit.mapper.mapproject.util.Constants;
import com.bjit.report_lang_properties.LanguagePropertyReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.logging.Level;
import matrix.db.BusinessObject;
import matrix.db.Context;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Tareq Sefati
 *
 */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
public class JasperReportGenerator {

    private static final Logger JASPER_REPORT_GENERATOR_LOGGER = Logger.getLogger(JasperReportGenerator.class);
    private String type = null;
    private boolean isSummaryRequired = false;
    private boolean isMBOMReport = false;

    public Map<String, String> generateReport(String jsonString, HashMap rootItemParams, HashMap deliveryItemParams, String format, String lang, String reportId) {
        Map<String, String> response = new HashMap<>();
        String outputFilePath = CommonUtil.createOutputDirectory();
        try {
            try {
                String rawJsonData = jsonString;
                if (!rawJsonData.isEmpty()) {
                    //JasperReport report = (JasperReport) JRLoader.loadObject(new File(CommonUtil.selectTemplateFile(lang)));
                    String jasperReportFile = PropertyReader.getProperty("template.file.directory");
                    Resource resource = new ClassPathResource(jasperReportFile);
                    JasperReport report = (JasperReport) JRLoader.loadObject(resource.getFile());

                    Map populateReportParameters;

                    populateReportParameters = NullOrEmptyChecker.isNullOrEmpty(this.type) ? populateReportParameters(rootItemParams, lang) : populateReportParameters(rootItemParams, lang, this.type);
                    populateReportParameters.put(JRParameter.IS_IGNORE_PAGINATION, format.equalsIgnoreCase(Constants.XLS));
                    if (deliveryItemParams != null) {
                        populateReportParameters.putAll(deliveryItemParams);
                    }
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(rawJsonData.getBytes("UTF-8"));
                    try {
                        JsonDataSource jsonDataSource = new JsonDataSource(jsonDataStream);
                        JasperPrint jasperPrint;

                        jasperPrint = JasperFillManager.fillReport(report, populateReportParameters, jsonDataSource);

                        String rootObjectType = (String) rootItemParams.get("rootObjectType");
                        String rootObjectName = (String) rootItemParams.get("rootObjectName");
                        String fileNameSuffix = CommonUtil.getFileNameSuffix(rootObjectType, rootObjectName);
                        String outputFilePathWithName;
                        if (reportId.isEmpty()) {
                            response.put("fileId", fileNameSuffix);
                            outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, fileNameSuffix);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory: " + outputFilePathWithName);
                        } else {
                            response.put("fileId", reportId);
                            outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, reportId);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory(report id): " + outputFilePathWithName);
                        }

                        //String reportFormat = new ReportMappingData().getReportFormat();
                        String reportFormat = format;
                        if (reportFormat.equalsIgnoreCase(Constants.PDF)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated.\n" + outputFilePathWithName);
                        } else if (reportFormat.equalsIgnoreCase(Constants.XLS)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.XLS;
                            JRXlsExporter xlsExporter = new JRXlsExporter();
                            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFilePathWithName);
                            xlsExporter.exportReport();
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Xlx report is generated.\n" + outputFilePathWithName);
                        } else {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated by default.(Invalid report format)\n" + outputFilePathWithName);
                        }

                        jasperPrint = null;
                        jsonDataSource = null;
                        JASPER_REPORT_GENERATOR_LOGGER.debug("Report is created.");
                        response.put("filePath", outputFilePathWithName);

                    } catch (Exception exp) {
                        JASPER_REPORT_GENERATOR_LOGGER.error(exp);
                    } finally {
                        jsonDataStream.close();
                    }
                } else {
                    JASPER_REPORT_GENERATOR_LOGGER.debug("Could not found data. Report is not created.");
                    response.put("filePath", "");
                }
            } catch (JRException ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            } catch (Exception ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            }
        } catch (Exception ex) {
            JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
        }
        return response;
    }

    public Map<String, String> generateReport(String jsonString, HashMap rootItemParams, HashMap deliveryItemParams, String format, String lang, String reportId, String type) {
        this.type = type;
        return generateReport(jsonString, rootItemParams, deliveryItemParams, format, lang, reportId);
    }

    public Map<String, String> generateReport(String responseBomData, HashMap rootItemParams, HashMap deliveryItemParams, String format, String lang, String requestId, boolean isSummaryRequired, String type, boolean isMBOMReport) {
        Map<String, String> response = new HashMap<>();
        String outputFilePath = CommonUtil.createOutputDirectory();
        try {
            try {
                if (!responseBomData.isEmpty()) {
                    //JasperReport report = (JasperReport) JRLoader.loadObject(new File(CommonUtil.selectTemplateFile(lang)));
                    String jasperReportFile = PropertyReader.getProperty("template.file.directory");
                    Resource resource = new ClassPathResource(jasperReportFile);
                    JasperReport report = (JasperReport) JRLoader.loadObject(resource.getFile());
                    this.isSummaryRequired = isSummaryRequired;
                    this.type = type;
                    this.isMBOMReport = isMBOMReport;
                    Map populateReportParameters = NullOrEmptyChecker.isNullOrEmpty(this.type) ? populateReportParameters(rootItemParams, lang) : populateReportParameters(rootItemParams, lang, this.type);
                    if (deliveryItemParams != null) {
                        populateReportParameters.putAll(deliveryItemParams);
                    }
                    populateReportParameters.put(JRParameter.IS_IGNORE_PAGINATION, format.equalsIgnoreCase(Constants.XLS));

                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(responseBomData.getBytes("UTF-8"));
                    try {
                        JsonDataSource jsonDataSource = new JsonDataSource(jsonDataStream);
                        JasperPrint jasperPrint;

                        jasperPrint = JasperFillManager.fillReport(report, populateReportParameters, jsonDataSource);

                        String rootObjectType = (String) rootItemParams.get("rootObjectType");
                        String rootObjectName = (String) rootItemParams.get("rootObjectName");
                        String outputFilePathWithName, fileName;
                        if (NullOrEmptyChecker.isNullOrEmpty(requestId)) {
                            fileName = CommonUtil.getFileNameSuffix(rootObjectType, rootObjectName);
                        } else {
                            fileName = CommonUtil.getFileNameWithReportId(rootObjectType, rootObjectName, requestId);
                        }
                        response.put("fileId", fileName);
                        outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, fileName);
                        JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory(report id): " + outputFilePathWithName);
                        //String reportFormat = new ReportMappingData().getReportFormat();
                        String reportFormat = format;
                        if (reportFormat.equalsIgnoreCase(Constants.PDF)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated.\n" + outputFilePathWithName);
                        } else if (reportFormat.equalsIgnoreCase(Constants.XLS)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.XLS;
                            JRXlsExporter xlsExporter = new JRXlsExporter();
                            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFilePathWithName);

                            xlsExporter.exportReport();
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Xlx report is generated.\n" + outputFilePathWithName);
                        } else {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated by default.(Invalid report format)\n" + outputFilePathWithName);
                        }

                        jasperPrint = null;
                        jsonDataSource = null;
                        JASPER_REPORT_GENERATOR_LOGGER.debug("Report is created.");
                        response.put("filePath", outputFilePathWithName);

                    } catch (Exception exp) {
                        JASPER_REPORT_GENERATOR_LOGGER.error(exp);
                    } finally {
                        jsonDataStream.close();
                    }
                } else {
                    JASPER_REPORT_GENERATOR_LOGGER.debug("Could not found data. Report is not created.");
                    response.put("filePath", "");
                }
            } catch (JRException ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            } catch (Exception ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            }
        } catch (Exception ex) {
            JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
        }
        return response;
    }

    public Map<String, String> generateBomExportReport(String responseBomData, HashMap rootItemParams, String format, String lang, String requestId, String reportId, String type) {
        Map<String, String> response = new HashMap<>();
        String outputFilePath = CommonUtil.createOutputDirectory();
        try {
            try {
                if (!responseBomData.isEmpty()) {
                    //JasperReport report = (JasperReport) JRLoader.loadObject(new File(CommonUtil.selectTemplateFile(lang)));
                    String jasperReportFile = PropertyReader.getProperty("template.file.directory");
                    Resource resource = new ClassPathResource(jasperReportFile);
                    JasperReport report = (JasperReport) JRLoader.loadObject(resource.getFile());
                    this.type = type;
                    Map populateReportParameters = NullOrEmptyChecker.isNullOrEmpty(this.type) ? populateReportParameters(rootItemParams, lang) : populateReportParameters(rootItemParams, lang, this.type);
                    populateReportParameters.put(JRParameter.IS_IGNORE_PAGINATION, format.equalsIgnoreCase(Constants.XLS));

                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(responseBomData.getBytes("UTF-8"));
                    try {
                        JsonDataSource jsonDataSource = new JsonDataSource(jsonDataStream);
                        JasperPrint jasperPrint;

                        jasperPrint = JasperFillManager.fillReport(report, populateReportParameters, jsonDataSource);

                        String rootObjectType = (String) rootItemParams.get("rootObjectType");
                        String rootObjectName = (String) rootItemParams.get("rootObjectName");
                        String fileNameSuffix = CommonUtil.getFileNameSuffix(rootObjectType, rootObjectName);
                        String outputFilePathWithName;
                        if (reportId.isEmpty()) {
                            response.put("fileId", fileNameSuffix);
                            outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, fileNameSuffix);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory: " + outputFilePathWithName);
                        } else {
                            response.put("fileId", reportId);
                            outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, reportId);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory(report id): " + outputFilePathWithName);
                        }

                        //String reportFormat = new ReportMappingData().getReportFormat();
                        String reportFormat = format;
                        if (reportFormat.equalsIgnoreCase(Constants.PDF)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated.\n" + outputFilePathWithName);
                        } else if (reportFormat.equalsIgnoreCase(Constants.XLS)) {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.XLS;
                            JRXlsExporter xlsExporter = new JRXlsExporter();
                            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFilePathWithName);

                            xlsExporter.exportReport();
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Xlx report is generated.\n" + outputFilePathWithName);
                        } else {
                            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
                            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
                            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated by default.(Invalid report format)\n" + outputFilePathWithName);
                        }

                        jasperPrint = null;
                        jsonDataSource = null;
                        JASPER_REPORT_GENERATOR_LOGGER.debug("Report is created.");
                        response.put("filePath", outputFilePathWithName);

                    } catch (Exception exp) {
                        JASPER_REPORT_GENERATOR_LOGGER.error(exp);
                    } finally {
                        jsonDataStream.close();
                    }
                } else {
                    JASPER_REPORT_GENERATOR_LOGGER.debug("Could not found data. Report is not created.");
                    response.put("filePath", "");
                }
            } catch (JRException ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            } catch (Exception ex) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
            }
        } catch (Exception ex) {
            JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + ex.getMessage());
        }
        return response;
    }

    private Map populateReportParameters(HashMap rootItemParams, String lang) {
        Map parameters = new HashMap();
        //JsonOutput jsonOutput = new JsonOutput();
        parameters.put("imageLocation", getClass().getClassLoader().getResource("/img/valmet.jpg").toString());
        parameters.put("printStartEndPage", new ReportMappingData().isPrintStartEndPage());
        parameters.put("isSummaryRequired", this.isSummaryRequired);
        parameters.put("isMBOMReport", this.isMBOMReport);
        parameters.putAll(new LanguagePropertyReader().getLabel(lang));
        parameters.putAll(rootItemParams);
        JASPER_REPORT_GENERATOR_LOGGER.debug("Report parameters: " + parameters.toString());
        return parameters;
    }

    private Map populateReportParameters(HashMap rootItemParams, String lang, String type) {
        Map parameters = new HashMap();
        //JsonOutput jsonOutput = new JsonOutput();
        parameters.put("imageLocation", getClass().getClassLoader().getResource("/img/valmet.jpg").toString());
        parameters.put("printStartEndPage", new ReportMappingData().isPrintStartEndPage());
        parameters.put("isSummaryRequired", this.isSummaryRequired);
        parameters.put("isMBOMReport", this.isMBOMReport);
        parameters.putAll(new LanguagePropertyReader().getLabel(lang, type));
        parameters.putAll(rootItemParams);
        JASPER_REPORT_GENERATOR_LOGGER.debug("Report parameters: " + parameters.toString());
        return parameters;
    }

    public Map<String, String> generateReport(String datasource, ReportBusinessModel businessModel) {
        Map<String, String> response = new HashMap<>();
        try {
            setNameofTheReportIfNull(businessModel);
           
            String outputFilePath = CommonUtil.createOutputDirectory();
            if (!NullOrEmptyChecker.isNullOrEmpty(datasource)) {
                String jasperReportTemplateLocation = PropertyReader.getProperty("single.level.main.template.file.directory");
                Resource resource = new ClassPathResource(jasperReportTemplateLocation);
                JasperReport reportObject = (JasperReport) JRLoader.loadObject(resource.getFile());
                
//                boolean isMBOMReport = businessModel.getParameter().getIsMBOMReport();
//                this.isMBOMReport = isMBOMReport;
                
                ByteArrayInputStream jsonDataStream = null;
                JasperPrint jasperPrint = null;
                JsonDataSource jsonDataSource = null;
                try {
                    jsonDataStream = new ByteArrayInputStream(datasource.getBytes("UTF-8"));
                    jsonDataSource = new JsonDataSource(jsonDataStream);
                    Map populateReportParameters = populateReportParameters(businessModel);
                    jasperPrint = JasperFillManager.fillReport(reportObject, populateReportParameters, jsonDataSource);
                    populateReportFilenameAndOutputPath(businessModel, response, outputFilePath, jasperPrint);
                } finally {
                    if (!NullOrEmptyChecker.isNull(jasperPrint)) {
                        jasperPrint = null;
                    }
                    if (!NullOrEmptyChecker.isNull(jsonDataSource)) {
                        jsonDataSource = null;
                    }
                    if (!NullOrEmptyChecker.isNull(jsonDataStream)) {
                        jsonDataStream.close();
                    }
                }
            } else {
                response.put("filePath", "");
            }
        } catch (Exception e) {
            JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + e.getMessage());
            response.put("filePath", "");
        }
        return response;
    }

    private void setNameofTheReportIfNull(ReportBusinessModel businessModel) throws Exception {
        Context context = businessModel.getParameter().getContext();
        String objectName = businessModel.getParameter().getName();
        String objectId = businessModel.getParameter().getObjectId();

        if (NullOrEmptyChecker.isNullOrEmpty(objectName)) {
            try {
                BusinessObject businessObject = new BusinessObject(objectId);
                businessObject.open(context);
                
                String getObjectName = businessObject.getName();
                businessModel.getParameter().setName(getObjectName);
                
                businessObject.close(context);
            } catch (Exception exp) {
                JASPER_REPORT_GENERATOR_LOGGER.error("Error: " + exp.getMessage());
                throw exp;
            }
        }

    }

    private Map populateReportParameters(ReportBusinessModel businessModel) throws IOException {
        Map parameters = new HashMap();
        parameters.put("imageLocation", getClass().getClassLoader().getResource("/img/valmet.jpg").toString());
        parameters.put("printStartEndPage", new ReportMappingData().isPrintStartEndPage());
        parameters.put("isSummaryRequired", Boolean.parseBoolean(businessModel.getParameter().getIsSummaryRequired()));
        parameters.put("isMBOMReport", (businessModel.getParameter().getIsMBOMReport()));
        if (NullOrEmptyChecker.isNullOrEmpty(businessModel.getParameter().getType())) {
            parameters.putAll(new LanguagePropertyReader().getLabel(businessModel.getParameter().getLang()));
        } else {
            parameters.putAll(new LanguagePropertyReader().getLabel(businessModel.getParameter().getLang(),
                    businessModel.getParameter().getType()));
        }
        parameters.put(JRParameter.IS_IGNORE_PAGINATION,
                businessModel.getParameter().getFormat()
                        .equalsIgnoreCase(PropertyReader.getProperty("report.single.level.xls.format")));
        parameters.put("lbl_footer_description", businessModel.isIsMBOMReport()
                ? parameters.get("mbom_lbl_footer_description")
                : parameters.get("ebom_lbl_footer_description"));

        String detailReportFile = PropertyReader.getProperty("single.level.detail.template.file.directory");
        try {
            Resource detailResource = new ClassPathResource(detailReportFile);
            parameters.put("detailReportPath", detailResource.getFile().getCanonicalPath());
        } catch (IOException e) {
            JASPER_REPORT_GENERATOR_LOGGER.error(MessageFormat.format(PropertyReader.getProperty("report.sub.template.directory.error"), "detail"));
            throw e;
        }
        if(Boolean.parseBoolean(businessModel.getParameter().getIsSummaryRequired())){
        String summaryReportFile = PropertyReader.getProperty("single.level.summary.template.file.directory");
        try {
            Resource summaryResource = new ClassPathResource(summaryReportFile);
            parameters.put("summaryReportPath", summaryResource.getFile().getCanonicalPath());
        } catch (IOException e) {
            JASPER_REPORT_GENERATOR_LOGGER.error(MessageFormat.format(PropertyReader.getProperty("report.sub.template.directory.error"), "summary"));
            throw e;
        }
    }

        if (Boolean.parseBoolean(businessModel.getParameter().getPrintDelivery())) {
            parameters.put(PropertyReader.getProperty("delivery.main.project.tile"), businessModel.getParameter().getMainProjTitle());
            parameters.put(PropertyReader.getProperty("delivery.project.search.key"), businessModel.getParameter().getPsk());
            parameters.put(PropertyReader.getProperty("delivery.subtitle"), businessModel.getParameter().getSubTitle());
            parameters.put(PropertyReader.getProperty("delivery.product"), businessModel.getParameter().getProduct());
        }
        JASPER_REPORT_GENERATOR_LOGGER.debug("Report parameters: " + parameters.toString());
        return parameters;
    }

    private void populateReportFilenameAndOutputPath(ReportBusinessModel businessModel, Map<String, String> response, String outputFilePath, JasperPrint jasperPrint) throws JRException {
        String outputFilePathWithName, fileName;
        if (NullOrEmptyChecker.isNullOrEmpty(businessModel.getParameter().getRequestId())) {
            fileName = CommonUtil.getFileNameSuffix(businessModel.getParameter().getType(),
                    businessModel.getParameter().getName());
        } else {
            fileName = CommonUtil.getFileNameWithReportId(businessModel.getParameter().getType(),
                    businessModel.getParameter().getName(),
                    businessModel.getParameter().getRequestId());
        }
        response.put("fileId", fileName);
        outputFilePathWithName = CommonUtil.generateOutputFileName(outputFilePath, fileName);
        JASPER_REPORT_GENERATOR_LOGGER.debug("Created Output file directory(report id): " + outputFilePathWithName);
        if (businessModel.getParameter().getFormat().equalsIgnoreCase(Constants.PDF)) {
            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated." + outputFilePathWithName);
        } else if (businessModel.getParameter().getFormat().equalsIgnoreCase(Constants.XLS)) {
            outputFilePathWithName = outputFilePathWithName + "." + Constants.XLS;
            JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFilePathWithName);

            xlsExporter.exportReport();
            JASPER_REPORT_GENERATOR_LOGGER.debug("Xlx report is generated.\n" + outputFilePathWithName);
        } else {
            outputFilePathWithName = outputFilePathWithName + "." + Constants.PDF;
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePathWithName);
            JASPER_REPORT_GENERATOR_LOGGER.debug("Pdf report is generated by default.(Unsupported report format)." + outputFilePathWithName);
        }
        response.put("filePath", outputFilePathWithName);
    }
}
