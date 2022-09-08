/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.config;

import com.bjit.common.rest.app.service.enovia_pdm.handlers.MailResponseHandler;
import com.bjit.common.rest.app.service.enovia_pdm.handlers.ServiceResponseHandler;
import com.bjit.common.rest.app.service.enovia_pdm.models.ResponseModel;
import com.bjit.common.rest.app.service.enovia_pdm.service.IMasterShipChange;
import com.bjit.common.rest.app.service.enovia_pdm.service.MasterShipChangeService;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author BJIT
 */
@Configuration
public class AppConfig {

    private static final Logger APP_CONFIG_LOGGER = Logger.getLogger(AppConfig.class);

    @Bean
    @Qualifier("responseMessageFormatterBean")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ResponseMessageFormaterBean responseMessageFormatterBean() {
        return new ResponseMessageFormaterBean();
    }

    @Bean
    @Qualifier("masterShipChangeService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    IMasterShipChange masterShipChangeService() {
        return new MasterShipChangeService();
    }

//    @Bean
//    @Qualifier("mailConfig")
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    MailConfig getMailConfig() {
//        MailConfig mailConfig = new MailConfig();
//        return mailConfig;
//    }
//
//    @Bean
//    @Lazy
//    @Qualifier("preConfiguredMail")
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//    MailConfig getPreconfiguredMail() {
//
//        String host = PropertyReader.getProperty("mastership.change.mail.host");
//        String protocol = PropertyReader.getProperty("mastership.change.mail.transport.protocol");
//        String from = PropertyReader.getProperty("mastership.change.mail.from");
//        String subject = PropertyReader.getProperty("mastership.change.mail.subject");
//
//        String to = PropertyReader.getProperty("mastership.change.mail.to");
//        List<String> toList = getReceiversList(to);
//
//        String cc = PropertyReader.getProperty("mastership.change.mail.cc");
//        List<String> ccList = getReceiversList(cc);
//
//        String bcc = PropertyReader.getProperty("mastership.change.mail.bcc");
//        List<String> bccList = getReceiversList(bcc);
//
//        Boolean debug = Boolean.parseBoolean(PropertyReader.getProperty("mastership.change.mail.debug"));
//
//        MailConfig mailConfig = new MailConfig(host, protocol, from, subject, toList, ccList, bccList, debug, getTempalte());
//
//        return mailConfig;
//    }
//
//    private List<String> getReceiversList(String receiver) {
//        List<String> toList;
//        if (Optional.ofNullable(receiver).filter(carbonCopy -> !carbonCopy.isEmpty()).isPresent()) {
//            toList = Arrays.asList(receiver.split("\\s*,\\s*"));
//        } else {
//            toList = Collections.emptyList();
//        }
//        return toList;
//    }
//
//    private String getTempalte() {
//        try {
//            String template = PropertyReader.getProperty("mc_mail.template");
//            ClassPathResource classPathResource = new ClassPathResource(template);
//            InputStream inputStream = classPathResource.getInputStream();
//            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
//            String data = new String(bdata, StandardCharsets.UTF_8);
//            return data;
//        } catch (Exception exp) {
//            APP_CONFIG_LOGGER.error(exp);
//            return "<html>"
//                    + "<head>"
//                    + "    <style>"
//                    + "        table {"
//                    + "            border: double;"
//                    + "        }"
//                    + "        tbody>tr:nth-of-type(odd) {"
//                    + "            background-color: #f2f2f2;"
//                    + "        }"
//                    + "        thead {"
//                    + "            background-color: #d3d3d3;"
//                    + "        }"
//                    + "        th,"
//                    + "        td {"
//                    + "            padding-left: 20px;"
//                    + "            padding-right: 20px;"
//                    + "        }"
//                    + "    </style>"
//                    + "</head>"
//                    + "<body>"
//                    + "    <table>"
//                    + "        <thead>"
//                    + "            <tr>"
//                    + "                <th>Item Name</th>"
//                    + "                <th>Item Revision</th>"
//                    + "                <th>Message</th>"
//                    + "            </tr>"
//                    + "        </thead>"
//                    + "        <tbody>$ERROR_MESSAGE_FROM_PDM$</tbody>"
//                    + "    </table>"
//                    + "</body>"
//                    + "</html>";
//        }
//    }

    @Bean
    @Qualifier("serviceResponseHandler")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ServiceResponseHandler getServiceResponseHandler() {
        ServiceResponseHandler<String, HashMap<String, List<ResponseModel>>> serviceResponseHandler = new ServiceResponseHandler();
        return serviceResponseHandler;
    }

    @Bean
    @Qualifier("mailResponseHandler")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    MailResponseHandler getMailResponseHandler() {
        MailResponseHandler<Boolean, HashMap<String, List<ResponseModel>>, HttpServletRequest> mailResponseHandler = new MailResponseHandler();
        return mailResponseHandler;
    }
}
