/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.mastershipChange;

import com.bjit.common.rest.app.service.model.rnp.RnPModel;
import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author BJIT
 */

//@Lazy
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Qualifier("preConfiguredMCMail")
public class MCMailConfigPreparation {

    private static final Logger RNP_MAIL_CONFIGURATION_PREPARATION_LOGGER = Logger.getLogger(MCMailConfigPreparation.class);

    @Autowired MCMailConfig mailConfig;
    
    public MCMailConfig getMCConfiguredMail(String applicationType, String templateType, MCModel mcModel) throws Exception {

        String host = PropertyReader.getProperty("mastership.change.mail.host");
        String protocol = PropertyReader.getProperty("mastership.change.mail.transport.protocol");
        String from = PropertyReader.getProperty("mastership.change.mail.from");
        String subject = PropertyReader.getProperty("mastership.change.mail.subject");

        String to = mcModel.getReceiverEmail(); //PropertyReader.getProperty("rnp.mail.to");
        List<String> toList = getReceiversList(to);

        String cc = PropertyReader.getProperty("mastership.change.mail.cc");
        List<String> ccList = getReceiversList(cc);

        String bcc = PropertyReader.getProperty("mastership.change.mail.bcc");
        List<String> bccList = getReceiversList(bcc);

        Boolean debug = Boolean.parseBoolean(PropertyReader.getProperty("mastership.change.mail.debug"));

        String mailTemplate = getTempalte(applicationType, templateType);

        mailConfig.setHost(host);
        mailConfig.setTransportProtocol(protocol);
        mailConfig.setSender(from);
        mailConfig.setSubject(subject);
        mailConfig.setTo(toList);
        mailConfig.setCc(ccList);
        mailConfig.setBcc(bccList);
        mailConfig.setDebug(debug);
        mailConfig.setTemplate(mailTemplate);
        
        
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
