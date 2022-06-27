/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.rnp;

import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author BJIT
 */
public class RnPMailConfigPreparation {

    private static final Logger RNP_MAIL_CONFIGURATION_PREPARATION_LOGGER = org.apache.log4j.Logger.getLogger(RnPMailConfigPreparation.class);

    public RnPMailConfig getRnPConfiguredMail(RnPModel rnpModel, String applicationType, String templateType) throws Exception {

        String host = PropertyReader.getProperty("rnp.mail.host");
        String protocol = PropertyReader.getProperty("rnp.mail.transport.protocol");
        String from = PropertyReader.getProperty("rnp.mail.from");
        String subject = PropertyReader.getProperty("rnp.mail.subject");

        String to = rnpModel.getReceiverEmail(); //PropertyReader.getProperty("rnp.mail.to");
        List<String> toList = getReceiversList(to);

        String cc = PropertyReader.getProperty("rnp.mail.cc");
        List<String> ccList = getReceiversList(cc);

        String bcc = PropertyReader.getProperty("rnp.mail.bcc");
        List<String> bccList = getReceiversList(bcc);

        Boolean debug = Boolean.parseBoolean(PropertyReader.getProperty("rnp.mail.debug"));

        String mailTemplate = getTempalte(applicationType, templateType);
        
        RnPMailConfig mailConfig = new RnPMailConfig(host, protocol, from, subject, toList, ccList, bccList, debug, mailTemplate);

        return mailConfig;
    }

    private String getTempalte(String applicationType, String templateType) {
        try {
            String template = PropertyReader.getProperty(applicationType.toLowerCase()+ ".mail." + templateType.toLowerCase() + ".template");
            ClassPathResource classPathResource = new ClassPathResource(template);
            InputStream inputStream = classPathResource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(bdata, StandardCharsets.UTF_8);
            return data;
        } catch (Exception exp) {
            RNP_MAIL_CONFIGURATION_PREPARATION_LOGGER.error(exp);
            return "<html>"
                    + "<head></head>"
                    + "<body>"
                    + "    <div><%=REPORT_DOWNLOAD_LINK%></div>"
                    + "</body>"
                    + "</html>";
        }
    }

//    private String getTempalte(String applicationType, String templateType) throws Exception {
//        try {
//            MailMapperProcessor mailMapperProcessor = new MailMapperProcessor();
//            HashMap<String, Templates> mailTemplatesMap = mailMapperProcessor.processAttributeXMLMapper();
//            
//            String mailTemplate = mailTemplatesMap.get(applicationType).getTemplateList().stream().filter(template -> template.getType().equalsIgnoreCase(templateType)).findFirst().get().getValue();
//            return mailTemplate;
//        } catch (Exception exp) {
//            RNP_MAIL_CONFIGURATION_PREPARATION_LOGGER.error(exp);
//            throw exp;
//        }
//    }

    private List<String> getReceiversList(String receiver) {
        List<String> toList;
        if (Optional.ofNullable(receiver).filter(carbonCopy -> !carbonCopy.isEmpty()).isPresent()) {
            toList = Arrays.asList(receiver.split("\\s*,\\s*"));
        } else {
            toList = Collections.emptyList();
        }
        return toList;
    }
}
