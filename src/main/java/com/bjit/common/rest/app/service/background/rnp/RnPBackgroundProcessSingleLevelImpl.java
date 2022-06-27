package com.bjit.common.rest.app.service.background.rnp;

import com.bjit.common.rest.app.service.background.processors.IBackGroundProcessor;
import com.bjit.common.rest.app.service.background.processors.IResponseSender;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportBusinessModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportDataModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.model.ReportParameterModel;
import com.bjit.common.rest.app.service.controller.export.report.single_level.provider.ReportDataProvider;
import com.bjit.common.rest.app.service.controller.export.report.single_level.validator.ReportParameterValidator;
import com.bjit.common.rest.app.service.mail.rnp.RnPMail;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailConfig;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailConfigPreparation;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailParameters;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailParamerterBuilder;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import com.bjit.mapper.mapproject.jasper_report.JasperReportGenerator;
import java.text.MessageFormat;
import java.util.Optional;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;

public class RnPBackgroundProcessSingleLevelImpl implements IBackGroundProcessor<File>, IResponseSender<File> {

    private static final Logger LOGGER = Logger.getLogger(RnPBackgroundProcessSingleLevelImpl.class);
    final private ReportParameterModel reportParameter;

    public RnPBackgroundProcessSingleLevelImpl(ReportParameterModel reportParameterModel) {
        this.reportParameter = reportParameterModel;
    }

    @Override
    public File process() throws Exception {
        return getReport();
    }

    @Override
    public Boolean send(File file) throws IOException, MessagingException, Exception {

        RnPMailConfigPreparation rnpMailConfigPreparation = new RnPMailConfigPreparation();

        RnPMailConfig rnPConfiguredMail = file.exists() ? rnpMailConfigPreparation.getRnPConfiguredMail(reportParameter, "RnP", "success") : rnpMailConfigPreparation.getRnPConfiguredMail(reportParameter, "RnP", "error");
        RnPMail rnpMail = new RnPMail();

        RnPMailParamerterBuilder rnpMailParamerterBuilder = new RnPMailParamerterBuilder();

        RnPMailParameters mailParameters = rnpMailParamerterBuilder.prepareMailParameter(file, this.reportParameter);
        rnpMail.sendMail(rnPConfiguredMail, mailParameters);

        return true;
    }

    protected File getReport() throws IOException, Exception {
        IResponse reportDataSource = new CustomResponseBuilder();
        ReportParameterValidator reportParameterValidator = new ReportParameterValidator();
        reportParameterValidator.validateReportParameter(reportParameter);
        ReportBusinessModel reportBusinessModel = new ReportBusinessModel();
        reportBusinessModel.setParameter(reportParameter);
        ReportDataProvider reportDataProvider = new ReportDataProvider();
        ReportDataModel reportDataModel = null;
        try {
            reportDataModel = reportDataProvider.provideReportData(reportBusinessModel);
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format(PropertyReader.getProperty("report.single.level.data.process.error"), e.getMessage()));
            throw e;
        }
        String singleLevelStructure = reportDataSource.setData(reportDataModel).setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.OK).buildResponse();

        Optional.ofNullable(singleLevelStructure).filter(structureData -> !structureData.isEmpty()).orElseThrow(()->new NullPointerException("Single level structure not found"));
        
        
        File reportGenerator = reportGenerator(reportBusinessModel, singleLevelStructure);

        return reportGenerator;
    }

    protected File reportGenerator(ReportBusinessModel reportBusinessModel, String singleLevelStructure) {
//        if (true) {
            JasperReportGenerator jasperReportGenerator = new JasperReportGenerator();
            try {
                Map<String, String> responseData = jasperReportGenerator.generateReport(singleLevelStructure, reportBusinessModel);
                String outputFile = responseData.get("filePath");
                if (!outputFile.isEmpty()) {
                    reportParameter.setIsFileGenerated(true);
                    if (reportParameter.isDownload()) {
                        return generateBOMReport(outputFile);
                    }
                } else {
                    LOGGER.debug(PropertyReader.getProperty("report.single.level.file.generation.failed"));
                }
            } catch (Exception exp) {
                LOGGER.error(exp.getMessage());
            } finally {
                jasperReportGenerator = null;
            }
//        }
//        else {
//            LOGGER.debug("Received data from ReportJsonData() method is empty.");
//        }
        return null;
    }

    protected File generateBOMReport(String outputFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(outputFile);
        File reportFile = path.toFile();
        reportParameter.setDownloadableFile(reportFile);
        return reportFile;
    }
}
