/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.rnp;

import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class RnPMailParamerterBuilder {

    private static final Logger MAIL_PARAMETER_BUILDER_LOGGER = Logger.getLogger(RnPMailParamerterBuilder.class);

    public RnPMailParameters prepareMailParameter(File file, RnPModel rnpModel) throws MalformedURLException {
        RnPMailParameters rnpMailParameters = new RnPMailParameters();
        rnpMailParameters.setReportDownloadLink(prepareReportDownloadLink(file, rnpModel));
        rnpMailParameters.setEnvName(getEnvironmentName());
        rnpMailParameters.setItemName(getItemName(file.getName()));
        rnpMailParameters.setMailSubject(PropertyReader.getProperty("rnp.mail.subject"));
        return rnpMailParameters;
    }

    private String prepareReportDownloadLink(File file, RnPModel rnpModel) throws MalformedURLException {
        String fileName = file.getName();
        String reportDownloadUrl = getBaseUrl(rnpModel) + PropertyReader.getProperty("mail.report.download.service") + fileName;
        reportDownloadUrl = "<a href='" + reportDownloadUrl + "'>" + getItemName(fileName) + "</a>";
        MAIL_PARAMETER_BUILDER_LOGGER.info(reportDownloadUrl);
        return reportDownloadUrl;
    }

    private static String getBaseUrl(RnPModel rnpModel) throws MalformedURLException {
        String devMachine = System.getenv("dev_machine");
        devMachine = Optional.ofNullable(devMachine).filter(devPc -> !devPc.isEmpty()).orElse("");
        MAIL_PARAMETER_BUILDER_LOGGER.info("Machine : " + devMachine);
        if((devMachine.equalsIgnoreCase("local_code_dev_machine")) && (rnpModel.getBaseUrl().contains("localhost") || rnpModel.getBaseUrl().contains("127.0.0.1"))){
            return rnpModel.getBaseUrl();
        }
        else{
            return PropertyReader.getProperty("enovia.rest.service.url");
        }
     
        
//        String systemURL = PropertyReader.getProperty("matrix.context.cas.connection.host");
//        try {
//            URL baseUrl = new URL(systemURL);
//            String protocol = baseUrl.getProtocol();
//            String host = baseUrl.getHost();
//            Integer port = baseUrl.getPort();
//            String preparedBaseUrl = protocol + "://" + host + (port < 0 ? "" : ":" + port) + "/";
//            return preparedBaseUrl;
//        } catch (MalformedURLException exp) {
//            MAIL_PARAMETER_BUILDER_LOGGER.error(exp);
//            throw exp;
//        }
    }

    private String getItemName(String fileName) {
        String[] fileNameParts = fileName.split("_");
        return fileNameParts.length > 2 ? fileNameParts[1] : fileName;
    }

    private String getEnvironmentName() {
        String environmentName = PropertyReader.getEnvironmentName();
        String[] envNameParts = environmentName.split("_");
        return envNameParts.length > 1 ? envNameParts[1] : envNameParts[0];
    }
}
