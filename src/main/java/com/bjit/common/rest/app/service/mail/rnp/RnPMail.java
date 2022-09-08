package com.bjit.common.rest.app.service.mail.rnp;

import com.bjit.common.rest.app.service.mail.TemplateParser;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

public class RnPMail {

    private static final Logger MAIL_LOGGER = Logger.getLogger(RnPMail.class);

    public void sendMail(RnPMailConfig mailConfig, RnPMailParameters mailParameter) throws MessagingException {
        try {
            Properties properties = prepareMessageAttributes(mailConfig);

            String mailTemplate = mailConfig.getTemplate();
            
            TemplateParser templateParser = new TemplateParser();
            String htmlMailTemplate = templateParser.parse(mailTemplate, mailParameter);

            Session session = Session.getDefaultInstance(properties, null);
            session.setDebug(mailConfig.getDebug());

            MimeMessage message = new MimeMessage(session);
            prepareMessageAttributes(mailConfig, message, templateParser, mailParameter);

            sendMail(htmlMailTemplate, message);
            MAIL_LOGGER.info("Mail sent successfully");
        } catch (MessagingException exp) {
            MAIL_LOGGER.info("Failed To Send Email. Cause: " + exp.getMessage());
            throw exp;
        }finally{
            MAIL_LOGGER.info("Mail sent receiver : " + mailConfig.getTo());
            MAIL_LOGGER.info("Mail sent CC : " + mailConfig.getCc().toString());
            MAIL_LOGGER.info("Mail sent BCC : " + mailConfig.getBcc().toString());
            MAIL_LOGGER.info("Mail sent subject : " + mailConfig.getSubject());
        }
    }

    public void sendMail(RnPMailConfig mailConfig, String to, String messageBody, String filePath) {
        throw new NotImplementedException("Not implemented exception");
    }

    private void sendMail(String messageBody, MimeMessage message) throws MessagingException {
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(messageBody, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);

        Transport.send(message);
    }

    private void prepareMessageAttributes(RnPMailConfig mailConfig, MimeMessage message, TemplateParser templateParser, RnPMailParameters mailParameter) throws MessagingException {
        mailConfig.getTo().forEach((String receiver) -> {
            try {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
        mailConfig.getCc().forEach((String cc) -> {
            try {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
        mailConfig.getBcc().forEach((bcc) -> {
            try {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });

        message.setFrom(new InternetAddress(mailConfig.getSender()));
        message.setSubject(templateParser.parse(mailConfig.getSubject(), mailParameter));
    }

    private Properties prepareMessageAttributes(RnPMailConfig mailConfig) throws MessagingException {
        Properties props = System.getProperties();

        props.put("mail.host", mailConfig.getHost());
        props.put("mail.transport.protocol", mailConfig.getTransportProtocol());
        props.put("mail.from", mailConfig.getSender());

        return props;
    }
}
