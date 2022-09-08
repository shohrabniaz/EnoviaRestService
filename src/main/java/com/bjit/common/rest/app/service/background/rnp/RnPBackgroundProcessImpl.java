package com.bjit.common.rest.app.service.background.rnp;

import com.bjit.common.rest.app.service.background.processors.IBackGroundProcessor;
import com.bjit.common.rest.app.service.background.processors.IResponseSender;
import com.bjit.common.rest.app.service.mail.rnp.RnPMail;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailConfig;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailConfigPreparation;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailParameters;
import com.bjit.common.rest.app.service.mail.rnp.RnPMailParamerterBuilder;
import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.mapper.mapproject.util.CommonUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.bjit.mapper.mapproject.jasper_report.JasperReportGenerator;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;

public class RnPBackgroundProcessImpl implements IBackGroundProcessor<File>, IResponseSender<File> {

    private static final Logger RNP_BACKGROUND_PROCESS_IMPL_LOGGER = Logger.getLogger(RnPBackgroundProcessImpl.class);
    final private RnPModel rnpModel;

    public RnPBackgroundProcessImpl(RnPModel rnpModel) {
        this.rnpModel = rnpModel;
    }

    @Override
    public File process() throws Exception {
        return getReport(/*this.rnpModel*/);
    }

    @Override
    public Boolean send(File file) throws IOException, MessagingException, Exception {

        RnPMailConfigPreparation rnpMailConfigPreparation = new RnPMailConfigPreparation();

        RnPMailConfig rnPConfiguredMail = file.exists() ? rnpMailConfigPreparation.getRnPConfiguredMail(rnpModel, "RnP", "success") : rnpMailConfigPreparation.getRnPConfiguredMail(rnpModel, "RnP", "error");
        RnPMail rnpMail = new RnPMail();

        RnPMailParamerterBuilder rnpMailParamerterBuilder = new RnPMailParamerterBuilder();

        RnPMailParameters mailParameters = rnpMailParamerterBuilder.prepareMailParameter(file, this.rnpModel);
        rnpMail.sendMail(rnPConfiguredMail, mailParameters);

        return true;
    }

    protected File getReport(/*RnPModel rnpModel*/) throws IOException, Exception {

        RnPBomData rnpBomData = new RnPBomData();

        ResponseEntity responseEntity = rnpBomData.generateBomData(rnpModel.getHttpRequest(), rnpModel.getHttpResponse(), rnpModel.getType(), rnpModel.getName(), rnpModel.getRev(), rnpModel.getObjectId(), rnpModel.getExpandLevel(),
                rnpModel.getIsDrawingInfoRequired(), rnpModel.getIsSummaryRequired(), rnpModel.getAttributeListString(), rnpModel.getPrintDelivery(), rnpModel.getMainProjTitle(), rnpModel.getPsk(), rnpModel.getSubTitle(), rnpModel.getProduct(),
                rnpModel.getLang(), rnpModel.getPrimaryLang(), rnpModel.getSecondaryLang(), rnpModel.getFormat(), rnpModel.getRequestId(), rnpModel.getContext(), rnpModel.getDocType());
        String responseBomData = (String) responseEntity.getBody();
        rnpModel.setResponseBomData(responseBomData);

        if (responseEntity.getStatusCodeValue() != 200) {
//                throw RuntimeException();
//                return responseEntity;
        }

        HashMap rootItemParams = CommonUtil.getInfoMapFromBomDataResponse(rnpModel.getResponseBomData(), "rootItemInfo");
        HashMap deliveryParams = null;
        if (!NullOrEmptyChecker.isNullOrEmpty(rnpModel.getPrintDelivery()) && Boolean.parseBoolean(rnpModel.getPrintDelivery())) {
            deliveryParams = CommonUtil.getInfoMapFromBomDataResponse(rnpModel.getResponseBomData(), "Delivery Project Info");
        }

        rnpModel.setResponseBomData(rnpModel.getResponseBomData());
        rnpModel.setRootItemParams(rootItemParams);
        rnpModel.setDeliveryParams(deliveryParams);

        File reportGenerator = reportGenerator(/*rnpModel*/);

        return reportGenerator;
    }

    protected File reportGenerator(/*RnPModel rnpModel*/) {
        if (!rnpModel.getResponseBomData().isEmpty()) {
            JasperReportGenerator jasperReportGenerator = new JasperReportGenerator();
            try {

                Map<String, String> responseData = jasperReportGenerator.generateReport(rnpModel.getResponseBomData(), rnpModel.getRootItemParams(), rnpModel.getDeliveryParams(), rnpModel.getFormat(), rnpModel.getLang(), rnpModel.getRequestId(), Boolean.parseBoolean(rnpModel.getIsSummaryRequired()), rnpModel.getType(), rnpModel.getIsMBOMReport());

                String outputFile = responseData.get("filePath");
                if (!outputFile.isEmpty()) {
                    rnpModel.setIsFileGenerated(true);
                    if (rnpModel.isDownload()) {
                        return generateBOMReport(outputFile);
                    }
                } else {
                    RNP_BACKGROUND_PROCESS_IMPL_LOGGER.debug("Failed to generate report file.");
                }
            } catch (Exception exp) {
                RNP_BACKGROUND_PROCESS_IMPL_LOGGER.error(exp.getMessage());
            } finally {
                jasperReportGenerator = null;
            }
        } else {
            RNP_BACKGROUND_PROCESS_IMPL_LOGGER.debug("Received data from ReportJsonData() method is empty.");
        }
        return null;
    }

    protected File generateBOMReport(String outputFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(outputFile);
//        String applicationType;
//        String probeContentType = Files.probeContentType(path);

//        if (probeContentType == null || probeContentType.equalsIgnoreCase("")) {
//            applicationType = "application/octet-stream";
//        } else {
//            applicationType = probeContentType;
//        }
        File reportFile = path.toFile();
        rnpModel.setDownloadableFile(reportFile);
        return reportFile;
    }
}
