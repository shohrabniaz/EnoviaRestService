package com.bjit.common.rest.app.service.enovia_pdm.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MailS {

    private static final Logger MAIL_LOGGER = Logger.getLogger(MailS.class);

    public void sendMail(MailConfigS mailConfig, String messageBody) throws MessagingException {
        try {
            Properties properties = prepareMessageAttributes(mailConfig);

            Session session = Session.getDefaultInstance(properties, null);
            session.setDebug(mailConfig.getDebug());

            MimeMessage message = new MimeMessage(session);
            prepareMessageAttributes(mailConfig, message);

            sendMail(messageBody, message);
            MAIL_LOGGER.info("Mail sent successfully");
        } catch (MessagingException exp) {
            MAIL_LOGGER.info("Failed To Send Email. Cause: " + exp.getMessage());
            throw exp;
        }
    }

    public void sendMail(MailConfigS mailConfig, String to, String messageBody, String filePath) {
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

    private void prepareMessageAttributes(MailConfigS mailConfig, MimeMessage message) throws MessagingException {
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
        message.setSubject(mailConfig.getSubject());
    }

    private Properties prepareMessageAttributes(MailConfigS mailConfig) throws MessagingException {
        Properties props = System.getProperties();

        props.put("mail.host", mailConfig.getHost());
        props.put("mail.transport.protocol", mailConfig.getTransportProtocol());
        props.put("mail.from", mailConfig.getSender());

        return props;
    }
}
